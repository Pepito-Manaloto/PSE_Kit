package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.listener.ImageViewOnClickHideExpand;
import com.aaron.pseplanner.service.LogManager;

import org.apache.commons.lang3.StringUtils;

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

public class CalculatorFragment extends AbstractCalculatorFragment
{
    public static final String CLASS_NAME = CalculatorFragment.class.getSimpleName();

    @BindView(R.id.edittext_buy_price)
    EditText buyPriceEditText;

    @BindView(R.id.edittext_shares)
    EditText sharesEditText;

    @BindView(R.id.edittext_sell_price)
    EditText sellPriceEditText;

    @BindView(R.id.textview_average_price)
    TextView averagePriceText;

    @BindView(R.id.textview_break_even_price)
    TextView priceToBreakEvenText;

    @BindView(R.id.textview_buy_gross_amount)
    TextView buyGrossAmountText;

    @BindView(R.id.textview_buy_net_amount)
    TextView buyNetAmountText;

    @BindView(R.id.textview_addt_brokers_commission)
    TextView additionalBrokersCommissionText;

    @BindView(R.id.textview_addt_brokers_commission_vat)
    TextView additionalVatOfCommissionText;

    @BindView(R.id.textview_addt_clearing_fee)
    TextView additionalClearingFeeText;

    @BindView(R.id.textview_addt_transaction_fee)
    TextView additionalTransactionFeeText;

    @BindView(R.id.textview_addt_total)
    TextView additionalTotal;

    @BindView(R.id.textview_sell_gross_amount)
    TextView sellGrossAmountText;

    @BindView(R.id.textview_sell_net_amount)
    TextView sellNetAmountText;

    @BindView(R.id.textview_deduct_brokers_commission)
    TextView deductionBrokersCommissionText;

    @BindView(R.id.textview_deduct_brokers_commission_vat)
    TextView deductionVatOfCommissionText;

    @BindView(R.id.textview_deduct_clearing_fee)
    TextView deductionClearingFeeText;

    @BindView(R.id.textview_deduct_transaction_fee)
    TextView deductionTransactionFeeText;

    @BindView(R.id.textview_deduct_sales_tax)
    TextView deductionSalesTax;

    @BindView(R.id.textview_deduct_total)
    TextView deductionTotal;

    @BindView(R.id.textview_gain_loss_amount)
    TextView gainLossAmountText;

    @BindView(R.id.textview_gain_loss_percent)
    TextView gainLossPercentText;

    @BindViews({ R.id.textview_average_price, R.id.textview_break_even_price, R.id.textview_buy_gross_amount, R.id.textview_buy_net_amount,
            R.id.textview_addt_brokers_commission, R.id.textview_addt_brokers_commission_vat, R.id.textview_addt_clearing_fee,
            R.id.textview_addt_transaction_fee, R.id.textview_addt_total })
    List<TextView> buyTextViews;

    @BindViews({ R.id.textview_sell_gross_amount, R.id.textview_sell_net_amount, R.id.textview_deduct_brokers_commission,
            R.id.textview_deduct_brokers_commission_vat, R.id.textview_deduct_clearing_fee, R.id.textview_deduct_transaction_fee,
            R.id.textview_deduct_sales_tax, R.id.textview_deduct_total, R.id.textview_gain_loss_amount, R.id.textview_gain_loss_percent })
    List<TextView> sellTextViews;

    private String buyPriceStrPrevious;
    private String sharesStrPrevious;
    private String sellPriceStrPrevious;

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflateFragment(R.layout.fragment_calculator, inflater, parent);
        this.unbinder = ButterKnife.bind(this, view);

        setEditTextOnFocusChangeListener(this.buyPriceEditText, this.sharesEditText, this.sellPriceEditText);
        setEditTextTextChangeListener(this.buyPriceEditText, this.sharesEditText, this.sellPriceEditText);

        initializeBuyAndSellNetAmountExpandCollapseImageView(view);

        LogManager.debug(CLASS_NAME, "onCreateView", "");

        return view;
    }

    private void initializeBuyAndSellNetAmountExpandCollapseImageView(View view)
    {
        ImageView buyNetAmountImageView = view.findViewById(R.id.imageview_buy_net_amount);
        GridLayout additionalFeesLayout = view.findViewById(R.id.additional_fees_layout);

        ImageView sellNetAmountImageView = view.findViewById(R.id.imageview_sell_net_amount);
        GridLayout deductionFeesLayout = view.findViewById(R.id.deduction_fees_layout);

        this.setImageViewOnClickListener(buyNetAmountImageView, additionalFeesLayout, sellNetAmountImageView, deductionFeesLayout);
    }

    /**
     * Sets the on click listener for image views. Will toggle update the image on click.
     */
    private void setImageViewOnClickListener(ImageView buyNetImageView, GridLayout additionalLayoutContainer, ImageView sellNetImageView,
            GridLayout deductionsLayoutContainer)
    {
        buyNetImageView.setOnClickListener(new ImageViewOnClickHideExpand(this.getActivity(), buyNetImageView, additionalLayoutContainer));
        sellNetImageView.setOnClickListener(new ImageViewOnClickHideExpand(this.getActivity(), sellNetImageView, deductionsLayoutContainer));
    }

    @Override
    public void onStop()
    {
        LogManager.debug(CLASS_NAME, "onStop", "");

        //ButterKnife.apply(this.editTexts, RESET_EDIT_TEXT);
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
                boolean buyPriceOrSharesChanged = !buyPriceStr.equals(this.buyPriceStrPrevious) || !sharesStr.equals(this.sharesStrPrevious);
                NumberFormat formatter = null;
                BigDecimal buyPrice = BigDecimal.ZERO;
                long shares = 0;

                if(buyPriceOrSharesChanged)
                {
                    formatter = NumberFormat.getInstance(Locale.US);
                    buyPrice = BigDecimal.valueOf(formatter.parse(buyPriceStr).doubleValue());
                    shares = formatter.parse(sharesStr).longValue();

                    boolean buyPriceAndSharesGreaterThanZero = buyPrice.signum() > 0 && shares > 0;
                    if(buyPriceAndSharesGreaterThanZero)
                    {
                        calculateAndUpdateViewOnBuy(buyPrice, shares);
                    }

                    // Set previous value to be compared on the next text change, to skip calculation if both are unchanged.
                    this.buyPriceStrPrevious = buyPriceStr;
                    this.sharesStrPrevious = sharesStr;
                }

                String sellPriceStr = this.sellPriceEditText.getText().toString();
                if(StringUtils.isNotBlank(sellPriceStr))
                {
                    boolean sellPriceChanged = !sellPriceStr.equals(this.sellPriceStrPrevious);
                    boolean eitherBuyPriceAndSharesOrSellPriceChanged = buyPriceOrSharesChanged || sellPriceChanged;
                    if(eitherBuyPriceAndSharesOrSellPriceChanged)
                    {
                        if(formatter == null)
                        {
                            formatter = NumberFormat.getInstance(Locale.US);
                        }

                        // Set if not yet set in buy/shares update in the code above
                        if(buyPrice.doubleValue() == 0.0)
                        {
                            buyPrice = BigDecimal.valueOf(formatter.parse(buyPriceStr).doubleValue());
                        }

                        // Set if not yet set in buy/shares update in the code above
                        if(shares == 0)
                        {
                            shares = formatter.parse(sharesStr).longValue();
                        }

                        BigDecimal sellPrice = BigDecimal.valueOf(formatter.parse(sellPriceStr).doubleValue());
                        boolean buyPriceSellPriceSharesGreaterThanZero = buyPrice.signum() > 0 && sellPrice.signum() > 0 && shares > 0;
                        if(buyPriceSellPriceSharesGreaterThanZero)
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

    /**
     * Sets all text views value to 0.
     */
    private void resetAllValues()
    {
        ButterKnife.apply(this.buyTextViews, RESET_TEXT_VIEW);
        resetSellValues();
    }

    /**
     * Sets all sell text views value to 0.
     */
    private void resetSellValues()
    {
        ButterKnife.apply(this.sellTextViews, RESET_TEXT_VIEW);

        double defaultValue = Double.parseDouble(getActivity().getString(R.string.default_value));
        formatService.formatTextColor(defaultValue, gainLossAmountText);
        formatService.formatTextColor(defaultValue, gainLossPercentText);
    }

    @Override
    protected String getClassName()
    {
        return CLASS_NAME;
    }
}
