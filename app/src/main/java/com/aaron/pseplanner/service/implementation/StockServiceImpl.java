package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.bean.BoardLot;
import com.aaron.pseplanner.constant.Constants;
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

        DecimalFormat df = new DecimalFormat(Constants.PRICE_FORMAT);
        df.setRoundingMode(RoundingMode.FLOOR);

        return df.format(number);
    }
}
