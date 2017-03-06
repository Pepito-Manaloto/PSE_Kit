package com.aaron.pseplanner.service.implementation;

import android.util.Pair;

import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.service.PSEService;

import java.util.Date;
import java.util.List;

/**
 * Created by Aaron on 2/26/2017.
 */
@Deprecated
public class PSEHttpClient extends BaseHttpClient
{
    private PSEService service;

    public PSEHttpClient(long connectionTimeout, long readTimeout, long pingInterval)
    {
        super(connectionTimeout, readTimeout, pingInterval);

        this.service = retrofit.create(PSEService.class);
    }

    public PSEHttpClient(long connectionTimeout, long readTimeout, long pingInterval, String proxyHost, int proxyPort)
    {
        super(connectionTimeout, readTimeout, pingInterval, proxyHost, proxyPort);

        this.service = retrofit.create(PSEService.class);
    }

    @Override
    public Pair<Ticker, Date> getTicker(String symbol)
    {
        return null;
    }

    @Override
    public Pair<List<Ticker>, Date> getTickerList()
    {
        return null;
    }

    @Override
    public Pair<List<Trade>, Date> getTradeList(List<String> symbols) throws HttpRequestException
    {
        return null;
    }

    @Override
    protected String getBaseURL()
    {
        return PSEService.BASE_URL;
    }
}
