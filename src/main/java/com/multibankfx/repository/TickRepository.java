package com.multibankfx.repository;

import com.multibankfx.model.BidAskEvent;
import org.springframework.data.relational.core.sql.SQL;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;

@Repository
public class TickRepository {

    private  final JdbcTemplate jdbcTemplate;

    public TickRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(BidAskEvent bidAsk) {

        Instant instant =  Instant.ofEpochMilli(bidAsk.timestamp());
        int update = jdbcTemplate.update(Query.BID_ASK_INSERT, bidAsk.symbol(), bidAsk.bid(), bidAsk.bid(), Timestamp.from(instant));
    }

    public void getCandle(String symbol,String timeInterval,Instant from,Instant to) {

    }
}
