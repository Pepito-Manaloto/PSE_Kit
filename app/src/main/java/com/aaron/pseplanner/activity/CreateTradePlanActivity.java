package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.service.LogManager;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.aaron.pseplanner.service.CalculatorService.ONE_HUNDRED;

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

        if(savedInstanceState != null)
        {
            this.selectedStock = savedInstanceState.getParcelable(DataKey.EXTRA_TICKER.toString());
        }
        else
        {
            Bundle bundle = getIntent().getExtras();
            if(bundle != null)
            {
                this.selectedStock = bundle.getParcelable(DataKey.EXTRA_TICKER.toString());
            }
        }

        LogManager.debug(CLASS_NAME, "onCreate", this.selectedStock == null ? null : this.selectedStock.toString());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_create_trade_plan);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView stockLabel = (TextView) findViewById(R.id.textview_stock);
        stockLabel.setText(this.selectedStock.getSymbol());
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
     * Sets the stock of the created trade plan if created, then sends it to the main activity.
     *
     * @param resultCode the result of the user's action
     */
    @Override
    protected void setActivityResultHome(int resultCode)
    {
        Intent data = new Intent();

        LogManager.debug(CLASS_NAME, "setActivityResultHome", "Result code: " + resultCode + " Ticker: " + this.selectedStock);

        if(resultCode == Activity.RESULT_OK)
        {
            this.selectedStock.setHasTradePlan(true);
            data.putExtra(DataKey.EXTRA_TICKER.toString(), this.selectedStock);
        }

        setResult(resultCode, data);
        finish();
    }

    /**
     * Sets the saved ticker dto and trade dto then sends it to the main activity fragment.
     *
     * @param dto the saved trade plan
     */
    @Override
    protected void setActivityResultSaveClicked(TradeDto dto)
    {
        Intent data = new Intent();

        this.selectedStock.setHasTradePlan(true);
        data.putExtra(DataKey.EXTRA_TICKER.toString(), this.selectedStock);
        data.putExtra(DataKey.EXTRA_TRADE.toString(), dto);
        setResult(Activity.RESULT_OK, data);
        finish();

        LogManager.debug(CLASS_NAME, "setActivityResultSaveClicked", "TradeDto result: " + dto);
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
}