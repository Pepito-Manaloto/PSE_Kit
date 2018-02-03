package com.aaron.pseplanner;

import android.os.Build;
import android.text.InputFilter;
import android.widget.EditText;

import com.aaron.pseplanner.service.ViewUtils;
import com.aaron.pseplanner.test.utils.UnitTestUtils;

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
    public void givenEditText_whenGetEditTextMaxLength_thenShouldReturnMaxLength() throws Exception
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
    public void givenNegativeNumber_whenGetOrdinalNumber_thenShouldThrowIllegalArgumentExceptionWithMessage()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Number cannot be less than zero");
        ViewUtils.getOrdinalNumber(-1);
    }

    @Test
    public void givenNumberZero_whenGetOrdinalNumber_thenShouldReturnFirstOrdinalNumber()
    {
        assertEquals("1st", ViewUtils.getOrdinalNumber(0));
    }

    @Test
    public void givenNumberOne_whenGetOrdinalNumber_thenShouldReturnSecondOrdinalNumber()
    {
        assertEquals("2nd", ViewUtils.getOrdinalNumber(1));
    }

    @Test
    public void givenNumberThree_whenGetOrdinalNumber_thenShouldReturnThirdOrdinalNumber()
    {
        assertEquals("3rd", ViewUtils.getOrdinalNumber(2));
    }

    @Test
    public void givenNumbersFromThreeToNinetyNine_whenGetOrdinalNumber_thenShouldReturnCorrectOrdinalNumbers()
    {
        for(int i = 3; i < 100; i++)
        {
            int expectedNumber = i + 1;
            assertEquals(expectedNumber + "th", ViewUtils.getOrdinalNumber(i));
        }
    }

    @Test
    public void givenZeroNumberAndText_whenCallingAddPositiveSign_thenShouldReturnUnchangedText()
    {
        String text = "123";
        assertEquals(text, ViewUtils.addPositiveSign(0, text));
    }

    @Test
    public void givenNegativeNumberAndText_whenCallingAddPositiveSign_thenShouldReturnUnchangedText()
    {
        String text = "123";
        assertEquals(text, ViewUtils.addPositiveSign(-1, text));
    }

    @Test
    public void givenPositiveNumberAndText_whenCallingAddPositiveSign_thenShouldReturnTextWithPlusSignAppended()
    {
        String text = "123";
        assertEquals("+" + text, ViewUtils.addPositiveSign(10, text));
    }
}
