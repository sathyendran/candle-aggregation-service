CREATE TABLE market_data (
                             time      TIMESTAMPTZ NOT NULL,
                             symbol    TEXT NOT NULL,
                             bid       DOUBLE PRECISION NOT NULL,
                             ask       DOUBLE PRECISION NOT NULL
);

SELECT create_hypertable('market_data', 'time');

CREATE INDEX idx_market_symbol_time ON market_data (symbol, time DESC);

CREATE MATERIALIZED VIEW candle_1s
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 second', time) AS bucket,
    symbol,
    first((bid + ask) / 2, time) AS open,
    max((bid + ask) / 2) AS high,
    min((bid + ask) / 2) AS low,
    last((bid + ask) / 2, time) AS close,
    count(*) AS volume
FROM market_data
GROUP BY bucket, symbol;

CREATE MATERIALIZED VIEW candle_5s
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('5 seconds', bucket) AS bucket_5s,
    symbol,
    first(open, bucket) AS open,
    max(high) AS high,
    min(low) AS low,
    last(close, bucket) AS close,
    sum(volume) AS volume
FROM candle_1s
GROUP BY bucket_5s, symbol;

CREATE MATERIALIZED VIEW candle_1m
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 minute', bucket_5s) AS bucket_1m,
    symbol,
    first(open, bucket_5s) AS open,
    max(high) AS high,
    min(low) AS low,
    last(close, bucket_5s) AS close,
    sum(volume) AS volume
FROM candle_5s
GROUP BY bucket_1m, symbol;

CREATE MATERIALIZED VIEW candle_1h
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', bucket_1m) AS bucket_1h,
    symbol,
    first(open, bucket_1m) AS open,
    max(high) AS high,
    min(low) AS low,
    last(close, bucket_1m) AS close,
    sum(volume) AS volume
FROM candle_1m
GROUP BY bucket_1h, symbol;

SELECT add_continuous_aggregate_policy('candle_1s',
                                       start_offset => INTERVAL '1 minute',
                                       end_offset   => INTERVAL '1 second',
                                       schedule_interval => INTERVAL '1 second');

SELECT add_continuous_aggregate_policy('candle_5s',
                                       start_offset => INTERVAL '5 minutes',
                                       end_offset   => INTERVAL '5 seconds',
                                       schedule_interval => INTERVAL '5 seconds');

SELECT add_continuous_aggregate_policy('candle_1m',
                                       start_offset => INTERVAL '1 hour',
                                       end_offset   => INTERVAL '1 minute',
                                       schedule_interval => INTERVAL '1 minute');
