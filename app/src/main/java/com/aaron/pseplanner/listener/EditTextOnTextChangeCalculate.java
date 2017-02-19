package com.aaron.pseplanner.listener;

import android.text.Editable;
import android.text.TextWatcher;

import com.aaron.pseplanner.service.InputCalculatorService;

/**
 * Created by aaron.asuncion on 12/29/2016.
 * Adds an event on edit text input changed, calculating the input using the given InputCalculatorService.
 * Can be decorated by providing a TextWatcher, which will be called first before this class.
 */
public class EditTextOnTextChangeCalculate implements TextWatcher
{
    private InputCalculatorService service;
    private TextWatcher textWatcher;

    public EditTextOnTextChangeCalculate(InputCalculatorService service)
    {
        this.service = service;
    }

    public EditTextOnTextChangeCalculate(InputCalculatorService service, TextWatcher textWatcher)
    {
        this.service = service;
        this.textWatcher = textWatcher;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        if(textWatcher != null)
        {
            textWatcher.beforeTextChanged(s, start, count, after);
        }

        /** No implementation. */
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        if(textWatcher != null)
        {
            textWatcher.onTextChanged(s, start, before, count);
        }

        /** No implementation. */
    }

    @Override
    public void afterTextChanged(Editable view)
    {
        if(textWatcher != null)
        {
            textWatcher.afterTextChanged(view);
        }

        this.service.calculate();
    }
}
