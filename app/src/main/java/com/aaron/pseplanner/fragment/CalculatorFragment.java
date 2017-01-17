package com.aaron.pseplanner.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.listener.CalculatorOnTextChangeListener;
import com.aaron.pseplanner.listener.EditTextOnFocusChangeHideKeyboard;
import com.aaron.pseplanner.listener.ImageViewOnClickHideExpand;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.StockService;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

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

    private TextView averagePriceText;
    private TextView priceToBreakEvenText;
    private TextView buyGrossAmountText;
    private TextView buyNetAmountText;
    private TextView additionalBrokersCommissionText;
    private TextView additionalVatOfCommissionText;
    private TextView additionalClearingFeeText;
    private TextView additionalTransactionFeeText;
    private TextView additionalTotal;
    private TextView sellGrossAmountText;
    private TextView sellNetAmountText;
    private TextView deductionBrokersCommissionText;
    private TextView deductionClearingFeeText;
    private TextView deductionTransactionFeeText;
    private TextView deductionSalesTax;
    private TextView deductionTotal;
    private TextView gainLossAmountText;
    private TextView gainLossPercentText;

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_calculator, parent, false);
        final Resources resource = getResources();

        this.averagePriceText = (TextView) view.findViewById(R.id.textview_average_price);
        this.priceToBreakEvenText = (TextView) view.findViewById(R.id.textview_break_even_price);
        this.buyGrossAmountText = (TextView) view.findViewById(R.id.textview_buy_gross_amount);
        this.buyNetAmountText = (TextView) view.findViewById(R.id.textview_buy_net_amount);
        this.additionalBrokersCommissionText = (TextView) view.findViewById(R.id.textview_addt_brokers_commission);
        this.additionalVatOfCommissionText = (TextView) view.findViewById(R.id.textview_addt_brokers_commission_vat);
        this.additionalClearingFeeText = (TextView) view.findViewById(R.id.textview_addt_clearing_fee);
        this.additionalTransactionFeeText = (TextView) view.findViewById(R.id.textview_addt_transaction_fee);
        this.additionalTotal = (TextView) view.findViewById(R.id.textview_addt_total);
        this.sellGrossAmountText = (TextView) view.findViewById(R.id.textview_sell_gross_amount);
        this.sellNetAmountText = (TextView) view.findViewById(R.id.textview_sell_net_amount);
        this.deductionBrokersCommissionText = (TextView) view.findViewById(R.id.textview_deduct_brokers_commission);
        this.deductionClearingFeeText = (TextView) view.findViewById(R.id.textview_deduct_clearing_fee);
        this.deductionTransactionFeeText = (TextView) view.findViewById(R.id.textview_deduct_transaction_fee);
        this.deductionSalesTax = (TextView) view.findViewById(R.id.textview_deduct_sales_tax);
        this.deductionTotal = (TextView) view.findViewById(R.id.textview_deduct_total);
        this.gainLossAmountText = (TextView) view.findViewById(R.id.textview_gain_loss_amount);
        this.gainLossPercentText = (TextView) view.findViewById(R.id.textview_gain_loss_percent);

        this.buyPriceEditText = (EditText) view.findViewById(R.id.edittext_buy_price);
        this.sharesEditText = (EditText) view.findViewById(R.id.edittext_shares);
        this.sellPriceEditText = (EditText) view.findViewById(R.id.edittext_sell_price);
        this.setEditTextOnFocusChangeListener(this.buyPriceEditText, this.sharesEditText, this.sellPriceEditText);
        this.setEditTextTextChangeListener(this.buyPriceEditText, this.sharesEditText, this.sellPriceEditText);

        this.buyNetAmountImageView = (ImageView) view.findViewById(R.id.imageview_buy_net_amount);
        this.sellNetAmountImageView = (ImageView) view.findViewById(R.id.imageview_sell_net_amount);
        this.setImageViewOnClickListener(this.buyNetAmountImageView, view.findViewById(R.id.additional_fees_layout), this.sellNetAmountImageView, view.findViewById(R.id.deduction_fees_layout));

        return view;
    }

    /**
     * Updates the values in the calculator fragment if buy price, shares, and sell price value are inputted.
     */
    public void calculate(CalculatorService calculatorService, final StockService stockService)
    {
        String buyPriceStr = this.buyPriceEditText.getText().toString();
        String sharesStr = this.sharesEditText.getText().toString();
        String sellPriceStr = this.sellPriceEditText.getText().toString();

        if(StringUtils.isNotBlank(buyPriceStr) && StringUtils.isNotBlank(sharesStr))
        {
            try
            {
                NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                double buyPrice = formatter.parse(buyPriceStr).doubleValue();
                long shares = formatter.parse(sharesStr).longValue();

                final double averagePrice = calculatorService.getAveragePriceAfterBuy(buyPrice);
                final double priceToBreakEven = calculatorService.getPriceToBreakEven(buyPrice);
                final double buyGrossAmount = calculatorService.getBuyGrossAmount(buyPrice, shares);
                final double buyNetAmount = calculatorService.getBuyNetAmount(buyPrice, shares);
                final double stockBrokersCommission = calculatorService.getStockbrokersCommission(buyGrossAmount);
                final double vatOfCommission = calculatorService.getVatOfCommission(stockBrokersCommission);
                final double clearingFee = calculatorService.getClearingFee(buyGrossAmount);
                final double transactionFee = calculatorService.getTransactionFee(buyGrossAmount);
                final double total = stockBrokersCommission + vatOfCommission + clearingFee + transactionFee;

                final boolean sellPriceNotEmpty = StringUtils.isNotBlank(sellPriceStr);

                if(sellPriceNotEmpty)
                {
                    double sellPrice = formatter.parse(sellPriceStr).doubleValue();

                    final double sellGrossAmount = calculatorService.getSellGrossAmount(sellPrice, shares);
                    final double sellNetAmount = calculatorService.getSellNetAmount(sellPrice, shares);
                    final double gainLossAmount = calculatorService.getGainLossAmount(buyPrice, shares, sellPrice);
                    final double percentGainLoss = calculatorService.getPercentGainLoss(buyPrice, shares, sellPrice);
                    final double salesTax = calculatorService.getSalesTax(sellGrossAmount);

                }

                this.getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        averagePriceText.setText(stockService.formatStockPrice(averagePrice));
                        priceToBreakEvenText.setText(stockService.formatStockPrice(priceToBreakEven));
                        buyGrossAmountText.setText(stockService.formatStockPrice(buyGrossAmount));
                        buyNetAmountText.setText(stockService.formatStockPrice(buyNetAmount));

                        additionalBrokersCommissionText.setText(stockService.formatStockPrice(stockBrokersCommission));
                        additionalVatOfCommissionText.setText(stockService.formatStockPrice(vatOfCommission));
                        additionalClearingFeeText.setText(stockService.formatStockPrice(clearingFee));
                        additionalTransactionFeeText.setText(stockService.formatStockPrice(transactionFee));
                        additionalTotal.setText(stockService.formatStockPrice(total));

                        if(sellPriceNotEmpty)
                        {
                        }
                    }
                });
            }
            catch(ParseException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    // TODO: Add onKeyListener to EditText, such that if one is empty reset all texts to 0

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
            editText.addTextChangedListener(new CalculatorOnTextChangeListener(editText, getEditTextMaxLength(editText.getFilters()), this));
        }
    }

    /**
     * Sets the on click listener for image views. Will toggle update the image on click.
     */
    private void setImageViewOnClickListener(ImageView buyNetImageView, View additionalLayoutContainer, ImageView sellNetImageView, View deductionsLayoutContainer)
    {
        buyNetImageView.setOnClickListener(new ImageViewOnClickHideExpand(this.getActivity(), buyNetImageView, additionalLayoutContainer));
        sellNetImageView.setOnClickListener(new ImageViewOnClickHideExpand(this.getActivity(), sellNetImageView, deductionsLayoutContainer));
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
