-- ============================================================================
-- 04_simd_operations.sql — SIMD-Accelerated Operations in PostgreSQL 18
-- ============================================================================
-- PostgreSQL 18 leverages SIMD (Single Instruction, Multiple Data) CPU
-- instructions for significant speedups in low-level operations like bit
-- counting, CRC calculations, text processing, and visibility map checks.
-- ============================================================================

-- ============================================================================
-- PART 1: Understanding SIMD in PostgreSQL
-- ============================================================================

-- SIMD allows a single CPU instruction to operate on multiple data elements
-- simultaneously. PostgreSQL 18 detects CPU capabilities at startup and uses:
--   - SSE2 / AVX2 / AVX-512 (x86_64)
--   - NEON (ARM/aarch64)
--
-- No configuration needed — it's automatic!

-- Check PostgreSQL build configuration for SIMD support
SELECT version();

-- View compile-time flags (look for SSE, AVX, NEON mentions)
-- Note: pg_config is a shell command, but we can check settings
SHOW config_file;

-- ============================================================================
-- PART 2: pg_popcount — SIMD-Accelerated Bit Counting
-- ============================================================================

-- pg_popcount counts the number of set bits (1s) in a binary value.
-- Used internally by bitmap indexes, visibility maps, and BRIN indexes.

-- Example: count bits in a bytea value
SELECT
    '\x0F'::bytea AS hex_value,
    pg_popcount('\x0F'::bytea) AS bits_set;
    -- 0x0F = 00001111 → 4 bits set

SELECT
    '\xFF'::bytea AS hex_value,
    pg_popcount('\xFF'::bytea) AS bits_set;
    -- 0xFF = 11111111 → 8 bits set

SELECT
    '\xAA'::bytea AS hex_value,
    pg_popcount('\xAA'::bytea) AS bits_set;
    -- 0xAA = 10101010 → 4 bits set

-- Multi-byte popcount — this is where SIMD shines
SELECT pg_popcount('\xFFFFFFFFFFFFFFFF'::bytea) AS bits_in_8_bytes;
-- 8 bytes × 8 bits = 64 bits set

-- ============================================================================
-- PART 3: Benchmarking Bit Operations (Visibility Map Simulation)
-- ============================================================================

-- Visibility maps use bit operations to track which pages are all-visible.
-- SIMD accelerates the scanning of these bitmaps.

-- Create a table to generate large visibility map activity
DROP TABLE IF EXISTS simd_bench;
CREATE TABLE simd_bench (
    id      BIGSERIAL PRIMARY KEY,
    payload TEXT
);

-- Insert 500K rows
INSERT INTO simd_bench (payload)
SELECT md5(random()::text)
FROM generate_series(1, 500000);

-- Run VACUUM to populate the visibility map
VACUUM simd_bench;

-- Check visibility map status
SELECT
    relname,
    pg_size_pretty(pg_relation_size(oid)) AS table_size,
    relpages AS total_pages,
    relallvisible AS all_visible_pages
FROM pg_class
WHERE relname = 'simd_bench';

-- The ratio of all_visible_pages / total_pages shows VM coverage
-- SIMD speeds up scanning this bitmap internally

-- ============================================================================
-- PART 4: CRC Calculations in WAL
-- ============================================================================

-- WAL (Write-Ahead Log) records include CRC-32C checksums for integrity.
-- PostgreSQL 18 uses SIMD-accelerated CRC computation.

-- This speeds up:
-- 1. WAL record writing (every INSERT, UPDATE, DELETE)
-- 2. WAL replay during recovery
-- 3. Streaming replication

-- Check if data checksums are enabled (uses CRC internally)
SHOW data_checksums;

-- Check WAL stats — CRC is computed for every WAL record
SELECT
    pg_current_wal_lsn() AS current_wal_position,
    pg_wal_lsn_diff(pg_current_wal_lsn(), '0/0') AS total_wal_bytes;

-- Generate WAL activity
\timing on

DROP TABLE IF EXISTS crc_bench;
CREATE TABLE crc_bench (id INT, data TEXT);

-- Each INSERT generates WAL records with CRC checksums
INSERT INTO crc_bench
SELECT g, md5(random()::text)
FROM generate_series(1, 100000) g;

\timing off

-- Check WAL generated
SELECT pg_wal_lsn_diff(pg_current_wal_lsn(), '0/0') AS total_wal_bytes_after;

-- ============================================================================
-- PART 5: Text Processing Speedups
-- ============================================================================

-- SIMD accelerates certain string operations:
-- - ASCII validation
-- - Character encoding conversions
-- - String comparison operations in sorting

-- Create test data for text processing
DROP TABLE IF EXISTS text_bench;
CREATE TABLE text_bench (
    id    BIGSERIAL PRIMARY KEY,
    name  TEXT,
    email TEXT,
    bio   TEXT
);

INSERT INTO text_bench (name, email, bio)
SELECT
    'User_' || g,
    'user_' || g || '@example.com',
    repeat(md5(random()::text), 10)  -- ~320 chars per row
FROM generate_series(1, 200000) g;

ANALYZE text_bench;

\timing on

-- Sort operation — string comparison benefits from SIMD
EXPLAIN (ANALYZE, BUFFERS)
SELECT name
FROM text_bench
ORDER BY name
LIMIT 100;

-- Pattern matching — internal scanning can use SIMD
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*)
FROM text_bench
WHERE email LIKE '%42%';

-- String aggregation
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    left(name, 7) AS prefix,
    count(*),
    avg(length(bio)) AS avg_bio_length
FROM text_bench
GROUP BY left(name, 7)
ORDER BY count(*) DESC
LIMIT 20;

\timing off

-- ============================================================================
-- PART 6: Bitmap Scan Operations
-- ============================================================================

-- Bitmap scans rely on bitwise operations (AND, OR, NOT) to combine
-- multiple index results. SIMD accelerates these bitmap manipulations.

CREATE INDEX idx_text_bench_name ON text_bench(name);
CREATE INDEX idx_text_bench_email ON text_bench(email);

ANALYZE text_bench;

\timing on

-- BitmapAnd — combines two bitmap indexes using bitwise AND
-- SIMD speeds up the bitmap combination step
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*)
FROM text_bench
WHERE name > 'User_1' AND name < 'User_2'
  AND email LIKE 'user_1%';

-- BitmapOr — combines two bitmap indexes using bitwise OR
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*)
FROM text_bench
WHERE name LIKE 'User_100%'
   OR email LIKE 'user_200%';

\timing off

-- ============================================================================
-- PART 7: VACUUM Performance (Visibility Map + Dead Tuple Bitmap)
-- ============================================================================

-- VACUUM benefits doubly from SIMD:
-- 1. Faster visibility map scanning
-- 2. Faster dead tuple bitmap operations

-- Create some dead tuples
UPDATE text_bench SET bio = bio || '!' WHERE id % 5 = 0;
DELETE FROM text_bench WHERE id % 11 = 0;

-- Check dead tuples before vacuum
SELECT
    relname,
    n_live_tup,
    n_dead_tup,
    n_mod_since_analyze
FROM pg_stat_user_tables
WHERE relname = 'text_bench';

-- Time the VACUUM
\timing on
VACUUM (VERBOSE) text_bench;
\timing off

-- Dead tuples after vacuum
SELECT
    relname,
    n_live_tup,
    n_dead_tup,
    last_vacuum
FROM pg_stat_user_tables
WHERE relname = 'text_bench';

-- ============================================================================
-- PART 8: Verifying SIMD Usage (pg_config)
-- ============================================================================

-- The following shell commands verify SIMD compilation support:
-- ┌─────────────────────────────────────────────────────────────────────────┐
-- │  # Check if PG was compiled with SIMD flags                            │
-- │  pg_config --cflags | grep -iE 'sse|avx|neon'                          │
-- │                                                                        │
-- │  # Check CPU capabilities on Linux                                     │
-- │  grep -m1 'flags' /proc/cpuinfo | tr ' ' '\n' | grep -iE              │
-- │    'sse|avx|popcnt|crc32'                                              │
-- │                                                                        │
-- │  # Expected flags for SIMD acceleration:                               │
-- │  #   sse2    — baseline for x86_64                                     │
-- │  #   sse4_2  — CRC32C hardware instruction                             │
-- │  #   avx2    — 256-bit vector operations                               │
-- │  #   popcnt  — hardware popcount instruction                           │
-- └─────────────────────────────────────────────────────────────────────────┘

-- ============================================================================
-- CLEANUP
-- ============================================================================

-- Uncomment to clean up:
-- DROP TABLE IF EXISTS simd_bench;
-- DROP TABLE IF EXISTS crc_bench;
-- DROP TABLE IF EXISTS text_bench;

-- ============================================================================
-- KEY TAKEAWAYS
-- ============================================================================
--
-- 1. SIMD is automatic — no configuration required, PG detects CPU at startup
-- 2. pg_popcount uses hardware POPCNT / SIMD for fast bit counting
-- 3. CRC-32C checksums in WAL are SIMD-accelerated (benefits all writes)
-- 4. Visibility map operations in VACUUM use SIMD bitmap scanning
-- 5. Bitmap index combining (BitmapAnd, BitmapOr) leverages SIMD
-- 6. Text processing (sorting, encoding validation) benefits from SIMD
-- 7. Typical speedup is 2-4x on supported operations with no config changes
-- ============================================================================

