package com.aaron.pseplanner.service.implementation;

import android.util.Log;

import com.aaron.pseplanner.service.CalculatorService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;

/**
 * Created by aaron.asuncion on 12/19/2016.
 */

public class DefaultCalculatorService implements CalculatorService
{
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal TWO = new BigDecimal("2");

    /**
     * Gets the buy gross amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param shares   the number of shares to buy
     * @return BigDecimal
     */
    @Override
    public BigDecimal getBuyGrossAmount(BigDecimal buyPrice, long shares)
    {
        return buyPrice.multiply(BigDecimal.valueOf(shares));
    }

    /**
     * Gets the buy net(additional fees added) amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param shares   the number of shares to buy
     * @return BigDecimal
     */
    @Override
    public BigDecimal getBuyNetAmount(BigDecimal buyPrice, long shares)
    {
        BigDecimal grossAmount = getBuyGrossAmount(buyPrice, shares);

        return grossAmount.add(grossAmount.multiply(TOTAL_BUY_FEE));
    }

    /**
     * Gets the average price after the stock to buy. Adjusted with the additional fee.
     *
     * @param buyPrice the price the stock to buy
     * @return BigDecimal
     */
    @Override
    public BigDecimal getAveragePriceAfterBuy(BigDecimal buyPrice)
    {
        BigDecimal buyPriceWithFees = buyPrice.multiply(TOTAL_BUY_FEE);
        return buyPrice.add(buyPriceWithFees);
    }

    /**
     * Gets the selling price needed in order to break-even after the stock to buy. Adjusted with the additional fee.
     *
     * @param buyPrice the price the stock to buy
     * @return BigDecimal
     */
    @Override
    public BigDecimal getPriceToBreakEven(BigDecimal buyPrice)
    {
        BigDecimal buyPriceWithFees = buyPrice.multiply(TOTAL_BUY_SELL_FEE);
        return buyPrice.add(buyPriceWithFees);
    }

    /**
     * Gets the sell gross amount of a stock trade.
     *
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares to sell
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSellGrossAmount(BigDecimal sellPrice, long shares)
    {
        return sellPrice.multiply(BigDecimal.valueOf(shares));
    }

    /**
     * Gets the sell net(additional fees added) amount of a stock trade.
     *
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares to sell
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSellNetAmount(BigDecimal sellPrice, long shares)
    {
        BigDecimal sellGrossAmount = getSellGrossAmount(sellPrice, shares);

        return sellGrossAmount.subtract(sellGrossAmount.multiply(TOTAL_SELL_FEE));
    }

    /**
     * Gets the gain/loss amount of a stock trade.
     *
     * @param buyPrice  the price the stock to buy
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares in the trade
     * @return BigDecimal
     */
    @Override
    public BigDecimal getGainLossAmount(BigDecimal buyPrice, long shares, BigDecimal sellPrice)
    {
        BigDecimal buyNetAmount = getBuyNetAmount(buyPrice, shares);
        BigDecimal sellNetAmount = getSellNetAmount(sellPrice, shares);

        return sellNetAmount.subtract(buyNetAmount);
    }

    /**
     * Gets the percent gain/loss amount of a stock trade.
     *
     * @param buyPrice  the price the stock to buy
     * @param sellPrice the price the stock to sell
     * @param shares    the number of shares in the trade
     * @return BigDecimal
     */
    @Override
    public BigDecimal getPercentGainLoss(BigDecimal buyPrice, long shares, BigDecimal sellPrice)
    {
        BigDecimal gainLossAmount = getGainLossAmount(buyPrice, shares, sellPrice);
        BigDecimal sellNetAmount = getSellNetAmount(sellPrice, shares);

        return ONE_HUNDRED.multiply(gainLossAmount.divide(sellNetAmount, MathContext.DECIMAL64));
    }

    /**
     * Gets risk/reward ratio of a stock trade.
     *
     * @param entryPrice   the planned price entry of a stock
     * @param targetPrice  the planned target price of a stock
     * @param cutlossPrice the planned cutloss price of a stock
     * @return BigDecimal
     */
    @Override
    public BigDecimal getRiskRewardRatio(BigDecimal entryPrice, BigDecimal targetPrice, BigDecimal cutlossPrice)
    {
        BigDecimal gain = targetPrice.subtract(entryPrice);
        BigDecimal loss = entryPrice.subtract(cutlossPrice);
        return gain.divide(loss, MathContext.DECIMAL64);
    }

    /**
     * Gets dividend yield of a stock.
     *
     * @param shares       the number of shares to buy
     * @param cashDividend the dividend amount per share
     * @return BigDecimal
     */
    @Override
    public BigDecimal getDividendYield(long shares, BigDecimal cashDividend)
    {
        return cashDividend.multiply(BigDecimal.valueOf(shares));
    }

    /**
     * Gets percent dividend yield of a stock.
     *
     * @param price        the price of the stock to buy
     * @param shares       the number of shares to buy
     * @param cashDividend the dividend amount per share
     * @return BigDecimal
     */
    @Override
    public BigDecimal getPercentDividendYield(BigDecimal price, long shares, BigDecimal cashDividend)
    {
        BigDecimal dividendYield = getDividendYield(shares, cashDividend);
        BigDecimal totalAmount = getBuyGrossAmount(price, shares);
        return ONE_HUNDRED.multiply(dividendYield.divide(totalAmount, MathContext.DECIMAL64));
    }

    /**
     * Gets the midpoint of the given price(high, low).
     *
     * @param high highest price
     * @param low  lowest price
     * @return BigDecimal
     */
    @Override
    public BigDecimal getMidpoint(BigDecimal high, BigDecimal low)
    {
        return high.subtract(high.subtract(low).divide(TWO, MathContext.DECIMAL64));
    }

    /**
     * Gets the stock broker's commission.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    @Override
    public BigDecimal getStockbrokersCommission(BigDecimal grossAmount)
    {
        BigDecimal commission = grossAmount.multiply(STOCK_BROKERS_COMMISSION);
        return commission.compareTo(MINIMUM_COMMISSION) < 0 ? MINIMUM_COMMISSION : commission;
    }

    /**
     * Gets the stock broker's commission's vat.
     *
     * @param stockbrokersCommission the stock trade's commission
     * @return BigDecimal
     */
    @Override
    public BigDecimal getVatOfCommission(BigDecimal stockbrokersCommission)
    {
        return stockbrokersCommission.multiply(VAT);
    }

    /**
     * Gets the clearing fee.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    @Override
    public BigDecimal getClearingFee(BigDecimal grossAmount)
    {
        return grossAmount.multiply(CLEARING_FEE);
    }

    /**
     * Gets the transaction fee.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    @Override
    public BigDecimal getTransactionFee(BigDecimal grossAmount)
    {
        return grossAmount.multiply(PSE_TRANSACTION_FEE);
    }

    /**
     * Gets the sales tax.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSalesTax(BigDecimal grossAmount)
    {
        return grossAmount.multiply(SALES_TAX);
    }

    /**
     * Gets the change between the previous price and current price based on the current price and percent change.
     *
     * @param currentPrice  the current price
     * @param percentChange the percent change
     * @return BigDecimal the amount change from the previous price
     */
    @Override
    public BigDecimal getCurrentAndPreviousPriceChange(double currentPrice, double percentChange)
    {
        // Absolute value, so that result will always be negative, then computing for change will always add amount
        BigDecimal percentChangeToDivide = BigDecimal.valueOf(percentChange).abs();
        BigDecimal bdAmount = BigDecimal.valueOf(currentPrice);
        BigDecimal change = BigDecimal.valueOf(currentPrice);

        percentChangeToDivide = percentChangeToDivide.divide(ONE_HUNDRED, MathContext.DECIMAL64).subtract(BigDecimal.ONE);

        // This will always result in a negative value, because the abs value of percentChange is used
        change = bdAmount.divide(percentChangeToDivide, MathContext.DECIMAL64).add(bdAmount);

        if(percentChange > 0)
        {
            // Remove negative sign, because the percent change is positive
            return change.abs();
        }
        if(percentChange < 0)
        {
            return change;
        }
        else
        {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Gets the previous price based on the current price and percent change.
     *
     * @param currentPrice  the current amount
     * @param percentChange the percent change
     * @return BigDecimal the previous price
     */
    @Override
    public BigDecimal getPreviousPrice(double currentPrice, double percentChange)
    {
        BigDecimal change = getCurrentAndPreviousPriceChange(currentPrice, percentChange);
        BigDecimal prevousAmount = BigDecimal.valueOf(currentPrice);

        if(percentChange > 0)
        {
            prevousAmount = prevousAmount.add(change);
        }
        else
        {
            prevousAmount = prevousAmount.subtract(change);
        }

        return prevousAmount;
    }

    /**
     * Gets day/s difference between the two dates.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return int the number of days between the two dates, -1 if either of the inputs are null
     */
    @Override
    public int getDaysBetween(Date date1, Date date2)
    {
        if(date1 == null || date2 ==null)
        {
            return -1;
        }

        long time1 = date1.getTime();
        long time2 = date2.getTime();
        double diff;

        if(time1 > time2)
        {
            diff = time1 - time2;
        }
        else if(time1 < time2)
        {
            diff = time2 - time1;
        }
        else
        {
            return 0;
        }

        // Convert milliseconds difference to day and round up
        return (int) Math.ceil(diff / DAY_IN_MILLISECONDS);
    }
}
