package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.service.CalculatorService;

import java.math.BigDecimal;
import java.math.MathContext;

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
}
