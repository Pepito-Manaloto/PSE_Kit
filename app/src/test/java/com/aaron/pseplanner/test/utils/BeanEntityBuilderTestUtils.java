package com.aaron.pseplanner.test.utils;

import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.entity.Ticker;
import com.aaron.pseplanner.entity.Trade;
import com.aaron.pseplanner.entity.TradeEntry;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.aaron.pseplanner.test.utils.UnitTestUtils.randomDate;
import static com.aaron.pseplanner.test.utils.UnitTestUtils.randomNumericString;

/**
 * Created by Aaron on 08/02/2018.
 */

public final class BeanEntityBuilderTestUtils
{
    private BeanEntityBuilderTestUtils()
    {
    }

    public static Trade givenTrade(String symbol, List<TradeEntry> tradeEntries)
    {
        return new Trade().setId(RandomUtils.nextLong())
                .setSymbol(symbol)
                .setEntryDate(randomDate())
                .setHoldingPeriod(RandomUtils.nextInt())
                .setCurrentPrice(randomNumericString(3000))
                .setAveragePrice(randomNumericString(3000))
                .setTotalShares(RandomUtils.nextLong())
                .setTotalAmount(randomNumericString(3000))
                .setPriceToBreakEven(randomNumericString(3000))
                .setTargetPrice(randomNumericString(3000))
                .setGainLoss(randomNumericString(3000))
                .setGainLossPercent(randomNumericString(100))
                .setGainToTarget(randomNumericString(3000))
                .setStopLoss(randomNumericString(3000))
                .setLossToStopLoss(randomNumericString(3000))
                .setStopDate(randomDate())
                .setDaysToStopDate(RandomUtils.nextInt())
                .setRiskReward(randomNumericString(100))
                .setCapital(RandomUtils.nextLong())
                .setPercentCapital(randomNumericString(100))
                .setTradeEntriesTransient(tradeEntries);
    }

    public static ArrayList<Trade> givenTradeList(List<String> symbol, List<List<TradeEntry>> tradeEntries)
    {
        int size = symbol.size();
        ArrayList<Trade> tradePlanList = new ArrayList<>(size);

        for(int i = 0; i < size; i++)
        {
            tradePlanList.add(givenTrade(symbol.get(i), tradeEntries.get(i)));
        }

        return tradePlanList;
    }

    public static List<TradeEntry> givenTradeEntryList(String symbol)
    {
        int size = RandomUtils.nextInt(1, 10);
        List<TradeEntry> list = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
        {
            list.add(new TradeEntry().setId((long) i)
                    .setTradeSymbol(symbol)
                    .setEntryPrice(randomNumericString(3000))
                    .setShares(RandomUtils.nextLong())
                    .setPercentWeight(randomNumericString(100))
                    .setOrder(i));
        }

        return list;
    }

    public static List<List<TradeEntry>> givenTradeEntryList(List<String> symbolList)
    {
        int size = symbolList.size();
        List<List<TradeEntry>> listOfList = new ArrayList<>(size);

        for(String symbol : symbolList)
        {
            List<TradeEntry> list = givenTradeEntryList(symbol);
            listOfList.add(list);
        }

        return listOfList;
    }

    public static TradeDto givenTradeDto(String symbol, List<TradeEntryDto> tradeEntries)
    {
        return new TradeDto(symbol).setId(RandomUtils.nextLong())
                .setEntryDate(randomDate())
                .setHoldingPeriod(RandomUtils.nextInt())
                .setCurrentPrice(new BigDecimal(randomNumericString(3000)))
                .setAveragePrice(new BigDecimal(randomNumericString(3000)))
                .setTotalShares(RandomUtils.nextLong())
                .setTotalAmount(new BigDecimal(randomNumericString(3000)))
                .setPriceToBreakEven(new BigDecimal(randomNumericString(3000)))
                .setTargetPrice(new BigDecimal(randomNumericString(3000)))
                .setGainLoss(new BigDecimal(randomNumericString(3000)))
                .setGainLossPercent(new BigDecimal(randomNumericString(3000)))
                .setGainToTarget(new BigDecimal(randomNumericString(3000)))
                .setStopLoss(new BigDecimal(randomNumericString(3000)))
                .setLossToStopLoss(new BigDecimal(randomNumericString(3000)))
                .setStopDate(randomDate())
                .setDaysToStopDate(RandomUtils.nextInt())
                .setRiskReward(new BigDecimal(randomNumericString(100)))
                .setCapital(RandomUtils.nextLong())
                .setPercentCapital(new BigDecimal(randomNumericString(100)))
                .setTradeEntries(tradeEntries);
    }

    public static Collection<TradeDto> givenTradeDtoList()
    {
        List<String> symbol = Arrays.asList("ACR", "CHP", "HOUSE", "PCOR", "VUL");
        List<List<TradeEntryDto>> tradeEntries = givenTradeEntryDtoList(symbol);

        int size = symbol.size();
        Collection<TradeDto> tradeDtos = new ArrayList<>(size);

        for(int i = 0; i < size; i++)
        {
            tradeDtos.add(givenTradeDto(symbol.get(i), tradeEntries.get(i)));
        }

        return tradeDtos;
    }

    public static List<TradeEntryDto> givenTradeEntryDto(String symbol)
    {
        int size = RandomUtils.nextInt(1, 10);
        List<TradeEntryDto> list = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
        {
            list.add(new TradeEntryDto().setSymbol(symbol)
                    .setEntryPrice(new BigDecimal(randomNumericString(3000)))
                    .setShares(RandomUtils.nextLong())
                    .setPercentWeight(new BigDecimal(randomNumericString(100))));
        }

        return list;
    }

    public static List<List<TradeEntryDto>> givenTradeEntryDtoList(List<String> symbolList)
    {
        int size = symbolList.size();
        List<List<TradeEntryDto>> listOfList = new ArrayList<>(size);

        for(String symbol : symbolList)
        {
            List<TradeEntryDto> list = givenTradeEntryDto(symbol);
            listOfList.add(list);
        }

        return listOfList;
    }

    public static Ticker givenTicker()
    {
        return new Ticker().setId(RandomUtils.nextLong())
                .setName(RandomStringUtils.randomAlphabetic(10))
                .setSymbol(RandomStringUtils.randomAlphabetic(4))
                .setCurrentPrice(randomNumericString(3000))
                .setChange(randomNumericString(3000))
                .setPercentChange(randomNumericString(3000))
                .setVolume(RandomUtils.nextLong())
                .setDateUpdate(randomDate());
    }

    public static ArrayList<Ticker> givenTickerList()
    {
        int size = RandomUtils.nextInt(1, 10);
        ArrayList<Ticker> list = new ArrayList<>(size);

        for(int i = 0; i < size; i++)
        {
            list.add(givenTicker());
        }

        return list;
    }

    public static TickerDto givenTickerDto()
    {
        return new TickerDto().setId(RandomUtils.nextLong())
                .setName(RandomStringUtils.randomAlphabetic(10))
                .setSymbol(RandomStringUtils.randomAlphabetic(4))
                .setCurrentPrice(new BigDecimal(randomNumericString(3000)))
                .setVolume(RandomUtils.nextLong())
                .setChange(new BigDecimal(randomNumericString(100)))
                .setPercentChange(new BigDecimal(randomNumericString(100)));
    }

    public static List<TickerDto> givenTickerDtoList()
    {
        int size = RandomUtils.nextInt(1, 10);
        List<TickerDto> list = new ArrayList<>(size);

        for(int i = 0; i < size; i++)
        {
            list.add(givenTickerDto());
        }

        return list;
    }
}
