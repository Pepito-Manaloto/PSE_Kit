package com.aaron.pseplanner.service;

import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.bean.Trade;

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
     * @return Ticker the stock
     */
    Ticker getTicker(String symbol);

    /**
     * Gets all tickers(stocks) from a web API.
     *
     * @return List<Ticker> the list of stock
     */
    List<Ticker> getTickerList();
}
