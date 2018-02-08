package com.aaron.pseplanner.service.implementation;

import android.util.Pair;

import com.aaron.pseplanner.RobolectricTest;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.response.phisix.ResponsePhisixStock;
import com.aaron.pseplanner.response.phisix.ResponsePhisixStockWrapper;
import com.aaron.pseplanner.response.phisix.ResponsePrice;
import com.aaron.pseplanner.service.CalculatorService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by Aaron on 02/01/2018.
 */
public abstract class AbstractHttpClientTest extends RobolectricTest
{
    private static final String CURRENCY = "PHP";

    @Spy
    private CalculatorService calculatorService = new DefaultCalculatorService();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void beforeTest()
    {
        initMocks(this);
    }

    protected ResponsePhisixStockWrapper givenResponseStockWrapper(String name, double amount, double percentChange, long volume, String symbol,
            Date dateUpdated)
    {
        ResponsePhisixStock responseStock = createResponseStock(name, amount, percentChange, volume, symbol);

        return createResponsePhisixStockWrapper(dateUpdated, responseStock);
    }

    private ResponsePhisixStock createResponseStock(String name, double amount, double percentChange, long volume, String symbol)
    {
        ResponsePrice price = new ResponsePrice();
        price.setAmount(amount);
        price.setCurrency(CURRENCY);

        return new ResponsePhisixStock(name, price, percentChange, volume, symbol);
    }

    /**
     * Only looks at the first parameter to determine the number of ResponseStock to add to the wrapper.
     */
    protected ResponsePhisixStockWrapper givenResponseStockWrapper(List<String> nameList, List<Double> amountList, List<Double> percentChangeList,
            List<Long> volumeList, List<String> symbolList, Date dateUpdated)
    {
        int size = nameList.size();
        ResponsePhisixStock[] responseStocks = new ResponsePhisixStock[size];
        for(int i = 0; i < size; i++)
        {
            responseStocks[i] = createResponseStock(nameList.get(i), amountList.get(i), percentChangeList.get(i), volumeList.get(i), symbolList.get(i));
        }

        return createResponsePhisixStockWrapper(dateUpdated, responseStocks);
    }

    /**
     * Only looks at the first parameter to determine the number of ResponseStock to add to the wrapper.
     */
    protected List<ResponsePhisixStockWrapper> givenListOfResponseStockWrapper(List<String> nameList, List<Double> amountList, List<Double> percentChangeList,
            List<Long> volumeList, List<String> symbolList, Date dateUpdated)
    {
        int size = nameList.size();
        List<ResponsePhisixStockWrapper> responsePhisixStockWrapperList = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
        {
            ResponsePhisixStock responsePhisixStock = createResponseStock(nameList.get(i), amountList.get(i), percentChangeList.get(i), volumeList.get(i),
                    symbolList.get(i));
            responsePhisixStockWrapperList.add(i, createResponsePhisixStockWrapper(dateUpdated, responsePhisixStock));
        }

        return responsePhisixStockWrapperList;
    }

    protected ResponsePhisixStockWrapper createResponsePhisixStockWrapper(Date dateUpdated, ResponsePhisixStock... responsePhisixStocks)
    {
        List<ResponsePhisixStock> responsePhisixStockList = Arrays.asList(responsePhisixStocks);
        ResponsePhisixStockWrapper response = new ResponsePhisixStockWrapper();
        response.setResponsePhisixStocksList(responsePhisixStockList);
        response.setDateUpdated(dateUpdated);

        return response;
    }

    protected Pair<TickerDto, Date> thenTheResponseIs(String symbol, String name, long volume, double amount, double percentChange, Date dateUpdated)
    {
        TickerDto dto = createTickerDto(symbol, name, volume, amount, percentChange);
        return Pair.create(dto, dateUpdated);
    }

    protected TickerDto createTickerDto(String symbol, String name, long volume, double amount, double percentChange)
    {
        BigDecimal change = calculatorService.getChangeBetweenCurrentAndPreviousPrice(amount, percentChange);
        return new TickerDto(symbol)
                .setName(name)
                .setVolume(volume)
                .setCurrentPrice(BigDecimal.valueOf(amount))
                .setChange(change)
                .setPercentChange(BigDecimal.valueOf(percentChange));
    }

    protected Pair<List<TickerDto>, Date> thenTheResponseAre(List<String> symbolList, List<String> nameList, List<Long> volumeList, List<Double> amountList,
            List<Double> percentChangeList, Date dateUpdated)
    {
        int size = nameList.size();
        List<TickerDto> tickerDtos = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
        {
            tickerDtos.add(i, createTickerDto(symbolList.get(i), nameList.get(i), volumeList.get(i), amountList.get(i), percentChangeList.get(i)));
        }

        return Pair.create(tickerDtos, dateUpdated);
    }

    protected void expectingExceptionWillBeThrownWithMessage(Class<? extends Exception> exceptionType, String message)
    {
        expectedException.expect(exceptionType);
        expectedException.expectMessage(message);
    }

    protected void thenResponseShouldMatchExpectedResponse(Pair<TickerDto, Date> response, Pair<TickerDto, Date> expected)
    {
        TickerDto actualTicker = response.first;
        Date acutalDate = response.second;

        TickerDto expectedTicker = expected.first;
        Date expectedDate = expected.second;

        assertEquals(expectedTicker, actualTicker);
        assertEquals(expectedDate, acutalDate);
    }

    protected void thenResponsesShouldMatchExpectedResponses(Pair<List<TickerDto>, Date> response, Pair<List<TickerDto>, Date> expected)
    {
        List<TickerDto> actualTickers = response.first;
        Date acutalDate = response.second;

        List<TickerDto> expectedTickers = expected.first;
        Date expectedDate = expected.second;

        assertThat(actualTickers, containsInAnyOrder(expectedTickers.toArray()));
        assertEquals(expectedDate, acutalDate);
    }
}
