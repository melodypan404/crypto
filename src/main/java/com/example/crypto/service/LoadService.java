package com.example.crypto.service;

import com.example.crypto.model.Kline;
import com.example.crypto.repository.KlineRepo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Service
@Validated
public class LoadService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KlineRepo KlineRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BinanceService binanceService;

    @Value("${urlTemplate}")
    private String urlTemplate;

    private Integer batchSize = 500;

    public void load(@NotNull @Min(0) Long startTime, @NotNull Long endTime, @NotBlank String symbol){
        long timeInterval = 500 * 60 * 1000;

        LongStream.range(startTime, endTime)
            .parallel()
            .filter(t -> (t - startTime) % timeInterval == 0)
            .mapToObj(i -> this.binanceService.add(i, Math.min(i + timeInterval, endTime), symbol))
            .forEach(batch -> {
                if (!batch.isEmpty()) {
                    KlineRepository.insertBatch(batch);
                }
            });
    }
}
