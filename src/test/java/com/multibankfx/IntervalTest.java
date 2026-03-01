package com.multibankfx;

import com.multibankfx.model.Interval;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IntervalTest {

    private Interval interval;

    @BeforeEach
    void setUp() {
        interval = new Interval();
    }

    @Test
    void knownDurationsShouldReturnTableName() {
        assertThat(interval.getTable("1s")).isEqualTo("candle_1s");
        assertThat(interval.getTable("5s")).isEqualTo("candle_5s");
        assertThat(interval.getTable("1m")).isEqualTo("candle_1m");
        assertThat(interval.getTable("15m")).isEqualTo("candle_15m");
        assertThat(interval.getTable("1h")).isEqualTo("candle_1h");
    }

    @Test
    void unknownDurationShouldThrow() {
        assertThrows(RuntimeException.class, () -> interval.getTable("2m"));
        assertThrows(RuntimeException.class, () -> interval.getTable(""));
        assertThrows(RuntimeException.class, () -> interval.getTable(null));
    }
}
