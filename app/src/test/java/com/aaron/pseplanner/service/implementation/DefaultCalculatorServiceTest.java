package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.UnitTestUtils;
import com.aaron.pseplanner.service.CalculatorService;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Test for CalculatorService.
 */
public class DefaultCalculatorServiceTest
{
    private static final double DELTA = 0.0001;
    private static final int ROUNDING_SCALE = 2;
    private CalculatorService service;

    @Before
    public void init()
    {
        this.service = new DefaultCalculatorService();
    }

    @Test
    public void givenBuyPriceAndShares_whenGetBuyGrossAmount_thenShouldReturnTheGrossAmount()
    {
        BigDecimal buyPrice = new BigDecimal("11.32");
        long shares = 10_000;

        assertEquals(113_200, service.getBuyGrossAmount(buyPrice, shares).doubleValue(), DELTA);
    }

    @Test
    public void givenBuyPriceAndShares_whenGetBuyNetAmount_thenShouldReturnTheNetAmount()
    {
        BigDecimal buyPrice = new BigDecimal("0.31");
        long shares = 1_300_000;

        assertEquals(404_188.85, service.getBuyNetAmount(buyPrice, shares).doubleValue(), DELTA);
    }

    @Test
    public void givenBuyPrice_whenGetAveragePriceAfterBuy_thenShouldReturnTheAveragePriceAfterBuy()
    {
        BigDecimal buyPrice = new BigDecimal("2.85");

        assertEquals(2.8584, service.getAveragePriceAfterBuy(buyPrice).doubleValue(), DELTA);
    }

    @Test
    public void givenBuyPrice_whenGetPriceToBreakEven_thenShouldReturnThePriceToBreakEven()
    {
        BigDecimal buyPrice = new BigDecimal("11.28");

        assertEquals(11.414232, service.getPriceToBreakEven(buyPrice).doubleValue(), DELTA);
    }

    @Test
    public void givenSellPriceAndShares_whenGetSellGrossAmount_thenShouldReturnTheGrossAmount()
    {
        BigDecimal sellPrice = new BigDecimal("248.8");
        long shares = 1250;

        assertEquals(311_000, service.getSellGrossAmount(sellPrice, shares).doubleValue(), DELTA);
    }

    @Test
    public void givenSellPriceAndShares_whenGetSellNetAmount_thenShouldReturnTheNetAmount()
    {
        BigDecimal sellPrice = new BigDecimal("248.8");
        long shares = 1250;

        assertEquals(308_216.55, service.getSellNetAmount(sellPrice, shares).doubleValue(), DELTA);
    }

    @Test
    public void givenLowerBuyPriceAndSharesAndHigherSellPrice_whenGetGainLossAmount_thenShouldReturnPositiveGainLossAmount()
    {
        BigDecimal buyPrice = new BigDecimal("1.33");
        long shares = 4_938_000;
        BigDecimal sellPrice = new BigDecimal("1.48");

        assertEquals(655_917.009, service.getGainLossAmount(buyPrice, shares, sellPrice).doubleValue(), DELTA);
    }

    @Test
    public void givenHigherBuyPriceAndSharesAndLowerSellPrice_whenGetGainLossAmount_thenShouldReturnNegativeGainLossAmount()
    {
        BigDecimal buyPrice = new BigDecimal("1.33");
        long shares = 4_938_000;
        BigDecimal sellPrice = new BigDecimal("1.29");

        assertEquals(-273_905.922, service.getGainLossAmount(buyPrice, shares, sellPrice).doubleValue(), DELTA);
    }

    @Test
    public void givenLowerBuyPriceAndSharesAndHigherSellPrice_whenGetPercentGainLoss_thenShouldReturnPositivePercentGainLoss()
    {
        BigDecimal buyPrice = new BigDecimal("1.33");
        long shares = 4_938_000;
        BigDecimal sellPrice = new BigDecimal("1.48");

        BigDecimal percentGainLossBigDecimal = service.getPercentGainLoss(buyPrice, shares, sellPrice);
        double percentGainLoss = getDoubleRoundedDownDecimalToTwoPlaces(percentGainLossBigDecimal);
        assertEquals(9.95, percentGainLoss, DELTA);
    }

    @Test
    public void givenHigherBuyPriceAndSharesAndLowerSellPrice_whenGetPercentGainLoss_thenShouldReturnNegativePercentGainLoss()
    {
        BigDecimal buyPrice = new BigDecimal("1.33");
        long shares = 4_938_000;
        BigDecimal sellPrice = new BigDecimal("1.29");

        BigDecimal percentGainLossBigDecimal = service.getPercentGainLoss(buyPrice, shares, sellPrice);
        double percentGainLoss = getDoubleRoundedDownDecimalToTwoPlaces(percentGainLossBigDecimal);
        assertEquals(-4.15, percentGainLoss, DELTA);
    }

    @Test
    public void givenEntryPriceAndTargetPriceAndCutlossPrice_whenGetRiskRewardRatio_thenShouldReturnTheRiskRewardRatio()
    {
        BigDecimal entryPrice = new BigDecimal("0.038");
        BigDecimal targetPrice = new BigDecimal("0.05");
        BigDecimal cutlossPrice = new BigDecimal("0.035");

        BigDecimal riskRewardRatioBigDecimal = service.getRiskRewardRatio(entryPrice, targetPrice, cutlossPrice);
        double riskRewardRatio = getDoubleRoundedDownDecimalToTwoPlaces(riskRewardRatioBigDecimal);
        assertEquals(3.33, riskRewardRatio, DELTA);
    }

    @Test
    public void givenSharesAndCashDividend_whenGetDividendYield_thenShouldReturnTheDividendYield()
    {
        long shares = 89_540;
        BigDecimal cashDividend = new BigDecimal("2.47");

        assertEquals(221163.80, service.getDividendYield(shares, cashDividend).doubleValue(), DELTA);
    }

    @Test
    public void givenPriceAndSharesAndCashDividend_whenGetPercentDividendYield_thenShouldReturnThePercentDividendYield()
    {
        BigDecimal price = new BigDecimal("87.7");
        long shares = 89_540;
        BigDecimal cashDividend = new BigDecimal("2.47");

        BigDecimal percentDividendYieldBigDecimal = service.getPercentDividendYield(price, shares, cashDividend);
        double percentDividendYield = getDoubleRoundedDownDecimalToTwoPlaces(percentDividendYieldBigDecimal);
        assertEquals(2.81, percentDividendYield, DELTA);
    }

    @Test
    public void givenHighAndLow_whenGetMidpoint_thenShouldReturnTheMidpoint()
    {
        BigDecimal high = new BigDecimal("2.79");
        BigDecimal low = new BigDecimal("2.47");

        assertEquals(2.63, service.getMidpoint(high, low).doubleValue(), DELTA);
    }

    @Test
    public void givenLowAndHigh_whenGetMidpoint_thenShouldReturnTheMidpoint()
    {
        BigDecimal low = new BigDecimal("2.47");
        BigDecimal high = new BigDecimal("2.79");

        assertEquals(2.63, service.getMidpoint(low, high).doubleValue(), DELTA);
    }

    @Test
    public void givenGrossAmount_whenGetStockbrokersCommission_thenShouldReturnTheStockbrokersCommission()
    {
        BigDecimal grossAmount = new BigDecimal("4546902");

        BigDecimal commissionBigDecimal = service.getStockbrokersCommission(grossAmount);
        double commission = getDoubleRoundedDownDecimalToTwoPlaces(commissionBigDecimal);
        assertEquals(11_367.25, commission, DELTA);
    }

    @Test
    public void givenGrossAmount_whenGetVatOfCommission_thenShouldReturnTheVatOfCommission()
    {
        BigDecimal grossAmount = new BigDecimal("4546902");
        BigDecimal commission = service.getStockbrokersCommission(grossAmount).setScale(ROUNDING_SCALE, BigDecimal.ROUND_HALF_EVEN);

        BigDecimal commissionVatBigDecimal = service.getVatOfCommission(commission);
        double commissionVat = getDoubleRoundedDownDecimalToTwoPlaces(commissionVatBigDecimal);
        assertEquals(1_364.07, commissionVat, DELTA);
    }

    @Test
    public void givenGrossAmount_whenGetClearingFee_thenShouldReturnTheClearingFee()
    {
        BigDecimal grossAmount = new BigDecimal("4546902");

        BigDecimal clearingFeeBigDecimal = service.getClearingFee(grossAmount);
        double clearingFee = getDoubleRoundedDownDecimalToTwoPlaces(clearingFeeBigDecimal);
        assertEquals(454.69, clearingFee, DELTA);
    }

    @Test
    public void givenGrossAmount_whenGetTransactionFee_thenShouldReturnTheTransactionFee()
    {
        BigDecimal grossAmount = new BigDecimal("4546902");

        BigDecimal transactionFeeBigDecimal = service.getTransactionFee(grossAmount);
        double transactionFee = getDoubleRoundedDownDecimalToTwoPlaces(transactionFeeBigDecimal);
        assertEquals(227.34, transactionFee, DELTA);
    }

    @Test
    public void givenGrossAmount_whenGetSalesTax_thenShouldReturnTheSalesTax()
    {
        BigDecimal grossAmount = new BigDecimal("4546902");

        BigDecimal salesTaxBigDecimal = service.getSalesTax(grossAmount);
        double salesTax = getDoubleRoundedDownDecimalToTwoPlaces(salesTaxBigDecimal);
        assertEquals(27_281.41, salesTax, DELTA);
    }

    @Test
    public void givenCurrentPriceAndPercentChange_whenGetChangeBetweenCurrentAndPreviousPrice_thenShouldReturnTheChangeBetweenCurrentAndPreviousPrice()
    {
        double currentPrice = 13.5;
        double percentChange = 8.47;

        BigDecimal changeBigDecimal = service.getChangeBetweenCurrentAndPreviousPrice(currentPrice, percentChange);
        double change = getDoubleRoundedDownDecimalToTwoPlaces(changeBigDecimal);
        assertEquals(1.14, change, DELTA);
    }

    @Test
    public void givenCurrentPriceAndNegativePercentChange_whenGetChangeBetweenCurrentAndPreviousPrice_thenShouldReturnTheNegativeChangeBetweenCurrentAndPreviousPrice()
    {
        double currentPrice = 13.5;
        double percentChange = -8.47;

        BigDecimal changeBigDecimal = service.getChangeBetweenCurrentAndPreviousPrice(currentPrice, percentChange);
        double change = getDoubleRoundedDownDecimalToTwoPlaces(changeBigDecimal);
        assertEquals(-1.14, change, DELTA);
    }

    @Test
    public void givenCurrentPriceAndNegativePercentChange_whenGetPreviousPrice_thenShouldReturnThePreviousPrice()
    {
        double currentPrice = 13.5;
        double percentChange = -8.47;

        BigDecimal previousBigDecimal = service.getPreviousPrice(currentPrice, percentChange);
        double previous = getDoubleRoundedDownDecimalToTwoPlaces(previousBigDecimal);
        assertEquals(14.64, previous, DELTA);
    }

    @Test
    public void givenCurrentPriceAndPercentChange_whenGetPreviousPrice_thenShouldReturnThePreviousPrice()
    {
        double currentPrice = 13.5;
        double percentChange = 8.47;

        BigDecimal previousBigDecimal = service.getPreviousPrice(currentPrice, percentChange);
        double previous = getDoubleRoundedDownDecimalToTwoPlaces(previousBigDecimal);
        assertEquals(12.35, previous, DELTA);
    }

    @Test
    public void givenDate1AndDate2SameYears_whenGetDaysBetween_thenShouldReturnTheDaysBetween()
    {
        Date date1 = UnitTestUtils.newDate(2017, 12, 31);
        Date date2 = UnitTestUtils.newDate(2018, 2, 28);

        assertEquals(59, service.getDaysBetween(date1, date2));
    }

    @Test
    public void givenDate2AndDate1SameYears_whenGetDaysBetween_thenShouldReturnTheDaysBetween()
    {
        Date date2 = UnitTestUtils.newDate(2018, 2, 28);
        Date date1 = UnitTestUtils.newDate(2017, 12, 31);

        assertEquals(59, service.getDaysBetween(date2, date1));
    }

    @Test
    public void givenDate1AndDate2DifferentYears_whenGetDaysBetween_thenShouldReturnTheDaysBetween()
    {
        Date date1 = UnitTestUtils.newDate(2017, 12, 31);
        Date date2 = UnitTestUtils.newDate(2018, 1, 1);

        assertEquals(1, service.getDaysBetween(date1, date2));
    }

    @Test
    public void givenDate1AndDate2SameDateDifferentTime_whenGetDaysBetween_thenShouldReturnOneDay()
    {
        Date date1 = UnitTestUtils.newDateTime(2018, 1, 1, 1, 1, 1);
        Date date2 = UnitTestUtils.newDate(2018, 1, 1);
        assertEquals(1, service.getDaysBetween(date1, date2));
    }

    @Test
    public void givenDate1AndDate2SameDateSameTime_whenGetDaysBetween_thenShouldReturnZeroDays()
    {
        Date date1 = UnitTestUtils.newDate(2018, 1, 1);
        Date date2 = UnitTestUtils.newDate(2018, 1, 1);
        assertEquals(0, service.getDaysBetween(date1, date2));
    }

    private double getDoubleRoundedDownDecimalToTwoPlaces(BigDecimal bigDecimal)
    {
        return bigDecimal.setScale(ROUNDING_SCALE, BigDecimal.ROUND_DOWN).doubleValue();
    }
}