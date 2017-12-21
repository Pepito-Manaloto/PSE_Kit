package com.aaron.pseplanner;

import android.os.Build;
import android.text.InputFilter;
import android.widget.EditText;

import com.aaron.pseplanner.service.ViewUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for ViewUtils.
 */
public class ViewUtilsTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getEditTextMaxLengthTest() throws Exception
    {
        UnitTestUtils.setFinalStatic(Build.VERSION.class.getField("SDK_INT"), Short.MAX_VALUE);

        int maxLength = 20;
        EditText editText = mock(EditText.class);
        InputFilter.LengthFilter filter = mock(InputFilter.LengthFilter.class);

        when(filter.getMax()).thenReturn(maxLength);
        when(editText.getFilters()).thenReturn(new InputFilter[] { filter });

        assertEquals(maxLength, ViewUtils.getEditTextMaxLength(editText.getFilters(), null));
    }

    @Test
    public void getOrdinalNumberTestNegative()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Number cannot be less than zero");
        ViewUtils.getOrdinalNumber(-1);
    }

    @Test
    public void getOrdinalNumberTestZero()
    {
        assertEquals("1st", ViewUtils.getOrdinalNumber(0));
    }

    @Test
    public void getOrdinalNumberTestOne()
    {
        assertEquals("2nd", ViewUtils.getOrdinalNumber(1));
    }

    @Test
    public void getOrdinalNumberTestTwo()
    {
        assertEquals("3rd", ViewUtils.getOrdinalNumber(2));
    }

    @Test
    public void getOrdinalNumberTest_GreaterThanTwo()
    {
        for(int i = 3; i < 100; i++)
        {
            int expectedNumber = i + 1;
            assertEquals(expectedNumber + "th", ViewUtils.getOrdinalNumber(i));
        }
    }

    @Test
    public void addPositiveSignTestZero()
    {
        String text = "123";
        assertEquals(text, ViewUtils.addPositiveSign(0, text));
    }

    @Test
    public void addPositiveSignTestNegative()
    {
        String text = "123";
        assertEquals(text, ViewUtils.addPositiveSign(-1, text));
    }

    @Test
    public void addPositiveSignTest()
    {
        String text = "123";
        assertEquals("+" + text, ViewUtils.addPositiveSign(10, text));
    }
}
