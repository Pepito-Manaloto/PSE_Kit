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

import static com.aaron.pseplanner.service.BeanEntityUtils.fromResponsePhisixStockToTickerDto;

/**
 * Created by Aaron on 2/26/2017.
 */

public class PhisixHttpClient extends BaseHttpClient
{
    public static final String CLASS_NAME = PhisixHttpClient.class.getSimpleName();
    private PhisixService service;
    private CalculatorService calculatorService;

    public PhisixHttpClient()
    {
        super();

        this.service = retrofit.create(PhisixService.class);
        this.calculatorService = new DefaultCalculatorService();

        LogManager.debug(CLASS_NAME, "PhisixHttpClient", "Initialized with default timeouts and without proxy.");
    }

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

                return Pair.create(convertResponsePhisixStockToTicker(phisixStock), phisixStockWrapper.getDateUpdated());
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
                    try
                    {
                        TickerDto dto = convertResponsePhisixStockToTicker(phisixStock);
                        tickerDtoList.add(dto);
                    }
                    catch(IllegalArgumentException e)
                    {
                        LogManager.warn(CLASS_NAME, "getAllTickerList", "Error parsing ResponsePhisixStock. Excluding symbol=" + phisixStock.getSymbol());
                    }
                }

                LogManager.debug(CLASS_NAME, "getAllTickerList", "TickerDtoList size: " + tickerDtoList.size());

                return Pair.create(tickerDtoList, phisixStockWrapper.getDateUpdated());
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
        Set<Single<ResponsePhisixStockWrapper>> singleSet = createSingleObservableSetOnGetStock(symbols, size);

        LogManager.debug(CLASS_NAME, "getTickerList", "Start " + size + " async calls.");
        return Single.zip(singleSet, new Function<Object[], Pair<List<TickerDto>, Date>>()
        {
            @Override
            public Pair<List<TickerDto>, Date> apply(Object[] objects) throws Exception
            {
                List<TickerDto> tickerList = new ArrayList<>(objects.length);
                Date lastUpdated = null;

                for(Object object : objects)
                {
                    // TODO: How to resolve this type cast?
                    ResponsePhisixStockWrapper phisixStockWrapper = (ResponsePhisixStockWrapper) object;

                    try
                    {
                        TickerDto dto = convertResponsePhisixStockToTicker(phisixStockWrapper.getResponseStock());
                        tickerList.add(dto);
                    }
                    catch(IllegalArgumentException e)
                    {
                        LogManager.warn(CLASS_NAME, "getTickerList",
                                "Error parsing ResponsePhisixStock. Excluding symbol=" + phisixStockWrapper.getResponseStock().getSymbol());
                    }

                    lastUpdated = phisixStockWrapper.getDateUpdated();
                }

                LogManager.debug(CLASS_NAME, "getTickerList", "Done with " + size + " async calls.");
                return Pair.create(tickerList, lastUpdated);
            }
        });
    }

    private Set<Single<ResponsePhisixStockWrapper>> createSingleObservableSetOnGetStock(Collection<String> symbols, int size)
    {
        Set<Single<ResponsePhisixStockWrapper>> singleSet = new HashSet<>(size);
        for(String symbol : symbols)
        {
            // Each observable will run in parallel
            Single<ResponsePhisixStockWrapper> singleObservable = this.service.getStock(symbol).subscribeOn(Schedulers.io());
            singleSet.add(singleObservable);
        }

        return singleSet;
    }

    private TickerDto convertResponsePhisixStockToTicker(ResponsePhisixStock phisixStock)
    {
        BigDecimal change = this.calculatorService.getChangeBetweenCurrentAndPreviousPrice(phisixStock.getAmount(), phisixStock.getPercentChange());

        // WARN: Id is null
        return fromResponsePhisixStockToTickerDto(phisixStock, change);
    }

    @Override
    protected String getBaseURL()
    {
        return PhisixService.BASE_URL;
    }
}
