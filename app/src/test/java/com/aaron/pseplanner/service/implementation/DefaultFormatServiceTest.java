package com.aaron.pseplanner.service.implementation;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.test.utils.UnitTestUtils;
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
    public void givenZeroPrice_whenFormatPrice_thenShouldReturnZero()
    {
        assertEquals("0", this.service.formatPrice(0));
    }

    @Test
    public void givenNegativePrice_whenFormatPrice_thenShouldReturnNegativePriceRoundedDownToTwoDecimalPlacesWithCommas()
    {
        assertEquals("-10,000,456.12", this.service.formatPrice(-10_000_456.12934678));
    }

    @Test
    public void givenPositivePrice_whenFormatPrice_thenShouldReturnPriceRoundedDownToTwoDecimalPlacesWithCommas()
    {
        assertEquals("10,000,456.12", this.service.formatPrice(10_000_456.12934678));
    }

    @Test
    public void givenZeroPrice_whenFormatStockPrice_thenShouldReturnZero()
    {
        assertEquals("0", this.service.formatStockPrice(0));
    }

    @Test
    public void givenNegativePrice_whenFormatStockPrice_thenShouldReturnNegativePriceRoundedDownToFourDecimalPlacesWithCommas()
    {
        assertEquals("-10,000.1234", this.service.formatStockPrice(-10000.12349678));
    }

    @Test
    public void givenPositivePrice_whenFormatStockPrice_thenShouldReturnPriceRoundedDownToFourDecimalPlacesWithCommas()
    {
        assertEquals("10,000.1234", this.service.formatStockPrice(10000.12349678));
    }

    @Test
    public void givenZeroPercent_whenFormatPercent_thenShouldReturnZeroNumberWithPercentSignAppended()
    {
        assertEquals("0%", this.service.formatPercent(0));
    }

    @Test
    public void givenNegativePercent_whenFormatPercent_thenShouldReturnTheNegativeNumberRoundedDownToTwoDecimalPlacesWithPercentSignAppended()
    {
        assertEquals("-10.12%", this.service.formatPercent(-10.12934678));
    }

    @Test
    public void givenPositivePercent_whenFormatPercent_thenShouldReturnTheNegativeNumberRoundedDownToTwoDecimalPlacesWithPercentSignAppended()
    {
        assertEquals("10.12%", this.service.formatPercent(10.12934678));
    }

    @Test
    public void givenZeroShares_whenFormatShares_thenShouldReturnZeroShares()
    {
        assertEquals("0", this.service.formatShares(0));
    }

    @Test
    public void givenNegativeShares_whenFormatShares_thenShouldReturnTheNegativeSharesWithCommas()
    {
        assertEquals("3,120,000,456", this.service.formatShares(-3120000456L));
    }

    @Test
    public void givenPositiveShares_whenFormatShares_thenShouldReturnTheNegativeSharesWithCommas()
    {
        assertEquals("3,120,000,456", this.service.formatShares(3120000456L));
    }

    @Test
    public void givenTextViewAndZeroPrice_whenFormatTextColor_thenSetTextColorIsCalledOnceWithBlackParameter()
    {
        TextView view = mock(TextView.class);

        this.service.formatTextColor(0, view);
        verify(view, times(1)).setTextColor(Color.BLACK);
    }

    @Test
    public void givenTextViewAndPositivePrice_whenFormatTextColor_thenSetTextColorIsCalledOnceWithGreenParameter()
    {
        TextView view = mock(TextView.class);

        this.service.formatTextColor(1, view);
        verify(view, times(1)).setTextColor(GREEN);
    }

    @Test
    public void givenTextViewAndNegativePrice_whenFormatTextColor_thenSetTextColorIsCalledOnceWithRedParameter()
    {
        TextView view = mock(TextView.class);

        this.service.formatTextColor(-1, view);
        verify(view, times(1)).setTextColor(Color.RED);
    }

    @Test
    public void givenDate_whenFormatDate_thenShouldReturnFormattedDate()
    {
        Date date = UnitTestUtils.newDate(2000, 12, 25);
        assertEquals("December 25, 2000", this.service.formatDate(date));
    }

    @Test
    public void givenDate_whenFormatLastUpdated_thenShouldReturnFormattedDate()
    {
        Date date = UnitTestUtils.newDateTime(2018, 11, 15, 14, 33, 52);
        assertEquals("November 15, Thursday 02:33:52 PM", this.service.formatLastUpdated(date));
    }

    @Test
    public void givenDateInLosAngelesAmericaTimeZone_whenFormatLastUpdated_thenShouldReturnFormattedDateInManilaPhilippinesTimeZone()
    {
        Date date = UnitTestUtils.newDateTime(2018, 4, 22, 10, 6, 11, TimeZone.getTimeZone("America/Los_Angeles"));
        assertEquals("April 23, Monday 01:06:11 AM", this.service.formatLastUpdated(date));
    }

    @Test
    public void givenDateInSydneyAustraliaTimeZone_whenFormatLastUpdated_thenShouldReturnFormattedDateInManilaPhilippinesTimeZone()
    {
        Date date = UnitTestUtils.newDateTime(2018, 2, 1, 2, 57, 8, TimeZone.getTimeZone("Australia/Sydney"));
        assertEquals("January 31, Wednesday 11:57:08 PM", this.service.formatLastUpdated(date));
    }
}