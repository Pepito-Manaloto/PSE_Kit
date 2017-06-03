package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.entity.TradeEntry;
import com.aaron.pseplanner.fragment.DatePickerFragment;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.ViewUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.aaron.pseplanner.service.CalculatorService.ONE_HUNDRED;

/**
 * Update Trade Plan Activity. Does not contain navigation views or menu items.
 */
public class UpdateTradePlanActivity extends SaveTradePlanActivity
{
    public static final String CLASS_NAME = UpdateTradePlanActivity.class.getSimpleName();
    private TradeDto tradeDtoPlanToUpdate;

    /**
     * Inflates the UI.
     *
     * @param savedInstanceState stores the current state: tradeDtoPlanToUpdate
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LogManager.debug(CLASS_NAME, "onCreate", "");

        if(savedInstanceState != null)
        {
            this.tradeDtoPlanToUpdate = savedInstanceState.getParcelable(DataKey.EXTRA_TRADE.toString());
        }
        else
        {
            Bundle bundle = getIntent().getExtras();
            if(bundle != null)
            {
                this.tradeDtoPlanToUpdate = bundle.getParcelable(DataKey.EXTRA_TRADE.toString());
            }
        }

        this.sharesEditText.setText(String.valueOf(tradeDtoPlanToUpdate.getTotalShares()));
        this.entryDateEditText.setText(DatePickerFragment.DATE_FORMATTER.format(tradeDtoPlanToUpdate.getEntryDate()));
        this.stopDateEditText.setText(DatePickerFragment.DATE_FORMATTER.format(tradeDtoPlanToUpdate.getStopDate()));
        this.stopLossEditText.setText(tradeDtoPlanToUpdate.getStopLoss().toPlainString());
        this.targetEditText.setText(tradeDtoPlanToUpdate.getTargetPrice().toPlainString());
        this.capitalEditText.setText(String.valueOf(tradeDtoPlanToUpdate.getCapital()));

        this.setEntryTranchesValues(this.layoutInflater, this.entryTranchesLayout, this.tradeDtoPlanToUpdate.getTradeEntries());

        LogManager.debug(CLASS_NAME, "onCreate", this.tradeDtoPlanToUpdate == null ? null : this.tradeDtoPlanToUpdate.toString());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_update_trade_plan);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView stockLabel = (TextView) findViewById(R.id.textview_stock);
        stockLabel.setText(this.tradeDtoPlanToUpdate.getSymbol());

        this.saveButton.setText(R.string.button_update_trade_plan);
    }

    /**
     * Sets each entry tranche to the view.
     */
    private void setEntryTranchesValues(LayoutInflater layoutInflater, LinearLayout entryTranchesLayout, List<TradeEntryDto> tradeEntries)
    {
        int entriesSize = tradeEntries.size() - 1; // Subtract one, because one tranche is already created

        for(int i = 0; i < entriesSize; i++)
        {
            this.addTranche(layoutInflater, entryTranchesLayout);
        }

        int numOfTranches = entryTranchesLayout.getChildCount();
        for(int i = 0; i < numOfTranches; i++)
        {
            View entryTrancheContainer = entryTranchesLayout.getChildAt(i);

            TextView labelTranche = (TextView) entryTrancheContainer.findViewById(R.id.label_tranche);
            labelTranche.setText(getString(R.string.label_tranche, ViewUtils.getOrdinalNumber(i)));

            EditText entryPrice = (EditText) entryTrancheContainer.findViewById(R.id.edittext_entry_price);
            entryPrice.setText(String.valueOf(tradeEntries.get(i).getEntryPrice()));

            EditText trancheWeight = (EditText) entryTrancheContainer.findViewById(R.id.edittext_tranche_weight);
            trancheWeight.setText(String.valueOf(tradeEntries.get(i).getPercentWeight()));
        }
    }

    /**
     * Saves current state in memory, when this activity is temporarily destroyed.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(this.tradeDtoPlanToUpdate != null)
        {
            outState.putParcelable(DataKey.EXTRA_TRADE.toString(), this.tradeDtoPlanToUpdate);
            LogManager.debug(CLASS_NAME, "onSaveInstanceState", "Trade Plan: " + this.tradeDtoPlanToUpdate);
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

        LogManager.debug(CLASS_NAME, "setActivityResultHome", "Result code: " + resultCode + " Trade Plan: " + this.tradeDtoPlanToUpdate);

        if(resultCode == Activity.RESULT_OK)
        {
            data.putExtra(DataKey.EXTRA_TRADE.toString(), this.tradeDtoPlanToUpdate);
        }

        setResult(resultCode, data);
        finish();
    }

    /**
     * Sets the saved trade dto then sends it to the main activity fragment.
     *
     * @param dto the saved trade plan
     */
    @Override
    protected void setActivityResultSaveClicked(TradeDto dto)
    {
        Intent data = new Intent();

        data.putExtra(DataKey.EXTRA_TRADE.toString(), dto);
        setResult(Activity.RESULT_OK, data);
        finish();

        LogManager.debug(CLASS_NAME, "setActivityResultHome", "TradeDto result: " + dto);
    }

    /**
     * Update database
     */
    @Override
    protected void saveTradePlan(TradeDto dto)
    {
        this.pseService.updateTradePlan(dto);
    }

    @Override
    protected String getSelectedSymbol()
    {
        return this.tradeDtoPlanToUpdate.getSymbol();
    }

    @Override
    protected BigDecimal getSelectedSymbolCurrentPrice()
    {
        return this.tradeDtoPlanToUpdate.getCurrentPrice();
    }
}