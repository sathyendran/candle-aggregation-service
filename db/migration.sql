CREATE EXTENSION IF NOT EXISTS timescaledb;

CREATE TABLE market_data (
                             time      TIMESTAMPTZ NOT NULL,
                             symbol    TEXT NOT NULL,
                             bid       DOUBLE PRECISION NOT NULL,
                             ask       DOUBLE PRECISION NOT NULL
);

SELECT create_hypertable(
               'market_data',
               'time',
               chunk_time_interval => INTERVAL '1 day'
       );

CREATE INDEX idx_market_symbol_time
    ON market_data (symbol, time DESC);

CREATE MATERIALIZED VIEW candle_1s
WITH (
    timescaledb.continuous,
    timescaledb.materialized_only = false
) AS
SELECT
    time_bucket('1 second', time) AS bucket,
    symbol,
    first((bid + ask)/2, time) AS open,
    max((bid + ask)/2) AS high,
    min((bid + ask)/2) AS low,
    last((bid + ask)/2, time) AS close,
    count(*) AS volume
FROM market_data
GROUP BY bucket, symbol;

SELECT add_continuous_aggregate_policy('candle_1s',
                                       start_offset => INTERVAL '2 hours',
                                       end_offset   => INTERVAL '1 second',
                                       schedule_interval => INTERVAL '10 seconds');


CREATE MATERIALIZED VIEW candle_5s
WITH (
    timescaledb.continuous,
    timescaledb.materialized_only = false
) AS
SELECT
    time_bucket('5 seconds', bucket) AS bucket,
    symbol,
    first(open, bucket) AS open,
    max(high) AS high,
    min(low) AS low,
    last(close, bucket) AS close,
    sum(volume) AS volume
FROM candle_1s
GROUP BY time_bucket('5 seconds', bucket), symbol;

SELECT add_continuous_aggregate_policy('candle_5s',
                                       start_offset => INTERVAL '6 hours',
                                       end_offset   => INTERVAL '5 seconds',
                                       schedule_interval => INTERVAL '30 seconds');


CREATE MATERIALIZED VIEW candle_1m
WITH (
    timescaledb.continuous,
    timescaledb.materialized_only = false
) AS
SELECT
    time_bucket('1 minute', bucket) AS bucket,
    symbol,
    first(open, bucket) AS open,
    max(high) AS high,
    min(low) AS low,
    last(close, bucket) AS close,
    sum(volume) AS volume
FROM candle_5s
GROUP BY time_bucket('1 minute', bucket), symbol;

SELECT add_continuous_aggregate_policy('candle_1m',
                                       start_offset => INTERVAL '1 day',
                                       end_offset   => INTERVAL '1 minute',
                                       schedule_interval => INTERVAL '1 minute');

CREATE MATERIALIZED VIEW candle_15m
WITH (
    timescaledb.continuous,
    timescaledb.materialized_only = false
) AS
SELECT
    time_bucket('15 minutes', bucket) AS bucket,
    symbol,
    first(open, bucket) AS open,
    max(high) AS high,
    min(low) AS low,
    last(close, bucket) AS close,
    sum(volume) AS volume
FROM candle_1m
GROUP BY time_bucket('15 minutes', bucket), symbol;

SELECT add_continuous_aggregate_policy('candle_15m',
                                       start_offset => INTERVAL '7 days',
                                       end_offset   => INTERVAL '15 minutes',
                                       schedule_interval => INTERVAL '5 minutes');

CREATE MATERIALIZED VIEW candle_1h
WITH (
    timescaledb.continuous,
    timescaledb.materialized_only = false
) AS
SELECT
    time_bucket('1 hour', bucket) AS bucket,
    symbol,
    first(open, bucket) AS open,
    max(high) AS high,
    min(low) AS low,
    last(close, bucket) AS close,
    sum(volume) AS volume
FROM candle_15m
GROUP BY time_bucket('1 hour', bucket), symbol;


SELECT add_continuous_aggregate_policy('candle_1h',
                                       start_offset      => INTERVAL '30 days',
                                       end_offset        => INTERVAL '1 hour',
                                       schedule_interval => INTERVAL '15 minutes');