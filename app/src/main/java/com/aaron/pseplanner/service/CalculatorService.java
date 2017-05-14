package com.aaron.pseplanner.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by aaron.asuncion on 12/19/2016.
 */

public interface CalculatorService
{
    // 0.25%
    BigDecimal STOCK_BROKERS_COMMISSION = BigDecimal.valueOf(0.0025);

    // 12%
    BigDecimal VAT = BigDecimal.valueOf(0.12);

    // 0.01
    BigDecimal CLEARING_FEE = BigDecimal.valueOf(0.0001);

    // 0.005%
    BigDecimal PSE_TRANSACTION_FEE = BigDecimal.valueOf(0.00005);

    // 0.5%
    BigDecimal SALES_TAX = BigDecimal.valueOf(0.005);

    // 0.295%
    BigDecimal TOTAL_BUY_FEE = STOCK_BROKERS_COMMISSION.add(STOCK_BROKERS_COMMISSION.multiply(VAT)).add(CLEARING_FEE).add(PSE_TRANSACTION_FEE);

    // 0.795%
    BigDecimal TOTAL_SELL_FEE = TOTAL_BUY_FEE.add(SALES_TAX);

    // 1.09%
    BigDecimal TOTAL_BUY_SELL_FEE = TOTAL_BUY_FEE.add(TOTAL_SELL_FEE);

    BigDecimal MINIMUM_COMMISSION = BigDecimal.valueOf(20);

    BigDecimal ONE_HUNDRED = new BigDecimal("100");

    BigDecimal TWO = new BigDecimal("2");

    long DAY_IN_MILLISECONDS = TimeUnit.DAYS.toMillis(1);

    /**
     * Gets the buy gross amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param shares   the number of shares to buy
     * @return BigDecimal
     */
    BigDecimal getBuyGrossAmount(BigDecimal buyPrice, long shares);

    /**
     * Gets the buy net(additional fees added) amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param shares   the number of shares to buy
     * @return BigDecimal
     */
    BigDecimal getBuyNetAmount(BigDecimal buyPrice, long shares);

    /**
     * Gets the average price after the stock to buy. Adjusted with the additional fee.
     *
     * @param buyPrice the price the stock to buy
     * @return BigDecimal
     */
    BigDecimal getAveragePriceAfterBuy(BigDecimal buyPrice);

    /**
     * Gets the selling price needed in order to break-even after the stock to buy. Adjusted with the additional fee.
     *
     * @param buyPrice the price the stock to buy
     * @return BigDecimal
     */
    BigDecimal getPriceToBreakEven(BigDecimal buyPrice);

    /**
     * Gets the sell gross amount of a stock trade.
     *
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares to sell
     * @return BigDecimal
     */
    BigDecimal getSellGrossAmount(BigDecimal sellPrice, long shares);

    /**
     * Gets the sell net(additional fees added) amount of a stock trade.
     *
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares to sell
     * @return BigDecimal
     */
    BigDecimal getSellNetAmount(BigDecimal sellPrice, long shares);


    /**
     * Gets the gain/loss amount of a stock trade.
     *
     * @param buyPrice  the price the stock to buy
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares in the trade
     * @return BigDecimal
     */
    BigDecimal getGainLossAmount(BigDecimal buyPrice, long shares, BigDecimal sellPrice);

    /**
     * Gets the percent gain/loss amount of a stock trade.
     *
     * @param buyPrice  the price the stock to buy
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares in the trade
     * @return BigDecimal
     */
    BigDecimal getPercentGainLoss(BigDecimal buyPrice, long shares, BigDecimal sellPrice);

    /**
     * Gets risk/reward ratio of a stock trade.
     *
     * @param entryPrice   the planned price entry of a stock
     * @param targetPrice  the planned target price of a stock
     * @param cutlossPrice the planned cutloss price of a stock
     * @return BigDecimal
     */
    BigDecimal getRiskRewardRatio(BigDecimal entryPrice, BigDecimal targetPrice, BigDecimal cutlossPrice);

    /**
     * Gets dividend yield of a stock.
     *
     * @param shares       the number of shares to buy
     * @param cashDividend the dividend amount per share
     * @return BigDecimal
     */
    BigDecimal getDividendYield(long shares, BigDecimal cashDividend);

    /**
     * Gets percent dividend yield of a stock.
     *
     * @param price        the price of the stock to buy
     * @param shares       the number of shares to buy
     * @param cashDividend the dividend amount per share
     * @return BigDecimal
     */
    BigDecimal getPercentDividendYield(BigDecimal price, long shares, BigDecimal cashDividend);

    /**
     * Gets the midpoint of the given price(high, low).
     *
     * @param high highest price
     * @param low  lowest price
     * @return BigDecimal
     */
    BigDecimal getMidpoint(BigDecimal high, BigDecimal low);

    /**
     * Gets the stock broker's commission.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    BigDecimal getStockbrokersCommission(BigDecimal grossAmount);

    /**
     * Gets the stock broker's commission's vat.
     *
     * @param stockbrokersCommission the stock trade's commission
     * @return BigDecimal
     */
    BigDecimal getVatOfCommission(BigDecimal stockbrokersCommission);

    /**
     * Gets the clearing fee.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    BigDecimal getClearingFee(BigDecimal grossAmount);

    /**
     * Gets the transaction fee.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    BigDecimal getTransactionFee(BigDecimal grossAmount);

    /**
     * Gets the sales tax.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    BigDecimal getSalesTax(BigDecimal grossAmount);

    /**
     * Gets the change between the previous price and current price based on the current price and percent change.
     *
     * @param currentPrice  the current price
     * @param percentChange the percent change
     * @return BigDecimal the amount change from the previous price
     */
    BigDecimal getCurrentAndPreviousPriceChange(double currentPrice, double percentChange);

    /**
     * Gets the previous price based on the current price and percent change.
     *
     * @param currentPrice  the current amount
     * @param percentChange the percent change
     * @return BigDecimal the previous price
     */
    BigDecimal getPreviousPrice(double currentPrice, double percentChange);

    /**
     * Gets day/s difference between the two dates.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return int the number of days between the two dates
     */
    int getDaysBetween(Date date1, Date date2);

}
