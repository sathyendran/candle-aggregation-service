package com.multibankfx.service;

import com.multibankfx.dto.CandleData;
import com.multibankfx.model.Candle;
import com.multibankfx.repository.TickRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class CandleService {

    private final TickRepository tickRepository;

    public CandleService(TickRepository tickRepository) {
        this.tickRepository = tickRepository;
    }


    public CandleData getCandle(String symbol, String timeInterval, long from, long to) {
        List<Candle> candles = tickRepository.getCandle(symbol, timeInterval, from, to);
        List<Long> time = new LinkedList<>();
        List<Double> open= new LinkedList<>();
        List<Double> high = new LinkedList<>();
        List<Double> low = new LinkedList<>();
        List<Double> close = new LinkedList<>();
        List<Integer> volume = new LinkedList<>();
        for (Candle candle : candles) {
            time.add(candle.time());
            open.add(candle.open());
            high.add(candle.high());
            low.add(candle.low());
            close.add(candle.close());
            volume.add(candle.volume());
        }
        return new CandleData("Ok",time,open,high,low,close,volume);
    }
}
