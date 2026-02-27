package com.multibankfx.ingestion;

import com.multibankfx.model.BidAskEvent;
import com.multibankfx.repository.TickRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BidAskConsumer {

    private static final Logger log = LoggerFactory.getLogger(BidAskConsumer.class);

    private final TickRepository tickRepository;


    public BidAskConsumer(TickRepository tickRepository) {
        this.tickRepository = tickRepository;
    }

    @KafkaListener(topics = "market-ticks",groupId = "candle-aagregator")
    public void consume(BidAskEvent bidAskEvent) {
        log.info("Received the tick{} ", bidAskEvent);
        tickRepository.insert(bidAskEvent);
    }


}
