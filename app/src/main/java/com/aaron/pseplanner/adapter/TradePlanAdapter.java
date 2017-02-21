package com.aaron.pseplanner.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.activity.TradePlanActivity;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;
import com.aaron.pseplanner.listener.ListRowOnTouchChangeActivity;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.ViewUtils;
import com.aaron.pseplanner.service.implementation.FormatServiceImpl;

import java.util.List;

/**
 * Created by Aaron on 2/17/2017.
 * Contains all trade plans, and is responsible for converting Trade bean to a UI row in the ListView.
 */
public class TradePlanAdapter extends ArrayAdapter<Trade>
{
    private Activity activity;
    private FormatService formatService;

    public TradePlanAdapter(Activity activity, List<Trade> tradeList)
    {
        super(activity, 0, tradeList);

        this.activity = activity;
        this.formatService = new FormatServiceImpl(activity);
    }


    /**
     * Populates the ListView.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        ViewHolder holder;

        if(convertView == null)
        {
            convertView = this.activity.getLayoutInflater().inflate(R.layout.fragment_trade_plan_row, parent, false);

            holder = new ViewHolder();
            holder.stock = (TextView) convertView.findViewById(R.id.textview_stock);
            holder.currentPrice = (TextView) convertView.findViewById(R.id.textview_current_price);
            holder.averagePrice = (TextView) convertView.findViewById(R.id.textview_average_price);
            holder.gainLoss = (TextView) convertView.findViewById(R.id.textview_gain_loss);
            holder.shares = (TextView) convertView.findViewById(R.id.textview_shares);
            holder.stopLoss = (TextView) convertView.findViewById(R.id.textview_stop_loss);
            holder.entryDate = (TextView) convertView.findViewById(R.id.textview_entry_date);
            holder.stopDate = (TextView) convertView.findViewById(R.id.textview_stop_date);
            holder.holdingPeriod = (TextView) convertView.findViewById(R.id.textview_holding_period);
            holder.scroll = (HorizontalScrollView) convertView.findViewById(R.id.horizontalscroll_list_row);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Trade trade = getItem(position);
        holder.setTickerView(trade, this.formatService, new ListRowOnTouchChangeActivity(this.activity, TradePlanActivity.class, DataKey.EXTRA_TRADE, trade, IntentRequestCode.VIEW_TRADE_PLAN, holder.scroll));

        return convertView;
    }

    /**
     * Holds the references of all the views in a list row, to improve performance by preventing repeated call of findViewById().
     */
    private static class ViewHolder
    {
        TextView stock;
        TextView currentPrice;
        TextView averagePrice;
        TextView gainLoss;
        TextView shares;
        TextView stopLoss;
        TextView entryDate;
        TextView stopDate;
        TextView holdingPeriod;
        HorizontalScrollView scroll;

        void setTickerView(Trade trade, FormatService service, View.OnTouchListener listener)
        {
            scroll.setOnTouchListener(listener);
            stock.setText(trade.getSymbol());
            currentPrice.setText(service.formatStockPrice(trade.getCurrentPrice()));
            averagePrice.setText(service.formatStockPrice(trade.getAveragePrice()));
            String gainLossValue = ViewUtils.addPositiveSign(trade.getGainLoss(), service.formatPrice(trade.getGainLoss()));
            String gainLossPercentValue = ViewUtils.addPositiveSign(trade.getGainLossPercent(), service.formatPercent(trade.getGainLossPercent()));
            gainLoss.setText(String.format("%s (%s)", gainLossValue, gainLossPercentValue));
            shares.setText(service.formatShares(trade.getTotalShares()));
            stopLoss.setText(service.formatPrice(trade.getStopLoss()));
            entryDate.setText(service.formatDate(trade.getEntryDate()));
            stopDate.setText(service.formatDate(trade.getStopDate()));
            String holdingPeriodLabel = trade.getHoldingPeriod() > 1 ? "days" : "day";
            holdingPeriod.setText(String.format("%s %s", trade.getHoldingPeriod(), holdingPeriodLabel));

            service.formatTextColor(trade.getGainLoss(), gainLoss);
        }
    }
}
