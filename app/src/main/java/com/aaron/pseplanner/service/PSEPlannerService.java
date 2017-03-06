package com.aaron.pseplanner.service;

import android.icu.text.TimeZoneNames;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;
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
     * @return String the last updated formatted date
     */
    String getLastUpdated();
}
