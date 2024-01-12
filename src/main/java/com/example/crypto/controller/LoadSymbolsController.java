package com.example.crypto.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoadSymbolsController {
    @Autowired
    private ValidSymbolsService validSymbolsService;

    @GetMapping("/symbols/load")
    public void load(){
        validSymbolsService.loadSymbols();
    }
}
