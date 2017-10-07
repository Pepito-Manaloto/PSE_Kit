package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.BoardLot;
import com.aaron.pseplanner.service.LogManager;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class DividendFragment extends AbstractCalculatorFragment
{
    public static final String CLASS_NAME = DividendFragment.class.getSimpleName();

    @BindView(R.id.edittext_price)
    EditText priceEditText;

    @BindView(R.id.edittext_shares)
    EditText sharesEditText;

    @BindView(R.id.edittext_cash_dividend)
    EditText dividendEditText;

    @BindViews({R.id.edittext_price, R.id.edittext_shares, R.id.edittext_cash_dividend})
    List<EditText> editTexts;

    @BindView(R.id.textview_yield)
    TextView yieldTextView;

    @BindView(R.id.textview_dividend_percent)
    TextView percentYieldTextView;

    @BindView(R.id.textview_total_amount)
    TextView totalAmountTextView;

    @BindViews({R.id.textview_yield, R.id.textview_dividend_percent, R.id.textview_total_amount})
    List<TextView> textViews;

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflateFragment(R.layout.fragment_dividend, inflater, parent);
        this.unbinder = ButterKnife.bind(this, view);

        setEditTextOnFocusChangeListener(this.priceEditText, this.sharesEditText, this.dividendEditText);
        setEditTextTextChangeListener(this.priceEditText, this.sharesEditText, this.dividendEditText);

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
     * Updates the values in the dividend fragment if the price, shares, and cash dividend values are inputted.
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
                BigDecimal price = BigDecimal.valueOf(formatter.parse(priceStr).doubleValue());
                long shares = formatter.parse(sharesStr).longValue();
                BigDecimal cashDividend = BigDecimal.valueOf(formatter.parse(cashDividendStr).doubleValue());

                if(!BoardLot.isValidBoardLot(price, shares))
                {
                    Toast.makeText(getContext(), R.string.boardlot_invalid, Toast.LENGTH_SHORT).show();
                }

                BigDecimal yield = calculatorService.getDividendYield(shares, cashDividend);
                BigDecimal percentYield = calculatorService.getPercentDividendYield(price, shares, cashDividend);

                this.yieldTextView.setText(formatService.formatPrice(yield.doubleValue()));
                this.percentYieldTextView.setText(formatService.formatPercent(percentYield.doubleValue()));
                this.formatService.formatTextColor(yield.doubleValue(), yieldTextView);
                this.formatService.formatTextColor(percentYield.doubleValue(), percentYieldTextView);

                BigDecimal totalAmount = calculatorService.getBuyGrossAmount(price, shares);
                this.totalAmountTextView.setText(formatService.formatPrice(totalAmount.doubleValue()));
            }
            catch(ParseException ex)
            {
                LogManager.error(CLASS_NAME, "calculate", "Error parsing input numbers.", ex);
            }
        }
        else
        {
            ButterKnife.apply(this.textViews, RESET_TEXT_VIEW);

            double defaultValue = Double.parseDouble(getActivity().getString(R.string.default_value));
            this.formatService.formatTextColor(defaultValue, yieldTextView);
            this.formatService.formatTextColor(defaultValue, percentYieldTextView);
        }
    }

    @Override
    protected String getClassName()
    {
        return CLASS_NAME;
    }
}
