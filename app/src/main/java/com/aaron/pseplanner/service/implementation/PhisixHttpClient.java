package com.aaron.pseplanner.service.implementation;

import android.util.Log;
import android.util.Pair;

import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.response.Phisix.ResponsePhisixStock;
import com.aaron.pseplanner.response.Phisix.ResponsePhisixStockWrapper;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PhisixService;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

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
    }

    public PhisixHttpClient(long connectionTimeout, long readTimeout, long pingInterval, String proxyHost, int proxyPort)
    {
        super(connectionTimeout, readTimeout, pingInterval, proxyHost, proxyPort);

        this.service = retrofit.create(PhisixService.class);
        this.calculatorService = new DefaultCalculatorService();
    }

    /**
     * Retrieves a stock from Phisix.
     *
     * @param symbol the stock to retrieve
     * @return the stock converted to Ticker object, plus the last updated date
     * @throws IllegalArgumentException if the parameter is empty
     * @throws HttpRequestException     request is not successful
     */
    @Override
    public Pair<Ticker, Date> getTicker(String symbol) throws HttpRequestException
    {
        if(StringUtils.isBlank(symbol))
        {
            throw new IllegalArgumentException("Symbol must not be empty");
        }

        Response<ResponsePhisixStockWrapper> response = null;

        try
        {
            response = this.service.getStocksList(symbol).execute();

            if(response != null)
            {
                if(!response.isSuccessful() && response.errorBody() != null)
                {
                    LogManager.error(CLASS_NAME, "getTicker", "Error getting stock from Phisix: " + response.errorBody().string());
                    throw new HttpRequestException(response.errorBody().string(), response.code());
                }

                ResponsePhisixStockWrapper phisixStockWrapper = response.body();
                ResponsePhisixStock phisixStock = phisixStockWrapper.getResponseStock();

                return new Pair<>(convertResponsePhisixStockToTicker(phisixStock), phisixStockWrapper.getDateUpdated());
            }

            throw new HttpRequestException("Response is null");
        }
        catch(IOException e)
        {
            LogManager.error(CLASS_NAME, "getTicker", "Error getting stock from Phisix: " + e.getCause().getClass().getSimpleName(), e);

            if(response != null)
            {
                throw new HttpRequestException(response.message(), e, response.code());
            }
            else
            {
                throw new HttpRequestException(e);
            }
        }
    }

    /**
     * Retrieves list of stocks from Phisix.
     *
     * @return the stocks list converted to Ticker object, plus the last updated date
     * @throws HttpRequestException request is not successful
     */
    @Override
    public Pair<List<Ticker>, Date> getTickerList() throws HttpRequestException
    {
        Response<ResponsePhisixStockWrapper> response = null;

        try
        {
            response = this.service.getStocksList().execute();

            if(response != null)
            {
                if(!response.isSuccessful() && response.errorBody() != null)
                {
                    LogManager.error(CLASS_NAME, "getTicker", "Error getting stock from Phisix: " + response.errorBody().string());
                    throw new HttpRequestException(response.errorBody().string(), response.code());
                }

                ResponsePhisixStockWrapper phisixStockWrapper = response.body();
                List<ResponsePhisixStock> responseList = phisixStockWrapper.getResponsePhisixStocksList();
                List<Ticker> tickerList = new ArrayList<>(responseList.size());

                for(ResponsePhisixStock phisixStock : responseList)
                {
                    tickerList.add(convertResponsePhisixStockToTicker(phisixStock));
                }

                return new Pair<>(tickerList, phisixStockWrapper.getDateUpdated());
            }

            throw new HttpRequestException("Response is null");
        }
        catch(IOException e)
        {
            LogManager.error(CLASS_NAME, "getTicker", "Error getting stock from Phisix: " + e.getCause().getClass().getSimpleName(), e);

            if(response != null)
            {
                throw new HttpRequestException(response.message(), e, response.code());
            }
            else
            {
                throw new HttpRequestException(e);
            }
        }
    }

    @Override
    public Pair<List<Trade>, Date> getTradeList(List<String> symbols) throws HttpRequestException
    {
        //TODO: implement
        return null;
    }

    private Ticker convertResponsePhisixStockToTicker(ResponsePhisixStock phisixStock)
    {
        return new Ticker(phisixStock.getSymbol(), phisixStock.getName(), phisixStock.getVolume(), BigDecimal.valueOf(phisixStock.getAmount()), this.calculatorService.getCurrentAndPreviousPriceChange(phisixStock.getAmount(), phisixStock.getPercentChange()), BigDecimal.valueOf(phisixStock.getPercentChange()));
    }

    @Override
    protected String getBaseURL()
    {
        return PhisixService.BASE_URL;
    }
}
