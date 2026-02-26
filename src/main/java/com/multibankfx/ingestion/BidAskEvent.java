package com.multibankfx.ingestion;

public record BidAskEvent(String symbol,
                          double bid,
                          double ask,
                          long timestamp) {

}
