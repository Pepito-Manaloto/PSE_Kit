package com.aaron.pseplanner.service;

import android.widget.TextView;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;
import java.util.TimeZone;

/**
 * Created by aaron.asuncion on 12/19/2016.
 */

public interface FormatService
{
    String DATE_PATTERN = "MMMM dd, yyyy";
    FastDateFormat DATE_FORMATTER = FastDateFormat.getInstance(DATE_PATTERN);

    String DATE_PATTERN_LAST_UPDATED = "MMMM dd, EEEE hh:mm:ss a";
    TimeZone MANILA_TIMEZONE = TimeZone.getTimeZone("Asia/Manila");
    FastDateFormat DATE_FORMATTER_LAST_UPDATED = FastDateFormat.getInstance(DATE_PATTERN_LAST_UPDATED, MANILA_TIMEZONE);

    String STOCK_PRICE_FORMAT = "#,###.####";
    String PRICE_FORMAT = "#,###.##";
    String SHARES_FORMAT = "#,###";

    /**
     * Formats the stock price.
     */
    String formatStockPrice(double number);

    /**
     * Formats the price.
     */
    String formatPrice(double number);

    /**
     * Formats the percent.
     */
    String formatPercent(double number);

    /**
     * Formats the shares.
     */
    String formatShares(long shares);

    /**
     * Formats the textview.
     */
    void formatTextColor(double price, TextView text);

    /**
     * Formats the Date object.
     */
    String formatDate(Date date);

    /**
     * Formats the given date.
     * Pattern: MMMM dd, EEEE hh:mm:ss a
     * Timezone: Manila, Philippines
     *
     * @param lastUpdated the date to format
     * @return String the last updated formatted date
     */
    String formatLastUpdated(Date lastUpdated);
}
