package com.aaron.pseplanner;

import android.app.Activity;

import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.implementation.FormatServiceImpl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for FormatService.
 */
public class FormatServiceImplTest
{
    //@Mock
    private Activity activity;

    private FormatService service;

    @Before
    public void init()
    {
        //this.service = new FormatServiceImpl();
    }

    @Test
    public void formatPrice() throws Exception
    {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void formatStockPrice() throws Exception
    {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void formatTextColor() throws Exception
    {
        assertEquals(4, 2 + 2);
    }
}