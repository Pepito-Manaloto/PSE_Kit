package com.aaron.pseplanner.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.activity.CreateTradePlanActivity;
import com.aaron.pseplanner.activity.TradePlanActivity;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;
import com.aaron.pseplanner.listener.ListRowOnTouchChangeActivity;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.implementation.DefaultFormatService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aaron.asuncion on 12/8/2016.
 * Contains all tickers, and is responsible for converting Ticker bean to a UI row in the ListView.
 */
public class TickerListAdapter extends FilterableArrayAdapter<TickerDto>
{
    public static final String CLASS_NAME = TickerListAdapter.class.getSimpleName();
    private Activity activity;
    private FormatService formatService;
    private ArrayList<TickerDto> tickerDtoList;
    private ArrayList<TickerDto> tickerDtoListTemp;


    public TickerListAdapter(Activity activity, List<TickerDto> tickerDtoList)
    {
        super(activity, 0, tickerDtoList);

        this.activity = activity;
        this.formatService = new DefaultFormatService(activity);
        this.tickerDtoList = (ArrayList<TickerDto>) tickerDtoList;
        this.tickerDtoListTemp = new ArrayList<>(tickerDtoList);
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
            listRowView = this.activity.getLayoutInflater().inflate(R.layout.fragment_ticker_row, parent, false);

            holder = new ViewHolder(listRowView);
            listRowView.setTag(holder);
        }
        else
        {
            listRowView = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        TickerDto tickerDto = getItem(position);
        holder.setTickerView(tickerDto, this.formatService, this.activity);

        return listRowView;
    }

    @Override
    protected ArrayList<TickerDto> getActualList()
    {
        return this.tickerDtoList;
    }

    @Override
    protected ArrayList<TickerDto> getTempList()
    {
        return this.tickerDtoListTemp;
    }

    /**
     * Holds the references of all the views in a list row, to improve performance by preventing repeated call of findViewById().
     * Cannot be private to be able to use butterknife.
     */
    static class ViewHolder
    {
        @BindView(R.id.ticker_stock_row)
        TextView stock;

        @BindView(R.id.ticker_price_row)
        TextView price;

        @BindView(R.id.ticker_change_row)
        TextView change;

        @BindView(R.id.ticker_percent_change_row)
        TextView percentChange;

        @BindView(R.id.ticker_icon_row)
        ImageView icon;

        @BindView(R.id.list_row_layout)
        LinearLayout layout;

        private ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }

        private void setTickerView(TickerDto tickerDto, FormatService service, Activity activity)
        {
            if(tickerDto.isHasTradePlan())
            {
                View.OnTouchListener listener = new ListRowOnTouchChangeActivity(activity, TradePlanActivity.class, DataKey.EXTRA_TICKER, tickerDto, IntentRequestCode.VIEW_TRADE_PLAN, this.layout);
                layout.setOnTouchListener(listener);

                icon.setImageResource(R.mipmap.check_icon);
            }
            else
            {
                View.OnTouchListener listener = new ListRowOnTouchChangeActivity(activity, CreateTradePlanActivity.class, DataKey.EXTRA_TICKER, tickerDto, IntentRequestCode.CREATE_TRADE_PLAN, this.layout);
                layout.setOnTouchListener(listener);

                // Why is it when this code is not present, some tickers', with hasTradePlan = false, icon is check_icon?
                icon.setImageResource(R.mipmap.add_button);
            }

            stock.setText(tickerDto.getSymbol());
            price.setText(service.formatStockPrice(tickerDto.getCurrentPrice().doubleValue()));
            change.setText(service.formatStockPrice(tickerDto.getChange().doubleValue()));
            String percentChangeText = service.formatStockPrice(tickerDto.getPercentChange().doubleValue()) + "%";
            percentChange.setText(percentChangeText);

            double changeDouble = tickerDto.getChange().doubleValue();
            service.formatTextColor(changeDouble, price);
            service.formatTextColor(changeDouble, change);
            service.formatTextColor(changeDouble, percentChange);
        }
    }
}
