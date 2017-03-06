package com.aaron.pseplanner.service;

import android.util.Pair;

import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.exception.HttpRequestException;

import java.util.Date;
import java.util.List;

/**
 * Created by aaron.asuncion on 2/23/2017.
 */
public interface HttpClient
{
    /**
     * Gets a specific ticker(stock) from a web API.
     *
     * @param symbol the ticker to retrieve
     * @return {@code Pair<Ticker, Date>} the stock and last updated date
     * @throws HttpRequestException http request failed
     */
    Pair<Ticker, Date> getTicker(String symbol) throws HttpRequestException;

    /**
     * Gets all tickers(stocks) from a web API.
     *
     * @return {@code Pair<List<Ticker>, Date>} the list of stocks and last updated date
     * @throws HttpRequestException http request failed
     */
    Pair<List<Ticker>, Date> getTickerList() throws HttpRequestException;

    /**
     * Gets the trades(stocks) that are passed in the parameter from a web API.
     *
     * @param symbols the list of stock symbol to retrieve
     * @return {@code Pair<List<Trade>, Date>} the list of stocks and last updated date
     * @throws HttpRequestException http request failed
     */
    Pair<List<Trade>, Date> getTradeList(List<String> symbols) throws HttpRequestException;
}
