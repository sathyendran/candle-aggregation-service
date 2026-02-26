package com.multibankfx.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BidAskConsumer {

    private static final Logger log = LoggerFactory.getLogger(BidAskConsumer.class);

    @KafkaListener(topics = "market-ticks",groupId = "candle-aagregatro")
    public void consume(BidAskEvent bidAskEvent) {
        log.info("Received the tick{} ", bidAskEvent);
    }
}
