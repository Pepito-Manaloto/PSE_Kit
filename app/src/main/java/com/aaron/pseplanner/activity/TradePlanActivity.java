package com.aaron.pseplanner.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.bean.TradeEntry;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.ViewUtils;
import com.aaron.pseplanner.service.implementation.FormatServiceImpl;

import java.util.List;

/**
 * Created by Aaron on 2/18/2017.
 */

public class TradePlanActivity extends AppCompatActivity
{
    public static final String CLASS_NAME = TradePlanActivity.class.getSimpleName();
    private Trade selectedStock;
    private FormatService formatService;

    /**
     * Inflates the UI.
     *
     * @param savedInstanceState this Bundle is unused in this method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_plan);

        if(savedInstanceState != null)
        {
            this.selectedStock = savedInstanceState.getParcelable(DataKey.EXTRA_TRADE.toString());
        }
        else
        {
            this.selectedStock = getIntent().getParcelableExtra(DataKey.EXTRA_TRADE.toString());
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

        this.formatService = new FormatServiceImpl(this);

        TextView stock = (TextView) findViewById(R.id.textview_stock);
        stock.setText(this.selectedStock.getSymbol());

        TextView entryDate = (TextView) findViewById(R.id.textview_entry_date);
        entryDate.setText(this.formatService.formatDate(this.selectedStock.getEntryDate()));

        TextView holdingPeriod = (TextView) findViewById(R.id.textview_holding_period);
        holdingPeriod.setText(String.valueOf(this.selectedStock.getHoldingPeriod()));

        TextView currentPrice = (TextView) findViewById(R.id.textview_current_price);
        currentPrice.setText(this.formatService.formatStockPrice(this.selectedStock.getCurrentPrice()));

        TextView totalShares = (TextView) findViewById(R.id.textview_total_shares);
        totalShares.setText(this.formatService.formatShares(this.selectedStock.getTotalShares()));

        TextView averagePrice = (TextView) findViewById(R.id.textview_average_price);
        averagePrice.setText(this.formatService.formatStockPrice(this.selectedStock.getAveragePrice()));

        TextView totalAmount = (TextView) findViewById(R.id.textview_total_amount);
        totalAmount.setText(this.formatService.formatPrice(this.selectedStock.getTotalAmount()));

        TextView gainLoss = (TextView) findViewById(R.id.textview_gain_loss);
        String gainLossValue = ViewUtils.addPositiveSign(this.selectedStock.getGainLoss(), this.formatService.formatPrice(this.selectedStock.getGainLoss()));
        String gainLossPercentValue = ViewUtils.addPositiveSign(this.selectedStock.getGainLossPercent(), this.formatService.formatPercent(this.selectedStock.getGainLossPercent()));
        gainLoss.setText(String.format("%s (%s)", gainLossValue, gainLossPercentValue));
        this.formatService.formatTextColor(this.selectedStock.getGainLoss(), gainLoss);

        this.setTranchesValues();

        TextView priceToBreakEven = (TextView) findViewById(R.id.textview_price_to_break_even);
        priceToBreakEven.setText(this.formatService.formatStockPrice(this.selectedStock.getPriceToBreakEven()));

        TextView target = (TextView) findViewById(R.id.textview_target);
        target.setText(this.formatService.formatStockPrice(this.selectedStock.getTargetPrice()));

        TextView gainTarget = (TextView) findViewById(R.id.textview_gain_target);
        String gainToTarget = "+" + this.formatService.formatPrice(this.selectedStock.getGainToTarget());
        gainTarget.setText(gainToTarget);
        this.formatService.formatTextColor(this.selectedStock.getGainToTarget(), gainTarget);

        TextView stopLoss = (TextView) findViewById(R.id.textview_stop_loss);
        stopLoss.setText(this.formatService.formatStockPrice(this.selectedStock.getStopLoss()));

        TextView lossStopLoss = (TextView) findViewById(R.id.textview_loss_stop_loss);
        lossStopLoss.setText(this.formatService.formatPrice(this.selectedStock.getLossToStopLoss()));
        this.formatService.formatTextColor(this.selectedStock.getLossToStopLoss(), lossStopLoss);

        TextView stopDate = (TextView) findViewById(R.id.textview_stop_date);
        stopDate.setText(this.formatService.formatDate(this.selectedStock.getStopDate()));

        TextView daysToStopDate = (TextView) findViewById(R.id.textview_days_to_stop_date);
        daysToStopDate.setText(String.valueOf(this.selectedStock.getDaysToStopDate()));

        TextView riskReward = (TextView) findViewById(R.id.textview_risk_reward);
        riskReward.setText(String.valueOf(this.selectedStock.getRiskReward()));

        TextView capital = (TextView) findViewById(R.id.textview_capital);
        capital.setText(this.formatService.formatPrice(this.selectedStock.getCapital()));

        TextView percentOfCapital = (TextView) findViewById(R.id.textview_percent_of_capital);
        percentOfCapital.setText(this.formatService.formatPercent(this.selectedStock.getPercentCapital()));
    }

    /**
     * Sets each entry tranche to the view.
     */
    private void setTranchesValues()
    {
        LinearLayout entryTranchesContainer = (LinearLayout) findViewById(R.id.entry_tranches_container);

        List<TradeEntry> tradeEntries = this.selectedStock.getTradeEntries();
        LayoutInflater inflater = LayoutInflater.from(this);
        int entryTranchNum = 0;
        for(TradeEntry entry : tradeEntries)
        {
            View entryTrancheLayout = inflater.inflate(R.layout.entry_tranche, null, false);
            TextView labelTranche = (TextView) entryTrancheLayout.findViewById(R.id.label_tranche);
            labelTranche.setText(getString(R.string.label_tranche, ViewUtils.getOrdinalNumber(entryTranchNum)));

            TextView entryPrice = (TextView) entryTrancheLayout.findViewById(R.id.textview_entry_price);
            entryPrice.setText(this.formatService.formatStockPrice(entry.getEntryPrice()));
            TextView shares = (TextView) entryTrancheLayout.findViewById(R.id.textview_shares);
            shares.setText(this.formatService.formatShares(entry.getShares()));
            TextView weight = (TextView) entryTrancheLayout.findViewById(R.id.textview_tranche_weight);
            weight.setText(this.formatService.formatPercent(entry.getPercentWeight()));

            entryTranchNum++;

            entryTranchesContainer.addView(entryTrancheLayout);
        }
    }

    /**
     * Initializes the toolbar(update and delete button).
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_trade_plan_options, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method is called when a user selects an item in the menu bar. Home button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                // Finish activity with no result to send back to home
                finish();
                return true;
            }
            case R.id.menu_update:
            {
                // TODO: go to update trade plan activity
            }
            case R.id.menu_delete:
            {
                createAndShowAlertDialog(this.selectedStock.getSymbol());
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * Creates and show the prompt dialog before deleting the selected trade plan.
     */
    private void createAndShowAlertDialog(String stock)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_trade_plan_prompt, stock));

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                deleteTradePlan();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Align message to center.
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        if(messageView != null)
        {
            messageView.setGravity(Gravity.CENTER);
        }
    }

    private void deleteTradePlan()
    {
        // TODO: delete this trade plan
        finish();
    }
}
