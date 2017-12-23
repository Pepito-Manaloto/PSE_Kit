package com.aaron.pseplanner.service.implementation;

import android.util.Pair;

import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.response.phisix.ResponsePhisixStock;
import com.aaron.pseplanner.response.phisix.ResponsePhisixStockWrapper;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PhisixService;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Aaron on 2/26/2017.
 */

public class PhisixHttpClient extends BaseHttpClient
{
    public static final String CLASS_NAME = PhisixHttpClient.class.getSimpleName();
    private PhisixService service;
    private CalculatorService calculatorService;

    public PhisixHttpClient(long connectionTimeout, long readTimeout, long pingInterval)
    {
        super(connectionTimeout, readTimeout, pingInterval);

        this.service = retrofit.create(PhisixService.class);
        this.calculatorService = new DefaultCalculatorService();

        LogManager.debug(CLASS_NAME, "PhisixHttpClient", "Initialized without proxy.");
    }

    public PhisixHttpClient(long connectionTimeout, long readTimeout, long pingInterval, String proxyHost, int proxyPort)
    {
        super(connectionTimeout, readTimeout, pingInterval, proxyHost, proxyPort);

        this.service = retrofit.create(PhisixService.class);
        this.calculatorService = new DefaultCalculatorService();

        LogManager.debug(CLASS_NAME, "PhisixHttpClient", "Initialized with proxy = " + proxyHost + ":" + proxyPort);
    }

    /**
     * Retrieves a stock from Phisix.
     *
     * @param symbol the stock to retrieve
     * @return the stock converted to TickerDto object, plus the last updated date
     * @throws IllegalArgumentException if the parameter is empty
     */
    @Override
    public Single<Pair<TickerDto, Date>> getTicker(String symbol)
    {
        if(StringUtils.isBlank(symbol))
        {
            throw new IllegalArgumentException("Symbol must not be empty");
        }

        LogManager.debug(CLASS_NAME, "getTicker", "Start");

        return this.service.getStock(symbol).map(new Function<ResponsePhisixStockWrapper, Pair<TickerDto, Date>>()
        {
            @Override
            public Pair<TickerDto, Date> apply(ResponsePhisixStockWrapper phisixStockWrapper) throws Exception
            {
                LogManager.debug(CLASS_NAME, "getTicker", "End");
                ResponsePhisixStock phisixStock = phisixStockWrapper.getResponseStock();

                return new Pair<>(convertResponsePhisixStockToTicker(phisixStock), phisixStockWrapper.getDateUpdated());
            }
        });
    }

    /**
     * Retrieves list of stocks from Phisix.
     *
     * @return the stocks list converted to TickerDto object, plus the last updated date
     */
    @Override
    public Single<Pair<List<TickerDto>, Date>> getAllTickerList()
    {
        LogManager.debug(CLASS_NAME, "getAllTickerList", "Start");

        return this.service.getStock().map(new Function<ResponsePhisixStockWrapper, Pair<List<TickerDto>, Date>>()
        {
            @Override
            public Pair<List<TickerDto>, Date> apply(ResponsePhisixStockWrapper phisixStockWrapper) throws Exception
            {
                LogManager.debug(CLASS_NAME, "getAllTickerList", "End");
                List<ResponsePhisixStock> responseList = phisixStockWrapper.getResponsePhisixStocksList();
                List<TickerDto> tickerDtoList = new ArrayList<>(responseList.size());

                for(ResponsePhisixStock phisixStock : responseList)
                {
                    tickerDtoList.add(convertResponsePhisixStockToTicker(phisixStock));
                }

                LogManager.debug(CLASS_NAME, "getAllTickerList", "TickerDtoList size: " + tickerDtoList.size());

                return new Pair<>(tickerDtoList, phisixStockWrapper.getDateUpdated());
            }
        });
    }

    /**
     * Retrieves a list of stock from Phisix.
     *
     * @param symbols the stocks to retrieve
     * @return the stock converted to TickerDto object, plus the last updated date
     * @throws IllegalArgumentException if the parameter is empty
     */
    @Override
    public Single<Pair<List<TickerDto>, Date>> getTickerList(Collection<String> symbols)
    {
        if(symbols == null || symbols.isEmpty())
        {
            throw new IllegalArgumentException("No trade plan/s to update");
        }

        final int size = symbols.size();

        Set<Single<ResponsePhisixStockWrapper>> singleSet = new HashSet<>(size);
        for(String symbol: symbols)
        {
            // Each observable will run in parallel
            Single<ResponsePhisixStockWrapper> singleObservable = this.service.getStock(symbol).subscribeOn(Schedulers.io());
            singleSet.add(singleObservable);
        }

        LogManager.debug(CLASS_NAME, "getTickerList", "Start " + size + " async calls.");
        return Single.zip(singleSet, new Function<Object[], Pair<List<TickerDto>, Date>>()
        {
            @Override
            public Pair<List<TickerDto>, Date> apply(Object[] objects) throws Exception
            {
                List<TickerDto> tickerList = new ArrayList<>(objects.length);
                Date lastUpdated = null;

                // How to resolve this type cast?
                ResponsePhisixStockWrapper[] phisixStockWrappers = (ResponsePhisixStockWrapper[]) objects;
                for(ResponsePhisixStockWrapper phisixStockWrapper: phisixStockWrappers)
                {
                    tickerList.add(convertResponsePhisixStockToTicker(phisixStockWrapper.getResponseStock()));
                    lastUpdated = phisixStockWrapper.getDateUpdated();
                }

                LogManager.debug(CLASS_NAME, "getTickerList", "Done with " + size + " async calls.");
                return new Pair<>(tickerList, lastUpdated);
            }
        });
    }

    private TickerDto convertResponsePhisixStockToTicker(ResponsePhisixStock phisixStock)
    {
        // WARN: Id is null
        return new TickerDto(null, phisixStock.getSymbol(), phisixStock.getName(), phisixStock.getVolume(), BigDecimal.valueOf(phisixStock.getAmount()), this.calculatorService.getCurrentAndPreviousPriceChange(phisixStock.getAmount(), phisixStock.getPercentChange()), BigDecimal.valueOf(phisixStock.getPercentChange()));
    }

    @Override
    protected String getBaseURL()
    {
        return PhisixService.BASE_URL;
    }
}
