package com.aaron.pseplanner.listener;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.aaron.pseplanner.RobolectricTest;

import org.junit.Test;
import org.robolectric.Robolectric;

import static com.aaron.pseplanner.UnitTestUtils.getPrivateField;
import static com.aaron.pseplanner.UnitTestUtils.setPrivateField;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Aaron on 06/01/2018.
 */

public class EditTextOnTextChangeAddCommaTest extends RobolectricTest
{
    private EditTextOnTextChangeAddComma listener;
    private EditTextOnTextChangeAddComma decoratedListener;
    private TextWatcher decorator;
    private static final String OLD_INPUT = "oldInput";

    @Test
    public void givenListenerAndMaxLengthAndInput_whenBeforeTextChanged_thenOldInputShouldBeEmpty() throws Exception
    {
        int maxLength = 16;
        String input = "123,456,789,012";
        EditText editText = createEditText(input);

        givenListenerAndMaxLength(editText, maxLength);

        whenBeforeTextChanged(input);

        thenOldInputShouldBeEmpty(listener);
    }

    @Test
    public void givenListenerAndMaxLengthAndInputExceedsMaxLength_whenBeforeTextChanged_thenOldInputShouldBeEqualToTheInput()
            throws Exception
    {
        int maxLength = 16;
        String input = "8,123,456,789,012";
        EditText editText = createEditText(input);

        givenListenerAndMaxLength(editText, maxLength);
        givenOldInput(listener, "1,234,567");

        whenBeforeTextChanged(input);

        thenOldInputShouldBeEqualToTheInput(listener, input);
    }

    @Test
    public void givenListenerAndMaxLengthAndOldInputAndInput_whenAfterTextChanged_thenInputShouldBeFormattedAndCursorRetainsPosition() throws Exception
    {
        int cursorPosition = 3;
        int maxLength = 16;
        String oldInput = "123,456,789,012";
        String input = "7094276921";
        EditText editText = createEditText(input, cursorPosition);

        givenListenerAndMaxLength(editText, maxLength);
        givenOldInput(listener, oldInput);

        whenAfterTextChanged(editText);

        String expected = "7,094,276,921";
        thenInputShouldBeFormatted(editText, expected);
        thenCursorRetainsPosition(editText, cursorPosition);
    }

    @Test
    public void givenListenerAndMaxLengthAndOldInputAndInputExceedsMaxLength_whenAfterTextChanged_thenInputShouldBeEqualToTheOldInputAndCursorPositionAtTheEndOfText()
            throws Exception
    {
        int cursorPosition = 17;
        int maxLength = 16;
        String oldInput = "123,456,789,012";
        String input = "6,435,462,252,014";
        EditText editText = createEditText(input, cursorPosition);

        givenListenerAndMaxLength(editText, maxLength);
        givenOldInput(listener, oldInput);

        whenAfterTextChanged(editText);

        thenInputShouldBeEqualToTheOldInput(oldInput, editText.getText().toString());
        thenCursorPositionAtTheEndOfText(editText);
    }

    @Test
    public void givenDecoratedListenerAndMaxLengthAndInput_whenBeforeTextChanged_thenOldInputShouldBeEmptyAndTheDecoratorIsInvokedOnce() throws Exception
    {
        int maxLength = 16;
        String input = "123,456,789,012";
        EditText editText = createEditText(input);

        givenDecoratedListenerAndMaxLength(editText, maxLength);

        whenBeforeTextChanged(input);

        thenOldInputShouldBeEmpty(decoratedListener);
        thenTheDecoratorBeforeTextChangedIsInvokedOnce();
    }

    @Test
    public void givenDecoratedListenerAndMaxLengthAndInputExceedsMaxLength_whenBeforeTextChanged_thenOldInputShouldBeEqualToTheInputAndTheDecoratorIsInvokedOnce()
            throws Exception
    {
        int maxLength = 16;
        String input = "9,123,456,789,012";
        EditText editText = createEditText(input);

        givenDecoratedListenerAndMaxLength(editText, maxLength);
        givenOldInput(decoratedListener, "1,234,567");

        whenBeforeTextChanged(input);

        thenOldInputShouldBeEqualToTheInput(decoratedListener, input);
        thenTheDecoratorBeforeTextChangedIsInvokedOnce();
    }

    @Test
    public void givenDecoratedListener_whenOnTextChanged_thenTheDecoratorIsInvokedOnce()
    {
        int maxLength = 16;
        String input = "123,456,789,012";
        EditText editText = createEditText(input);

        givenDecoratedListenerAndMaxLength(editText, maxLength);

        whenOnTextChanged(input);

        thenTheDecoratorOnTextChangedIsInvokedOnce();
    }

    @Test
    public void givenDecoratedListenerAndMaxLengthAndOldInputAndInput_whenAfterTextChanged_thenInputShouldBeFormattedAndCursorRetainsPositionAndTheDecoratorIsInvokedOnce()
            throws Exception
    {
        int cursorPosition = 3;
        int maxLength = 12;
        String oldInput = "456,789,012";
        String input = "594276921";
        EditText editText = createEditText(input, cursorPosition);

        givenDecoratedListenerAndMaxLength(editText, maxLength);
        givenOldInput(decoratedListener, oldInput);

        whenAfterTextChanged(editText);

        String expected = "594,276,921";
        thenInputShouldBeFormatted(editText, expected);
        thenCursorRetainsPosition(editText, cursorPosition);
        thenTheDecoratorAfterTextChangedIsInvokedOnce();
    }

    @Test
    public void givenDecoratedListenerAndMaxLengthAndOldInputAndInputExceedsMaxLength_whenAfterTextChanged_thenInputShouldBeEqualToTheOldInputAndCursorPositionAtTheEndOfTextAndTheDecoratorIsInvokedOnce()
            throws Exception
    {
        int cursorPosition = 13;
        int maxLength = 10;
        String oldInput = "56,789,012";
        String input = "9,594,276,921";
        EditText editText = createEditText(input, cursorPosition);

        givenDecoratedListenerAndMaxLength(editText, maxLength);
        givenOldInput(decoratedListener, oldInput);

        whenAfterTextChanged(editText);

        thenInputShouldBeEqualToTheOldInput(editText.getText().toString(), oldInput);
        thenCursorPositionAtTheEndOfText(editText);
        thenTheDecoratorAfterTextChangedIsInvokedOnce();
    }

    private void givenListenerAndMaxLength(EditText editText, int maxLength)
    {
        listener = new EditTextOnTextChangeAddComma(editText, maxLength);
    }

    private void givenDecoratedListenerAndMaxLength(EditText editText, int maxLength)
    {
        decorator = mock(TextWatcher.class);
        decoratedListener = new EditTextOnTextChangeAddComma(editText, maxLength, decorator);
    }

    private void givenOldInput(EditTextOnTextChangeAddComma listener, String oldInput) throws Exception
    {
        setPrivateField(listener, OLD_INPUT, oldInput);
    }

    @NonNull
    private EditText createEditText(String input)
    {
        return createEditText(input, input.length());
    }

    @NonNull
    private EditText createEditText(String input, int cursorPosition)
    {
        AttributeSet attributeSet = Robolectric.buildAttributeSet()
                .addAttribute(android.R.attr.text, input)
                .build();

        EditText editText = new EditText(getContext(), attributeSet);
        editText.setSelection(cursorPosition);

        return editText;
    }

    private void whenAfterTextChanged(EditText editText)
    {
        Editable editable = editText.getText();

        if(listener != null)
        {
            listener.afterTextChanged(editable);
        }
        else
        {
            decoratedListener.afterTextChanged(editable);
        }
    }

    private void whenBeforeTextChanged(String input)
    {
        if(listener != null)
        {
            listener.beforeTextChanged(input, 0, 0, 0);
        }
        else
        {
            decoratedListener.beforeTextChanged(input, 0, 0, 0);
        }
    }

    private void whenOnTextChanged(String input)
    {
        if(listener != null)
        {
            listener.onTextChanged(input, 0, 0, 0);
        }
        else
        {
            decoratedListener.onTextChanged(input, 0, 0, 0);
        }
    }

    private void thenInputShouldBeEqualToTheOldInput(String input, String oldInput)
    {
        assertEquals(oldInput, input);
    }

    private void thenOldInputShouldBeEqualToTheInput(EditTextOnTextChangeAddComma listener, String input) throws Exception
    {
        String oldInput = (String) getPrivateField(listener, OLD_INPUT);
        assertEquals(input, oldInput);
    }

    private void thenOldInputShouldBeEmpty(EditTextOnTextChangeAddComma listener) throws Exception
    {
        String oldInput = (String) getPrivateField(listener, OLD_INPUT);
        assertEquals("", oldInput);
    }

    private void thenInputShouldBeFormatted(EditText editText, String expected)
    {
        String input = editText.getText().toString();
        assertEquals(expected, input);
    }

    private void thenCursorRetainsPosition(EditText editText, int cursorPosition)
    {
        assertEquals(cursorPosition, editText.getSelectionEnd());
    }

    private void thenCursorPositionAtTheEndOfText(EditText editText)
    {
        assertEquals(editText.getText().toString().length(), editText.getSelectionEnd());
    }

    private void thenTheDecoratorBeforeTextChangedIsInvokedOnce()
    {
        verify(decorator, times(1)).beforeTextChanged(any(CharSequence.class), anyInt(), anyInt(), anyInt());
    }

    private void thenTheDecoratorOnTextChangedIsInvokedOnce()
    {
        verify(decorator, times(1)).onTextChanged(any(CharSequence.class), anyInt(), anyInt(), anyInt());
    }

    private void thenTheDecoratorAfterTextChangedIsInvokedOnce()
    {
        verify(decorator, times(1)).afterTextChanged(any(Editable.class));
    }
}
