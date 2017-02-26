package com.aaron.pseplanner.fragment;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aaron.pseplanner.service.InputCalculatorService;
import com.aaron.pseplanner.listener.EditTextOnFocusChangeHideKeyboard;
import com.aaron.pseplanner.listener.EditTextOnTextChangeAddComma;
import com.aaron.pseplanner.listener.EditTextOnTextChangeCalculate;
import com.aaron.pseplanner.listener.EditTextOnTextChangeWrapper;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.ViewUtils;
import com.aaron.pseplanner.service.implementation.DefaultCalculatorService;
import com.aaron.pseplanner.service.implementation.DefaultFormatService;

/**
 * Created by aaron.asuncion on 1/24/2017.
 */

public abstract class AbstractCalculatorFragment extends Fragment implements InputCalculatorService
{
    protected CalculatorService calculatorService;
    protected FormatService formatService;

    /**
     * Initializes services and returns the fragment of the provided resId.
     */
    protected View inflateFragment(int resId, LayoutInflater inflater, ViewGroup parent)
    {
        View view = inflater.inflate(resId, parent, false);

        this.calculatorService = new DefaultCalculatorService();
        this.formatService = new DefaultFormatService(getActivity());

        return view;
    }

    /**
     * Sets the on focus change listener for edit texts. Will hide keyboard on focus change.
     */
    protected void setEditTextOnFocusChangeListener(EditText... editTexts)
    {
        EditTextOnFocusChangeHideKeyboard listener = new EditTextOnFocusChangeHideKeyboard(this.getActivity());
        for(EditText editText : editTexts)
        {
            editText.setOnFocusChangeListener(listener);
        }
    }

    /**
     * Sets the text change listener for edit texts. Will format the input (adds commas and round off decimal to 4 places).
     */
    protected void setEditTextTextChangeListener(EditText... editTexts)
    {
        for(EditText editText : editTexts)
        {
            editText.addTextChangedListener(new EditTextOnTextChangeWrapper(editText,
                    new EditTextOnTextChangeCalculate(this,
                            new EditTextOnTextChangeAddComma(editText, ViewUtils.getEditTextMaxLength(editText.getFilters(), getClassName())))));
        }
    }

    protected abstract String getClassName();
}
