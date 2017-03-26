package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.listener.ImageViewOnClickHideExpand;
import com.aaron.pseplanner.service.LogManager;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class CalculatorFragment extends AbstractCalculatorFragment
{
    public static final String CLASS_NAME = CalculatorFragment.class.getSimpleName();

    private EditText buyPriceEditText;
    private EditText sharesEditText;
    private EditText sellPriceEditText;

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
    private TextView deductionVatOfCommissionText;
    private TextView deductionClearingFeeText;
    private TextView deductionTransactionFeeText;
    private TextView deductionSalesTax;
    private TextView deductionTotal;
    private TextView gainLossAmountText;
    private TextView gainLossPercentText;

    private String buyPriceStrPrevious;
    private String sharesStrPrevious;
    private String sellPriceStrPrevious;

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflateFragment(R.layout.fragment_calculator, inflater, parent);

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
        this.deductionVatOfCommissionText = (TextView) view.findViewById(R.id.textview_deduct_brokers_commission_vat);
        this.deductionClearingFeeText = (TextView) view.findViewById(R.id.textview_deduct_clearing_fee);
        this.deductionTransactionFeeText = (TextView) view.findViewById(R.id.textview_deduct_transaction_fee);
        this.deductionSalesTax = (TextView) view.findViewById(R.id.textview_deduct_sales_tax);
        this.deductionTotal = (TextView) view.findViewById(R.id.textview_deduct_total);

        this.gainLossAmountText = (TextView) view.findViewById(R.id.textview_gain_loss_amount);
        this.gainLossPercentText = (TextView) view.findViewById(R.id.textview_gain_loss_percent);

        this.buyPriceEditText = (EditText) view.findViewById(R.id.edittext_buy_price);
        this.sharesEditText = (EditText) view.findViewById(R.id.edittext_shares);
        this.sellPriceEditText = (EditText) view.findViewById(R.id.edittext_sell_price);
        setEditTextOnFocusChangeListener(this.buyPriceEditText, this.sharesEditText, this.sellPriceEditText);
        setEditTextTextChangeListener(this.buyPriceEditText, this.sharesEditText, this.sellPriceEditText);

        ImageView buyNetAmountImageView = (ImageView) view.findViewById(R.id.imageview_buy_net_amount);
        ImageView sellNetAmountImageView = (ImageView) view.findViewById(R.id.imageview_sell_net_amount);
        this.setImageViewOnClickListener(buyNetAmountImageView, view.findViewById(R.id.additional_fees_layout), sellNetAmountImageView, view.findViewById(R.id.deduction_fees_layout));

        LogManager.debug(CLASS_NAME, "onCreateView", "");

        return view;
    }

    @Override
    public void onStop()
    {
        LogManager.debug(CLASS_NAME, "onStop", "");

        this.resetEditTexts();
        super.onStop();
    }

    /**
     * Updates the values in the calculator fragment if buy price, shares, and sell price values are inputted.
     */
    @Override
    public void calculate()
    {
        String buyPriceStr = this.buyPriceEditText.getText().toString();
        String sharesStr = this.sharesEditText.getText().toString();

        if(StringUtils.isNotBlank(buyPriceStr) && StringUtils.isNotBlank(sharesStr))
        {
            try
            {
                boolean buyPriceAndSharesChanged = !buyPriceStr.equals(this.buyPriceStrPrevious) || !sharesStr.equals(this.sharesStrPrevious);
                NumberFormat formatter = null;
                BigDecimal buyPrice = BigDecimal.ZERO;
                long shares = 0;

                // If at least one changed, then proceed calculating. Do not change if only sell price changed.
                if(buyPriceAndSharesChanged)
                {
                    formatter = NumberFormat.getInstance(Locale.US);
                    buyPrice = BigDecimal.valueOf(formatter.parse(buyPriceStr).doubleValue());
                    shares = formatter.parse(sharesStr).longValue();

                    calculateAndUpdateViewOnBuy(buyPrice, shares);

                    // Set previous value to be compared on the next text change, to skip calculation if both are unchanged.
                    this.buyPriceStrPrevious = buyPriceStr;
                    this.sharesStrPrevious = sharesStr;
                }

                String sellPriceStr = this.sellPriceEditText.getText().toString();
                if(StringUtils.isNotBlank(sellPriceStr))
                {
                    // If either changed, proceed calculating
                    if(buyPriceAndSharesChanged || !sellPriceStr.equals(this.sellPriceStrPrevious))
                    {
                        if(formatter == null)
                        {
                            formatter = NumberFormat.getInstance(Locale.US);
                        }

                        if(buyPrice.doubleValue() == 0.0)
                        {
                            buyPrice = BigDecimal.valueOf(formatter.parse(buyPriceStr).doubleValue());
                        }

                        if(shares == 0)
                        {
                            shares = formatter.parse(sharesStr).longValue();
                        }

                        BigDecimal sellPrice = BigDecimal.valueOf(formatter.parse(sellPriceStr).doubleValue());

                        if(sellPrice.doubleValue() != 0.0)
                        {
                            calculateAndUpdateViewOnSell(buyPrice, sellPrice, shares);
                        }

                        // Set previous value to be compared on the next text change, to skip calculation if unchanged.
                        this.sellPriceStrPrevious = sellPriceStr;
                    }
                }
                else
                {
                    // sellPriceEditText is empty. reset to 0
                    resetSellValues();
                }
            }
            catch(ParseException ex)
            {
                LogManager.error(CLASS_NAME, "calculate", "Error parsing input numbers.", ex);
            }
        }
        else
        {
            // Either buyPriceEditText or sharesEditText is empty, reset to 0.
            resetAllValues();
        }

    }

    /**
     * Calculates the buy input, and updates the view.
     */
    private void calculateAndUpdateViewOnBuy(BigDecimal buyPrice, long shares)
    {
        BigDecimal averagePrice = calculatorService.getAveragePriceAfterBuy(buyPrice);
        BigDecimal priceToBreakEven = calculatorService.getPriceToBreakEven(buyPrice);
        BigDecimal buyGrossAmount = calculatorService.getBuyGrossAmount(buyPrice, shares);
        BigDecimal buyNetAmount = calculatorService.getBuyNetAmount(buyPrice, shares);

        averagePriceText.setText(formatService.formatStockPrice(averagePrice.doubleValue()));
        priceToBreakEvenText.setText(formatService.formatStockPrice(priceToBreakEven.doubleValue()));
        buyGrossAmountText.setText(formatService.formatPrice(buyGrossAmount.doubleValue()));
        buyNetAmountText.setText(formatService.formatPrice(buyNetAmount.doubleValue()));

        BigDecimal stockBrokersCommission = calculatorService.getStockbrokersCommission(buyGrossAmount);
        BigDecimal vatOfCommission = calculatorService.getVatOfCommission(stockBrokersCommission);
        BigDecimal clearingFee = calculatorService.getClearingFee(buyGrossAmount);
        BigDecimal transactionFee = calculatorService.getTransactionFee(buyGrossAmount);
        BigDecimal total = stockBrokersCommission.add(vatOfCommission).add(clearingFee).add(transactionFee);

        additionalBrokersCommissionText.setText(formatService.formatPrice(stockBrokersCommission.doubleValue()));
        additionalVatOfCommissionText.setText(formatService.formatPrice(vatOfCommission.doubleValue()));
        additionalClearingFeeText.setText(formatService.formatPrice(clearingFee.doubleValue()));
        additionalTransactionFeeText.setText(formatService.formatPrice(transactionFee.doubleValue()));
        additionalTotal.setText(formatService.formatPrice(total.doubleValue()));
    }

    /**
     * Calculates the sell input, and updates the view.
     */
    private void calculateAndUpdateViewOnSell(BigDecimal buyPrice, BigDecimal sellPrice, long shares)
    {
        BigDecimal sellGrossAmount = calculatorService.getSellGrossAmount(sellPrice, shares);
        BigDecimal sellNetAmount = calculatorService.getSellNetAmount(sellPrice, shares);

        sellGrossAmountText.setText(formatService.formatPrice(sellGrossAmount.doubleValue()));
        sellNetAmountText.setText(formatService.formatPrice(sellNetAmount.doubleValue()));

        BigDecimal stockBrokersCommission = calculatorService.getStockbrokersCommission(sellGrossAmount);
        BigDecimal vatOfCommission = calculatorService.getVatOfCommission(stockBrokersCommission);
        BigDecimal clearingFee = calculatorService.getClearingFee(sellGrossAmount);
        BigDecimal transactionFee = calculatorService.getTransactionFee(sellGrossAmount);
        BigDecimal salesTax = calculatorService.getSalesTax(sellGrossAmount);
        BigDecimal total = stockBrokersCommission.add(vatOfCommission).add(clearingFee).add(transactionFee).add(salesTax);

        deductionBrokersCommissionText.setText(formatService.formatPrice(stockBrokersCommission.doubleValue()));
        deductionVatOfCommissionText.setText(formatService.formatPrice(vatOfCommission.doubleValue()));
        deductionClearingFeeText.setText(formatService.formatPrice(clearingFee.doubleValue()));
        deductionTransactionFeeText.setText(formatService.formatPrice(transactionFee.doubleValue()));
        deductionSalesTax.setText(formatService.formatPrice(salesTax.doubleValue()));
        deductionTotal.setText(formatService.formatPrice(total.doubleValue()));

        BigDecimal gainLossAmount = calculatorService.getGainLossAmount(buyPrice, shares, sellPrice);
        BigDecimal percentGainLoss = calculatorService.getPercentGainLoss(buyPrice, shares, sellPrice);

        gainLossAmountText.setText(formatService.formatPrice(gainLossAmount.doubleValue()));
        gainLossPercentText.setText(formatService.formatPercent(percentGainLoss.doubleValue()));
        formatService.formatTextColor(gainLossAmount.doubleValue(), gainLossAmountText);
        formatService.formatTextColor(percentGainLoss.doubleValue(), gainLossPercentText);
    }

    private void resetEditTexts()
    {
        this.buyPriceEditText.setText("");
        this.sharesEditText.setText("");
        this.sellPriceEditText.setText("");
    }

    /**
     * Sets all text views value to 0.
     */
    private void resetAllValues()
    {
        this.averagePriceText.setText(R.string.default_value);
        this.priceToBreakEvenText.setText(R.string.default_value);
        this.buyGrossAmountText.setText(R.string.default_value);
        this.buyNetAmountText.setText(R.string.default_value);

        this.additionalBrokersCommissionText.setText(R.string.default_value);
        this.additionalVatOfCommissionText.setText(R.string.default_value);
        this.additionalClearingFeeText.setText(R.string.default_value);
        this.additionalTransactionFeeText.setText(R.string.default_value);
        this.additionalTotal.setText(R.string.default_value);
        resetSellValues();
    }

    /**
     * Sets all sell text views value to 0.
     */
    private void resetSellValues()
    {
        this.sellGrossAmountText.setText(R.string.default_value);
        this.sellNetAmountText.setText(R.string.default_value);
        this.deductionBrokersCommissionText.setText(R.string.default_value);
        this.deductionVatOfCommissionText.setText(R.string.default_value);
        this.deductionClearingFeeText.setText(R.string.default_value);
        this.deductionTransactionFeeText.setText(R.string.default_value);
        this.deductionSalesTax.setText(R.string.default_value);
        this.deductionTotal.setText(R.string.default_value);
        this.gainLossAmountText.setText(R.string.default_value);
        this.gainLossPercentText.setText(R.string.default_value);

        double defaultValue = Double.parseDouble(getActivity().getString(R.string.default_value));
        formatService.formatTextColor(defaultValue, gainLossAmountText);
        formatService.formatTextColor(defaultValue, gainLossPercentText);
    }

    /**
     * Sets the on click listener for image views. Will toggle update the image on click.
     */
    private void setImageViewOnClickListener(ImageView buyNetImageView, View additionalLayoutContainer, ImageView sellNetImageView, View deductionsLayoutContainer)
    {
        buyNetImageView.setOnClickListener(new ImageViewOnClickHideExpand(this.getActivity(), buyNetImageView, additionalLayoutContainer));
        sellNetImageView.setOnClickListener(new ImageViewOnClickHideExpand(this.getActivity(), sellNetImageView, deductionsLayoutContainer));
    }

    @Override
    protected String getClassName()
    {
        return CLASS_NAME;
    }
}
