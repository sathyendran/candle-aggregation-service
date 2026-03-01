package com.multibankfx.api;


import com.multibankfx.ingestion.BidAskConsumer;
import com.multibankfx.service.CandleService;
import com.multibankfx.dto.CandleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
public class CandleController {

    private final CandleService candleService;

    private static final Logger log = LoggerFactory.getLogger(CandleController.class);

    public CandleController(CandleService candleService) {
        this.candleService = candleService;
    }

    @GetMapping
    public ResponseEntity<CandleData> getCandle(@RequestParam("symbol") String symbol,
                                                @RequestParam("interval") String timeInterval,
                                                @RequestParam("from") long from,
                                                @RequestParam("to") long to) {

        log.info("History API for {} with time interval{} from {} and {}", symbol, timeInterval, from, to);
        if (from > to) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(candleService.getCandle(symbol, timeInterval, from, to));
    }
}
