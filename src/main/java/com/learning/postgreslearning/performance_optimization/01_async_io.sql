-- ============================================================================
-- 01_async_io.sql — Asynchronous I/O in PostgreSQL 18
-- ============================================================================
-- PostgreSQL 18 introduces native async I/O, enabling non-blocking disk
-- operations. This script explores configuration, monitoring, and benchmarking.
-- ============================================================================

-- ============================================================================
-- PART 1: Check Current I/O Configuration
-- ============================================================================

-- What I/O method is PostgreSQL 18 using?
SHOW io_method;
-- Possible values:
--   sync      → traditional synchronous I/O (default fallback)
--   worker    → async I/O via background worker processes
--   io_uring  → Linux io_uring for true kernel-level async I/O

-- How many I/O workers are configured?
SHOW io_workers;

-- Prefetch settings (how many pages to read ahead)
SHOW effective_io_concurrency;
SHOW maintenance_io_concurrency;

-- ============================================================================
-- PART 2: Create Test Data for I/O Benchmarking
-- ============================================================================

-- Create a large table to observe I/O behavior
DROP TABLE IF EXISTS io_test_large;
CREATE TABLE io_test_large (
    id         BIGSERIAL PRIMARY KEY,
    data       TEXT,
    category   INT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Insert 1 million rows (takes ~10-20 seconds)
INSERT INTO io_test_large (data, category)
SELECT
    md5(random()::text) || md5(random()::text),  -- ~64 chars of random data
    (random() * 100)::int                         -- category 0-100
FROM generate_series(1, 1000000);

-- Check table size
SELECT
    pg_size_pretty(pg_total_relation_size('io_test_large')) AS total_size,
    pg_size_pretty(pg_relation_size('io_test_large'))       AS table_size,
    pg_size_pretty(pg_indexes_size('io_test_large'))        AS index_size;

-- ============================================================================
-- PART 3: Observe I/O During Sequential Scan
-- ============================================================================

-- Enable timing to see query duration
\timing on

-- Force a sequential scan (no index usage)
SET enable_indexscan = off;
SET enable_bitmapscan = off;

-- Run a full table scan — this is where async I/O shines
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT count(*) FROM io_test_large WHERE data LIKE '%abc%';

-- Reset
SET enable_indexscan = on;
SET enable_bitmapscan = on;

-- ============================================================================
-- PART 4: Compare with Index Scan (I/O Pattern Difference)
-- ============================================================================

-- Create an index on category
CREATE INDEX idx_io_test_category ON io_test_large(category);

-- Analyze to update statistics
ANALYZE io_test_large;

-- Index scan — random I/O pattern
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT * FROM io_test_large WHERE category = 42;

-- Range scan — sequential-ish I/O pattern
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT * FROM io_test_large WHERE category BETWEEN 10 AND 20;

-- ============================================================================
-- PART 5: Monitor I/O Statistics (pg_stat_io) — PG 16+ feature, enhanced in 18
-- ============================================================================

-- pg_stat_io shows I/O stats by backend type and context
-- Reset stats first for clean measurement
SELECT pg_stat_reset_shared('io');

-- Run a heavy query
SELECT count(*), avg(length(data)) FROM io_test_large;

-- Now check I/O stats
SELECT
    backend_type,
    context,
    reads,
    read_bytes,
    read_time,
    writes,
    write_bytes,
    write_time,
    fsyncs,
    fsync_time
FROM pg_stat_io
WHERE reads > 0 OR writes > 0
ORDER BY reads DESC;

-- ============================================================================
-- PART 6: effective_io_concurrency Impact
-- ============================================================================

-- This controls how many concurrent I/O requests PG can issue for prefetching
-- Default is 1 (conservative), SSDs can handle 200+

-- Check current value
SHOW effective_io_concurrency;

-- For bitmap heap scans, this controls prefetch lookahead
-- Create a scenario where bitmap scan is used
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM io_test_large WHERE category IN (5, 15, 25, 35, 45);

-- Change effective_io_concurrency (session level)
SET effective_io_concurrency = 200;

-- Run the same query again — compare buffer stats
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM io_test_large WHERE category IN (5, 15, 25, 35, 45);

-- Reset
RESET effective_io_concurrency;

-- ============================================================================
-- PART 7: Maintenance I/O Concurrency (VACUUM, CREATE INDEX)
-- ============================================================================

-- This setting affects bulk operations like VACUUM and index creation
SHOW maintenance_io_concurrency;

-- Time an index creation (I/O intensive)
\timing on

SET maintenance_io_concurrency = 10;
DROP INDEX IF EXISTS idx_io_test_data;
CREATE INDEX idx_io_test_data ON io_test_large(data);

-- Now try with higher concurrency
SET maintenance_io_concurrency = 200;
DROP INDEX IF EXISTS idx_io_test_data;
CREATE INDEX idx_io_test_data ON io_test_large(data);

RESET maintenance_io_concurrency;
\timing off

-- ============================================================================
-- PART 8: COPY Command + Async I/O
-- ============================================================================

-- COPY is a bulk loading command that benefits heavily from async writes

-- Export data
\timing on
COPY io_test_large TO '/tmp/io_test_export.csv' WITH (FORMAT CSV);
\timing off

-- Create target table
DROP TABLE IF EXISTS io_test_import;
CREATE TABLE io_test_import (LIKE io_test_large INCLUDING ALL);

-- Import — async I/O accelerates the write path
\timing on
COPY io_test_import FROM '/tmp/io_test_export.csv' WITH (FORMAT CSV);
\timing off

-- Compare sizes
SELECT 'io_test_large' AS table_name, pg_size_pretty(pg_total_relation_size('io_test_large'))
UNION ALL
SELECT 'io_test_import', pg_size_pretty(pg_total_relation_size('io_test_import'));

-- ============================================================================
-- CLEANUP
-- ============================================================================

-- Uncomment to clean up test data:
-- DROP TABLE IF EXISTS io_test_large;
-- DROP TABLE IF EXISTS io_test_import;

-- ============================================================================
-- KEY TAKEAWAYS
-- ============================================================================
--
-- 1. PostgreSQL 18 async I/O reduces wait time on disk operations
-- 2. io_method controls the async strategy (worker vs io_uring)
-- 3. effective_io_concurrency helps with prefetching during bitmap scans
-- 4. maintenance_io_concurrency speeds up VACUUM and index creation
-- 5. pg_stat_io gives visibility into actual I/O patterns
-- 6. COPY and sequential scans benefit the most from async I/O
-- 7. On SSDs/NVMe, set effective_io_concurrency = 200+ for best results
-- ============================================================================
