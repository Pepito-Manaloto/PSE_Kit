package com.aaron.pseplanner.service.implementation;

import android.util.Pair;

import com.aaron.pseplanner.UnitTestUtils;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.response.phisix.ResponsePhisixStockWrapper;
import com.aaron.pseplanner.service.PhisixService;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;
import io.reactivex.Single;

import static org.mockito.Mockito.when;

/**
 * Created by Aaron on 28/12/2017.
 */
public class PhisixHttpClientTest extends AbstractHttpClientTest
{
    @Mock
    private PhisixService service;

    @InjectMocks
    private PhisixHttpClient client;

    @Test
    public void getTickerTest()
    {
        Date dateUpdated = UnitTestUtils.newDateTime(2018, 5, 17, 9, 45, 21);
        String name = "Alsons Consolidated Resource";
        String symbol = "ACR";
        double percentChange = 13.89;
        long volume = 1_719_000;
        double amount = 1.35;

        ResponsePhisixStockWrapper mockedResponseWrapper = givenResponseStockWrapper(name, amount, percentChange, volume, symbol, dateUpdated);
        givenGetStockSymbolReturnMockedResponse(symbol, mockedResponseWrapper);

        Pair<TickerDto, Date> response = whenApiGetTickerRequest(symbol);
        Pair<TickerDto, Date> expectedResponse = thenTheResponseIs(symbol, name, volume, amount, percentChange, dateUpdated);

        thenResponseShouldMatchExpectedResponse(response, expectedResponse);
    }

    @Test
    public void getTickerTestNullSymbol()
    {
        Date dateUpdated = UnitTestUtils.newDateTime(2018, 5, 17, 9, 45, 21);
        String name = "Alsons Consolidated Resource";
        String symbol = "ACR";
        double percentChange = 13.89;
        long volume = 1_719_000;
        double amount = 1.35;

        ResponsePhisixStockWrapper mockedResponseWrapper = givenResponseStockWrapper(name, amount, percentChange, volume, symbol, dateUpdated);
        givenGetStockSymbolReturnMockedResponse(symbol, mockedResponseWrapper);

        expectingExceptionWillBeThrownWithMessage(IllegalArgumentException.class, "Symbol must not be empty");
        whenApiGetTickerRequest(null);
    }

    @Test
    public void getTickerTestEmptySymbol()
    {
        Date dateUpdated = UnitTestUtils.newDateTime(2018, 5, 17, 9, 45, 21);
        String name = "Alsons Consolidated Resource";
        String symbol = "ACR";
        double percentChange = 13.89;
        long volume = 1_719_000;
        double amount = 1.35;

        ResponsePhisixStockWrapper mockedResponseWrapper = givenResponseStockWrapper(name, amount, percentChange, volume, symbol, dateUpdated);
        givenGetStockSymbolReturnMockedResponse(symbol, mockedResponseWrapper);

        expectingExceptionWillBeThrownWithMessage(IllegalArgumentException.class, "Symbol must not be empty");
        whenApiGetTickerRequest("");
    }

    @Test
    public void getAllTickerListTest()
    {
        Date dateUpdated = UnitTestUtils.newDateTime(2018, 5, 17, 9, 45, 21);
        List<String> nameList = Arrays.asList("Alsons Consolidated Resource", "Calata Corporation", "Eagle Cement Corporation", "Philippine National Bank",
                "Wilcon Depot, Inc.");
        List<String> symbolList = Arrays.asList("ACR", "CAL", "EAGLE", "PNB", "WLCON");
        List<Double> percentChangeList = Arrays.asList(13.89, -29.12, 0.10, 3.93, 37.05);
        List<Long> volumeList = Arrays.asList(1_719_000L, 380_000L, 153_400L, 19_800L, 730_000L);
        List<Double> amountList = Arrays.asList(1.35, 2.31, 14.44, 57.15, 8.03);

        ResponsePhisixStockWrapper mockedResponseWrapper = givenResponseStockWrapper(nameList, amountList, percentChangeList, volumeList, symbolList,
                dateUpdated);
        givenGetAllStocksReturnMockedResponse(mockedResponseWrapper);

        Pair<List<TickerDto>, Date> response = whenApiGetAllTickersRequest();
        Pair<List<TickerDto>, Date> expectedResponse = thenTheResponseAre(symbolList, nameList, volumeList, amountList, percentChangeList, dateUpdated);

        thenResponsesShouldMatchExpectedResponses(response, expectedResponse);
    }

    @Test
    public void getTickerListTest()
    {
        Date dateUpdated = UnitTestUtils.newDateTime(2018, 5, 17, 9, 45, 21);
        List<String> nameList = Arrays.asList("Alsons Consolidated Resource", "Calata Corporation", "Eagle Cement Corporation", "Philippine National Bank",
                "Wilcon Depot, Inc.");
        List<String> symbolList = Arrays.asList("ACR", "CAL", "EAGLE", "PNB", "WLCON");
        List<Double> percentChangeList = Arrays.asList(13.89, -29.12, 0.10, 3.93, 37.05);
        List<Long> volumeList = Arrays.asList(1_719_000L, 380_000L, 153_400L, 19_800L, 730_000L);
        List<Double> amountList = Arrays.asList(1.35, 2.31, 14.44, 57.15, 8.03);

        List<ResponsePhisixStockWrapper> mockedResponseWrapperList = givenListOfResponseStockWrapper(nameList, amountList, percentChangeList, volumeList,
                symbolList, dateUpdated);

        givenGetStockSymbolListReturnMockedResponse(symbolList, mockedResponseWrapperList);

        Pair<List<TickerDto>, Date> response = whenApiGetListOfTickersRequest(symbolList);
        Pair<List<TickerDto>, Date> expectedResponse = thenTheResponseAre(symbolList, nameList, volumeList, amountList, percentChangeList, dateUpdated);

        thenResponsesShouldMatchExpectedResponses(response, expectedResponse);
    }

    @Test
    public void getTickerListTestEmptySymbol()
    {
        Date dateUpdated = UnitTestUtils.newDateTime(2018, 5, 17, 9, 45, 21);
        List<String> nameList = Arrays.asList("Alsons Consolidated Resource", "Calata Corporation", "Eagle Cement Corporation", "Philippine National Bank",
                "Wilcon Depot, Inc.");
        List<String> symbolList = Arrays.asList("ACR", "CAL", "EAGLE", "PNB", "WLCON");
        List<Double> percentChangeList = Arrays.asList(13.89, -29.12, 0.10, 3.93, 37.05);
        List<Long> volumeList = Arrays.asList(1_719_000L, 380_000L, 153_400L, 19_800L, 730_000L);
        List<Double> amountList = Arrays.asList(1.35, 2.31, 14.44, 57.15, 8.03);

        ResponsePhisixStockWrapper mockedResponseWrapper = givenResponseStockWrapper(nameList, amountList, percentChangeList, volumeList, symbolList,
                dateUpdated);
        givenGetAllStocksReturnMockedResponse(mockedResponseWrapper);

        expectingExceptionWillBeThrownWithMessage(IllegalArgumentException.class, "No trade plan/s to update");
        whenApiGetListOfTickersRequest(Collections.<String> emptyList());
    }

    @Test
    public void getTickerListTestNullSymbol()
    {
        Date dateUpdated = UnitTestUtils.newDateTime(2018, 5, 17, 9, 45, 21);
        List<String> nameList = Arrays.asList("Alsons Consolidated Resource", "Calata Corporation", "Eagle Cement Corporation", "Philippine National Bank",
                "Wilcon Depot, Inc.");
        List<String> symbolList = Arrays.asList("ACR", "CAL", "EAGLE", "PNB", "WLCON");
        List<Double> percentChangeList = Arrays.asList(13.89, -29.12, 0.10, 3.93, 37.05);
        List<Long> volumeList = Arrays.asList(1_719_000L, 380_000L, 153_400L, 19_800L, 730_000L);
        List<Double> amountList = Arrays.asList(1.35, 2.31, 14.44, 57.15, 8.03);

        ResponsePhisixStockWrapper mockedResponseWrapper = givenResponseStockWrapper(nameList, amountList, percentChangeList, volumeList, symbolList,
                dateUpdated);
        givenGetAllStocksReturnMockedResponse(mockedResponseWrapper);

        expectingExceptionWillBeThrownWithMessage(IllegalArgumentException.class, "No trade plan/s to update");
        whenApiGetListOfTickersRequest(null);
    }

    private void givenGetStockSymbolListReturnMockedResponse(List<String> symbol, List<ResponsePhisixStockWrapper> mockedResponseWrapperList)
    {
        int size = symbol.size();
        for(int i = 0; i < size; i++)
        {
            when(service.getStock(symbol.get(i))).thenReturn(Single.just(mockedResponseWrapperList.get(i)));
        }
    }

    private void givenGetStockSymbolReturnMockedResponse(String symbol, ResponsePhisixStockWrapper mockedResponse)
    {
        when(service.getStock(symbol)).thenReturn(Single.just(mockedResponse));
    }

    private void givenGetAllStocksReturnMockedResponse(ResponsePhisixStockWrapper mockedResponse)
    {
        when(service.getStock()).thenReturn(Single.just(mockedResponse));
    }

    private Pair<TickerDto, Date> whenApiGetTickerRequest(String symbol)
    {
        return client.getTicker(symbol).blockingGet();
    }

    private Pair<List<TickerDto>, Date> whenApiGetListOfTickersRequest(Collection<String> symbols)
    {
        return client.getTickerList(symbols).blockingGet();
    }

    private Pair<List<TickerDto>, Date> whenApiGetAllTickersRequest()
    {
        return client.getAllTickerList().blockingGet();
    }
}
