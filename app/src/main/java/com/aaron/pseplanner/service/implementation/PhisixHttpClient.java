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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

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
            LogManager.debug(CLASS_NAME, "getTicker", "Start");

            response = this.service.getStock(symbol).execute();

            LogManager.debug(CLASS_NAME, "getTicker", "End");

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
    public Pair<List<Ticker>, Date> getAllTickerList() throws HttpRequestException
    {
        Response<ResponsePhisixStockWrapper> response = null;

        try
        {
            LogManager.debug(CLASS_NAME, "getAllTickerList", "Start");

            response = this.service.getStock().execute();

            LogManager.debug(CLASS_NAME, "getAllTickerList", "End");

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

    /**
     * Retrieves a list of stock from Phisix.
     *
     * @param symbols the stocks to retrieve
     * @return the stock converted to Ticker object, plus the last updated date
     * @throws IllegalArgumentException if the parameter is empty
     * @throws HttpRequestException     request is not successful
     */
    @Override
    public Pair<List<Ticker>, Date> getTickerList(Collection<String> symbols) throws HttpRequestException
    {
        if(symbols == null || symbols.isEmpty())
        {
            throw new IllegalArgumentException("Symbols must not be empty");
        }

        int size = symbols.size();
        final CallbackResult<Ticker> callbackResult = new CallbackResult<>();
        callbackResult.setResponseList(new ArrayList<Ticker>(size));
        // Used to wait for all async requests to finish
        final CountDownLatch doneSignal = new CountDownLatch(size);

        LogManager.debug(CLASS_NAME, "getTickerList", "Start " + size + " async calls.");

        for(String symbol : symbols)
        {
            this.service.getStock(symbol).enqueue(new Callback<ResponsePhisixStockWrapper>()
            {
                @Override
                public void onResponse(Call<ResponsePhisixStockWrapper> call, Response<ResponsePhisixStockWrapper> response)
                {
                    try
                    {
                        if(response != null)
                        {
                            if(!response.isSuccessful() && response.errorBody() != null)
                            {
                                LogManager.error(CLASS_NAME, "getTickerList", "Error getting stock from Phisix: " + response.errorBody().string());
                                callbackResult.setErrorMessage(response.errorBody().string());
                                callbackResult.setErrorCode(response.code());
                            }

                            ResponsePhisixStockWrapper phisixStockWrapper = response.body();
                            ResponsePhisixStock phisixStock = phisixStockWrapper.getResponseStock();

                            callbackResult.setLastUpdated(phisixStockWrapper.getDateUpdated());
                            callbackResult.addResponseToList(convertResponsePhisixStockToTicker(phisixStock));
                        }
                    }
                    catch(IOException e)
                    {
                        LogManager.error(CLASS_NAME, "getTickerList", "Error getting stock from Phisix: " + e.getCause().getClass().getSimpleName(), e);
                        callbackResult.setErrorMessage(response.message());
                        callbackResult.setErrorCode(response.code());
                    }
                    finally
                    {
                        doneSignal.countDown();
                    }
                }

                @Override
                public void onFailure(Call<ResponsePhisixStockWrapper> call, Throwable t)
                {
                    LogManager.error(CLASS_NAME, "getTickerList", "Error getting stock from Phisix: " + t.getCause().getClass().getSimpleName(), t);
                    callbackResult.setErrorMessage(t.getMessage());
                    doneSignal.countDown();
                }
            });
        }

        LogManager.debug(CLASS_NAME, "getTickerList", "Done with " + size + " async calls.");

        try
        {
            // Wait for all async requests to finish
            doneSignal.await();
        }
        catch(InterruptedException e)
        {
            LogManager.error(CLASS_NAME, "getTickerList", "Error waiting for requests to Phisix: " + e.getCause().getClass().getSimpleName(), e);
        }

        LogManager.debug(CLASS_NAME, "getTickerList", "Done with " + size + " async calls w/ responses.");

        // Throw exception is at least one failed.
        if(!callbackResult.isSuccessful())
        {
            throw new HttpRequestException(callbackResult.getErrorMessage(), callbackResult.getErrorCode());
        }

        return new Pair<>(callbackResult.getResponseList(), callbackResult.getLastUpdated());
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
