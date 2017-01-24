package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.BoardLot;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class DividendFragment extends AbstractCalculatorFragment
{
    public static final String LOG_MARKER = DividendFragment.class.getSimpleName();

    private EditText priceEditText;
    private EditText sharesEditText;
    private EditText dividendEditText;

    private TextView yieldTextView;
    private TextView percentYieldTextView;
    private TextView totalAmountTextView;

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflateFragment(R.layout.fragment_dividend, inflater, parent);

        this.yieldTextView = (TextView) view.findViewById(R.id.textview_yield);
        this.percentYieldTextView = (TextView) view.findViewById(R.id.textview_dividend_percent);
        this.totalAmountTextView = (TextView) view.findViewById(R.id.textview_total_amount);

        this.priceEditText = (EditText) view.findViewById(R.id.edittext_price);
        this.sharesEditText = (EditText) view.findViewById(R.id.edittext_shares);
        this.dividendEditText = (EditText) view.findViewById(R.id.edittext_cash_dividend);
        setEditTextOnFocusChangeListener(this.priceEditText, this.sharesEditText, this.dividendEditText);
        setEditTextTextChangeListener(this.priceEditText, this.sharesEditText, this.dividendEditText);

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        this.resetEditTexts();
    }

    /**
     * Updates the values in the dividend fragment if price price, shares, and cash dividend values are inputted.
     */
    @Override
    public void calculate()
    {
        String priceStr = this.priceEditText.getText().toString();
        String sharesStr = this.sharesEditText.getText().toString();
        String cashDividendStr = this.dividendEditText.getText().toString();

        if(StringUtils.isNotBlank(priceStr) && StringUtils.isNotBlank(sharesStr) && StringUtils.isNotBlank(cashDividendStr))
        {
            try
            {
                NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                double price = formatter.parse(priceStr).doubleValue();
                long shares = formatter.parse(sharesStr).longValue();
                double cashDividend = formatter.parse(cashDividendStr).doubleValue();

                if(!BoardLot.isValidBoardLot(price, shares))
                {
                    Toast.makeText(getContext(), R.string.boardlot_invalid, Toast.LENGTH_SHORT).show();
                }

                double yield = calculatorService.getDividendYield(shares, cashDividend);
                double percentYield = calculatorService.getPercentDividendYield(price, shares, cashDividend);

                this.yieldTextView.setText(formatService.formatPrice(yield));
                this.percentYieldTextView.setText(formatService.formatPrice(percentYield));
                this.formatService.formatTextColor(yield, yieldTextView);
                this.formatService.formatTextColor(percentYield, percentYieldTextView);

                double totalAmount = calculatorService.getBuyGrossAmount(price, shares);
                this.totalAmountTextView.setText(formatService.formatPrice(totalAmount));
            }
            catch(ParseException ex)
            {
                Log.e(LOG_MARKER, "Error parsing input numbers.", ex);
            }
        }
        else
        {
            this.yieldTextView.setText(R.string.default_value);
            this.percentYieldTextView.setText(R.string.default_value);
            this.totalAmountTextView.setText(R.string.default_value);

            double defaultValue = Double.parseDouble(getActivity().getString(R.string.default_value));
            this.formatService.formatTextColor(defaultValue, yieldTextView);
            this.formatService.formatTextColor(defaultValue, percentYieldTextView);
        }
    }

    private void resetEditTexts()
    {
        this.priceEditText.setText("");
        this.sharesEditText.setText("");
        this.dividendEditText.setText("");
    }
}
