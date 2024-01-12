package com.example.crypto.service;

import com.example.crypto.model.exception.InvalidInputException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class InputValidationService {

    @Autowired
    private BinanceService binanceService;

    public void validateTimeRange(@NotNull Long startTime, @NotNull Long endTime){
        if(startTime >= endTime){
            String error = "start time is %d, and end time is %d. start time must be smaller end time";
            throw new InvalidInputException(String.format(error, startTime, endTime));
        }
    }

    public void validateSymbol(@NotBlank String symbol) {
        List<String> validSymbols = binanceService.loadSymbols();
        if (!validSymbols.contains(symbol)) {
            String error = "Symbol %s is not valid";
            throw new InvalidInputException(String.format(error, symbol));
        }
    }
}
