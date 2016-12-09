package com.aaron.pseplanner.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by aaron.asuncion on 12/8/2016.
 */

public class StockUtils
{
    public static String format(double number)
    {
        if(number == 0)
        {
            return "0";
        }

        String priceStr;

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.FLOOR);

        return df.format(number);
    }

    public static String formatWithPercent(double number)
    {
        return format(number) + "%";
    }
}
