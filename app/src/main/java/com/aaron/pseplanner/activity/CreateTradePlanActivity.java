package com.aaron.pseplanner.activity;

import android.content.Intent;
import android.os.Bundle;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.service.LogManager;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Create Trade Plan Activity. Does not contain navigation views or menu items.
 */
public class CreateTradePlanActivity extends SaveTradePlanActivity
{
    public static final String CLASS_NAME = CreateTradePlanActivity.class.getSimpleName();
    private TickerDto selectedStock;

    /**
     * Inflates the UI.
     *
     * @param savedInstanceState this Bundle is unused in this method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LogManager.debug(CLASS_NAME, "onCreate", "");

        initializeSelectedStock(savedInstanceState);

        LogManager.debug(CLASS_NAME, "onCreate", selectedStock == null ? null : selectedStock.toString());

        stockLabel.setText(selectedStock.getSymbol());
    }

    private void initializeSelectedStock(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            selectedStock = savedInstanceState.getParcelable(DataKey.EXTRA_TICKER.toString());
        }
        else
        {
            Bundle bundle = getIntent().getExtras();
            if(bundle != null)
            {
                selectedStock = bundle.getParcelable(DataKey.EXTRA_TICKER.toString());
            }
        }
    }

    /**
     * Saves current state in memory, when this activity is temporarily destroyed.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(this.selectedStock != null)
        {
            outState.putParcelable(DataKey.EXTRA_TICKER.toString(), this.selectedStock);
            LogManager.debug(CLASS_NAME, "onSaveInstanceState", "Ticker: " + this.selectedStock);
        }
    }

    /**
     * Sets the stock of the created trade plan if created.
     *
     * @param intent the intent to put the extra data
     */
    @Override
    protected void setIntentExtraOnResultHome(Intent intent)
    {
        LogManager.debug(CLASS_NAME, "setIntentExtraOnResultHome", "Intent extra is Ticker: " + selectedStock);
        selectedStock.setHasTradePlan(true);
        intent.putExtra(DataKey.EXTRA_TICKER.toString(), selectedStock);
    }

    /**
     * Sets the saved ticker dto.
     *
     * @param intent the intent to put the extra data
     */
    @Override
    protected void setIntentExtraOnResultSaveClicked(Intent intent)
    {
        selectedStock.setHasTradePlan(true);
        intent.putExtra(DataKey.EXTRA_TICKER.toString(), selectedStock);

        LogManager.debug(CLASS_NAME, "setIntentExtraOnResultSaveClicked", "Intent extra is Ticker: " + selectedStock);
    }

    /**
     * Insert to database
     */
    @Override
    protected void saveTradePlan(TradeDto dto)
    {
        this.pseService.insertTradePlan(dto);
    }

    @Override
    protected String getSelectedSymbol()
    {
        return this.selectedStock.getSymbol();
    }

    @Override
    protected BigDecimal getSelectedSymbolCurrentPrice()
    {
        return this.selectedStock.getCurrentPrice();
    }

    /**
     * Always set date planned to today's date.
     */
    @Override
    protected Date getDatePlannedToSet()
    {
        return new Date();
    }

    @Override
    protected int getToolbarTitle()
    {
        return R.string.title_create_trade_plan;
    }
}