package com.example.crypto.service;

import com.example.crypto.model.Kline;
import com.example.crypto.repository.KlineRepo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.stream.Stream;

@Service
@Validated
public class BinanceService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KlineRepo KlineRepository;

    @Autowired
    private SymbolsRepo SymbolsRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${urlTemplate}")
    private String urlTemplate;

    @Value("${validSymbolsURL}")
    private String validSymbolsURL;


    public List<Kline> add(@NotNull @Min(0) Long startTime, @NotNull Long endTime, @NotBlank String symbol) {
        String resourceUrl = String.format(urlTemplate, symbol, startTime, endTime);
        ResponseEntity<String[][]> response = restTemplate.getForEntity(resourceUrl, String[][].class);
        String[][] dataArray = response.getBody();

        return Stream.of(dataArray)
                .parallel()
                .map(k -> this.setKline(k, symbol))
                .collect(Collectors.toList());
    }

    private Kline setKline (String[] dataRow, String symbol) {
        Kline k = new Kline();
        k.setSymbol(symbol);
        k.setOpenTime(Long.parseLong(dataRow[0]));
        k.setOpenPrice(Double.parseDouble(dataRow[1]));
        k.setHighPrice(Double.parseDouble(dataRow[2]));
        k.setLowPrice(Double.parseDouble(dataRow[3]));
        k.setClosePrice(Double.parseDouble(dataRow[4]));
        k.setVolume(Double.parseDouble(dataRow[5]));
        k.setCloseTime(Long.parseLong(dataRow[6]));
        k.setQuoteAssetVolume(Double.parseDouble(dataRow[7]));
        k.setNumTrades(Integer.parseInt(dataRow[8]));
        k.setTakerBuyBaseAssetVolume(Double.parseDouble(dataRow[9]));
        k.setTakerBuyQuoteAssetVolume(Double.parseDouble(dataRow[10]));
        return k;
    }

    public List<String> loadSymbols() {
        ResponseEntity<String> response = restTemplate.getForEntity(validSymbolsURL, String.class);
        String jsonResponse = response.getBody();

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray symbolsArray = jsonObject.getJSONArray("symbols");

        List<String> symbolsList = new ArrayList<>();
        for (int i = 0; i < symbolsArray.length(); i++) {
            JSONObject symbolObject = symbolsArray.getJSONObject(i);
            String symbol = symbolObject.getString("symbol");
            symbolsList.add(symbol);
        }

        return symbolsList;
    }
}
