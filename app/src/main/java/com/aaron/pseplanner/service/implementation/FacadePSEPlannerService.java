package com.aaron.pseplanner.service.implementation;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.HttpClient;
import com.aaron.pseplanner.service.PSEPlannerService;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by aaron.asuncion on 1/31/2017.
 */

public class FacadePSEPlannerService implements PSEPlannerService
{
    private static final long DEFAUT_TIMEOUT = 10_000;
    private Date lastUpdated;
    private HttpClient phisixHttpClient;
    private HttpClient pseHttpClient;
    private FormatService formatService;
    
    public FacadePSEPlannerService(@NonNull Activity activity)
    {
        this.phisixHttpClient = new PhisixHttpClient(DEFAUT_TIMEOUT, DEFAUT_TIMEOUT, DEFAUT_TIMEOUT);
        this.pseHttpClient = new PSEHttpClient(DEFAUT_TIMEOUT, DEFAUT_TIMEOUT, DEFAUT_TIMEOUT);
        this.formatService = new DefaultFormatService(activity);
    }

    public FacadePSEPlannerService(@NonNull Activity activity, long connectionTimeout, long readTimeout, long pingInterval)
    {
        this.phisixHttpClient = new PhisixHttpClient(connectionTimeout, readTimeout, pingInterval);
        this.pseHttpClient = new PSEHttpClient(connectionTimeout, readTimeout, pingInterval);
        this.formatService = new DefaultFormatService(activity);
    }

    /**
     * Returns the datetime of when the last http request occurs. Gets the cached lastUpdated first if not null, else retrieve from database.
     * Pattern: MMMM dd, EEEE hh:mm:ss a
     * Timezone: Manila, Philippines
     */
    @Override
    public String getLastUpdated()
    {
        if(this.lastUpdated != null)
        {
            return this.formatService.formatLastUpdated(this.lastUpdated);
        }
        else
        {
            // TODO: query database
            return this.formatService.formatLastUpdated(new Date());
        }
    }

    @Override
    public Pair<Ticker, Date> getTicker(String symbol) throws HttpRequestException
    {
        // TODO: update database ticker and last updated
        return this.phisixHttpClient.getTicker(symbol);
    }

    @Override
    public Pair<List<Ticker>, Date> getAllTickerList() throws HttpRequestException
    {
        // TODO: update database ticker and last updated
        return this.phisixHttpClient.getAllTickerList();
    }

    @Override
    public Pair<List<Ticker>, Date> getTickerList(Collection<String> symbols) throws HttpRequestException
    {
        // TODO: update database trade and last updated
        return this.phisixHttpClient.getTickerList(symbols);
    }
}