package com.multibankfx;

import com.multibankfx.dto.CandleData;
import com.multibankfx.model.Candle;
import com.multibankfx.repository.TickRepository;
import com.multibankfx.service.CandleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandleServiceTest {

    @Mock
    private TickRepository tickRepository;

    private CandleService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CandleService(tickRepository);
    }

    @Test
    void getCandleAggregatesDataFromRepository() {
        Candle candle1 = new Candle(1L, 1.0, 1.1, 0.9, 1.05, 100);
        Candle candle2 = new Candle(2L, 1.05, 1.2, 1.0, 1.15, 200);
        when(tickRepository.getCandle("EURUSD", "1m", 0L, 100L))
                .thenReturn(List.of(candle1, candle2));

        CandleData result = service.getCandle("EURUSD", "1m", 0L, 100L);
        assertThat(result.status()).isEqualTo("Ok");
        assertThat(result.time()).containsExactly(1L, 2L);
        assertThat(result.open()).containsExactly(1.0, 1.05);
        assertThat(result.high()).containsExactly(1.1, 1.2);
        assertThat(result.low()).containsExactly(0.9, 1.0);
        assertThat(result.close()).containsExactly(1.05, 1.15);
        assertThat(result.volume()).containsExactly(100, 200);
    }
}