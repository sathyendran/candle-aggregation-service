package com.multibankfx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CandleData(
        @JsonProperty("s") String status,
        @JsonProperty("t") List<Long> time,
        @JsonProperty("o") List<Double> open,
        @JsonProperty("h") List<Double> high,
        @JsonProperty("l") List<Double> low,
        @JsonProperty("c") List<Double> close,
        @JsonProperty("v") List<Integer> volume
) {
}
