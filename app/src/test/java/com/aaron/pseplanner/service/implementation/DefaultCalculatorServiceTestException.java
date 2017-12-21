package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.service.CalculatorService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Test for CalculatorService.
 */
public class DefaultCalculatorServiceTestException
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CalculatorService service;
    private BigDecimal negative;

    @Before
    public void init()
    {
        this.service = new DefaultCalculatorService();
        this.negative = BigDecimal.valueOf(-1);
    }

    @Test
    public void getBuyGrossAmountTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyGrossAmount(null, 100);
    }

    @Test
    public void getBuyGrossAmountTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyGrossAmount(BigDecimal.ZERO, 100);
    }

    @Test
    public void getBuyGrossAmountTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyGrossAmount(negative, 100);
    }

    @Test
    public void getBuyGrossAmountTestSharesZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getBuyGrossAmount(BigDecimal.TEN, 0);
    }

    @Test
    public void getBuyGrossAmountTestSharesNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getBuyGrossAmount(BigDecimal.TEN, -1);
    }

    @Test
    public void getBuyNetAmountTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyNetAmount(null, 100);
    }

    @Test
    public void getBuyNetAmountTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyNetAmount(BigDecimal.ZERO, 100);
    }

    @Test
    public void getBuyNetAmountTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyNetAmount(negative, 100);
    }

    @Test
    public void getBuyNetAmountTestSharesZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getBuyNetAmount(BigDecimal.TEN, 0);
    }

    @Test
    public void getBuyNetAmountTestSharesNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getBuyNetAmount(BigDecimal.TEN, -1);
    }

    @Test
    public void getAveragePriceAfterBuyTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getAveragePriceAfterBuy(null);
    }

    @Test
    public void getAveragePriceAfterBuyTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getAveragePriceAfterBuy(BigDecimal.ZERO);
    }

    @Test
    public void getAveragePriceAfterBuyTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getAveragePriceAfterBuy(negative);
    }

    @Test
    public void getPriceToBreakEvenTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPriceToBreakEven(null);
    }

    @Test
    public void getPriceToBreakEvenTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPriceToBreakEven(BigDecimal.ZERO);
    }

    @Test
    public void getPriceToBreakEvenTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPriceToBreakEven(negative);
    }

    @Test
    public void getSellGrossAmountTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellGrossAmount(null, 100);
    }

    @Test
    public void getSellGrossAmountTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellGrossAmount(BigDecimal.ZERO, 100);
    }

    @Test
    public void getSellGrossAmountTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellGrossAmount(negative, 100);
    }

    @Test
    public void getSellGrossAmountTestSharesZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getSellGrossAmount(BigDecimal.TEN, 0);
    }

    @Test
    public void getSellGrossAmountTestSharesNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getSellGrossAmount(BigDecimal.TEN, -1);
    }

    @Test
    public void getSellNetAmountTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellNetAmount(null, 100);
    }

    @Test
    public void getSellNetAmountTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellNetAmount(BigDecimal.ZERO, 100);
    }

    @Test
    public void getSellNetAmountTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellNetAmount(negative, 100);
    }

    @Test
    public void getSellNetAmountTestSharesZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getSellNetAmount(BigDecimal.TEN, 0);
    }

    @Test
    public void getSellNetAmountTestSharesNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getSellNetAmount(BigDecimal.TEN, -1);
    }

    @Test
    public void getGainLossAmountTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getGainLossAmount(BigDecimal.TEN, 100, null);
    }

    @Test
    public void getGainLossAmountTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getGainLossAmount(BigDecimal.TEN, 100, BigDecimal.ZERO);
    }

    @Test
    public void getGainLossAmountTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getGainLossAmount(BigDecimal.TEN, 100, negative);
    }

    @Test
    public void getGainLossAmountTestSharesZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getGainLossAmount(BigDecimal.TEN, 0, BigDecimal.TEN);
    }

    @Test
    public void getGainLossAmountTestSharesNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getGainLossAmount(BigDecimal.TEN, -1, BigDecimal.TEN);
    }

    @Test
    public void getPercentGainLossTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentGainLoss(BigDecimal.TEN, 100, null);
    }

    @Test
    public void getPercentGainLossTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentGainLoss(BigDecimal.TEN, 100, BigDecimal.ZERO);
    }

    @Test
    public void getPercentGainLossTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentGainLoss(BigDecimal.TEN, 100, negative);
    }

    @Test
    public void getPercentGainLossTestSharesZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getPercentGainLoss(BigDecimal.TEN, 0, BigDecimal.TEN);
    }

    @Test
    public void getPercentGainLossTestSharesNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getPercentGainLoss(BigDecimal.TEN, -1, BigDecimal.TEN);
    }

    @Test
    public void getRiskRewardRatioTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getRiskRewardRatio(BigDecimal.TEN, BigDecimal.TEN, null);
    }

    @Test
    public void getRiskRewardRatioTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getRiskRewardRatio(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);
    }

    @Test
    public void getRiskRewardRatioTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getRiskRewardRatio(BigDecimal.TEN, BigDecimal.TEN, negative);
    }

    @Test
    public void getDividendYieldTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getDividendYield(100, null);
    }

    @Test
    public void getDividendYieldTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getDividendYield(100, BigDecimal.ZERO);
    }

    @Test
    public void getDividendYieldTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getDividendYield(100, negative);
    }

    @Test
    public void getDividendYieldTestSharesZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getDividendYield(0, BigDecimal.TEN);
    }

    @Test
    public void getDividendYieldTestSharesNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getDividendYield(-1, BigDecimal.TEN);
    }

    @Test
    public void getPercentDividendYieldTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentDividendYield(BigDecimal.TEN, 100, null);
    }

    @Test
    public void getPercentDividendYieldTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentDividendYield(BigDecimal.TEN, 100, BigDecimal.ZERO);
    }

    @Test
    public void getPercentDividendYieldTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentDividendYield(BigDecimal.TEN, 100, negative);
    }

    @Test
    public void getPercentDividendYieldTestSharesZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getPercentDividendYield(BigDecimal.TEN, 0, BigDecimal.TEN);
    }

    @Test
    public void getPercentDividendYieldTestSharesNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getPercentDividendYield(BigDecimal.TEN, -1, BigDecimal.TEN);
    }

    @Test
    public void getMidpointTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getMidpoint(BigDecimal.TEN, null);
    }

    @Test
    public void getMidpointTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getMidpoint(BigDecimal.TEN, BigDecimal.ZERO);
    }

    @Test
    public void getMidpointTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getMidpoint(BigDecimal.TEN, negative);
    }

    @Test
    public void getStockbrokersCommissionTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getStockbrokersCommission(null);
    }

    @Test
    public void getStockbrokersCommissionTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getStockbrokersCommission(BigDecimal.ZERO);
    }

    @Test
    public void getStockbrokersCommissionTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getStockbrokersCommission(negative);
    }

    @Test
    public void getVatOfCommissionTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getVatOfCommission(null);
    }

    @Test
    public void getVatOfCommissionTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getVatOfCommission(BigDecimal.ZERO);
    }

    @Test
    public void getVatOfCommissionTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getVatOfCommission(negative);
    }

    @Test
    public void getClearingFeeTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getClearingFee(null);
    }

    @Test
    public void getClearingFeeTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getClearingFee(BigDecimal.ZERO);
    }

    @Test
    public void getClearingFeeTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getClearingFee(negative);
    }

    @Test
    public void getTransactionFeeTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getTransactionFee(null);
    }

    @Test
    public void getTransactionFeeTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getTransactionFee(BigDecimal.ZERO);
    }

    @Test
    public void getTransactionFeeTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getTransactionFee(negative);
    }

    @Test
    public void getSalesTaxTestNull()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSalesTax(null);
    }

    @Test
    public void getSalesTaxTestZero()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSalesTax(BigDecimal.ZERO);
    }

    @Test
    public void getSalesTaxTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSalesTax(negative);
    }

    @Test
    public void getCurrentAndPreviousPriceChangeTestZeroBoth()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getCurrentAndPreviousPriceChange(0, 0);
    }

    @Test
    public void getCurrentAndPreviousPriceChangeTestZeroFirst()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getCurrentAndPreviousPriceChange(10, 0);
    }

    @Test
    public void getCurrentAndPreviousPriceChangeTestNegativeFirst()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getCurrentAndPreviousPriceChange(10, -1);
    }

    @Test
    public void getCurrentAndPreviousPriceChangeTestZeroSecond()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getCurrentAndPreviousPriceChange(0, 10);
    }

    @Test
    public void getCurrentAndPreviousPriceChangeTestNegativeSecond()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getCurrentAndPreviousPriceChange(-1, 10);
    }

    @Test
    public void getCurrentAndPreviousPriceChangeTestNegativeBoth()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getCurrentAndPreviousPriceChange(-1, -1);
    }

    @Test
    public void getPreviousPriceTestZeroBoth()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPreviousPrice(0, 0);
    }

    @Test
    public void getPreviousPriceTestZeroFirst()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPreviousPrice(10, 0);
    }

    @Test
    public void getPreviousPriceTestNegativeFirst()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPreviousPrice(10, -1);
    }

    @Test
    public void getPreviousPriceTestZeroSecond()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPreviousPrice(0, 10);
    }

    @Test
    public void getPreviousPriceTestNegativeSecond()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPreviousPrice(-1, 10);
    }

    @Test
    public void getPreviousPriceTestNegativeBoth()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPreviousPrice(-1, -1);
    }

    @Test
    public void getDaysBetweenNullBoth()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Dates to compare must not be null");
        this.service.getDaysBetween(null, null);
    }

    @Test
    public void getDaysBetweenNullFirst()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Dates to compare must not be null");
        this.service.getDaysBetween(null, new Date());
    }

    @Test
    public void getDaysBetweenNullSecond()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Dates to compare must not be null");
        this.service.getDaysBetween(new Date(), null);
    }
}