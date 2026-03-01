package com.multibankfx.repository;

import com.multibankfx.model.BidAskEvent;
import com.multibankfx.model.Candle;
import com.multibankfx.model.Interval;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class TickRepository {

    private final JdbcTemplate jdbcTemplate;
    private final Interval interval;

    public TickRepository(JdbcTemplate jdbcTemplate, Interval interval) {
        this.jdbcTemplate = jdbcTemplate;
        this.interval = interval;
    }

    public void insert(BidAskEvent bidAsk) {


        int update = jdbcTemplate.update(Query.BID_ASK_INSERT, bidAsk.symbol(), bidAsk.bid(), bidAsk.bid(), getTimeStamp(bidAsk.timestamp()));
    }

    public List<Candle> getCandle(String symbol, String timeInterval, long from, long to) {
        String tableName = interval.getTable(timeInterval);
        String query = String.format(Query.getCandle, tableName);
        return jdbcTemplate.query(
                query,
                mapToHandle(),
                symbol, getTimeStamp(from), getTimeStamp(to)
        );
    }

    private RowMapper<Candle> mapToHandle() {
        return (rs, rowNum) -> new Candle(
                rs.getLong("time"),
                rs.getDouble("open"),
                rs.getDouble("high"),
                rs.getDouble("low"),
                rs.getDouble("close"),
                rs.getInt("volume")
        );
    }

    private Timestamp getTimeStamp(long time) {
        return Timestamp.from(Instant.ofEpochMilli(time));
    }
}
