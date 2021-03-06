package com.aaron.pseplanner.service.implementation;

import android.util.Pair;

import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.service.PSEService;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;

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
    public Single<Pair<TickerDto, Date>> getTicker(String symbol)
    {
        return null;
    }

    @Override
    public Single<Pair<List<TickerDto>, Date>> getAllTickerList()
    {
        return null;
    }

    @Override
    public Single<Pair<List<TickerDto>, Date>> getTickerList(Collection<String> symbols)
    {
        return null;
    }

    @Override
    protected String getBaseURL()
    {
        return PSEService.BASE_URL;
    }
}
