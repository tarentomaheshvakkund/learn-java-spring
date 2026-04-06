-- ============================================================================
-- 01_installation_and_setup.sql — Installation & Configuration
-- ============================================================================
-- Set up PostgreSQL 18 with Docker, explore key configuration parameters,
-- and create your first databases and roles.
-- ============================================================================

-- ============================================================================
-- PART 1: Verify Your PostgreSQL Installation
-- ============================================================================

-- Check version — confirm you're running PG 18
SELECT version();

-- Check server start time
SELECT pg_postmaster_start_time();

-- Server uptime
SELECT now() - pg_postmaster_start_time() AS uptime;

-- Current database and user
SELECT current_database(), current_user, session_user;

-- ============================================================================
-- PART 2: Key postgresql.conf Parameters (Memory)
-- ============================================================================

-- shared_buffers — PG's main shared memory cache
-- Recommendation: 25% of total RAM (e.g., 256MB for 1GB RAM)
SHOW shared_buffers;

-- work_mem — Memory for each sort/hash operation within a query
-- WARNING: Each query node can use this much, so total can be work_mem × (nodes × connections)
SHOW work_mem;

-- maintenance_work_mem — Memory for maintenance operations (VACUUM, CREATE INDEX)
-- Can be set higher than work_mem since these run less frequently
SHOW maintenance_work_mem;

-- effective_cache_size — Planner's estimate of available OS cache
-- Doesn't allocate memory, just guides the planner
SHOW effective_cache_size;

-- temp_buffers — Per-session memory for temporary tables
SHOW temp_buffers;

-- ============================================================================
-- PART 3: Key postgresql.conf Parameters (WAL & Durability)
-- ============================================================================

-- wal_level — Controls how much info is written to WAL
-- Values: minimal, replica (default), logical
SHOW wal_level;

-- fsync — Forces WAL writes to disk (NEVER turn off in production)
SHOW fsync;

-- synchronous_commit — Trade durability for speed
-- 'on' = wait for WAL flush, 'off' = async (risk of small data loss on crash)
SHOW synchronous_commit;

-- checkpoint_timeout — Time between automatic checkpoints
SHOW checkpoint_timeout;

-- max_wal_size — Max WAL size before forced checkpoint
SHOW max_wal_size;

-- ============================================================================
-- PART 4: Key postgresql.conf Parameters (Connections)
-- ============================================================================

-- max_connections — Maximum concurrent connections
-- Keep low (100-300), use connection pooling for more
SHOW max_connections;

-- superuser_reserved_connections — Reserved for superuser access
SHOW superuser_reserved_connections;

-- listen_addresses — Which network interfaces to accept connections on
SHOW listen_addresses;

-- port — TCP port (default 5432)
SHOW port;

-- ============================================================================
-- PART 5: Key postgresql.conf Parameters (Query Planning)
-- ============================================================================

-- random_page_cost — Planner's estimate for random I/O cost (SSD: set to 1.1)
SHOW random_page_cost;

-- seq_page_cost — Planner's estimate for sequential I/O cost
SHOW seq_page_cost;

-- default_statistics_target — Rows sampled for ANALYZE (higher = better plans, slower ANALYZE)
SHOW default_statistics_target;

-- enable_* — Toggle planner strategies (useful for debugging, not production)
SHOW enable_seqscan;
SHOW enable_indexscan;
SHOW enable_hashjoin;
SHOW enable_mergejoin;

-- ============================================================================
-- PART 6: Creating Databases
-- ============================================================================

-- List existing databases
SELECT datname, datdba, encoding, datcollate, datctype
FROM pg_database
ORDER BY datname;

-- Create a new database
CREATE DATABASE playground
    WITH OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TEMPLATE = template0;

-- Create a database from a template (copies all objects)
-- template1 is the default template; template0 is the "clean" template
CREATE DATABASE playground_copy TEMPLATE playground;

-- Modify a database
ALTER DATABASE playground SET work_mem = '64MB';
ALTER DATABASE playground SET statement_timeout = '30s';

-- View per-database settings
SELECT datname, datconfig
FROM pg_database
WHERE datname = 'playground';

-- ============================================================================
-- PART 7: Creating Roles & Users
-- ============================================================================

-- In PostgreSQL, users ARE roles (with LOGIN privilege)

-- Create a login role (user)
CREATE ROLE app_user WITH
    LOGIN
    PASSWORD 'secure_password'
    CREATEDB
    VALID UNTIL '2027-01-01';

-- Create a non-login role (group role)
CREATE ROLE readonly_group WITH NOLOGIN;

-- Create another user and add to group
CREATE ROLE report_user WITH LOGIN PASSWORD 'report_pass';
GRANT readonly_group TO report_user;

-- View all roles
SELECT
    rolname,
    rolsuper,
    rolcreaterole,
    rolcreatedb,
    rolcanlogin,
    rolconnlimit,
    rolvaliduntil
FROM pg_roles
WHERE rolname NOT LIKE 'pg_%'
ORDER BY rolname;

-- ============================================================================
-- PART 8: Granting Privileges
-- ============================================================================

-- Grant database-level access
GRANT CONNECT ON DATABASE learning TO app_user;
GRANT CONNECT ON DATABASE learning TO readonly_group;

-- Grant schema-level access
GRANT USAGE ON SCHEMA public TO app_user;
GRANT USAGE ON SCHEMA public TO readonly_group;

-- Grant table-level access (for readonly group)
-- This applies to all CURRENT tables in public schema
GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_group;

-- Make it apply to FUTURE tables too
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT ON TABLES TO readonly_group;

-- Full access for app_user
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO app_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- ============================================================================
-- PART 9: pg_hba.conf — Authentication Configuration (Conceptual)
-- ============================================================================

-- pg_hba.conf controls WHO can connect, from WHERE, using WHAT method
-- Format: TYPE  DATABASE  USER  ADDRESS  METHOD

-- Common entries (these are NOT SQL — they go in pg_hba.conf):
-- ┌─────────────────────────────────────────────────────────────────────────┐
-- │  # Local connections via Unix socket                                    │
-- │  local   all         postgres                      peer                │
-- │  local   all         all                           scram-sha-256       │
-- │                                                                        │
-- │  # IPv4 connections                                                     │
-- │  host    all         all         127.0.0.1/32      scram-sha-256       │
-- │  host    all         all         192.168.1.0/24    scram-sha-256       │
-- │                                                                        │
-- │  # IPv6 connections                                                     │
-- │  host    all         all         ::1/128           scram-sha-256       │
-- │                                                                        │
-- │  # Reject everything else                                               │
-- │  host    all         all         0.0.0.0/0         reject              │
-- └─────────────────────────────────────────────────────────────────────────┘

-- Authentication methods:
--   trust          → No password (only for local dev!)
--   scram-sha-256  → Secure password auth (recommended)
--   md5            → Legacy password auth (use scram instead)
--   peer           → OS username must match PG username (Unix socket only)
--   cert           → SSL client certificate
--   reject         → Always deny

-- View current pg_hba.conf location
SHOW hba_file;

-- View loaded HBA rules (PG 15+)
SELECT * FROM pg_hba_file_rules ORDER BY line_number;

-- ============================================================================
-- PART 10: Useful Server Information Queries
-- ============================================================================

-- All current settings (non-default)
SELECT name, setting, unit, source, context
FROM pg_settings
WHERE source != 'default'
ORDER BY name;

-- Database sizes
SELECT
    datname,
    pg_size_pretty(pg_database_size(datname)) AS size
FROM pg_database
WHERE datistemplate = false
ORDER BY pg_database_size(datname) DESC;

-- Active connections
SELECT
    datname,
    usename,
    client_addr,
    state,
    query_start,
    now() - query_start AS query_duration,
    left(query, 60) AS query_preview
FROM pg_stat_activity
WHERE state IS NOT NULL
ORDER BY query_start;

-- ============================================================================
-- CLEANUP
-- ============================================================================

-- Uncomment to clean up:
-- DROP DATABASE IF EXISTS playground;
-- DROP DATABASE IF EXISTS playground_copy;
-- DROP ROLE IF EXISTS app_user;
-- DROP ROLE IF EXISTS report_user;
-- DROP ROLE IF EXISTS readonly_group;

-- ============================================================================
-- KEY TAKEAWAYS
-- ============================================================================
--
-- 1. shared_buffers (25% RAM) and work_mem are the most impactful memory settings
-- 2. wal_level must be 'replica' or 'logical' for replication/backups
-- 3. Roles and users are the same thing — users are roles with LOGIN
-- 4. Use group roles (NOLOGIN) + GRANT for manageable access control
-- 5. ALTER DEFAULT PRIVILEGES ensures future objects get correct permissions
-- 6. pg_hba.conf controls authentication — use scram-sha-256, never trust in prod
-- 7. random_page_cost = 1.1 for SSDs dramatically improves planner decisions
-- 8. pg_settings, pg_stat_activity, pg_database are essential monitoring views
-- ============================================================================

