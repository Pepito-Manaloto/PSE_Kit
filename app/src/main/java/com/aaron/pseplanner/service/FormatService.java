package com.aaron.pseplanner.service;

import android.widget.TextView;

import java.util.Date;

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
     * Formats the shares.
     */
    String formatShares(long shares);

    /**
     * Formats the textview.
     */
    void formatTextColor(double price, TextView text);

    /**
     * Append a number sign(+/-).
     */
    String addNumberSign(double number, String text);

    /**
     * Formats the Date object.
     */
    String formatDate(Date date);
}
