package com.aaron.pseplanner.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aaron on 2/17/2017.
 * Contains all trade plans, and is responsible for converting Trade bean to a UI row in the ListView.
 */
public class TradePlanListAdapter extends FilterableArrayAdapter<TradeDto>
{
    public static final String CLASS_NAME = TradePlanListAdapter.class.getSimpleName();
    private Activity activity;
    private FormatService formatService;
    private ArrayList<TradeDto> tradeDtoList;
    private ArrayList<TradeDto> tradeDtoListTemp;

    public TradePlanListAdapter(Activity activity, List<TradeDto> tradeDtoList)
    {
        super(activity, 0, tradeDtoList);

        this.activity = activity;
        this.formatService = new DefaultFormatService(activity);
        // ArrayList is used because this will be added in an intent
        this.tradeDtoList = (ArrayList<TradeDto>) tradeDtoList;
        this.tradeDtoListTemp = new ArrayList<>(tradeDtoList);
    }

    /**
     * Populates the ListView.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        ViewHolder holder;
        View listRowView;

        if(convertView == null)
        {
            listRowView = this.activity.getLayoutInflater().inflate(R.layout.fragment_trade_plan_row, parent, false);
            holder = new ViewHolder(listRowView);

            listRowView.setTag(holder);
        }
        else
        {
            listRowView = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        TradeDto tradeDto = getItem(position);
        holder.setTickerView(tradeDto, this.formatService, new ListRowOnTouchChangeActivity(this.activity, TradePlanActivity.class, DataKey.EXTRA_TRADE, tradeDto, DataKey.EXTRA_TRADE_LIST, this.tradeDtoList, IntentRequestCode.VIEW_TRADE_PLAN, holder.scroll));

        return listRowView;
    }

    @Override
    protected ArrayList<TradeDto> getActualList()
    {
        return this.tradeDtoList;
    }

    @Override
    protected ArrayList<TradeDto> getTempList()
    {
        return this.tradeDtoListTemp;
    }

    /**
     * Holds the references of all the views in a list row, to improve performance by preventing repeated call of findViewById().
     * Cannot be private to be able to use butterknife.
     */
    static class ViewHolder
    {
        @BindView(R.id.textview_stock)
        TextView stock;

        @BindView(R.id.textview_current_price)
        TextView currentPrice;

        @BindView(R.id.textview_average_price)
        TextView averagePrice;

        @BindView(R.id.textview_gain_loss)
        TextView gainLoss;

        @BindView(R.id.textview_shares)
        TextView shares;

        @BindView(R.id.textview_stop_loss)
        TextView stopLoss;

        @BindView(R.id.textview_entry_date)
        TextView entryDate;

        @BindView(R.id.textview_stop_date)
        TextView stopDate;

        @BindView(R.id.textview_holding_period)
        TextView holdingPeriod;

        @BindView(R.id.horizontalscroll_list_row)
        HorizontalScrollView scroll;

        private ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }

        private void setTickerView(TradeDto tradeDto, FormatService service, View.OnTouchListener listener)
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
