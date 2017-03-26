package com.aaron.pseplanner.service;

import android.icu.text.TimeZoneNames;

import com.aaron.pseplanner.bean.TickerDto;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by aaron.asuncion on 1/31/2017.
 */

public interface PSEPlannerService extends HttpClient
{
    /**
     * Returns the datetime of when the last http request occurs.
     * Pattern: MMMM dd, EEEE hh:mm:ss a
     * Timezone: Manila, Philippines
     *
     * @param preference the shared preference key, determines which last updated date will be retrieved
     * @return String the last updated formatted date
     */
    String getLastUpdated(String preference);

    /**
     * Insert/Update the ticker dto in the database.
     */
    boolean saveTickerList(List<TickerDto> tickerDtoList);

    /**
     * Retrieves the list of tickers from the database.
     *
     * @return {@code List<TickerDto>} the list of ticker dto
     */
    List<TickerDto> getTickerListFromDatabase();
}
