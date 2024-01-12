package com.example.crypto.service;

import com.example.crypto.model.Kline;
import com.example.crypto.repository.KlineRepo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Validated
public class GetService {
    @Autowired
    private KlineRepo KlineRepository;

    public @NotNull List<Kline> get(@NotBlank String symbol, @NotNull Long startTime, @NotNull Long endTime, @NotNull Integer interval) {
        List<Kline> klines = KlineRepository.findInterval(symbol, startTime, endTime);
        klines.sort(Comparator.comparingLong(Kline::getOpenTime));

        return IntStream.range(0, klines.size())
                .parallel()
                .filter(i-> i % interval==0)
                .mapToObj(i -> this.aggregate(klines, i, interval,symbol))
                .collect(Collectors.toList());
    }

    private Kline aggregate(List<Kline> klines, Integer startIdx, Integer interval, String symbol){
        int endIdx = Math.min(startIdx + interval, klines.size());
        Kline firstKline = klines.get(startIdx);
        Kline lastKline = klines.get(endIdx - 1);

        double highPrice = Double.MIN_VALUE;
        double lowPrice = Double.MAX_VALUE;
        double volume = 0;
        double quoteAssetVolume = 0;
        int numTrades = 0;
        double takerBuyBaseAssetVolume = 0;
        double takerBuyQuoteAssetVolume = 0;

        for (int j = startIdx; j < endIdx; j++) {
            Kline curr = klines.get(j);
            highPrice = Math.max(highPrice, curr.getHighPrice());
            lowPrice = Math.min(lowPrice, curr.getLowPrice());
            volume += curr.getVolume();
            quoteAssetVolume += curr.getQuoteAssetVolume();
            numTrades += curr.getNumTrades();
            takerBuyBaseAssetVolume += curr.getTakerBuyBaseAssetVolume();
            takerBuyQuoteAssetVolume += curr.getTakerBuyQuoteAssetVolume();
        }

        Kline combinedKline = new Kline();
        combinedKline.setSymbol(symbol);
        combinedKline.setOpenTime(firstKline.getOpenTime());
        combinedKline.setOpenPrice(firstKline.getOpenPrice());
        combinedKline.setHighPrice(highPrice);
        combinedKline.setLowPrice(lowPrice);
        combinedKline.setClosePrice(lastKline.getClosePrice());
        combinedKline.setCloseTime(lastKline.getCloseTime());
        combinedKline.setVolume(volume);
        combinedKline.setQuoteAssetVolume(quoteAssetVolume);
        combinedKline.setNumTrades(numTrades);
        combinedKline.setTakerBuyBaseAssetVolume(takerBuyBaseAssetVolume);
        combinedKline.setTakerBuyQuoteAssetVolume(takerBuyQuoteAssetVolume);
        return combinedKline;
    }
}
