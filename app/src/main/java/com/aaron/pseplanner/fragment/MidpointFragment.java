package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.service.LogManager;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class MidpointFragment extends AbstractCalculatorFragment
{
    public static final String CLASS_NAME = MidpointFragment.class.getSimpleName();

    @BindView(R.id.edittext_high)
    EditText highEditText;

    @BindView(R.id.edittext_low)
    EditText lowEditText;

    @BindView(R.id.textview_midpoint)
    TextView midpointTextView;

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflateFragment(R.layout.fragment_midpoint, inflater, parent);
        this.unbinder = ButterKnife.bind(this, view);

        setEditTextOnFocusChangeListener(this.highEditText, this.lowEditText);
        setEditTextTextChangeListener(this.highEditText, this.lowEditText);

        LogManager.debug(CLASS_NAME, "onCreateView", "");

        return view;
    }

    @Override
    public void onStop()
    {
        LogManager.debug(CLASS_NAME, "onStop", "");
        //ButterKnife.apply(this.editTexts, RESET_EDIT_TEXT);

        super.onStop();
    }

    /**
     * Updates the values in the calculator fragment if high and low values are inputted.
     */
    @Override
    public void calculate()
    {
        String highStr = this.highEditText.getText().toString();
        String lowStr = this.lowEditText.getText().toString();

        if(StringUtils.isNotBlank(highStr) && StringUtils.isNotBlank(lowStr))
        {
            try
            {
                NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                BigDecimal high = BigDecimal.valueOf(formatter.parse(highStr).doubleValue());
                BigDecimal low = BigDecimal.valueOf(formatter.parse(lowStr).doubleValue());

                boolean highIsGreaterThanLow = high.compareTo(low) > 0;
                if(highIsGreaterThanLow)
                {
                    BigDecimal midpoint = calculatorService.getMidpoint(high, low);
                    this.midpointTextView.setText(formatService.formatStockPrice(midpoint.doubleValue()));
                }
                else
                {
                    Toast.makeText(getContext(), R.string.midpoint_invalid_input, Toast.LENGTH_LONG).show();
                }
            }
            catch(ParseException ex)
            {
                LogManager.error(CLASS_NAME, "calculate", "Error parsing input numbers.", ex);
            }
        }
        else
        {
            this.midpointTextView.setText(R.string.default_value);
        }
    }

    @Override
    protected String getClassName()
    {
        return CLASS_NAME;
    }
}
