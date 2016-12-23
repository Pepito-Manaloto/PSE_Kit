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
import com.aaron.pseplanner.service.implementation.StockServiceImpl;
import com.aaron.pseplanner.service.StockService;

import java.util.List;

/**
 * Created by aaron.asuncion on 12/8/2016.
 */

public class TickerAdapter extends ArrayAdapter<Ticker>
{
    private Activity activity;
    private List<Ticker> tickerList;
    private StockService stockService;

    public TickerAdapter(Activity activity, List<Ticker> tickerList)
    {
        super(activity, 0, tickerList);

        this.activity = activity;
        this.tickerList = tickerList;
        this.stockService = new StockServiceImpl();
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
        holder.setTickerView(ticker, this.stockService, this.activity, new ListRowOnTouchChangeActivity(this.activity, /*TODO: CreateTradePlanActivity.class*/ null, ticker, holder.layout));

        return convertView;
    }

    private static class ViewHolder
    {
        TextView stock;
        TextView price;
        TextView change;
        TextView percentChange;
        LinearLayout layout;

        void setTickerView(Ticker ticker, StockService service, Activity activity, View.OnTouchListener listener)
        {
            layout.setOnTouchListener(listener);
            stock.setText(ticker.getSymbol());
            price.setText(service.formatStockPrice(ticker.getCurrentPrice()));
            change.setText(service.formatStockPrice(ticker.getChange()));
            String percentChangeText = service.formatStockPrice(ticker.getPercentChange()) + "%";
            percentChange.setText(percentChangeText);

            if(ticker.getChange() > 0)
            {
                int colorGreen;
                if(Build.VERSION.SDK_INT >= 23)
                {
                    colorGreen = activity.getColor(R.color.darkGreen);
                }
                else
                {
                    colorGreen = activity.getResources().getColor(R.color.darkGreen);
                }

                price.setTextColor(colorGreen);
                change.setTextColor(colorGreen);
                percentChange.setTextColor(colorGreen);
            }
            else if(ticker.getChange() < 0)
            {
                price.setTextColor(Color.RED);
                change.setTextColor(Color.RED);
                percentChange.setTextColor(Color.RED);
            }
        }
    }
}
