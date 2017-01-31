package com.aaron.pseplanner.service;

import android.widget.TextView;

/**
 * Created by aaron.asuncion on 12/19/2016.
 */

public interface FormatService
{
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
     * Formats the textview.
     */
    void formatTextColor(double price, TextView text);
}
