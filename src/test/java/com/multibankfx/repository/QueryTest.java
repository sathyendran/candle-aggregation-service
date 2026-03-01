package com.multibankfx.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QueryTest {

    @Test
    void constantsShouldContainPlaceholders() {
        assertThat(Query.BID_ASK_INSERT).contains("INSERT INTO market_data");
        assertThat(Query.getCandle).contains("SELECT").contains("FROM %s");
    }
}