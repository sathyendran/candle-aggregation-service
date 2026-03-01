package com.multibankfx.ingestion;

import com.multibankfx.model.BidAskEvent;
import com.multibankfx.repository.TickRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class BidAskConsumerTest {

    @Mock
    private TickRepository tickRepository;

    private BidAskConsumer consumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        consumer = new BidAskConsumer(tickRepository);
    }

    @Test
    void consumeShouldCallInsert() {
        BidAskEvent event = new BidAskEvent("EURUSD", 1.0, 1.1, 123456L);
        consumer.consume(event);
        verify(tickRepository).insert(event);
    }
}
