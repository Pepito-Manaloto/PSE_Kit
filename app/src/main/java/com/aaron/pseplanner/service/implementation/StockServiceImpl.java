package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.service.StockService;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by aaron.asuncion on 12/8/2016.
 */

public class StockServiceImpl implements StockService
{
    @Override
    public String formatStockPrice(double number)
    {
        if(number == 0)
        {
            return "0";
        }

        String priceStr;

        //TODO: determine the decimal format to use depending on the given number

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.FLOOR);

        return df.format(number);
    }
}
