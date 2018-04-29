package com.aaron.pseplanner.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.activity.UpdateTradePlanActivity;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;
import com.aaron.pseplanner.constant.TrancheStatus;
import com.aaron.pseplanner.listener.ImageViewOnClickHideExpand;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.ViewUtils;
import com.aaron.pseplanner.service.implementation.TradePlanFormatService;
import com.aaron.pseplanner.service.implementation.FacadePSEPlannerService;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by aaron.asuncion on 2/22/2017.
 */

public class TradePlanFragment extends Fragment
{
    public static final String CLASS_NAME = TradePlanFragment.class.getSimpleName();

    private TradeDto selectedStock;
    private FormatService formatService;
    private PSEPlannerService pseService;
    private Unbinder unbinder;

    @BindView(R.id.textview_stock)
    TextView stock;

    @BindView(R.id.textview_date_planned)
    TextView datePlanned;

    @BindView(R.id.textview_days_since_planned)
    TextView daysSincePlanned;

    @BindView(R.id.textview_entry_date)
    TextView entryDate;

    @BindView(R.id.textview_holding_period)
    TextView holdingPeriod;

    @BindView(R.id.textview_current_price)
    TextView currentPrice;

    @BindView(R.id.textview_total_shares)
    TextView totalShares;

    @BindView(R.id.textview_average_price)
    TextView averagePrice;

    @BindView(R.id.textview_total_amount)
    TextView totalAmount;

    @BindView(R.id.textview_gain_loss)
    TextView gainLoss;

    @BindView(R.id.entry_tranches_container)
    LinearLayout entryTranchesContainer;

    @BindView(R.id.imageview_entry)
    ImageView trancheImageView;

    @BindView(R.id.textview_price_to_break_even)
    TextView priceToBreakEven;

    @BindView(R.id.textview_target)
    TextView target;

    @BindView(R.id.textview_gain_target)
    TextView gainTarget;

    @BindView(R.id.textview_stop_loss)
    TextView stopLoss;

    @BindView(R.id.textview_loss_stop_loss)
    TextView lossStopLoss;

    @BindView(R.id.textview_stop_date)
    TextView stopDate;

    @BindView(R.id.textview_days_to_stop_date)
    TextView daysToStopDate;

    @BindView(R.id.textview_risk_reward)
    TextView riskReward;

    @BindView(R.id.textview_capital)
    TextView capital;

    @BindView(R.id.textview_percent_of_capital)
    TextView percentOfCapital;

    /**
     * Creates a new TradePlanFragment instance and stores the passed TradeDto data as argument.
     * Note: Android will call no-argument constructor of a fragment when it decides to recreate the fragment;
     * hence, overloading a fragment constructor for data passing will not be able to save the passed data.
     * That is why this static initializer is used.
     * There is also no way to pass data to TradePlanPageAdapter through savedInstanceState intent, that is why we pass data through instance creation.
     *
     * @param selectedStock the selected trade
     */
    public static TradePlanFragment newInstance(TradeDto selectedStock)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DataKey.EXTRA_TRADE.toString(), selectedStock);

        TradePlanFragment tradePlanFragment = new TradePlanFragment();
        tradePlanFragment.setArguments(bundle);

        return tradePlanFragment;
    }

    /**
     * Initialize none UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LogManager.debug(CLASS_NAME, "onCreate", "");

        Activity activity = getActivity();
        if(activity != null)
        {
            Bundle args = getArguments();

            if(args != null)
            {
                this.selectedStock = args.getParcelable(DataKey.EXTRA_TRADE.toString());
            }

            this.formatService = new TradePlanFormatService(getActivity());
            this.pseService = new FacadePSEPlannerService(getActivity());
            setHasOptionsMenu(true);
        }

        LogManager.debug(CLASS_NAME, "onCreate", this.selectedStock == null ? null : this.selectedStock.toString());
    }

    /**
     * Initialize TradeDto Plan UI view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        LogManager.debug(CLASS_NAME, "onCreateView", "");

        ScrollView view = (ScrollView) inflater.inflate(R.layout.fragment_trade_plan, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        stock.setText(this.selectedStock.getSymbol());

        String datePlannedStr = this.formatService.formatDate(this.selectedStock.getDatePlanned());
        datePlanned.setText(datePlannedStr);

        String daysSincePlannedLabel = this.selectedStock.getDaysSincePlanned() > 1 ? "days" : "day";
        daysSincePlanned.setText(String.format("%s %s", this.selectedStock.getDaysSincePlanned(), daysSincePlannedLabel));

        String entryDateStr = this.formatService.formatDate(this.selectedStock.getEntryDate());
        boolean isEntryDateSet = StringUtils.isNotBlank(entryDateStr);
        entryDate.setText(isEntryDateSet ? entryDateStr : "None");

        String holdingPeriodLabel = this.selectedStock.getHoldingPeriod() > 1 ? "days" : "day";
        holdingPeriod.setText(String.format("%s %s", this.selectedStock.getHoldingPeriod(), holdingPeriodLabel));

        currentPrice.setText(this.formatService.formatStockPrice(this.selectedStock.getCurrentPrice().doubleValue()));

        totalShares.setText(this.formatService.formatShares(this.selectedStock.getTotalShares()));

        averagePrice.setText(this.formatService.formatStockPrice(this.selectedStock.getAveragePrice().doubleValue()));

        totalAmount.setText(this.formatService.formatPrice(this.selectedStock.getTotalAmount().doubleValue()));

        if(isEntryDateSet)
        {
            String gainLossValue = ViewUtils.addPositiveSign(this.selectedStock.getGainLoss().doubleValue(),
                    this.formatService.formatPrice(this.selectedStock.getGainLoss().doubleValue()));
            String gainLossPercentValue = ViewUtils.addPositiveSign(this.selectedStock.getGainLossPercent().doubleValue(),
                    this.formatService.formatPercent(this.selectedStock.getGainLossPercent().doubleValue()));
            gainLoss.setText(String.format("%s (%s)", gainLossValue, gainLossPercentValue));
            this.formatService.formatTextColor(this.selectedStock.getGainLoss().doubleValue(), gainLoss);
        }
        else
        {
            gainLoss.setText("-");
        }

        trancheImageView.setOnClickListener(new ImageViewOnClickHideExpand(getContext(), trancheImageView, entryTranchesContainer));
        this.setTranchesValues(entryTranchesContainer);

        priceToBreakEven.setText(this.formatService.formatStockPrice(this.selectedStock.getPriceToBreakEven().doubleValue()));

        target.setText(this.formatService.formatStockPrice(this.selectedStock.getTargetPrice().doubleValue()));

        double gainToTargetValue = this.selectedStock.getGainToTarget().doubleValue();
        if(gainToTargetValue == 0)
        {
            gainTarget.setText("-");
        }
        else
        {
            String gainToTarget = "+" + this.formatService.formatPrice(gainToTargetValue);
            gainTarget.setText(gainToTarget);
            this.formatService.formatTextColor(gainToTargetValue, gainTarget);
        }

        stopLoss.setText(this.formatService.formatStockPrice(this.selectedStock.getStopLoss().doubleValue()));

        lossStopLoss.setText(this.formatService.formatPrice(this.selectedStock.getLossToStopLoss().doubleValue()));
        this.formatService.formatTextColor(this.selectedStock.getLossToStopLoss().doubleValue(), lossStopLoss);

        stopDate.setText(this.formatService.formatDate(this.selectedStock.getStopDate()));

        daysToStopDate.setText(String.valueOf(this.selectedStock.getDaysToStopDate()));

        riskReward.setText(this.formatService.formatStockPrice(this.selectedStock.getRiskReward().doubleValue()));

        capital.setText(this.formatService.formatPrice(this.selectedStock.getCapital()));

        percentOfCapital.setText(this.formatService.formatPercent(this.selectedStock.getPercentCapital().doubleValue()));

        return view;
    }

    /**
     * Sets each entry tranche to the view.
     */
    private void setTranchesValues(LinearLayout entryTranchesContainer)
    {
        List<TradeEntryDto> tradeEntries = this.selectedStock.getTradeEntries();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        int entryTrancheNum = 0;
        for(TradeEntryDto entry : tradeEntries)
        {
            View entryTrancheLayout = inflater.inflate(R.layout.entry_tranche, null, false);
            TextView labelTranche = entryTrancheLayout.findViewById(R.id.label_tranche);
            labelTranche.setText(getString(R.string.label_tranche, ViewUtils.getOrdinalNumber(entryTrancheNum)));

            TextView entryPrice = entryTrancheLayout.findViewById(R.id.textview_entry_price);
            entryPrice.setText(this.formatService.formatStockPrice(entry.getEntryPrice().doubleValue()));

            TextView shares = entryTrancheLayout.findViewById(R.id.textview_shares);
            shares.setText(this.formatService.formatShares(entry.getShares()));

            TextView weight = entryTrancheLayout.findViewById(R.id.textview_tranche_weight);
            weight.setText(this.formatService.formatPercent(entry.getPercentWeight().doubleValue()));

            TextView status = entryTrancheLayout.findViewById(R.id.textview_tranche_status);
            status.setText(TrancheStatus.getTrancheStatus(entry.isExecuted()));

            entryTrancheNum++;
            entryTranchesContainer.addView(entryTrancheLayout);
        }
    }

    /**
     * Initializes the toolbar(update and delete button).
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.toolbar_trade_plan_options, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This method is called when a user selects an item in the menu bar. Home button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Activity activity = getActivity();
        if(activity != null)
        {
            switch(item.getItemId())
            {
                case android.R.id.home:
                {
                    // Finish activity with no result to send back to home
                    getActivity().finish();
                    return true;
                }
                case R.id.menu_update:
                {
                    Intent intent = new Intent(getActivity(), UpdateTradePlanActivity.class);
                    intent.putExtra(DataKey.EXTRA_TRADE.toString(), this.selectedStock);
                    getActivity().startActivityForResult(intent, IntentRequestCode.UPDATE_TRADE_PLAN.code());

                    return true;
                }
                case R.id.menu_delete:
                {
                    createAndShowAlertDialog(this.selectedStock.getSymbol());
                    return true;
                }
                default:
                {
                    return super.onOptionsItemSelected(item);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates and show the prompt dialog before deleting the selected trade plan.
     */
    private void createAndShowAlertDialog(String stock)
    {
        Activity activity = getActivity();
        if(activity != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            TextView messageView = dialog.findViewById(android.R.id.message);
            if(messageView != null)
            {
                messageView.setGravity(Gravity.CENTER);
            }
        }
    }

    /**
     * Deletes the selected trade plan and sets it as extra to the Main activity to update trade plan list and ticker list.
     */
    private void deleteTradePlan()
    {
        this.pseService.deleteTradePlan(this.selectedStock);

        Intent data = new Intent();
        data.putExtra(DataKey.EXTRA_TRADE.toString(), this.selectedStock);

        Activity activity = getActivity();
        if(activity != null)
        {
            activity.setResult(Activity.RESULT_OK, data);
            activity.finish();
        }

        LogManager.debug(CLASS_NAME, "deleteTradePlan", "Deleted: " + this.selectedStock);
    }

    /**
     * A Fragment may continue to exist after its Views are destroyed, you need to call .unbind() from a Fragment to release the reference to the Views (and allow the associated
     * memory to be reclaimed).
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if(this.unbinder != null)
        {
            this.unbinder.unbind();
        }
    }
}
