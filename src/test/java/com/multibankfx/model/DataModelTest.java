package com.multibankfx.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataModelTest {

    @Test
    void candleRecordShouldStoreValues() {
        Candle c = new Candle(10L, 1.0, 2.0, 0.5, 1.5, 100);
        assertThat(c.time()).isEqualTo(10L);
        assertThat(c.open()).isEqualTo(1.0);
        assertThat(c.high()).isEqualTo(2.0);
        assertThat(c.low()).isEqualTo(0.5);
        assertThat(c.close()).isEqualTo(1.5);
        assertThat(c.volume()).isEqualTo(100);
    }

    @Test
    void bidAskEventRecordShouldStoreValues() {
        BidAskEvent e = new BidAskEvent("EURUSD", 1.1, 1.2, 123L);
        assertThat(e.symbol()).isEqualTo("EURUSD");
        assertThat(e.bid()).isEqualTo(1.1);
        assertThat(e.ask()).isEqualTo(1.2);
        assertThat(e.timestamp()).isEqualTo(123L);
    }
}