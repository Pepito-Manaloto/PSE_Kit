package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.bean.TradeEntry;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.fragment.DatePickerFragment;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.ViewUtils;

import java.util.List;

/**
 * Update Trade Plan Activity. Does not contain navigation views or menu items.
 */
public class UpdateTradePlanActivity extends SaveTradePlanActivity
{
    public static final String CLASS_NAME = UpdateTradePlanActivity.class.getSimpleName();
    private Trade tradePlanToUpdate;

    /**
     * Inflates the UI.
     *
     * @param savedInstanceState this Bundle is unused in this method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            this.tradePlanToUpdate = savedInstanceState.getParcelable(DataKey.EXTRA_TRADE.toString());
        }
        else
        {
            this.tradePlanToUpdate = getIntent().getParcelableExtra(DataKey.EXTRA_TRADE.toString());
        }

        this.sharesEditText.setText(String.valueOf(tradePlanToUpdate.getTotalShares()));
        this.entryDateEditText.setText(DatePickerFragment.DATE_FORMATTER.format(tradePlanToUpdate.getEntryDate()));
        this.stopDateEditText.setText(DatePickerFragment.DATE_FORMATTER.format(tradePlanToUpdate.getStopDate()));
        this.stopLossEditText.setText(tradePlanToUpdate.getStopLoss().toPlainString());
        this.targetEditText.setText(tradePlanToUpdate.getTargetPrice().toPlainString());
        this.capitalEditText.setText(String.valueOf(tradePlanToUpdate.getCapital()));

        this.setEntryTranchesValues(this.layoutInflater, this.entryTranchesLayout, this.tradePlanToUpdate.getTradeEntries());

        LogManager.debug(CLASS_NAME, "onCreate", this.tradePlanToUpdate == null ? null : this.tradePlanToUpdate.toString());

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
        stockLabel.setText(this.tradePlanToUpdate.getSymbol());

        this.saveButton.setText(R.string.button_update_trade_plan);
    }

    /**
     * Sets each entry tranche to the view.
     */
    private void setEntryTranchesValues(LayoutInflater layoutInflater, LinearLayout entryTranchesLayout, List<TradeEntry> tradeEntries)
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

        outState.putParcelable(DataKey.EXTRA_TRADE.toString(), this.tradePlanToUpdate);

        LogManager.debug(CLASS_NAME, "onSaveInstanceState", "");
    }

    /**
     * Sets the stock of the created trade plan if created, then sends it to the main activity.
     *
     * @param resultCode the result of the user's action
     */
    @Override
    protected void setActivityResult(int resultCode)
    {
        Intent data = new Intent();

        if(resultCode == Activity.RESULT_OK)
        {
            data.putExtra(DataKey.EXTRA_TRADE.toString(), this.tradePlanToUpdate);
        }

        setResult(resultCode, data);
        finish();
    }

    /**
     * Update database
     */
    @Override
    protected void saveTradePlan()
    {

    }
}