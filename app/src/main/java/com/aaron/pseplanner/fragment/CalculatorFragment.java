package com.aaron.pseplanner.fragment;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.listener.EditTextOnFocusChangeHideKeyboard;
import com.aaron.pseplanner.listener.EditTextTextChangeAddComma;
import com.aaron.pseplanner.listener.ImageViewOnClickCollapseExpand;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class CalculatorFragment extends Fragment
{
    public static final String LOG_MARKER = CalculatorFragment.class.getSimpleName();

    private EditText buyPriceEditText;
    private EditText sharesEditText;
    private EditText sellPriceEditText;

    private ImageView buyNetAmountImageView;
    private ImageView sellNetAmountImageView;

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_calculator, parent, false);
        final Resources resource = getResources();

        this.buyPriceEditText = (EditText) view.findViewById(R.id.edittext_buy_price);
        this.sharesEditText = (EditText) view.findViewById(R.id.edittext_shares);
        this.sellPriceEditText = (EditText) view.findViewById(R.id.edittext_sell_price);
        this.setEditTextOnFocusChangeListener(this.buyPriceEditText, this.sharesEditText, this.sellPriceEditText);
        this.setEditTextTextChangeListener(this.buyPriceEditText, this.sharesEditText, this.sellPriceEditText);



        this.buyNetAmountImageView = (ImageView) view.findViewById(R.id.imageview_buy_net_amount);
        this.sellNetAmountImageView = (ImageView) view.findViewById(R.id.imageview_sell_net_amount);
        this.setImageViewOnClickListener(view, this.buyNetAmountImageView, this.sellNetAmountImageView);

        return view;
    }

    /**
     * Sets the on focus change listener for edit texts. Will hide keyboard on focus change.
     */
    private void setEditTextOnFocusChangeListener(EditText... editTexts)
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
    private void setEditTextTextChangeListener(EditText... editTexts)
    {
        for(EditText editText : editTexts)
        {
            editText.addTextChangedListener(new EditTextTextChangeAddComma(editText, getEditTextMaxLength(editText.getFilters())));
        }
    }

    /**
     * Sets the on click listener for image views. Will toggle update the image on click.
     */
    private void setImageViewOnClickListener(View view, ImageView buyNetImageView, ImageView sellNetImageView)
    {
        List<TextView> additionalFeesTextView = new ArrayList<>();
        additionalFeesTextView.add((TextView) view.findViewById(R.id.label_addt_brokers_commission));
        additionalFeesTextView.add((TextView) view.findViewById(R.id.textview_addt_brokers_commission));
        additionalFeesTextView.add((TextView) view.findViewById(R.id.label_addt_clearing_fee));
        additionalFeesTextView.add((TextView) view.findViewById(R.id.textview_addt_clearing_fee));
        additionalFeesTextView.add((TextView) view.findViewById(R.id.label_addt_transaction_fee));
        additionalFeesTextView.add((TextView) view.findViewById(R.id.textview_addt_transaction_fee));
        additionalFeesTextView.add((TextView) view.findViewById(R.id.label_addt_total));
        additionalFeesTextView.add((TextView) view.findViewById(R.id.textview_addt_total));
        buyNetImageView.setOnClickListener(new ImageViewOnClickCollapseExpand(this.getActivity(), buyNetImageView, additionalFeesTextView));

        List<TextView> deductionsTextView = new ArrayList<>();
        deductionsTextView.add((TextView) view.findViewById(R.id.label_deduct_brokers_commission));
        deductionsTextView.add((TextView) view.findViewById(R.id.textview_deduct_brokers_commission));
        deductionsTextView.add((TextView) view.findViewById(R.id.label_deduct_clearing_fee));
        deductionsTextView.add((TextView) view.findViewById(R.id.textview_deduct_clearing_fee));
        deductionsTextView.add((TextView) view.findViewById(R.id.label_deduct_transaction_fee));
        deductionsTextView.add((TextView) view.findViewById(R.id.textview_deduct_transaction_fee));
        deductionsTextView.add((TextView) view.findViewById(R.id.label_deduct_sales_tax));
        deductionsTextView.add((TextView) view.findViewById(R.id.textview_deduct_sales_tax));
        deductionsTextView.add((TextView) view.findViewById(R.id.label_deduct_total));
        deductionsTextView.add((TextView) view.findViewById(R.id.textview_deduct_total));
        sellNetImageView.setOnClickListener(new ImageViewOnClickCollapseExpand(this.getActivity(), sellNetImageView, deductionsTextView));
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
