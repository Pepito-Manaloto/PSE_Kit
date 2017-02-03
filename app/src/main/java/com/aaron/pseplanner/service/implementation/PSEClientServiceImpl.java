package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.service.PSEClientService;

import java.util.Date;
import java.util.Objects;

/**
 * Created by aaron.asuncion on 1/31/2017.
 */

public class PSEClientServiceImpl implements PSEClientService
{
    private Date lastUpdated;

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
}
