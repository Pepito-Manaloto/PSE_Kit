package com.aaron.pseplanner.response.phisix;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Aaron on 03/12/2017.
 */

public class ResponsePhisixStockWrapperTest
{
    @Test
    public void getResponseStockTestNull()
    {
        ResponsePhisixStockWrapper response = new ResponsePhisixStockWrapper();
        assertNull(response.getResponseStock());
    }

    @Test
    public void getResponseStockTestEmpty()
    {
        ResponsePhisixStockWrapper response = new ResponsePhisixStockWrapper();
        response.setResponsePhisixStocksList(Collections.<ResponsePhisixStock> emptyList());
        assertNull(response.getResponseStock());
    }

    @Test
    public void getResponseStockTest()
    {
        ResponsePhisixStockWrapper response = new ResponsePhisixStockWrapper();
        ResponsePhisixStock expected = new ResponsePhisixStock("PSE", new ResponsePrice(), 0, 0, null);
        response.setResponsePhisixStocksList(Arrays.asList(expected,
                new ResponsePhisixStock("two", new ResponsePrice(), 0, 0, null),
                new ResponsePhisixStock("three", new ResponsePrice(), 0, 0, null)));
        assertEquals(expected, response.getResponseStock());
    }
}
