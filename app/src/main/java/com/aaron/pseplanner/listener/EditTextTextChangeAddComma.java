package com.aaron.pseplanner.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

/**
 * Created by Aaron on 12/3/2016.
 */
public class EditTextTextChangeAddComma implements TextWatcher
{
    private EditText editText;
    private final DecimalFormat formatter;
    private int max;
    private String oldInput;

    public EditTextTextChangeAddComma(EditText editText, int maxIntegerDigits)
    {
        this.editText = editText;
        this.formatter = new DecimalFormat("#,###.####");

        // Divide by three because there would be a comma for every 3 digits
        this.max = maxIntegerDigits - ((maxIntegerDigits / 3) - (maxIntegerDigits % 3 == 0 ? 1 : 0));
        this.formatter.setMaximumIntegerDigits(this.max);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        if(exceedsMaxLength(s.toString()))
        {
            oldInput = s.toString();
        }
        else
        {
            oldInput = "";
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        /** No implementation. */
    }

    /**
     * Adds comma to separate the thousands and rounds off decimal to 4 places of the inputted number in the edit-text.
     */
    @Override
    public void afterTextChanged(Editable view)
    {
        this.editText.removeTextChangedListener(this);
        String input = view.toString();

        if(StringUtils.isNotBlank(input))
        {
            int cursorPosition = this.editText.getSelectionStart();

            // remove all commas if it is previously formatted
            input = StringUtils.replace(input, ",", "");
            if(oldInput.length() <= 0 || !exceedsMaxLength(input))
            {
                this.editText.setText(formatNumber(input));
            }
            else
            {
                // If oldInput has content, then max length is reached. Set oldInput to stop additional inputs.
                this.editText.setText(this.oldInput);
            }

            int editTextLength = this.editText.getText().length();
            if(cursorPosition >= (editTextLength - 1))
            {
                // place the cursor at the end of text
                this.editText.setSelection(editTextLength);
            }
            else // TODO: Some scenarios not covered
            {
                // retain cursor position, if this is not present the cursor will move to 0th position
                this.editText.setSelection(cursorPosition);
            }
        }

        this.editText.addTextChangedListener(this);
    }

    private boolean exceedsMaxLength(String input)
    {
        int wholeNumberCount;
        int decimalIndex = input.indexOf('.');
        if(decimalIndex != -1)
        {
            wholeNumberCount = input.substring(0, decimalIndex).length();
        }
        else
        {
            wholeNumberCount = input.length();
        }
        return wholeNumberCount > this.max;
    }

    private String formatNumber(String input)
    {
        String formattedInput;
        try
        {
            double number = Double.parseDouble(input);
            formattedInput = this.formatter.format(number);

            // append '.' to be able to continue adding decimal places
            if(input.endsWith("."))
            {
                formattedInput += ".";
            }
        }
        catch(NumberFormatException e)
        {
            formattedInput = "";
        }

        return formattedInput;
    }
}
