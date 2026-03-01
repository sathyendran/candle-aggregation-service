package com.multibankfx.repository;

public final class Query {

    public static final String BID_ASK_INSERT = """
               INSERT INTO market_data (
                symbol,
                bid,
                ask,
                time
            )
            VALUES (?, ?, ?, ?);
     """;

    public static final  String getCandle = """
            SELECT
                (EXTRACT(EPOCH FROM bucket) * 1000)::BIGINT AS time,
                open,
                high,
                low,
                close,
                volume
            FROM %s
            WHERE symbol = ?
              AND bucket >= ?
              AND bucket <= ?
            ORDER BY bucket;
           """;
}
