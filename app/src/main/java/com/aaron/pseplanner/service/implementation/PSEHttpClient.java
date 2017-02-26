package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.service.HttpClient;
import com.aaron.pseplanner.service.PSEService;

import java.util.List;

/**
 * Created by Aaron on 2/26/2017.
 */

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
    public Ticker getTicker(String symbol)
    {
        return null;
    }

    @Override
    public List<Ticker> getTickerList()
    {
        return null;
    }

    @Override
    protected String getBaseURL()
    {
        return PSEService.BASE_URL;
    }
}
