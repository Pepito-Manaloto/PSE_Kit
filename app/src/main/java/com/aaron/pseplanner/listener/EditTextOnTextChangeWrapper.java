package com.aaron.pseplanner.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by aaron.asuncion on 1/24/2017.
 * Wrapper for any EditTextOnTextChange listeners.
 * Removes and adds back textChange listener on afterTextChanged() to prevent recursive infinite loop.
 */
public class EditTextOnTextChangeWrapper implements TextWatcher
{
    private EditText editText;
    private TextWatcher textWatcher;

    public EditTextOnTextChangeWrapper(EditText editText, TextWatcher textWatcher)
    {
        this.editText = editText;
        this.textWatcher = textWatcher;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        this.textWatcher.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        this.textWatcher.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        // Remove listener first to prevent recursive infinite calls, because the EditText will be modified.
        this.editText.removeTextChangedListener(this);

        this.textWatcher.afterTextChanged(s);

        // Add back listener after EditText modification
        this.editText.addTextChangedListener(this);
    }
}
