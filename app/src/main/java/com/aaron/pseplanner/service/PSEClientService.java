package com.aaron.pseplanner.service;

import android.icu.text.TimeZoneNames;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.TimeZone;

/**
 * Created by aaron.asuncion on 1/31/2017.
 */

public interface PSEClientService
{
    String DATE_PATTERN = "MMMM dd, EEEE hh:mm:ss a";
    TimeZone MANILA_TIMEZONE = TimeZone.getTimeZone("Asia/Manila");
    FastDateFormat DATE_FORMATTER = FastDateFormat.getInstance(DATE_PATTERN, MANILA_TIMEZONE);

    /**
     * Returns the datetime of when the last http request occurs.
     * Pattern: MMMM dd, EEEE hh:mm:ss a
     * Timezone: Manila, Philippines
     */
    String getLastUpdated();


}
