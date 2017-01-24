package com.aaron.pseplanner.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.listener.ListRowOnTouchChangeActivity;
import com.aaron.pseplanner.service.implementation.FormatServiceImpl;
import com.aaron.pseplanner.service.FormatService;

import java.util.List;

/**
 * Created by aaron.asuncion on 12/8/2016.
 * Contains all tickers, and is responsible for converting Ticker bean to a UI row in the ListView.
 */
public class TickerAdapter extends ArrayAdapter<Ticker>
{
    private Activity activity;
    private List<Ticker> tickerList;
    private FormatService formatService;

    public TickerAdapter(Activity activity, List<Ticker> tickerList)
    {
        super(activity, 0, tickerList);

        this.activity = activity;
        this.tickerList = tickerList;
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
            convertView = this.activity.getLayoutInflater().inflate(R.layout.fragment_ticker_row, parent, false);

            holder = new ViewHolder();
            holder.stock = (TextView) convertView.findViewById(R.id.ticker_stock_row);
            holder.price = (TextView) convertView.findViewById(R.id.ticker_price_row);
            holder.change = (TextView) convertView.findViewById(R.id.ticker_change_row);
            holder.percentChange = (TextView) convertView.findViewById(R.id.ticker_percent_change_row);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.list_row_layout);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Ticker ticker = getItem(position);
        holder.setTickerView(ticker, this.formatService, this.activity, new ListRowOnTouchChangeActivity(this.activity, /*TODO: CreateTradePlanActivity.class*/ null, ticker, holder.layout));

        return convertView;
    }

    private static class ViewHolder
    {
        TextView stock;
        TextView price;
        TextView change;
        TextView percentChange;
        LinearLayout layout;

        void setTickerView(Ticker ticker, FormatService service, Activity activity, View.OnTouchListener listener)
        {
            layout.setOnTouchListener(listener);
            stock.setText(ticker.getSymbol());
            price.setText(service.formatStockPrice(ticker.getCurrentPrice()));
            change.setText(service.formatStockPrice(ticker.getChange()));
            String percentChangeText = service.formatStockPrice(ticker.getPercentChange()) + "%";
            percentChange.setText(percentChangeText);

            service.formatTextColor(ticker.getChange(), price);
            service.formatTextColor(ticker.getChange(), change);
            service.formatTextColor(ticker.getChange(), percentChange);
        }
    }
}
