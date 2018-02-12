package com.aaron.pseplanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.fragment.DatePickerFragment;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.ViewUtils;

import java.math.BigDecimal;
import java.util.List;

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

        initializeTradeDto(savedInstanceState);

        initializeEditTextValues();
        LogManager.debug(CLASS_NAME, "onCreate", this.tradeDtoPlanToUpdate == null ? null : this.tradeDtoPlanToUpdate.toString());

        this.stockLabel.setText(this.tradeDtoPlanToUpdate.getSymbol());
        this.saveButton.setText(R.string.button_update_trade_plan);
    }

    private void initializeTradeDto(Bundle savedInstanceState)
    {
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
    }

    private void initializeEditTextValues()
    {
        this.sharesEditText.setText(String.valueOf(tradeDtoPlanToUpdate != null ? tradeDtoPlanToUpdate.getTotalShares() : 0));
        this.entryDateEditText.setText(DatePickerFragment.DATE_FORMATTER.format(tradeDtoPlanToUpdate.getEntryDate()));
        this.stopDateEditText.setText(DatePickerFragment.DATE_FORMATTER.format(tradeDtoPlanToUpdate.getStopDate()));
        this.stopLossEditText.setText(tradeDtoPlanToUpdate.getStopLoss().toPlainString());
        this.targetEditText.setText(tradeDtoPlanToUpdate.getTargetPrice().toPlainString());
        this.capitalEditText.setText(String.valueOf(tradeDtoPlanToUpdate.getCapital()));

        this.setEntryTranchesValues(this.layoutInflater, this.entryTranchesLayout, this.tradeDtoPlanToUpdate.getTradeEntries());
    }

    /**
     * Sets each entry tranche to the view.
     */
    private void setEntryTranchesValues(LayoutInflater layoutInflater, LinearLayout entryTranchesLayout, List<TradeEntryDto> tradeEntries)
    {
        int entriesSize = tradeEntries.size() - 1; // Subtract one, because one tranche is already created

        for(int i = 0; i < entriesSize; i++)
        {
            View entryTrancheContainer = addTranche(layoutInflater, entryTranchesLayout);

            TextView labelTranche = entryTrancheContainer.findViewById(R.id.label_tranche);
            labelTranche.setText(getString(R.string.label_tranche, ViewUtils.getOrdinalNumber(i)));

            EditText entryPrice = entryTrancheContainer.findViewById(R.id.edittext_entry_price);
            entryPrice.setText(String.valueOf(tradeEntries.get(i).getEntryPrice()));

            EditText trancheWeight = entryTrancheContainer.findViewById(R.id.edittext_tranche_weight);
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
     * Sets the stock of the created trade plan if created.
     *
     * @param intent the intent to put the extra data
     */
    @Override
    protected void setIntentExtraOnResultHome(Intent intent)
    {
        LogManager.debug(CLASS_NAME, "setIntentExtraOnResultHome", "Intent extra is Trade Plan: " + this.tradeDtoPlanToUpdate);
        intent.putExtra(DataKey.EXTRA_TRADE.toString(), this.tradeDtoPlanToUpdate);
    }

    /**
     * Nothing to add to the intent.
     *
     * @param intent the intent to put the extra data
     */
    @Override
    protected void setIntentExtraOnResultSaveClicked(Intent intent)
    {
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

    @Override
    protected int getToolbarTitle()
    {
        return R.string.title_update_trade_plan;
    }
}