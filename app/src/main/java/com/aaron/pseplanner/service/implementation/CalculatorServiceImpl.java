package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.service.CalculatorService;

/**
 * Created by aaron.asuncion on 12/19/2016.
 */

public class CalculatorServiceImpl implements CalculatorService
{
    /**
     * Gets the buy gross amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param shares   the number of shares to buy
     * @return double
     */
    @Override
    public double getBuyGrossAmount(double buyPrice, long shares)
    {
        return buyPrice * shares;
    }

    /**
     * Gets the buy net(additional fees added) amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param shares   the number of shares to buy
     * @return double
     */
    @Override
    public double getBuyNetAmount(double buyPrice, long shares)
    {
        double grossAmount = getBuyGrossAmount(buyPrice, shares);

        return grossAmount + (grossAmount * TOTAL_BUY_FEE);
    }

    /**
     * Gets the average price after the stock to buy. Adjusted with the additional fee.
     *
     * @param buyPrice the price the stock to buy
     * @return double
     */
    @Override
    public double getAveragePriceAfterBuy(double buyPrice)
    {
        double buyPriceWithFees = buyPrice * TOTAL_BUY_FEE;
        return buyPrice + buyPriceWithFees;
    }

    /**
     * Gets the selling price needed in order to break-even after the stock to buy. Adjusted with the additional fee.
     *
     * @param buyPrice the price the stock to buy
     * @return double
     */
    @Override
    public double getPriceToBreakEven(double buyPrice)
    {
        double buyPriceWithFees = buyPrice * TOTAL_BUY_SELL_FEE;
        return buyPrice + buyPriceWithFees;
    }

    /**
     * Gets the sell gross amount of a stock trade.
     *
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares to sell
     * @return double
     */
    @Override
    public double getSellGrossAmount(double sellPrice, long shares)
    {
        return sellPrice * shares;
    }

    /**
     * Gets the sell net(additional fees added) amount of a stock trade.
     *
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares to sell
     * @return double
     */
    @Override
    public double getSellNetAmount(double sellPrice, long shares)
    {
        double sellGrossAmount = getSellGrossAmount(sellPrice, shares);

        return sellGrossAmount - (sellGrossAmount * TOTAL_SELL_FEE);
    }

    /**
     * Gets the gain/loss amount of a stock trade.
     *
     * @param buyPrice  the price the stock to buy
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares in the trade
     * @return double
     */
    @Override
    public double getGainLossAmount(double buyPrice, long shares, double sellPrice)
    {
        double buyNetAmount = getBuyNetAmount(buyPrice, shares);
        double sellNetAmount = getSellNetAmount(sellPrice, shares);

        return sellNetAmount - buyNetAmount;
    }

    /**
     * Gets the percent gain/loss amount of a stock trade.
     *
     * @param buyPrice  the price the stock to buy
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares in the trade
     * @return double
     */
    @Override
    public double getPercentGainLoss(double buyPrice, long shares, double sellPrice)
    {
        double gainLossAmount = getGainLossAmount(buyPrice, shares, sellPrice);
        double sellNetAmount = getSellNetAmount(sellPrice, shares);

        return 100 * (gainLossAmount / sellNetAmount);
    }

    /**
     * Gets risk/reward ratio of a stock trade.
     *
     * @param entryPrice   the planned price entry of a stock
     * @param targetPrice  the planned target price of a stock
     * @param cutlossPrice the planned cutloss price of a stock
     * @return double
     */
    @Override
    public double getRiskRewardRatio(double entryPrice, double targetPrice, double cutlossPrice)
    {
        double gain = targetPrice - entryPrice;
        double loss = entryPrice - cutlossPrice;
        return gain / loss;
    }

    /**
     * Gets dividend yield of a stock.
     *
     * @param shares       the number of shares to buy
     * @param cashDividend the dividend amount per share
     * @return double
     */
    @Override
    public double getDividendYield(long shares, double cashDividend)
    {
        return shares * cashDividend;
    }

    /**
     * Gets percent dividend yield of a stock.
     *
     * @param price        the price of the stock to buy
     * @param shares       the number of shares to buy
     * @param cashDividend the dividend amount per share
     * @return double
     */
    @Override
    public double getPercentDividendYield(double price, long shares, double cashDividend)
    {
        double dividendYield = getDividendYield(shares, cashDividend);
        double totalAmount = getBuyGrossAmount(price, shares);
        return 100 * (dividendYield / totalAmount);
    }

    /**
     * Gets the midpoint of the given price(high, low).
     *
     * @param high highest price
     * @param low  lowest price
     * @return double
     */
    @Override
    public double getMidpoint(double high, double low)
    {
        return high - ((high - low) / 2);
    }

    /**
     * Gets the stock broker's commission.
     *
     * @param grossAmount the stock trade's gross amount
     * @return double
     */
    @Override
    public double getStockbrokersCommission(double grossAmount)
    {
        double commission = grossAmount * STOCK_BROKERS_COMMISSION;
        return commission < MINIMUM_COMMISSION ? MINIMUM_COMMISSION : commission;
    }

    /**
     * Gets the stock broker's commission's vat.
     *
     * @param stockbrokersCommission the stock trade's commission
     * @return double
     */
    @Override
    public double getVatOfCommission(double stockbrokersCommission)
    {
        return stockbrokersCommission * VAT;
    }

    /**
     * Gets the clearing fee.
     *
     * @param grossAmount the stock trade's gross amount
     * @return double
     */
    @Override
    public double getClearingFee(double grossAmount)
    {
        return grossAmount * CLEARING_FEE;
    }

    /**
     * Gets the transaction fee.
     *
     * @param grossAmount the stock trade's gross amount
     * @return double
     */
    @Override
    public double getTransactionFee(double grossAmount)
    {
        return grossAmount * PSE_TRANSACTION_FEE;
    }

    /**
     * Gets the sales tax.
     *
     * @param grossAmount the stock trade's gross amount
     * @return double
     */
    @Override
    public double getSalesTax(double grossAmount)
    {
        return grossAmount * SALES_TAX;
    }
}
