package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.aaron.pseplanner.R;

import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class MidpointFragment extends AbstractCalculatorFragment
{
    public static final String LOG_MARKER = MidpointFragment.class.getSimpleName();
    private EditText highEditText;
    private EditText lowEditText;

    private TextView midpointTextView;

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflateFragment(R.layout.fragment_midpoint, inflater, parent);

        this.midpointTextView = (TextView) view.findViewById(R.id.textview_midpoint);

        this.highEditText = (EditText) view.findViewById(R.id.edittext_high);
        this.lowEditText = (EditText) view.findViewById(R.id.edittext_low);
        setEditTextOnFocusChangeListener(this.highEditText, this.lowEditText);
        setEditTextTextChangeListener(this.highEditText, this.lowEditText);

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        this.highEditText.setText("");
        this.lowEditText.setText("");
    }

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
                double high = formatter.parse(highStr).doubleValue();
                double low = formatter.parse(lowStr).doubleValue();

                if(high > low)
                {
                    double midpoint = calculatorService.getMidpoint(high, low);
                    this.midpointTextView.setText(formatService.formatStockPrice(midpoint));
                }
                else
                {
                    // TODO: error
                }
            }
            catch(ParseException ex)
            {
                Log.e(LOG_MARKER, "Error parsing input numbers.", ex);
            }
        }
        else
        {
            this.midpointTextView.setText(R.string.default_value);
        }
    }
}
