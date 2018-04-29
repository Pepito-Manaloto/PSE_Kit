package com.aaron.pseplanner;

import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.entity.Ticker;
import com.aaron.pseplanner.entity.Trade;
import com.aaron.pseplanner.entity.TradeEntry;
import com.aaron.pseplanner.response.phisix.ResponsePhisixStock;
import com.aaron.pseplanner.response.phisix.ResponsePrice;
import com.aaron.pseplanner.service.BeanEntityUtils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.aaron.pseplanner.test.utils.BeanEntityBuilderTestUtils.givenTicker;
import static com.aaron.pseplanner.test.utils.BeanEntityBuilderTestUtils.givenTickerDto;
import static com.aaron.pseplanner.test.utils.BeanEntityBuilderTestUtils.givenTickerList;
import static com.aaron.pseplanner.test.utils.BeanEntityBuilderTestUtils.givenTrade;
import static com.aaron.pseplanner.test.utils.BeanEntityBuilderTestUtils.givenTradeEntryDto;
import static com.aaron.pseplanner.test.utils.BeanEntityBuilderTestUtils.givenTradeEntryList;
import static com.aaron.pseplanner.test.utils.BeanEntityBuilderTestUtils.givenTradeList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Aaron on 05/02/2018.
 */

public class BeanEntityUtilsTest
{
    @Test
    public void givenTrade_whenFromTradeToTradeDto_thenTradeIsConvertedToTradeDto()
    {
        String symbol = "TEL";
        Trade trade = givenTrade(symbol, givenTradeEntryList(symbol));

        TradeDto tradeDto = BeanEntityUtils.fromTradeToTradeDto(trade);

        thenTradeIsConvertedToTradeDto(trade, tradeDto);
    }

    @Test
    public void givenTradeEntryList_whenFromTradeEntryListToTradeEntryDtoList_thenTradeEntryListIsConvertedToTradeEntryDtoList()
    {
        List<TradeEntry> tradeEntry = givenTradeEntryList("IS");

        List<TradeEntryDto> tradeEntryDtoList = BeanEntityUtils.fromTradeEntryListToTradeEntryDtoList(tradeEntry);

        thenTradeEntryListIsConvertedToTradeEntryDtoList(tradeEntry, tradeEntryDtoList);
    }

    @Test
    public void givenTradeList_whenFromTradeListToTradeDtoList_thenTradeListIsConvertedToTradeDtoList()
    {
        List<String> symbolList = Arrays.asList("PLC", "CAL", "X");
        List<Trade> tradeList = givenTradeList(symbolList, givenTradeEntryList(symbolList));

        List<TradeDto> tradeDtoList = BeanEntityUtils.fromTradeListToTradeDtoList(tradeList);

        thenTradeListIsConvertedToTradeDtoList(tradeList, tradeDtoList);
    }

    @Test
    public void givenTicker_whenFromTickerToTickerDto_thenTickerIsConvertedToTickerDto()
    {
        Ticker ticker = givenTicker();

        TickerDto tickerDto = BeanEntityUtils.fromTickerToTickerDto(ticker);

        thenTickerIsConvertedToTickerDto(ticker, tickerDto);
    }

    @Test
    public void givenResponsePhisixStockAndChange_whenFromResponsePhisixStockToTickerDto_thenResponsePhisixStockIsConvertedToTickerDto()
    {
        ResponsePhisixStock responsePhisixStock = givenResponsePhisixStockAndChange();
        BigDecimal change = BigDecimal.valueOf(RandomUtils.nextDouble());

        TickerDto tickerDto = BeanEntityUtils.fromResponsePhisixStockToTickerDto(responsePhisixStock, change);

        thenResponsePhisixStockIsConvertedToTickerDto(responsePhisixStock, change, tickerDto);
    }

    @Test
    public void givenTickerList_whenFromTickerListToTickerDtoList_thenTickerListIsConvertedToTickerDtoList()
    {
        ArrayList<Ticker> tickerList = givenTickerList();

        List<TickerDto> tickerDtoList = BeanEntityUtils.fromTickerListToTickerDtoList(tickerList);

        thenTickerListIsConvertedToTickerDtoList(tickerList, tickerDtoList);
    }

    @Test
    public void givenTradeEntryDtoList_whenFromTradeEntryDtoListToTradeEntryList_thenTradeEntryDtoListIsConvertedToTradeEntryList()
    {
        List<TradeEntryDto> tradeEntryDto = givenTradeEntryDto("IS");

        List<TradeEntry> tradeEntryList = BeanEntityUtils.fromTradeEntryDtoListToTradeEntryList(tradeEntryDto);

        thenTradeEntryDtoListIsConvertedToTradeEntryList(tradeEntryDto, tradeEntryList);
    }

    @Test
    public void givenTickerDto_whenFromTickerDtoToTicker_thenTickerDtoIsConvertedToTicker()
    {
        TickerDto tickerDto = givenTickerDto();

        Ticker ticker = BeanEntityUtils.fromTickerDtoToTicker(tickerDto, new Date());

        thenTickerDtoIsConvertedToTicker(tickerDto, ticker);
    }

    @Test
    public void givenNull_whenFromTradeToTradeDto_thenEmptyTradeDtoShouldBeReturned()
    {
        TradeDto tradeDto = BeanEntityUtils.fromTradeToTradeDto(null);

        thenEmptyTradeDtoShouldBeReturned(tradeDto);
    }

    @Test
    public void givenNull_whenFromTradeEntryListToTradeEntryDtoList_thenEmptyTradeEntryDtoListShouldBeReturned()
    {
        List<TradeEntryDto> tradeEntryDtoList = BeanEntityUtils.fromTradeEntryListToTradeEntryDtoList(null);

        assertEquals(0, tradeEntryDtoList.size());
    }

    @Test
    public void givenNull_whenFromTradeListToTradeDtoList_thenEmptyTradeDtoListShouldBeReturned()
    {
        List<TradeDto> tradeDtoList = BeanEntityUtils.fromTradeListToTradeDtoList(null);

        assertEquals(0, tradeDtoList.size());
    }

    @Test
    public void givenNull_whenFromTickerToTickerDto_thenEmptyTickerDtoShouldBeReturned()
    {
        TickerDto tickerDto = BeanEntityUtils.fromTickerToTickerDto(null);

        thenEmptyTickerDtoShouldBeReturned(tickerDto);
    }

    @Test
    public void givenNull_whenFromTickerListToTickerDtoList_thenEmptyTickerDtoListShouldBeReturned()
    {
        List<TickerDto> tickerDtoList = BeanEntityUtils.fromTickerListToTickerDtoList(null);

        assertEquals(0, tickerDtoList.size());
    }

    @Test
    public void givenNull_whenFromResponsePhisixStockToTickerDto_thenEmptyTickerShouldBeReturned()
    {
        TickerDto tickerDto = BeanEntityUtils.fromResponsePhisixStockToTickerDto(null, null);

        thenEmptyTickerDtoShouldBeReturned(tickerDto);
    }

    @Test
    public void givenNull_whenFromTradeDtoToTrade_thenEmptyTradeShouldBeReturned()
    {
        Trade trade = BeanEntityUtils.fromTradeDtoToTrade(null);

        thenEmptyTradeShouldBeReturned(trade);
    }

    @Test
    public void givenNull_whenFromTradeEntryDtoListToTradeEntryList_thenEmptyTradeEntryListShouldBeReturned()
    {
        List<TradeEntry> tradeEntryList = BeanEntityUtils.fromTradeEntryDtoListToTradeEntryList(null);

        assertEquals(0, tradeEntryList.size());
    }

    @Test
    public void givenNull_whenFromTickerDtoToTicker_thenEmptyTickerShouldBeReturned()
    {
        Ticker ticker = BeanEntityUtils.fromTickerDtoToTicker(null, null);

        thenEmptyTickerShouldBeReturned(ticker);
    }

    private ResponsePhisixStock givenResponsePhisixStockAndChange()
    {
        ResponsePrice price = new ResponsePrice();
        price.setCurrency("PHP");
        price.setAmount(RandomUtils.nextDouble());

        return new ResponsePhisixStock(RandomStringUtils.randomAlphabetic(10), price, RandomUtils.nextDouble(),
                RandomUtils.nextLong(), RandomStringUtils.randomAlphabetic(3));
    }

    private void thenResponsePhisixStockIsConvertedToTickerDto(ResponsePhisixStock responsePhisixStock, BigDecimal change, TickerDto tickerDto)
    {
        double delta = 0.01;
        assertEquals(responsePhisixStock.getName(), tickerDto.getName());
        assertEquals(responsePhisixStock.getSymbol(), tickerDto.getSymbol());
        assertEquals(responsePhisixStock.getAmount(), tickerDto.getCurrentPrice().doubleValue(), delta);
        assertEquals(change, tickerDto.getChange());
        assertEquals(responsePhisixStock.getPercentChange(), tickerDto.getPercentChange().doubleValue(), delta);
    }

    private void thenTradeIsConvertedToTradeDto(Trade trade, TradeDto tradeDto)
    {
        assertEquals(trade.getId(), tradeDto.getId());
        assertEquals(trade.getSymbol(), tradeDto.getSymbol());
        assertEquals(trade.getDatePlanned(), tradeDto.getDatePlanned());
        assertEquals(trade.getEntryDate(), tradeDto.getEntryDate());
        assertEquals(trade.getCurrentPrice(), tradeDto.getCurrentPrice().toPlainString());
        assertEquals(trade.getAveragePrice(), tradeDto.getAveragePrice().toPlainString());
        assertEquals(trade.getTotalShares(), tradeDto.getTotalShares());
        assertEquals(trade.getTotalAmount(), tradeDto.getTotalAmount().toPlainString());
        assertEquals(trade.getPriceToBreakEven(), tradeDto.getPriceToBreakEven().toPlainString());
        assertEquals(trade.getTargetPrice(), tradeDto.getTargetPrice().toPlainString());
        assertEquals(trade.getPriceToBreakEven(), tradeDto.getPriceToBreakEven().toPlainString());
        assertEquals(trade.getTargetPrice(), tradeDto.getTargetPrice().toPlainString());
        assertEquals(trade.getGainLoss(), tradeDto.getGainLoss().toPlainString());
        assertEquals(trade.getGainLossPercent(), tradeDto.getGainLossPercent().toPlainString());
        assertEquals(trade.getGainToTarget(), tradeDto.getGainToTarget().toPlainString());
        assertEquals(trade.getLossToStopLoss(), tradeDto.getLossToStopLoss().toPlainString());
        assertEquals(trade.getStopLoss(), tradeDto.getStopLoss().toPlainString());
        assertEquals(trade.getStopDate(), tradeDto.getStopDate());
        assertEquals(trade.getRiskReward(), tradeDto.getRiskReward().toPlainString());
        assertEquals(trade.getCapital(), tradeDto.getCapital());
        assertEquals(trade.getPercentCapital(), tradeDto.getPercentCapital().toPlainString());

        thenTradeEntryListIsConvertedToTradeEntryDtoList(trade.getTradeEntries(), tradeDto.getTradeEntries());
    }

    private void thenTradeEntryListIsConvertedToTradeEntryDtoList(List<TradeEntry> tradeEntries, List<TradeEntryDto> tradeEntryDtos)
    {
        int expectedSize = tradeEntryDtos.size();
        assertEquals(expectedSize, tradeEntries.size());

        for(int i = 0; i < expectedSize; i++)
        {
            assertEquals(tradeEntries.get(i).getTradeSymbol(), tradeEntryDtos.get(i).getSymbol());
            assertEquals(tradeEntries.get(i).getEntryPrice(), tradeEntryDtos.get(i).getEntryPrice().toPlainString());
            assertEquals(tradeEntries.get(i).getShares(), tradeEntryDtos.get(i).getShares());
            assertEquals(tradeEntries.get(i).getPercentWeight(), tradeEntryDtos.get(i).getPercentWeight().toPlainString());
            assertEquals(tradeEntries.get(i).isExecuted(), tradeEntryDtos.get(i).isExecuted());
        }
    }

    private void thenTradeEntryDtoListIsConvertedToTradeEntryList(List<TradeEntryDto> tradeEntryDtos, List<TradeEntry> tradeEntries)
    {
        thenTradeEntryListIsConvertedToTradeEntryDtoList(tradeEntries, tradeEntryDtos);
    }

    private void thenTradeListIsConvertedToTradeDtoList(List<Trade> tradeList, List<TradeDto> tradeDtoList)
    {
        int expectedSize = tradeList.size();
        assertEquals(expectedSize, tradeDtoList.size());

        for(int i = 0; i < expectedSize; i++)
        {
            thenTradeIsConvertedToTradeDto(tradeList.get(i), tradeDtoList.get(i));
        }
    }

    private void thenTickerIsConvertedToTickerDto(Ticker ticker, TickerDto tickerDto)
    {
        assertEquals(ticker.getId(), tickerDto.getId());
        assertEquals(ticker.getSymbol(), tickerDto.getSymbol());
        assertEquals(ticker.getName(), tickerDto.getName());
        assertEquals(ticker.getCurrentPrice(), tickerDto.getCurrentPrice().toPlainString());
        assertEquals(ticker.getChange(), tickerDto.getChange().toPlainString());
        assertEquals(ticker.getPercentChange(), tickerDto.getPercentChange().toPlainString());
        assertEquals(ticker.getVolume(), tickerDto.getVolume());
    }

    private void thenTickerDtoIsConvertedToTicker(TickerDto tickerDto, Ticker ticker)
    {
        thenTickerIsConvertedToTickerDto(ticker, tickerDto);
    }

    private void thenTickerListIsConvertedToTickerDtoList(ArrayList<Ticker> tickerList, List<TickerDto> tickerDtoList)
    {
        int expectedSize = tickerList.size();
        assertEquals(expectedSize, tickerDtoList.size());

        for(int i = 0; i < expectedSize; i++)
        {
            thenTickerIsConvertedToTickerDto(tickerList.get(i), tickerDtoList.get(i));
        }
    }

    private void thenEmptyTickerDtoShouldBeReturned(TickerDto tickerDto)
    {
        assertNull(tickerDto.getId());
        assertNull(tickerDto.getSymbol());
        assertNull(tickerDto.getName());
        assertNull(tickerDto.getCurrentPrice());
        assertNull(tickerDto.getChange());
        assertNull(tickerDto.getPercentChange());
        assertEquals(0, tickerDto.getVolume());
    }

    private void thenEmptyTickerShouldBeReturned(Ticker ticker)
    {
        assertNull(ticker.getId());
        assertNull(ticker.getSymbol());
        assertNull(ticker.getName());
        assertNull(ticker.getCurrentPrice());
        assertNull(ticker.getChange());
        assertNull(ticker.getPercentChange());
        assertNull(ticker.getDateUpdate());
        assertEquals(0, ticker.getVolume());
    }

    private void thenEmptyTradeDtoShouldBeReturned(TradeDto tradeDto)
    {
        assertNull(tradeDto.getId());
        assertNull(tradeDto.getSymbol());
        assertNull(tradeDto.getDatePlanned());
        assertEquals(0, tradeDto.getDaysSincePlanned());
        assertNull(tradeDto.getEntryDate());
        assertEquals(0, tradeDto.getHoldingPeriod());
        assertNull(tradeDto.getCurrentPrice());
        assertNull(tradeDto.getAveragePrice());
        assertEquals(0, tradeDto.getTotalShares());
        assertNull(tradeDto.getTotalAmount());
        assertNull(tradeDto.getPriceToBreakEven());
        assertNull(tradeDto.getTargetPrice());
        assertNull(tradeDto.getPriceToBreakEven());
        assertNull(tradeDto.getTargetPrice());
        assertNull(tradeDto.getGainLoss());
        assertNull(tradeDto.getGainLossPercent());
        assertNull(tradeDto.getGainToTarget());
        assertNull(tradeDto.getLossToStopLoss());
        assertNull(tradeDto.getStopLoss());
        assertNull(tradeDto.getStopDate());
        assertEquals(0, tradeDto.getDaysToStopDate());
        assertNull(tradeDto.getRiskReward());
        assertEquals(0, tradeDto.getCapital());
        assertNull(tradeDto.getPercentCapital());
        assertNull(tradeDto.getTradeEntries());
    }

    private void thenEmptyTradeShouldBeReturned(Trade trade)
    {
        assertNull(trade.getId());
        assertNull(trade.getSymbol());
        assertNull(trade.getDatePlanned());
        assertNull(trade.getEntryDate());
        assertNull(trade.getCurrentPrice());
        assertNull(trade.getAveragePrice());
        assertEquals(0, trade.getTotalShares());
        assertNull(trade.getTotalAmount());
        assertNull(trade.getPriceToBreakEven());
        assertNull(trade.getTargetPrice());
        assertNull(trade.getPriceToBreakEven());
        assertNull(trade.getTargetPrice());
        assertNull(trade.getGainLoss());
        assertNull(trade.getGainLossPercent());
        assertNull(trade.getGainToTarget());
        assertNull(trade.getLossToStopLoss());
        assertNull(trade.getStopLoss());
        assertNull(trade.getStopDate());
        assertNull(trade.getRiskReward());
        assertEquals(0, trade.getCapital());
        assertNull(trade.getPercentCapital());
    }
}