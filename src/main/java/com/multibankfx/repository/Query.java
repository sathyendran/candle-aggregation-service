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
}
