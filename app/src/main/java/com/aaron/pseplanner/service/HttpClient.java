package com.aaron.pseplanner.service;

import android.util.Pair;

import com.aaron.pseplanner.bean.TickerDto;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by aaron.asuncion on 2/23/2017.
 */
public interface HttpClient
{
    long DEFAUT_TIMEOUT = 15_000;

    /**
     * Gets a specific ticker(stock) from a web API.
     *
     * @param symbol the ticker to retrieve
     * @return {@code Single<Pair<TickerDto, Date>>} the Single observable stock and last updated date
     */
    Single<Pair<TickerDto, Date>> getTicker(String symbol);

    /**
     * Gets all tickers(stocks) from a web API.
     *
     * @return {@code Single<Pair<List<TickerDto>, Date>>} the Single Observable list of stocks and last updated date
     */
    Single<Pair<List<TickerDto>, Date>> getAllTickerList();

    /**
     * Gets the tickers(stocks) that are passed in the parameter from a web API.
     *
     * @param symbols the collection of stock symbol to retrieve
     * @return {@code Single<Pair<List<TickerDto>, Date>>} the Single observable list of stocks and last updated date
     */
    Single<Pair<List<TickerDto>, Date>> getTickerList(Collection<String> symbols);
}
