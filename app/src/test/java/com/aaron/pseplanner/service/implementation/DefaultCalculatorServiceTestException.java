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
    public void givenNullBuyPriceAndShares_whenGetBuyGrossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyGrossAmount(null, 100);
    }

    @Test
    public void givenZeroBuyPriceAndShares_whenGetBuyGrossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyGrossAmount(BigDecimal.ZERO, 100);
    }

    @Test
    public void givenNegativeBuyPriceAndShares_whenGetBuyGrossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyGrossAmount(negative, 100);
    }

    @Test
    public void givenBuyPriceAndZeroShares_whenGetBuyGrossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getBuyGrossAmount(BigDecimal.TEN, 0);
    }

    @Test
    public void givenBuyPriceAndNegativeShares_whenGetBuyGrossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getBuyGrossAmount(BigDecimal.TEN, -1);
    }

    @Test
    public void givenNullBuyPriceAndShares_whenGetBuyNetAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyNetAmount(null, 100);
    }

    @Test
    public void givenZeroBuyPriceAndShares_whenGetBuyNetAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyNetAmount(BigDecimal.ZERO, 100);
    }

    @Test
    public void givenNegativeBuyPriceAndShares_whenGetBuyNetAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getBuyNetAmount(negative, 100);
    }

    @Test
    public void givenBuyPriceAndZeroShares_whenGetBuyNetAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getBuyNetAmount(BigDecimal.TEN, 0);
    }

    @Test
    public void givenBuyPriceAndNegativeShares_whenGetBuyNetAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getBuyNetAmount(BigDecimal.TEN, -1);
    }

    @Test
    public void givenNullBuyPrice_whenGetAveragePriceAfterBuy_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getAveragePriceAfterBuy(null);
    }

    @Test
    public void givenZeroBuyPrice_whenGetAveragePriceAfterBuy_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getAveragePriceAfterBuy(BigDecimal.ZERO);
    }

    @Test
    public void givenNegativeBuyPrice_whenGetAveragePriceAfterBuy_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getAveragePriceAfterBuy(negative);
    }

    @Test
    public void givenNullBuyPrice_whenGetPriceToBreakEven_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPriceToBreakEven(null);
    }

    @Test
    public void givenZeroBuyPrice_whenGetPriceToBreakEven_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPriceToBreakEven(BigDecimal.ZERO);
    }

    @Test
    public void givenNegativeBuyPrice_whenGetPriceToBreakEven_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPriceToBreakEven(negative);
    }

    @Test
    public void givenNullSellPriceAndShares_whenGetSellGrossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellGrossAmount(null, 100);
    }

    @Test
    public void givenZeroSellPriceAndShares_whenGetSellGrossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellGrossAmount(BigDecimal.ZERO, 100);
    }

    @Test
    public void givenNegativeSellPriceAndShares_whenGetSellGrossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellGrossAmount(negative, 100);
    }

    @Test
    public void givenSellPriceAndZeroShares_whenGetSellGrossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getSellGrossAmount(BigDecimal.TEN, 0);
    }

    @Test
    public void givenSellPriceAndNegativeShares_whenGetSellGrossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getSellGrossAmount(BigDecimal.TEN, -1);
    }

    @Test
    public void givenNullSellPriceAndShares_whenGetSellNetAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellNetAmount(null, 100);
    }

    @Test
    public void givenZeroSellPriceAndShares_whenGetSellNetAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellNetAmount(BigDecimal.ZERO, 100);
    }

    @Test
    public void givenNegativeSellPriceAndShares_whenGetSellNetAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSellNetAmount(negative, 100);
    }

    @Test
    public void givenSellPriceAndZeroShares_whenGetSellNetAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getSellNetAmount(BigDecimal.TEN, 0);
    }

    @Test
    public void givenSellPriceAndNegativeShares_whenGetSellNetAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getSellNetAmount(BigDecimal.TEN, -1);
    }

    @Test
    public void givenBuyPriceAndSharesAndNullSellPrice_whenGetGainLossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getGainLossAmount(BigDecimal.TEN, 100, null);
    }

    @Test
    public void givenBuyPriceAndSharesAndZeroSellPrice_whenGetGainLossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getGainLossAmount(BigDecimal.TEN, 100, BigDecimal.ZERO);
    }

    @Test
    public void givenBuyPriceAndSharesAndNegativeSellPrice_whenGetGainLossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getGainLossAmount(BigDecimal.TEN, 100, negative);
    }

    @Test
    public void givenBuyPriceAndZeroSharesAndSellPrice_whenGetGainLossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getGainLossAmount(BigDecimal.TEN, 0, BigDecimal.TEN);
    }

    @Test
    public void givenBuyPriceAndNegativeSharesAndSellPrice_whenGetGainLossAmount_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getGainLossAmount(BigDecimal.TEN, -1, BigDecimal.TEN);
    }

    @Test
    public void givenBuyPriceAndSharesAndNullSellPrice_whenGetPercentGainLoss_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentGainLoss(BigDecimal.TEN, 100, null);
    }

    @Test
    public void givenBuyPriceAndSharesAndZeroSellPrice_whenGetPercentGainLoss_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentGainLoss(BigDecimal.TEN, 100, BigDecimal.ZERO);
    }

    @Test
    public void givenBuyPriceAndSharesAndNegativeSellPrice_whenGetPercentGainLoss_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentGainLoss(BigDecimal.TEN, 100, negative);
    }

    @Test
    public void givenBuyPriceAndZeroSharesAndSellPrice_whenGetPercentGainLoss_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getPercentGainLoss(BigDecimal.TEN, 0, BigDecimal.TEN);
    }

    @Test
    public void givenBuyPriceAndNegativeSharesAndSellPrice_whenGetPercentGainLoss_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getPercentGainLoss(BigDecimal.TEN, -1, BigDecimal.TEN);
    }

    @Test
    public void givenEntryPriceAndTargetPriceAndNullCutlossPrice_whenGetRiskRewardRatio_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getRiskRewardRatio(BigDecimal.TEN, BigDecimal.TEN, null);
    }

    @Test
    public void givenEntryPriceAndTargetPriceAndZeroCutlossPrice_whenGetRiskRewardRatio_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getRiskRewardRatio(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);
    }

    @Test
    public void givenEntryPriceAndTargetPriceAndNegativeCutlossPrice_whenGetRiskRewardRatio_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getRiskRewardRatio(BigDecimal.TEN, BigDecimal.TEN, negative);
    }

    @Test
    public void givenSharesAndNullCashDividend_whenGetDividendYield_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getDividendYield(100, null);
    }

    @Test
    public void givenSharesAndZeroCashDividend_whenGetDividendYield_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getDividendYield(100, BigDecimal.ZERO);
    }

    @Test
    public void givenSharesAndNegativeCashDividend_whenGetDividendYield_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getDividendYield(100, negative);
    }

    @Test
    public void givenZeroSharesAndCashDividend_whenGetDividendYield_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getDividendYield(0, BigDecimal.TEN);
    }

    @Test
    public void givenNegativeSharesAndCashDividend_whenGetDividendYield_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getDividendYield(-1, BigDecimal.TEN);
    }

    @Test
    public void givenPriceAndSharesAndNullCashDividend_whenGetPercentDividendYield_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentDividendYield(BigDecimal.TEN, 100, null);
    }

    @Test
    public void givenPriceAndSharesAndZeroCashDividend_whenGetPercentDividendYield_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentDividendYield(BigDecimal.TEN, 100, BigDecimal.ZERO);
    }

    @Test
    public void givenPriceAndSharesAndNegativeCashDividend_whenGetPercentDividendYield_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPercentDividendYield(BigDecimal.TEN, 100, negative);
    }

    @Test
    public void givenPriceAndZeroSharesAndCashDividend_whenGetPercentDividendYield_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getPercentDividendYield(BigDecimal.TEN, 0, BigDecimal.TEN);
    }

    @Test
    public void givenPriceAndNegativeSharesAndCashDividend_whenGetPercentDividendYield_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Shares must be greater than zero");
        this.service.getPercentDividendYield(BigDecimal.TEN, -1, BigDecimal.TEN);
    }

    @Test
    public void givenHighAndNullLow_whenGetMidpoint_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getMidpoint(BigDecimal.TEN, null);
    }

    @Test
    public void givenHighAndZeroLow_whenGetMidpoint_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getMidpoint(BigDecimal.TEN, BigDecimal.ZERO);
    }

    @Test
    public void givenHighAndNegativeLow_whenGetMidpoint_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getMidpoint(BigDecimal.TEN, negative);
    }

    @Test
    public void givenNullGrossAmount_whenGetStockbrokersCommission_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getStockbrokersCommission(null);
    }

    @Test
    public void givenZeroGrossAmount_whenGetStockbrokersCommission_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getStockbrokersCommission(BigDecimal.ZERO);
    }

    @Test
    public void givenNegativeGrossAmount_whenGetStockbrokersCommission_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getStockbrokersCommission(negative);
    }

    @Test
    public void givenNullStockBrokersCommission_whenGetVatOfCommission_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getVatOfCommission(null);
    }

    @Test
    public void givenZeroStockBrokersCommission_whenGetVatOfCommission_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getVatOfCommission(BigDecimal.ZERO);
    }

    @Test
    public void givenNegativeStockBrokersCommission_whenGetVatOfCommission_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getVatOfCommission(negative);
    }

    @Test
    public void givenNullGrossAmount_whenGetClearingFee_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getClearingFee(null);
    }

    @Test
    public void givenZeroGrossAmount_whenGetClearingFee_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getClearingFee(BigDecimal.ZERO);
    }

    @Test
    public void givenNegativeGrossAmount_whenGetClearingFee_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getClearingFee(negative);
    }

    @Test
    public void givenNullGrossAmount_whenGetTransactionFee_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getTransactionFee(null);
    }

    @Test
    public void givenZeroGrossAmount_whenGetTransactionFee_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getTransactionFee(BigDecimal.ZERO);
    }

    @Test
    public void givenNegativeGrossAmount_whenGetTransactionFee_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getTransactionFee(negative);
    }

    @Test
    public void givenNullGrossAmount_whenGetSalesTax_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSalesTax(null);
    }

    @Test
    public void givenZeroGrossAmount_whenGetSalesTax_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSalesTax(BigDecimal.ZERO);
    }

    @Test
    public void givenNegativeGrossAmount_whenGetSalesTax_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getSalesTax(negative);
    }

    @Test
    public void givenNegativeCurrentPriceAndPercentChange_whenGetChangeBetweenCurrentAndPreviousPrice_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getChangeBetweenCurrentAndPreviousPrice(-1, 10);
    }

    @Test
    public void givenNegativeCurrentPriceAndNegativePercentChange_whenGetChangeBetweenCurrentAndPreviousPrice_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getChangeBetweenCurrentAndPreviousPrice(-1, -1);
    }

    @Test
    public void givenZeroCurrentPriceAndPercentChange_whenGetPreviousPrice_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPreviousPrice(0, 10);
    }

    @Test
    public void givenNegativeCurrentPriceAndPercentChange_whenGetPreviousPrice_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Inputs to be calculated must be greater than zero");
        this.service.getPreviousPrice(-1, 10);
    }

    @Test
    public void givenNullDate1AndNullDate2_whenGetDaysBetween_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Dates to compare must not be null");
        this.service.getDaysBetween(null, null);
    }

    @Test
    public void givenNullDate1AndDate2_whenGetDaysBetween_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Dates to compare must not be null");
        this.service.getDaysBetween(null, new Date());
    }

    @Test
    public void givenDate1AndNullDate2_whenGetDaysBetween_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Dates to compare must not be null");
        this.service.getDaysBetween(new Date(), null);
    }
}