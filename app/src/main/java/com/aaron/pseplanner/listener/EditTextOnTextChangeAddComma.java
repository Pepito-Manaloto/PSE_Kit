package com.aaron.pseplanner.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

import static com.aaron.pseplanner.service.implementation.TradePlanFormatService.STOCK_PRICE_FORMAT;

/**
 * Created by Aaron on 12/3/2016.
 * Adds commas on the inputted number in the EditText.
 * Can be decorated with another TextWatcher, which will be called first before this class.
 */
public class EditTextOnTextChangeAddComma implements TextWatcher
{
    private static final String DECIMAL_NUMBER_WITH_ONLY_ZERO_DECIMAL_POINT_PATTERN = "\\d+\\.0*";
    private static final String COMMA = ",";
    private static final int DECIMAL_POINT = '.';
    private static final int NUMBER_OF_DIGITS_PER_COMMA = 3;

    private EditText editText;
    private final DecimalFormat formatter;
    private int max;
    private String oldInput;
    private TextWatcher textWatcher;

    public EditTextOnTextChangeAddComma(EditText editText, int maxIntegerDigits)
    {
        this.editText = editText;
        this.formatter = new DecimalFormat(STOCK_PRICE_FORMAT);

        this.max = computeMaximumInputDigits(maxIntegerDigits);
        this.formatter.setMaximumIntegerDigits(this.max);
    }

    public EditTextOnTextChangeAddComma(EditText editText, int maxIntegerDigits, TextWatcher textWatcher)
    {
        this(editText, maxIntegerDigits);
        this.textWatcher = textWatcher;
    }

    private int computeMaximumInputDigits(int maxLength)
    {
        // If digits is divisible by NUMBER_OF_DIGITS_PER_COMMA, then reduce the number of commas by 1.
        // Because there must be at least one digit preceeding a comma.
        /*
          Equation is defined by: (let maxInputDigits = x)
          1) maxLength = x + ((x / numberOfDigitsPerComma) - commasSubtrahend)
          2) maxLength = x + ((x / 3) - 1) --> x + 1/3x, which is 4/3x
          3) maxLength = 4/3x - 1
          4) 4/3x = maxLength + 1
          5) (3/4) * 4/3x = maxLength + 1 * (3/4) --> cancel out
          6) x = (3 * maxLength + 1) / 4
         */
        return (NUMBER_OF_DIGITS_PER_COMMA * (maxLength + 1)) / 4;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        if(textWatcher != null)
        {
            textWatcher.beforeTextChanged(s, start, count, after);
        }

        setOldInput(s.toString());
    }

    private void setOldInput(String text)
    {
        if(exceedsMaxLength(text))
        {
            oldInput = text;
        }
        else
        {
            oldInput = "";
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        if(textWatcher != null)
        {
            textWatcher.onTextChanged(s, start, before, count);
        }

        /* No implementation. */
    }

    /**
     * Adds comma to separate the thousands and rounds off decimal to 4 places of the inputted number in the edit-text.
     */
    @Override
    public void afterTextChanged(Editable view)
    {
        if(textWatcher != null)
        {
            textWatcher.afterTextChanged(view);
        }

        String input = view.toString();

        if(StringUtils.isNotBlank(input))
        {
            int cursorPosition = this.editText.getSelectionStart();
            int oldLength = input.length();

            updateEditText(input);
            updateEditTextCursor(cursorPosition, oldLength);
        }
    }

    private String removeAllCommas(String input)
    {
        return StringUtils.replace(input, COMMA, "");
    }

    private void updateEditText(String input)
    {
        boolean oldInputIsEmpty = oldInput == null || oldInput.length() <= 0;
        boolean oldInputIsEmptyOrInputDoesNotExceedMaxLength = oldInputIsEmpty || !exceedsMaxLength(input);

        if(oldInputIsEmptyOrInputDoesNotExceedMaxLength)
        {
            this.editText.setText(formatNumber(input));
        }
        else
        {
            // If oldInput has content, then max length is reached. Set oldInput to stop additional inputs.
            this.editText.setText(this.oldInput);
        }
    }

    private void updateEditTextCursor(int cursorPosition, int oldLength)
    {
        int editTextLength = this.editText.getText().length();
        boolean cursorPositionExceedsInputLength = cursorPosition >= (editTextLength - 1);
        int oldInputNewInputLengthDifference = editTextLength - oldLength;

        if(cursorPositionExceedsInputLength)
        {
            // place the cursor at the end of text
            this.editText.setSelection(editTextLength);
        }
        else if(oldInputNewInputLengthDifference > 0)
        {
            this.editText.setSelection(cursorPosition + 1);
        }
        else if(cursorPosition > 0 && oldInputNewInputLengthDifference < 0)
        {
            this.editText.setSelection(cursorPosition - 1);
        }
        else
        {
            // retain cursor position, if this is not present the cursor will move to 0th position
            this.editText.setSelection(cursorPosition);
        }
    }

    /**
     * Check if the number of digits in the given whole number input exceeds the maximum.
     */
    private boolean exceedsMaxLength(String input)
    {
        String inputWithoutCommas = removeAllCommas(input);
        int wholeNumberCount = getWholeNumberCount(inputWithoutCommas);

        return wholeNumberCount > this.max;
    }

    private int getWholeNumberCount(String input)
    {
        int wholeNumberCount;
        int decimalIndex = input.indexOf(DECIMAL_POINT);
        boolean decimalPointDoesNotExist = decimalIndex != -1;
        if(decimalPointDoesNotExist)
        {
            wholeNumberCount = input.substring(0, decimalIndex).length();
        }
        else
        {
            wholeNumberCount = input.length();
        }

        return wholeNumberCount;
    }

    /**
     * Formats the given input/number in "#,###.####" format.
     */
    private String formatNumber(String input)
    {
        String formattedInput = input;
        String inputWithoutCommas = removeAllCommas(input);
        try
        {
            // Stop formatting if period or 0 decimal value is inputted.
            if(!input.matches(DECIMAL_NUMBER_WITH_ONLY_ZERO_DECIMAL_POINT_PATTERN))
            {
                double number = Double.parseDouble(inputWithoutCommas);
                formattedInput = this.formatter.format(number);
            }
        }
        catch(NumberFormatException e)
        {
            formattedInput = "";
        }

        return formattedInput;
    }
}
