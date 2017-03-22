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
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;
import com.aaron.pseplanner.listener.ListRowOnTouchChangeActivity;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.ViewUtils;
import com.aaron.pseplanner.service.implementation.DefaultFormatService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2/17/2017.
 * Contains all trade plans, and is responsible for converting Trade bean to a UI row in the ListView.
 */
public class TradePlanListAdapter extends ArrayAdapter<TradeDto>
{
    private Activity activity;
    private FormatService formatService;
    private ArrayList<TradeDto> tradeDtoList;

    public TradePlanListAdapter(Activity activity, List<TradeDto> tradeDtoList)
    {
        super(activity, 0, tradeDtoList);

        this.activity = activity;
        this.formatService = new DefaultFormatService(activity);
        // ArrayList is used because this will be added in an intent
        this.tradeDtoList = (ArrayList<TradeDto>) tradeDtoList;
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

        TradeDto tradeDto = getItem(position);
        holder.setTickerView(tradeDto, this.formatService, new ListRowOnTouchChangeActivity(this.activity, TradePlanActivity.class, DataKey.EXTRA_TRADE, tradeDto, DataKey.EXTRA_TRADE_LIST, this.tradeDtoList, IntentRequestCode.VIEW_TRADE_PLAN, holder.scroll));

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

        void setTickerView(TradeDto tradeDto, FormatService service, View.OnTouchListener listener)
        {
            scroll.setOnTouchListener(listener);
            stock.setText(tradeDto.getSymbol());
            currentPrice.setText(service.formatStockPrice(tradeDto.getCurrentPrice().doubleValue()));
            averagePrice.setText(service.formatStockPrice(tradeDto.getAveragePrice().doubleValue()));
            String gainLossValue = ViewUtils.addPositiveSign(tradeDto.getGainLoss().doubleValue(), service.formatPrice(tradeDto.getGainLoss().doubleValue()));
            String gainLossPercentValue = ViewUtils.addPositiveSign(tradeDto.getGainLossPercent().doubleValue(), service.formatPercent(tradeDto.getGainLossPercent().doubleValue()));
            gainLoss.setText(String.format("%s (%s)", gainLossValue, gainLossPercentValue));
            shares.setText(service.formatShares(tradeDto.getTotalShares()));
            stopLoss.setText(service.formatPrice(tradeDto.getStopLoss().doubleValue()));
            entryDate.setText(service.formatDate(tradeDto.getEntryDate()));
            stopDate.setText(service.formatDate(tradeDto.getStopDate()));
            String holdingPeriodLabel = tradeDto.getHoldingPeriod() > 1 ? "days" : "day";
            holdingPeriod.setText(String.format("%s %s", tradeDto.getHoldingPeriod(), holdingPeriodLabel));

            service.formatTextColor(tradeDto.getGainLoss().doubleValue(), gainLoss);
        }
    }
}
