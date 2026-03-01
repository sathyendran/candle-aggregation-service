package com.multibankfx.repository;

import com.multibankfx.model.BidAskEvent;
import com.multibankfx.model.Candle;
import com.multibankfx.model.Interval;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TickRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Interval interval;

    private TickRepository tickRepository;

    @BeforeEach
    void setUp() {
        tickRepository = new TickRepository(jdbcTemplate, interval);
    }

    @Test
    void shouldInsertBidAskEvent() {
        // Given
        long now = System.currentTimeMillis();
        BidAskEvent event = new BidAskEvent("BTC-USD", 100.5, 101.0, now);

        when(jdbcTemplate.update(anyString(), any(), any(), any(), any()))
                .thenReturn(1);

        // When
        tickRepository.insert(event);

        // Then
        ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);

        verify(jdbcTemplate).update(
                anyString(),
                eq(event.symbol()),
                eq(event.bid()),
                eq(event.bid()),
                eq(Timestamp.from(Instant.ofEpochMilli(now)))
        );
    }

    @Test
    void shouldReturnCandlesForGivenRange() {
        // Given
        String symbol = "BTC-USD";
        String intervalValue = "1m";
        long from = 1620000000000L;
        long to = 1620000600000L;

        when(interval.getTable(intervalValue)).thenReturn("candle_1m");

        List<Candle> mockResponse = List.of(
                new Candle(1620000000L, 100.0, 110.0, 95.0, 105.0, 10)
        );

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(), any(), any()))
                .thenReturn(mockResponse);

        // When
        List<Candle> result = tickRepository.getCandle(symbol, intervalValue, from, to);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).open());
        assertEquals(105.0, result.get(0).close());

        verify(interval).getTable(intervalValue);

        verify(jdbcTemplate).query(
                contains("candle_1m"),
                any(RowMapper.class),
                eq(symbol),
                eq(Timestamp.from(Instant.ofEpochMilli(from))),
                eq(Timestamp.from(Instant.ofEpochMilli(to)))
        );
    }

    @Test
    void shouldMapRowToCandleCorrectly() throws Exception {
        // Given
        var repository = new TickRepository(jdbcTemplate, interval);

        var rowMapperMethod = TickRepository.class
                .getDeclaredMethod("mapToHandle");
        rowMapperMethod.setAccessible(true);

        RowMapper<Candle> mapper =
                (RowMapper<Candle>) rowMapperMethod.invoke(repository);

        var rs = mock(java.sql.ResultSet.class);

        when(rs.getLong("time")).thenReturn(1620000000L);
        when(rs.getDouble("open")).thenReturn(100.0);
        when(rs.getDouble("high")).thenReturn(110.0);
        when(rs.getDouble("low")).thenReturn(95.0);
        when(rs.getDouble("close")).thenReturn(105.0);
        when(rs.getInt("volume")).thenReturn(20);

        // When
        Candle candle = mapper.mapRow(rs, 1);

        // Then
        assertEquals(1620000000L, candle.time());
        assertEquals(100.0, candle.open());
        assertEquals(110.0, candle.high());
        assertEquals(95.0, candle.low());
        assertEquals(105.0, candle.close());
        assertEquals(20, candle.volume());
    }
}