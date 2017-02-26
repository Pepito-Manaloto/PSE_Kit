package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.service.HttpClient;
import com.aaron.pseplanner.service.PSEPlannerService;

import java.util.Date;

/**
 * Created by aaron.asuncion on 1/31/2017.
 */

public class DefaultPSEPlannerService implements PSEPlannerService
{
    private Date lastUpdated;
    private HttpClient httpClient;


    /**
     * Returns the datetime of when the last http request occurs. Gets the cached lastUpdated first if not null, else retrieve from database.
     * Pattern: MMMM dd, EEEE hh:mm:ss a
     * Timezone: Manila, Philippines
     */
    @Override
    public String getLastUpdated()
    {
        if(lastUpdated != null)
        {
            return DATE_FORMATTER.format(lastUpdated);
        }
        else
        {
            // TODO: query database
            return DATE_FORMATTER.format(new Date());
        }
    }

    public void setHttpClient(HttpClient client)
    {
        this.httpClient = client;
    }
}