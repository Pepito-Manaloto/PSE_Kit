package com.aaron.pseplanner.service;

import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.entity.Ticker;
import com.aaron.pseplanner.entity.Trade;
import com.aaron.pseplanner.entity.TradeEntry;
import com.aaron.pseplanner.response.phisix.ResponsePhisixStock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Aaron on 05/02/2018.
 * Utility class for converting between bean and entity.
 */
public final class BeanEntityUtils
{
    private BeanEntityUtils()
    {
    }

    public static TradeDto fromTradeToTradeDto(Trade trade)
    {
        if(trade == null)
        {
            return new TradeDto();
        }

        List<TradeEntryDto> tradeEntryDtos = fromTradeEntryListToTradeEntryDtoList(trade.getTradeEntries());

        return new TradeDto(trade.getSymbol()).setId(trade.getId())
                .setEntryDate(trade.getEntryDate())
                .setHoldingPeriod(trade.getHoldingPeriod())
                .setCurrentPrice(new BigDecimal(trade.getCurrentPrice()))
                .setAveragePrice(new BigDecimal(trade.getAveragePrice()))
                .setTotalShares(trade.getTotalShares())
                .setTotalAmount(new BigDecimal(trade.getTotalAmount()))
                .setPriceToBreakEven(new BigDecimal(trade.getPriceToBreakEven()))
                .setTargetPrice(new BigDecimal(trade.getTargetPrice()))
                .setGainLoss(new BigDecimal(trade.getGainLoss()))
                .setGainLossPercent(new BigDecimal(trade.getGainLossPercent()))
                .setGainToTarget(new BigDecimal(trade.getGainToTarget()))
                .setStopLoss(new BigDecimal(trade.getStopLoss()))
                .setLossToStopLoss(new BigDecimal(trade.getLossToStopLoss()))
                .setStopDate(trade.getStopDate())
                .setDaysToStopDate(trade.getDaysToStopDate())
                .setRiskReward(new BigDecimal(trade.getRiskReward()))
                .setCapital(trade.getCapital())
                .setPercentCapital(new BigDecimal(trade.getPercentCapital()))
                .setTradeEntries(tradeEntryDtos);
    }

    public static List<TradeEntryDto> fromTradeEntryListToTradeEntryDtoList(List<TradeEntry> tradeEntryList)
    {
        if(tradeEntryList == null || tradeEntryList.isEmpty())
        {
            return Collections.emptyList();
        }

        List<TradeEntryDto> tradeEntryDtos = new ArrayList<>(tradeEntryList.size());
        for(TradeEntry tradeEntry : tradeEntryList)
        {
            tradeEntryDtos.add(new TradeEntryDto(tradeEntry.getTradeSymbol(), tradeEntry.getEntryPrice(),
                    tradeEntry.getShares(), tradeEntry.getPercentWeight()));
        }

        return tradeEntryDtos;
    }

    public static ArrayList<TradeDto> fromTradeListToTradeDtoList(List<Trade> tradePlanList)
    {
        if(tradePlanList == null || tradePlanList.isEmpty())
        {
            return new ArrayList<>();
        }

        ArrayList<TradeDto> tradePlanDtoList = new ArrayList<>(tradePlanList.size());

        for(Trade trade : tradePlanList)
        {
            tradePlanDtoList.add(fromTradeToTradeDto(trade));
        }

        return tradePlanDtoList;
    }

    public static TickerDto fromTickerToTickerDto(Ticker ticker)
    {
        if(ticker == null)
        {
            return new TickerDto();
        }

        return new TickerDto(ticker.getSymbol()).setId(ticker.getId())
                .setName(ticker.getName())
                .setCurrentPrice(new BigDecimal(ticker.getCurrentPrice()))
                .setVolume(ticker.getVolume())
                .setChange(new BigDecimal(ticker.getChange()))
                .setPercentChange(new BigDecimal(ticker.getPercentChange()));
    }

    public static ArrayList<TickerDto> fromTickerListToTickerDtoList(List<Ticker> tickerList)
    {
        if(tickerList == null || tickerList.isEmpty())
        {
            return new ArrayList<>();
        }

        ArrayList<TickerDto> tickerDtoList = new ArrayList<>(tickerList.size());

        for(Ticker ticker : tickerList)
        {
            tickerDtoList.add(fromTickerToTickerDto(ticker));
        }

        return tickerDtoList;
    }

    public static TickerDto fromResponsePhisixStockToTickerDto(ResponsePhisixStock phisixStock, BigDecimal change)
    {
        if(phisixStock == null)
        {
            return new TickerDto();
        }

        return new TickerDto(phisixStock.getSymbol())
                .setName(phisixStock.getName())
                .setCurrentPrice(BigDecimal.valueOf(phisixStock.getAmount()))
                .setVolume(phisixStock.getVolume())
                .setChange(change)
                .setPercentChange(BigDecimal.valueOf(phisixStock.getPercentChange()));
    }
}
