package com.aaron.pseplanner.service.implementation;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.service.FormatService;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by aaron.asuncion on 12/8/2016.
 */
public class DefaultFormatService implements FormatService
{
    private int green;

    public DefaultFormatService(@NonNull Context context)
    {
        green = ContextCompat.getColor(context, R.color.darkGreen);
    }

    /**
     * Formats the given stock price, adding commas and rounding down to 4 decimal places.
     */
    @Override
    public String formatStockPrice(double stockPrice)
    {
        return format(stockPrice, STOCK_PRICE_FORMAT, RoundingMode.DOWN);
    }

    /**
     * Formats the given price, adding commas and rounding up to 2 decimal places.
     */
    @Override
    public String formatPrice(double price)
    {
        return format(price, PRICE_FORMAT, RoundingMode.DOWN);
    }

    /**
     * Formats the given percent, adding percent and rounding up to 2 decimal places.
     */
    @Override
    public String formatPercent(double percent)
    {
        return format(percent, PERCENT_FORMAT, RoundingMode.DOWN) + "%";
    }

    /**
     * Formats the given shares, removes any decimal point/s and negative sign.
     */
    @Override
    public String formatShares(long shares)
    {
        return format(Math.abs(shares), SHARES_FORMAT, RoundingMode.DOWN);
    }

    /**
     * Formats the TextView, Green for positive number, while Red for negative number.
     */
    @Override
    public void formatTextColor(double price, TextView text)
    {
        if(text != null)
        {
            if(price > 0)
            {
                text.setTextColor(green);
            }
            else if(price < 0)
            {
                text.setTextColor(Color.RED);
            }
            else
            {
                text.setTextColor(Color.BLACK);
            }
        }
    }

    /**
     * Formats the given number with the given format.
     */
    private String format(double number, String format, RoundingMode mode)
    {
        if(number == 0)
        {
            return "0";
        }

        DecimalFormat df = new DecimalFormat(format);
        df.setRoundingMode(mode);

        return df.format(number);
    }

    /**
     * Formats the date to 'MMMM dd, yyyy' pattern.
     *
     * @param date the Date to format
     * @return formatted date string
     */
    @Override
    public String formatDate(Date date)
    {
        if(date == null)
        {
            return "";
        }

        return DATE_FORMATTER.format(date);
    }

    /**
     * Formats the date to 'MMMM dd, EEEE hh:mm:ss a' pattern.
     *
     * @param date the Date to format
     * @return formatted date string
     */
    @Override
    public String formatLastUpdated(Date date)
    {
        if(date == null)
        {
            return "";
        }

        return DATE_FORMATTER_LAST_UPDATED.format(date);
    }
}
