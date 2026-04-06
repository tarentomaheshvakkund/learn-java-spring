-- ============================================================================
-- 02_incremental_backups.sql — Incremental Base Backups in PostgreSQL 18
-- ============================================================================
-- PostgreSQL 18 adds incremental backup support to pg_basebackup.
-- This script covers the concepts, setup, and commands (run from shell).
-- ============================================================================

-- ============================================================================
-- PART 1: Understanding WAL Summarization (Foundation for Incremental Backups)
-- ============================================================================

-- Incremental backups rely on WAL summarization — PG 18 tracks which
-- data blocks changed between backups by summarizing WAL records.

-- Check if WAL summarization is enabled
SHOW summarize_wal;
-- Should be 'on' for incremental backups to work

-- Check WAL summarizer status
SELECT * FROM pg_wal_summary_stats;

-- View WAL summaries (each tracks changes since last summary)
SELECT * FROM pg_available_wal_summaries()
ORDER BY start_lsn DESC
LIMIT 10;

-- ============================================================================
-- PART 2: Backup Configuration Check
-- ============================================================================

-- Key settings for backups
SHOW wal_level;              -- Must be 'replica' or 'logical'
SHOW archive_mode;           -- 'on' for production backups
SHOW max_wal_senders;        -- Concurrent backup connections allowed
SHOW summarize_wal;          -- Must be 'on' for incremental

-- Check data directory location
SHOW data_directory;

-- ============================================================================
-- PART 3: Full Backup (Shell Commands — Run Outside psql)
-- ============================================================================

-- The following are SHELL commands, not SQL. Run them in your terminal.

-- Step 1: Take a full base backup (this is your "base" for incrementals)
-- ┌─────────────────────────────────────────────────────────────────────────┐
-- │  docker exec pg18-learn pg_basebackup \                                │
-- │    -D /tmp/backup_full \                                               │
-- │    -Ft \                                                               │
-- │    -z \                                                                │
-- │    --checkpoint=fast \                                                 │
-- │    -U postgres                                                        │
-- │                                                                        │
-- │  Flags:                                                                │
-- │    -D /tmp/backup_full  → Destination directory                        │
-- │    -Ft                  → Tar format                                   │
-- │    -z                   → Compress with gzip                           │
-- │    --checkpoint=fast    → Start backup immediately                     │
-- └─────────────────────────────────────────────────────────────────────────┘

-- Step 2: The full backup creates a MANIFEST file
-- This manifest is the reference point for incremental backups
-- ┌─────────────────────────────────────────────────────────────────────────┐
-- │  docker exec pg18-learn ls -la /tmp/backup_full/                       │
-- │  # You'll see: base.tar.gz, pg_wal.tar.gz, backup_manifest            │
-- └─────────────────────────────────────────────────────────────────────────┘

-- ============================================================================
-- PART 4: Make Some Changes (So Incremental Has Something to Capture)
-- ============================================================================

-- Create a table and insert data AFTER the full backup
DROP TABLE IF EXISTS backup_test;
CREATE TABLE backup_test (
    id    SERIAL PRIMARY KEY,
    name  TEXT,
    value NUMERIC
);

INSERT INTO backup_test (name, value)
SELECT
    'item_' || g,
    random() * 1000
FROM generate_series(1, 100000) g;

-- Update some rows (causes page modifications)
UPDATE backup_test SET value = value * 2 WHERE id % 3 = 0;

-- Delete some rows (more page modifications)
DELETE FROM backup_test WHERE id % 7 = 0;

-- Force a checkpoint to flush changes to disk
CHECKPOINT;

-- ============================================================================
-- PART 5: Incremental Backup (Shell Commands)
-- ============================================================================

-- Step 3: Take an incremental backup referencing the full backup's manifest
-- ┌─────────────────────────────────────────────────────────────────────────┐
-- │  docker exec pg18-learn pg_basebackup \                                │
-- │    -D /tmp/backup_incr1 \                                              │
-- │    --incremental=/tmp/backup_full/backup_manifest \                    │
-- │    -Ft \                                                               │
-- │    --checkpoint=fast \                                                 │
-- │    -U postgres                                                        │
-- │                                                                        │
-- │  Key flag:                                                             │
-- │    --incremental=<manifest>  → Only backup blocks changed since        │
-- │                                the referenced backup                   │
-- └─────────────────────────────────────────────────────────────────────────┘

-- Step 4: Compare sizes — incremental should be MUCH smaller
-- ┌─────────────────────────────────────────────────────────────────────────┐
-- │  echo "=== Full Backup ===" && \                                       │
-- │  docker exec pg18-learn du -sh /tmp/backup_full/ && \                  │
-- │  echo "=== Incremental Backup ===" && \                                │
-- │  docker exec pg18-learn du -sh /tmp/backup_incr1/                      │
-- └─────────────────────────────────────────────────────────────────────────┘

-- ============================================================================
-- PART 6: Restoring from Incremental Backups (pg_combinebackup)
-- ============================================================================

-- To restore, you COMBINE full + incremental(s) into a single restorable backup
-- ┌─────────────────────────────────────────────────────────────────────────┐
-- │  # Combine full + incremental into a restore-ready directory           │
-- │  docker exec pg18-learn pg_combinebackup \                             │
-- │    /tmp/backup_full \                                                  │
-- │    /tmp/backup_incr1 \                                                 │
-- │    -o /tmp/backup_combined                                             │
-- │                                                                        │
-- │  # The combined backup can be used to start a new PG instance          │
-- │  # just like a regular pg_basebackup output                            │
-- └─────────────────────────────────────────────────────────────────────────┘

-- ============================================================================
-- PART 7: Chaining Multiple Incrementals
-- ============================================================================

-- You can chain incrementals: Full → Incr1 → Incr2 → Incr3
-- Each incremental references the PREVIOUS backup's manifest

-- Example chain:
-- ┌─────────────────────────────────────────────────────────────────────────┐
-- │  # Monday: Full backup                                                 │
-- │  pg_basebackup -D /backups/monday_full ...                             │
-- │                                                                        │
-- │  # Tuesday: Incremental from Monday's manifest                         │
-- │  pg_basebackup -D /backups/tuesday_incr \                              │
-- │    --incremental=/backups/monday_full/backup_manifest ...              │
-- │                                                                        │
-- │  # Wednesday: Incremental from Tuesday's manifest                      │
-- │  pg_basebackup -D /backups/wednesday_incr \                            │
-- │    --incremental=/backups/tuesday_incr/backup_manifest ...             │
-- │                                                                        │
-- │  # To restore to Wednesday's state:                                    │
-- │  pg_combinebackup \                                                    │
-- │    /backups/monday_full \                                              │
-- │    /backups/tuesday_incr \                                             │
-- │    /backups/wednesday_incr \                                           │
-- │    -o /restore/wednesday                                               │
-- └─────────────────────────────────────────────────────────────────────────┘

-- ============================================================================
-- PART 8: Monitoring WAL Summarization
-- ============================================================================

-- Check summarizer process status
SELECT * FROM pg_stat_activity WHERE backend_type = 'wal summarizer';

-- View available summaries with LSN ranges
SELECT
    start_lsn,
    end_lsn,
    tli
FROM pg_available_wal_summaries()
ORDER BY start_lsn DESC
LIMIT 5;

-- Check current WAL position (for reference)
SELECT pg_current_wal_lsn(), pg_current_wal_insert_lsn();

-- ============================================================================
-- CLEANUP
-- ============================================================================

-- Uncomment to clean up:
-- DROP TABLE IF EXISTS backup_test;

-- Shell cleanup (run in terminal):
-- docker exec pg18-learn rm -rf /tmp/backup_full /tmp/backup_incr1 /tmp/backup_combined

-- ============================================================================
-- KEY TAKEAWAYS
-- ============================================================================
--
-- 1. Incremental backups only store CHANGED blocks since last backup
-- 2. Requires summarize_wal = on (PG 18 default)
-- 3. pg_basebackup --incremental=<manifest> creates an incremental
-- 4. pg_combinebackup merges full + incremental(s) for restore
-- 5. Incrementals can be chained: Full → Incr1 → Incr2 → ...
-- 6. Dramatically reduces backup storage and time (80-95% smaller)
-- 7. Each backup produces a backup_manifest for the next incremental
-- ============================================================================
