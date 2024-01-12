package com.example.crypto.controller;

import com.example.crypto.model.Kline;
import com.example.crypto.service.GetService;
import com.example.crypto.service.InputValidationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GetController {
    @Autowired
    private GetService getService;
    @Autowired
    private InputValidationService validationService;

    @GetMapping("/get")
    public List<Kline> get(@RequestParam String symbol, @RequestParam Long startTime,
                           @RequestParam Long endTime, @RequestParam Integer interval) {
        // todo validate interval
        validationService.validateSymbol(symbol);
        validationService.validateTimeRange(startTime, endTime);
        return getService.get(symbol, startTime, endTime, interval);
        //http://localhost:8080/get?symbol=BTCUSDT&startTime=1703408460000&endTime=1704229140000&interval=5
    }
}
