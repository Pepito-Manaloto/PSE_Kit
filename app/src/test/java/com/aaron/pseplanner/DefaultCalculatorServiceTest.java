package com.aaron.pseplanner;

import com.aaron.pseplanner.service.CalculatorService;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DefaultCalculatorServiceTest
{
    private CalculatorService service;

    @Before
    public void init()
    {
        //this.service = new DefaultCalculatorService();
    }

    @Test
    public void addition_isCorrect() throws Exception
    {
        assertEquals(4, 2 + 2);
    }
}