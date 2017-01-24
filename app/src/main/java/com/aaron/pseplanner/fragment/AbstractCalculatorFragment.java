package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.fragment.service.InputCalculatorService;
import com.aaron.pseplanner.listener.EditTextOnFocusChangeHideKeyboard;
import com.aaron.pseplanner.listener.EditTextOnTextChangeAddComma;
import com.aaron.pseplanner.listener.EditTextOnTextChangeCalculate;
import com.aaron.pseplanner.listener.EditTextOnTextChangeWrapper;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.implementation.CalculatorServiceImpl;
import com.aaron.pseplanner.service.implementation.FormatServiceImpl;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Created by aaron.asuncion on 1/24/2017.
 */

public abstract class AbstractCalculatorFragment extends Fragment implements InputCalculatorService
{
    public static final String LOG_MARKER = AbstractCalculatorFragment.class.getSimpleName();

    protected CalculatorService calculatorService;
    protected FormatService formatService;

    /**
     * Initializes services and returns the fragment of the provided resId.
     */
    protected View inflateFragment(int resId, LayoutInflater inflater, ViewGroup parent)
    {
        View view = inflater.inflate(resId, parent, false);

        this.calculatorService = new CalculatorServiceImpl();
        this.formatService = new FormatServiceImpl(getActivity());

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
                            new EditTextOnTextChangeAddComma(editText, getEditTextMaxLength(editText.getFilters())))));
        }
    }

    /**
     * Retrieves the edit text's android:maxLength.
     */
    private int getEditTextMaxLength(InputFilter[] filters)
    {
        for(InputFilter filter : filters)
        {
            if(filter instanceof InputFilter.LengthFilter)
            {
                if(android.os.Build.VERSION.SDK_INT >= 21)
                {
                    return ((InputFilter.LengthFilter) filter).getMax();
                }
                else
                {
                    try
                    {
                        return (int) FieldUtils.readField(filter, "mMax", true);
                    }
                    catch(IllegalAccessException e)
                    {
                        Log.e(LOG_MARKER, "Error retrieving EditText's maxLength.", e);
                    }
                }
            }
        }

        return 0;
    }
}
