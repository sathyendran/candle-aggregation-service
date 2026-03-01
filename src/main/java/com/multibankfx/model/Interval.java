package com.multibankfx.model;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Interval {

    private final Map<String, String> timeIntervalTableMap = Map.of(
            "1s", "candle_1s",
            "5s", "candle_5s",
            "1m", "candle_1m",
            "15m", "candle_15m",
            "1h", "candle_1h"
    );


    public String getTable(String duration) {
        if (timeIntervalTableMap.get(duration) == null) {
            throw new RuntimeException("Illegal Time Interval");
        }
        return timeIntervalTableMap.get(duration);
    }
}
