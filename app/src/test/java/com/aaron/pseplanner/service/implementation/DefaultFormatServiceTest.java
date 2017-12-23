package com.aaron.pseplanner.service.implementation;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.UnitTestUtils;
import com.aaron.pseplanner.service.FormatService;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for FormatService.
 */
public class DefaultFormatServiceTest
{
    private FormatService service;
    private static final int GREEN = R.color.darkGreen;

    @Before
    public void init()
    {
        Activity activity = mock(Activity.class);
        when(activity.getColor(anyInt())).thenReturn(GREEN);
        Resources resources = mock(Resources.class);
        when(activity.getResources()).thenReturn(resources);
        when(resources.getColor(anyInt())).thenReturn(GREEN);

        this.service = new DefaultFormatService(activity);
    }

    @Test
    public void formatPriceTestZero()
    {
        assertEquals("0", this.service.formatPrice(0));
    }

    @Test
    public void formatPriceTestNegative()
    {
        assertEquals("-10,000,456.12", this.service.formatPrice(-10000456.12934678));
    }

    @Test
    public void formatPriceTestPositive()
    {
        assertEquals("10,000,456.12", this.service.formatPrice(10000456.12934678));
    }

    @Test
    public void formatStockPriceTestZero()
    {
        assertEquals("0", this.service.formatStockPrice(0));
    }

    @Test
    public void formatStockPriceTestNegative()
    {
        assertEquals("-10,000.1234", this.service.formatStockPrice(-10000.12349678));
    }

    @Test
    public void formatStockPriceTestPositive()
    {
        assertEquals("10,000.1234", this.service.formatStockPrice(10000.12349678));
    }

    @Test
    public void formatPercentTestZero()
    {
        assertEquals("0%", this.service.formatPercent(0));
    }

    @Test
    public void formatPercentTestNegative()
    {
        assertEquals("-10.12%", this.service.formatPercent(-10.12934678));
    }

    @Test
    public void formatPercentTestPositive()
    {
        assertEquals("10.12%", this.service.formatPercent(10.12934678));
    }

    @Test
    public void formatSharesTestZero()
    {
        assertEquals("0", this.service.formatShares(0));
    }

    @Test
    public void formatSharesTestNegative()
    {
        assertEquals("3,120,000,456", this.service.formatShares(-3120000456L));
    }

    @Test
    public void formatSharesTestPositive()
    {
        assertEquals("3,120,000,456", this.service.formatShares(3120000456L));
    }

    @Test
    public void formatTextColorTestZero()
    {
        TextView view = mock(TextView.class);

        this.service.formatTextColor(0, view);
        verify(view, times(1)).setTextColor(Color.BLACK);
    }

    @Test
    public void formatTextColorTestPositive()
    {
        TextView view = mock(TextView.class);

        this.service.formatTextColor(1, view);
        verify(view, times(1)).setTextColor(GREEN);
    }

    @Test
    public void formatTextColorTestNegative()
    {
        TextView view = mock(TextView.class);

        this.service.formatTextColor(-1, view);
        verify(view, times(1)).setTextColor(Color.RED);
    }

    @Test
    public void formatDateTest()
    {
        Date date = UnitTestUtils.newDate(2000, 12, 25);
        assertEquals("December 25, 2000", this.service.formatDate(date));
    }

    @Test
    public void formatLastUpdatedTest()
    {
        Date date = UnitTestUtils.newDateTime(2018, 11, 15, 14, 33, 52);
        assertEquals("November 15, Thursday 02:33:52 PM", this.service.formatLastUpdated(date));
    }

    @Test
    public void formatLastUpdatedTest_LosAngelesTimezone()
    {
        Date date = UnitTestUtils.newDateTime(2018, 4, 22, 10, 6, 11, TimeZone.getTimeZone("America/Los_Angeles"));
        assertEquals("April 23, Monday 01:06:11 AM", this.service.formatLastUpdated(date));
    }

    @Test
    public void formatLastUpdatedTest_SydneyTimezone()
    {
        Date date = UnitTestUtils.newDateTime(2018, 2, 1, 2, 57, 8, TimeZone.getTimeZone("Australia/Sydney"));
        assertEquals("January 31, Wednesday 11:57:08 PM", this.service.formatLastUpdated(date));
    }
}