-- ============================================================================
-- 03_parallel_queries.sql — Parallel Query Execution in PostgreSQL 18
-- ============================================================================
-- PostgreSQL 18 improves parallel scans for partitioned tables and enhances
-- planning/execution for parallel joins, aggregations, and sorts.
-- ============================================================================

-- ============================================================================
-- PART 1: Check Parallel Query Configuration
-- ============================================================================

SHOW max_worker_processes;
SHOW max_parallel_workers;
SHOW max_parallel_workers_per_gather;
SHOW min_parallel_table_scan_size;
SHOW min_parallel_index_scan_size;
SHOW parallel_setup_cost;
SHOW parallel_tuple_cost;

-- ============================================================================
-- PART 2: Create Large Partitioned Table
-- ============================================================================

DROP TABLE IF EXISTS sales_partitioned CASCADE;

CREATE TABLE sales_partitioned (
    id          BIGSERIAL,
    sale_date   DATE NOT NULL,
    region      TEXT NOT NULL,
    product_id  INT NOT NULL,
    quantity    INT NOT NULL,
    amount      NUMERIC(10,2) NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()
) PARTITION BY RANGE (sale_date);

-- Create monthly partitions for one year
CREATE TABLE sales_2025_01 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');
CREATE TABLE sales_2025_02 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');
CREATE TABLE sales_2025_03 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-03-01') TO ('2025-04-01');
CREATE TABLE sales_2025_04 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-04-01') TO ('2025-05-01');
CREATE TABLE sales_2025_05 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-05-01') TO ('2025-06-01');
CREATE TABLE sales_2025_06 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-06-01') TO ('2025-07-01');
CREATE TABLE sales_2025_07 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-07-01') TO ('2025-08-01');
CREATE TABLE sales_2025_08 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-08-01') TO ('2025-09-01');
CREATE TABLE sales_2025_09 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-09-01') TO ('2025-10-01');
CREATE TABLE sales_2025_10 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');
CREATE TABLE sales_2025_11 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-11-01') TO ('2025-12-01');
CREATE TABLE sales_2025_12 PARTITION OF sales_partitioned FOR VALUES FROM ('2025-12-01') TO ('2026-01-01');

-- Insert 5 million rows across partitions
INSERT INTO sales_partitioned (sale_date, region, product_id, quantity, amount)
SELECT
    DATE '2025-01-01' + (random() * 364)::int,
    (ARRAY['NORTH', 'SOUTH', 'EAST', 'WEST', 'CENTRAL'])[(random() * 4 + 1)::int],
    (random() * 5000)::int + 1,
    (random() * 10)::int + 1,
    round((random() * 1000)::numeric, 2)
FROM generate_series(1, 5000000);

-- Update statistics
ANALYZE sales_partitioned;

-- ============================================================================
-- PART 3: Baseline — Parallelism Disabled
-- ============================================================================

\timing on

-- Disable parallelism for baseline
SET max_parallel_workers_per_gather = 0;

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    region,
    date_trunc('month', sale_date) AS month,
    SUM(amount) AS total_amount,
    AVG(quantity) AS avg_qty,
    COUNT(*) AS tx_count
FROM sales_partitioned
GROUP BY region, date_trunc('month', sale_date)
ORDER BY month, region;

-- ============================================================================
-- PART 4: Enable Parallelism and Compare
-- ============================================================================

SET max_parallel_workers_per_gather = 4;
SET max_parallel_workers = 8;
SET min_parallel_table_scan_size = '8MB';

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    region,
    date_trunc('month', sale_date) AS month,
    SUM(amount) AS total_amount,
    AVG(quantity) AS avg_qty,
    COUNT(*) AS tx_count
FROM sales_partitioned
GROUP BY region, date_trunc('month', sale_date)
ORDER BY month, region;

-- Look for in the plan:
-- - Gather
-- - Parallel Append
-- - Partial Aggregate
-- - Finalize Aggregate

-- ============================================================================
-- PART 5: Parallel Partition Scan
-- ============================================================================

-- Query a broad date range to trigger multiple partitions and parallel append
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    product_id,
    SUM(quantity) AS total_qty,
    SUM(amount) AS total_sales
FROM sales_partitioned
WHERE sale_date BETWEEN '2025-03-01' AND '2025-10-31'
GROUP BY product_id
HAVING SUM(amount) > 10000
ORDER BY total_sales DESC
LIMIT 100;

-- ============================================================================
-- PART 6: Partition Pruning + Parallelism
-- ============================================================================

-- Narrow date filter should prune partitions and still allow parallel scan
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    region,
    COUNT(*) AS tx_count,
    SUM(amount) AS revenue
FROM sales_partitioned
WHERE sale_date BETWEEN '2025-06-01' AND '2025-06-30'   -- only June partition
GROUP BY region;

-- ============================================================================
-- PART 7: Parallel Hash Join Demo
-- ============================================================================

DROP TABLE IF EXISTS product_dimension;
CREATE TABLE product_dimension (
    product_id   INT PRIMARY KEY,
    category     TEXT,
    brand        TEXT,
    is_active    BOOLEAN
);

INSERT INTO product_dimension (product_id, category, brand, is_active)
SELECT
    g,
    (ARRAY['Electronics', 'Home', 'Fashion', 'Food', 'Sports'])[(random() * 4 + 1)::int],
    'Brand_' || ((random() * 200)::int + 1),
    (random() > 0.1)
FROM generate_series(1, 5000) g;

ANALYZE product_dimension;

-- Join fact (5M rows) with dimension (5k rows)
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    d.category,
    d.brand,
    COUNT(*) AS tx_count,
    SUM(s.amount) AS total_revenue
FROM sales_partitioned s
JOIN product_dimension d ON s.product_id = d.product_id
WHERE d.is_active = true
GROUP BY d.category, d.brand
ORDER BY total_revenue DESC
LIMIT 50;

-- Look for:
-- - Parallel Hash Join
-- - Parallel Seq Scan
-- - Gather Merge

-- ============================================================================
-- PART 8: Tuning Knobs Experiment
-- ============================================================================

-- Try lower parallel_tuple_cost to encourage parallel plans
SET parallel_tuple_cost = 0.01;
SET parallel_setup_cost = 100;

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    region,
    SUM(amount)
FROM sales_partitioned
GROUP BY region;

-- Reset planner costs
RESET parallel_tuple_cost;
RESET parallel_setup_cost;

\timing off

-- ============================================================================
-- PART 9: Check Current Parallel Worker Activity
-- ============================================================================

SELECT
    pid,
    backend_type,
    state,
    wait_event_type,
    wait_event,
    query
FROM pg_stat_activity
WHERE backend_type IN ('parallel worker', 'client backend')
  AND state IS NOT NULL
ORDER BY backend_type, pid;

-- ============================================================================
-- CLEANUP
-- ============================================================================

-- Uncomment to clean up:
-- DROP TABLE IF EXISTS sales_partitioned CASCADE;
-- DROP TABLE IF EXISTS product_dimension;

-- ============================================================================
-- KEY TAKEAWAYS
-- ============================================================================
--
-- 1. PostgreSQL 18 improves parallel scans over partitioned tables
-- 2. Parallel plans use Gather / Gather Merge with worker processes
-- 3. Partition pruning and parallel scan can work together
-- 4. Partial Aggregate + Finalize Aggregate improves large GROUP BY queries
-- 5. Parallel Hash Join accelerates large fact-to-dimension joins
-- 6. Tuning max_parallel_workers_per_gather affects query speed significantly
-- 7. Lower parallel_*_cost can make planner choose parallel plans more often
-- ============================================================================
