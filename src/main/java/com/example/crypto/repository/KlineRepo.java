package com.example.crypto.repository;

import com.example.crypto.model.Kline;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface KlineRepo {
    @Select("SELECT * FROM kline")
    public List < Kline > findAll();

    @Select("SELECT * FROM kline WHERE symbol = #{symbol} AND open_time = #{openTime} AND close_time = #{closeTime}")
    public Kline findByID(String symbol, Long openTime, Long closeTime);

    @Select("SELECT * FROM kline WHERE symbol = #{symbol} AND open_time > #{openTime} AND close_time < #{closeTime}")
    public List<Kline> findInterval(String symbol, Long openTime, Long closeTime);

    @Delete("DELETE FROM kline WHERE symbol = #{symbol} AND open_time = #{openTime} AND close_time = #{closeTime}")
    public int deleteByID(String symbol, Long openTime, Long closeTime);

    @Insert("INSERT INTO kline(symbol, open_time, open_price, high_price, low_price, close_price, volume, close_time, quote_asset_volume, num_trades, taker_buy_base_asset_volume, taker_buy_quote_asset_volume)" +
            "VALUES (#{symbol}, #{openTime}, #{openPrice}, #{highPrice}, #{lowPrice}, #{closePrice}, #{volume}, #{closeTime}, #{quoteAssetVolume}, #{numTrades}, #{takerBuyBaseAssetVolume}, #{takerBuyQuoteAssetVolume})")
    public int insert (Kline kline);

    @Insert({
            "<script>",
            "insert into kline (symbol, open_time, open_price, high_price, low_price, close_price, volume, close_time, quote_asset_volume, num_trades, taker_buy_base_asset_volume, taker_buy_quote_asset_volume)",
            "values ",
            "<foreach  collection='klineList' item='kline' separator=','>",
            "( #{kline.symbol}, #{kline.openTime}, #{kline.openPrice}, #{kline.highPrice}, #{kline.lowPrice}, #{kline.closePrice}, #{kline.volume}, #{kline.closeTime}, #{kline.quoteAssetVolume}, #{kline.numTrades}, #{kline.takerBuyBaseAssetVolume}, #{kline.takerBuyQuoteAssetVolume})",
            "</foreach>",
            "</script>"
    })
    int insertBatch(List<Kline> klineList);

    // 10 days of data, two different types of insert, calculate performace difference with @around


    @Update("Update kline set open_price = #{openPrice}, high_price = #{highPrice}, low_price = #{lowPrice}, close_price = #{closePrice}, volume = #{volume}," +
            "quote_asset_volume = #{quoteAssetVolume}, num_trades = #{numTrades}, taker_buy_base_asset_volume = #{takerBuyBaseAssetVolume}, taker_buy_quote_asset_volume = #{takerBuyQuoteAssetVolume}" +
            "WHERE symbol = #{symbol} AND open_time = #{openTime} AND close_time = #{closeTime}")
    public int update(Kline kline);
}
