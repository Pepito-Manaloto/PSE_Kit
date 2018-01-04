package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.UnitTestUtils;
import com.aaron.pseplanner.service.CalculatorService;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * Test for CalculatorService.
 */
public class DefaultCalculatorServiceTest
{
    private static final double DELTA = 0.0001;
    private CalculatorService service;

    @Before
    public void init()
    {
        this.service = new DefaultCalculatorService();
    }

    @Test
    public void getBuyGrossAmountTest()
    {
        assertEquals(113_200, service.getBuyGrossAmount(new BigDecimal("11.32"), 10_000).doubleValue(), DELTA);
    }

    @Test
    public void getBuyNetAmountTest()
    {
        assertEquals(404_188.85, service.getBuyNetAmount(new BigDecimal("0.31"), 1_300_000).doubleValue(), DELTA);
    }

    @Test
    public void getAveragePriceAfterBuyTest()
    {
        assertEquals(2.8584, service.getAveragePriceAfterBuy(new BigDecimal("2.85")).doubleValue(), DELTA);
    }

    @Test
    public void getPriceToBreakEvenTest()
    {
        assertEquals(11.4029, service.getPriceToBreakEven(new BigDecimal("11.28")).doubleValue(), DELTA);
    }

    @Test
    public void getSellGrossAmountTest()
    {
        assertEquals(311_000, service.getSellGrossAmount(new BigDecimal("248.8"), 1250).doubleValue(), DELTA);
    }

    @Test
    public void getSellNetAmountTest()
    {
        assertEquals(308_527.55, service.getSellNetAmount(new BigDecimal("248.8"), 1250).doubleValue(), DELTA);
    }

    @Test
    public void getGainLossAmountTestPositive()
    {
        assertEquals(663_225.2489, service.getGainLossAmount(new BigDecimal("1.33"), 4_938_000, new BigDecimal("1.48")).doubleValue(), DELTA);
    }

    @Test
    public void getGainLossAmountTestNegative()
    {
        assertEquals(-267_535.9020, service.getGainLossAmount(new BigDecimal("1.33"), 4_938_000, new BigDecimal("1.29")).doubleValue(), DELTA);
    }

    @Test
    public void getPercentGainLossTestPositive()
    {
        BigDecimal percentGainLoss = service.getPercentGainLoss(new BigDecimal("1.33"), 4_938_000, new BigDecimal("1.48")).setScale(2,
                BigDecimal.ROUND_HALF_EVEN);
        assertEquals(10.07, percentGainLoss.doubleValue(), DELTA);
    }

    @Test
    public void getPercentGainLossTestNegative()
    {
        BigDecimal percentGainLoss = service.getPercentGainLoss(new BigDecimal("1.33"), 4_938_000, new BigDecimal("1.29")).setScale(2,
                BigDecimal.ROUND_HALF_EVEN);
        assertEquals(-4.06, percentGainLoss.doubleValue(), DELTA);
    }

    @Test
    public void getRiskRewardRatioTest()
    {
        BigDecimal entryPrice = new BigDecimal("0.038");
        BigDecimal targetPrice = new BigDecimal("0.05");
        BigDecimal cutlossPrice = new BigDecimal("0.035");

        BigDecimal riskRewardRatio = service.getRiskRewardRatio(entryPrice, targetPrice, cutlossPrice).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(3.39, riskRewardRatio.doubleValue(), DELTA);
    }

    @Test
    public void getDividendYieldTest()
    {
        assertEquals(221163.80, service.getDividendYield(89_540, new BigDecimal("2.47")).doubleValue(), DELTA);
    }

    @Test
    public void getPercentDividendYieldTest()
    {
        BigDecimal price = new BigDecimal("87.7");
        BigDecimal cashDividend = new BigDecimal("2.47");
        BigDecimal percentDividendYield = service.getPercentDividendYield(price, 89_540, cashDividend).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(2.82, percentDividendYield.doubleValue(), DELTA);
    }

    @Test
    public void getMidpointTest()
    {
        assertEquals(2.63, service.getMidpoint(new BigDecimal("2.79"), new BigDecimal("2.47")).doubleValue(), DELTA);
    }

    @Test
    public void getStockbrokersCommissionTest()
    {
        BigDecimal commission = service.getStockbrokersCommission(new BigDecimal("4546902")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(11_367.26, commission.doubleValue(), DELTA);
    }

    @Test
    public void getVatOfCommissionTest()
    {
        BigDecimal commission = service.getStockbrokersCommission(new BigDecimal("4546902")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal commissionVat = service.getVatOfCommission(commission).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(1_364.07, commissionVat.doubleValue(), DELTA);
    }

    @Test
    public void getClearingFeeTest()
    {
        BigDecimal clearingFee = service.getClearingFee(new BigDecimal("4546902")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(454.69, clearingFee.doubleValue(), DELTA);
    }

    @Test
    public void getTransactionFeeTest()
    {
        BigDecimal transactionFee = service.getTransactionFee(new BigDecimal("4546902")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(227.35, transactionFee.doubleValue(), DELTA);
    }

    @Test
    public void getSalesTaxTest()
    {
        BigDecimal salesTax = service.getSalesTax(new BigDecimal("4546902")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(22_734.51, salesTax.doubleValue(), DELTA);
    }

    @Test
    public void getChangeBetweenCurrentAndPreviousTestPrice()
    {
        BigDecimal change = service.getChangeBetweenCurrentAndPreviousPrice(13.5, 8.47).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(1.14, change.doubleValue(), DELTA);
    }

    @Test
    public void getChangeBetweenCurrentAndPreviousPriceTestNegative()
    {
        BigDecimal change = service.getChangeBetweenCurrentAndPreviousPrice(13.5, -8.47).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(-1.14, change.doubleValue(), DELTA);
    }

    @Test
    public void getPreviousPriceTestNegative()
    {
        BigDecimal previous = service.getPreviousPrice(13.5, -8.47).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(14.64, previous.doubleValue(), DELTA);
    }

    @Test
    public void getPreviousPriceTest()
    {
        BigDecimal previous = service.getPreviousPrice(13.5, 8.47).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(12.36, previous.doubleValue(), DELTA);
    }

    @Test
    public void getDaysBetweenTest()
    {
        assertEquals(59, service.getDaysBetween(UnitTestUtils.newDate(2017, 12, 31), UnitTestUtils.newDate(2018, 2, 28)));
    }

    @Test
    public void getDaysBetweenTestOne()
    {
        assertEquals(1, service.getDaysBetween(UnitTestUtils.newDate(2017, 12, 31), UnitTestUtils.newDate(2018, 1, 1)));
    }

    @Test
    public void getDaysBetweenTestIntraDay()
    {
        assertEquals(1, service.getDaysBetween(UnitTestUtils.newDateTime(2018, 1, 1, 1, 1, 1), UnitTestUtils.newDate(2018, 1, 1)));
    }

    @Test
    public void getDaysBetweenTestZero()
    {
        assertEquals(0, service.getDaysBetween(UnitTestUtils.newDate(2018, 1, 1), UnitTestUtils.newDate(2018, 1, 1)));
    }
}