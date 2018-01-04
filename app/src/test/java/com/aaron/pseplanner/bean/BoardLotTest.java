package com.aaron.pseplanner.bean;

import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Aaron on 04/01/2018.
 */

public class BoardLotTest
{
    @Test
    public void isValidBoardLotTestValid()
    {
        BigDecimal price = new BigDecimal("13.42");
        long shares = 10_300;
        assertTrue(BoardLot.isValidBoardLot(price, shares));
    }

    @Test
    public void isValidBoardLotTestInvalidPrice()
    {
        BigDecimal price = new BigDecimal("13.41");
        long shares = 10_300;
        assertFalse(BoardLot.isValidBoardLot(price, shares));
    }

    @Test
    public void isValidBoardLotTestInvalidShares()
    {
        BigDecimal price = new BigDecimal("13.42");
        long shares = 10_320;
        assertFalse(BoardLot.isValidBoardLot(price, shares));
    }

    @Test
    public void isValidBoardLotTestPriceNull()
    {
        long shares = 10_000;
        assertFalse(BoardLot.isValidBoardLot(null, shares));
    }

    @Test
    public void isValidBoardLotTestPriceNegative()
    {
        BigDecimal price = new BigDecimal("-13.42");
        long shares = 10_000;
        assertFalse(BoardLot.isValidBoardLot(price, shares));
    }

    @Test
    public void isValidBoardLotTestPriceZero()
    {
        BigDecimal price = BigDecimal.ZERO;
        long shares = 10_000;
        assertFalse(BoardLot.isValidBoardLot(price, shares));
    }

    @Test
    public void isValidBoardLotTestSharesNegative()
    {
        BigDecimal price = new BigDecimal("13.42");
        long shares = -10_300;
        assertFalse(BoardLot.isValidBoardLot(price, shares));
    }

    @Test
    public void isValidBoardLotTestSharesZero()
    {
        BigDecimal price = new BigDecimal("13.42");
        assertFalse(BoardLot.isValidBoardLot(price, 0));
    }

    @Test
    public void priceWithinRangeTestTrue()
    {
        BigDecimal price = new BigDecimal("356.4");
        assertTrue(BoardLot.TWO_HUNDRED.priceWithinRange(price));
    }

    @Test
    public void priceWithinRangeTestFalse()
    {
        BigDecimal price = new BigDecimal("556.8");
        assertFalse(BoardLot.TWO_HUNDRED.priceWithinRange(price));
    }

    @Test
    public void priceWithinRangeTestNull()
    {
        assertFalse(BoardLot.TWO_HUNDRED.priceWithinRange(null));
    }
}
