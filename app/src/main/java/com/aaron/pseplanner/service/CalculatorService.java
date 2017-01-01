package com.aaron.pseplanner.service;

import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.bean.Trade;

/**
 * Created by aaron.asuncion on 12/19/2016.
 */

public interface CalculatorService
{
    double getBuyGrossAmount(double buyPrice, long shares);
    double getBuyNetAmount(double buyPrice, long shares);
    double getAveragePriceAfterBuy(double buyPrice);
    double getPriceToBreakEven(double buyPrice);
    double getSellGrossAmount(double sellPrice, long shares);
    double getSellNetAmount(double sellPrice, long shares);
    double getStockbrokersCommission(double grossAmount);
    double getVatOfCommission(double stockbrokersCommission);
    double getClearingFee(double grossAmount);
    double getTransactionFee(double grossAmount);
    double getSalesTax(double grossAmount);
    double getGainLossAmount(double buyPrice, long shares, double sellPrice);
    double getPercentGainLoss(double buyPrice, long shares, double sellPrice);
    double getRiskRewardRatio(double entryPrice, double targetPrice, double cutlossPrice);
    double getDividendYield(long shares, double cashDividend);
    double getPercentDividendYield(double price, long shares, double cashDividend);
    double getMidpoint(double high, double low);
}
