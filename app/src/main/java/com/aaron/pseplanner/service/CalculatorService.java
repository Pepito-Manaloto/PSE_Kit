package com.aaron.pseplanner.service;

import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.bean.Trade;

/**
 * Created by aaron.asuncion on 12/19/2016.
 */

public interface CalculatorService
{
    /**
     * Gets the buy gross amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param shares   the number of shares to buy
     * @return double
     */
    double getBuyGrossAmount(double buyPrice, long shares);

    /**
     * Gets the buy net(additional fees added) amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param shares   the number of shares to buy
     * @return double
     */
    double getBuyNetAmount(double buyPrice, long shares);

    /**
     * Gets the average price after the stock to buy. Adjusted with the additional fee.
     *
     * @param buyPrice the price the stock to buy
     * @return double
     */
    double getAveragePriceAfterBuy(double buyPrice);

    /**
     * Gets the selling price needed in order to break-even after the stock to buy. Adjusted with the additional fee.
     *
     * @param buyPrice the price the stock to buy
     * @return double
     */
    double getPriceToBreakEven(double buyPrice);

    /**
     * Gets the sell gross amount of a stock trade.
     *
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares to sell
     * @return double
     */
    double getSellGrossAmount(double sellPrice, long shares);

    /**
     * Gets the sell net(additional fees added) amount of a stock trade.
     *
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares to sell
     * @return double
     */
    double getSellNetAmount(double sellPrice, long shares);


    /**
     * Gets the gain/loss amount of a stock trade.
     *
     * @param buyPrice  the price the stock to buy
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares in the trade
     * @return double
     */
    double getGainLossAmount(double buyPrice, long shares, double sellPrice);

    /**
     * Gets the percent gain/loss amount of a stock trade.
     *
     * @param buyPrice  the price the stock to buy
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares in the trade
     * @return double
     */
    double getPercentGainLoss(double buyPrice, long shares, double sellPrice);

    /**
     * Gets risk/reward ratio of a stock trade.
     *
     * @param entryPrice   the planned price entry of a stock
     * @param targetPrice  the planned target price of a stock
     * @param cutlossPrice the planned cutloss price of a stock
     * @return double
     */
    double getRiskRewardRatio(double entryPrice, double targetPrice, double cutlossPrice);

    /**
     * Gets dividend yield of a stock.
     *
     * @param shares       the number of shares to buy
     * @param cashDividend the dividend amount per share
     * @return double
     */
    double getDividendYield(long shares, double cashDividend);

    /**
     * Gets percent dividend yield of a stock.
     *
     * @param price        the price of the stock to buy
     * @param shares       the number of shares to buy
     * @param cashDividend the dividend amount per share
     * @return double
     */
    double getPercentDividendYield(double price, long shares, double cashDividend);

    /**
     * Gets the midpoint of the given price(high, low).
     *
     * @param high highest price
     * @param low  lowest price
     * @return double
     */
    double getMidpoint(double high, double low);

    /**
     * Gets the stock broker's commission.
     *
     * @param grossAmount the stock trade's gross amount
     * @return double
     */
    double getStockbrokersCommission(double grossAmount);

    /**
     * Gets the stock broker's commission's vat.
     *
     * @param stockbrokersCommission the stock trade's commission
     * @return double
     */
    double getVatOfCommission(double stockbrokersCommission);

    /**
     * Gets the clearing fee.
     *
     * @param grossAmount the stock trade's gross amount
     * @return double
     */
    double getClearingFee(double grossAmount);

    /**
     * Gets the transaction fee.
     *
     * @param grossAmount the stock trade's gross amount
     * @return double
     */
    double getTransactionFee(double grossAmount);

    /**
     * Gets the sales tax.
     *
     * @param grossAmount the stock trade's gross amount
     * @return double
     */
    double getSalesTax(double grossAmount);
}
