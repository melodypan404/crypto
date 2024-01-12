package com.example.crypto.controller;

import com.example.crypto.service.InputValidationService;
import com.example.crypto.service.LoadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.example.crypto.model.Kline;
import com.example.crypto.repository.KlineRepo;

import java.util.Arrays;

@RestController
public class LoadController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LoadService service;

    @Autowired
    private InputValidationService validationService;

    @PostMapping("/load")
    @ResponseStatus(HttpStatus.CREATED)
    public void load(@RequestParam String symbol, @RequestParam Long startTime, @RequestParam Long endTime){

        validationService.validateSymbol(symbol);
        validationService.validateTimeRange(startTime, endTime);
        // http://localhost:8080/load?symbol=BTCUSDT&startTime=1703408460000&endTime=1704229140000
        service.load(startTime,endTime,symbol);
        // Insert Batch: around -- function takes 18137 ms
        // Insert: around -- function takes 1739920 ms

    }
}
