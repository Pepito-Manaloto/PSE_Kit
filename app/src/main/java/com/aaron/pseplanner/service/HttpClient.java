package com.aaron.pseplanner.service;

import android.util.Pair;

import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.exception.HttpRequestException;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by aaron.asuncion on 2/23/2017.
 */
public interface HttpClient
{
    long DEFAUT_TIMEOUT = 10_000;

    /**
     * Gets a specific ticker(stock) from a web API.
     *
     * @param symbol the ticker to retrieve
     * @return {@code Pair<TickerDto, Date>} the stock and last updated date
     * @throws HttpRequestException http request failed
     */
    Pair<TickerDto, Date> getTicker(String symbol) throws HttpRequestException;

    /**
     * Gets all tickers(stocks) from a web API.
     *
     * @return {@code Pair<List<TickerDto>, Date>} the list of stocks and last updated date
     * @throws HttpRequestException http request failed
     */
    Pair<List<TickerDto>, Date> getAllTickerList() throws HttpRequestException;

    /**
     * Gets the tickers(stocks) that are passed in the parameter from a web API.
     *
     * @param symbols the collection of stock symbol to retrieve
     * @return {@code Pair<List<TickerDto>, Date>} the list of stocks and last updated date
     * @throws HttpRequestException http request failed
     */
    Pair<List<TickerDto>, Date> getTickerList(Collection<String> symbols) throws HttpRequestException;
}
