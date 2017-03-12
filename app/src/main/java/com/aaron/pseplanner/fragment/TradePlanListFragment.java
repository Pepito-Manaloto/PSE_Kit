package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.TradePlanListAdapter;
import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.bean.TradeEntry;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.implementation.DefaultCalculatorService;

import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class TradePlanListFragment extends AbstractListFragment<Trade>
{
    public static final String CLASS_NAME = TradePlanListFragment.class.getSimpleName();

    // This storage needs to be thread-safe because this will be modified in AsyncTask
    private ConcurrentHashMap<String, Trade> tradesMap;
    private ArrayList<Trade> tradeList;
    private CalculatorService calculatorService;

    /**
     * Gets a new instance of TradePlanListFragment with the Trade list.
     *
     * @param list the list of Trade
     * @return TickerListFragment
     */
    public static TradePlanListFragment newInstance(ArrayList<Trade> list)
    {
        TradePlanListFragment tradePlanListFragment = new TradePlanListFragment();

        if(list != null && !list.isEmpty())
        {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString(), list);
            tradePlanListFragment.setArguments(bundle);
        }

        return tradePlanListFragment;
    }

    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.calculatorService = new DefaultCalculatorService();

        if(getArguments() != null && getArguments().containsKey(DataKey.EXTRA_TRADE_LIST.toString()))
        {
            this.tradeList = getArguments().getParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString());
        }
        else if(savedInstanceState != null && savedInstanceState.containsKey(DataKey.EXTRA_TRADE_LIST.toString()))
        {
            this.tradeList = savedInstanceState.getParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString());
        }
        else
        {
            // TODO: retrieve and parse data from database
            this.tradesMap = new ConcurrentHashMap<>();
            for(int i = 0; i < 5; i++)
            {
                this.tradesMap.put("CPG", new Trade("CPG", new Date(), 1, 0.53, 0.55, 300_000, 156_000, 0.567, 0.6, -28_020, -3.86, 53_543, 0.51, -10_000.7, new Date(), 23, 2.34, 500_000, 30.42, Arrays.asList(new TradeEntry("CPG", 0.54, 150_000, 50), new TradeEntry("CPG", 0.55, 150_000, 50))));
                this.tradesMap.put("CYBR", new Trade("CYBR", new Date(), 14, 0.56, 0.55, 500_000, 256_000, 0.577, 0.6, 98_120, 2.86, 53_543, 0.53, -40_000, new Date(), 56, 5.8, 500_000, 20.42, Arrays.asList(new TradeEntry("CYBR", 0.54, 200_000, 37), new TradeEntry("CYBR", 0.55, 150_000, 31.5), new TradeEntry("CYBR", 0.56, 150_000, 31.5))));
                this.tradesMap.put("GERI", new Trade("GERI", new Date(), 63, 1.02, 0.98, 800_000, 891_020, 0.989, 1.1, 138_020, 6.86, 53_543, 0.91, -7_000.34, new Date(), 87, 8.14, 700_000, 57.42, Arrays.asList(new TradeEntry("GERI", 1.02, 800_000, 100))));
            }

            this.tradeList = new ArrayList<>(this.tradesMap.values());
        }
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.list_fragment_trade_plan, parent, false);
        this.lastUpdatedTextView = (TextView) view.findViewById(R.id.textview_last_updated);
        updateListOnUiThread(this.tradeList, this.client.getLastUpdated());

        return view;
    }

    /**
     * Saves current state in memory.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString(), this.tradeList);

        LogManager.debug(CLASS_NAME, "onSaveInstanceState", "");
    }

    @Override
    protected ArrayAdapter getArrayAdapter(List<Trade> tradeList)
    {
        return new TradePlanListAdapter(getActivity(), tradeList);
    }

    /**
     * Http request is blocking, this method MUST be called in an AsyncTask.
     *
     * @throws HttpRequestException if the http request failed, does not update the list
     */
    @Override
    public void updateList() throws HttpRequestException
    {
        Pair<List<Ticker>, Date> response = this.client.getTickerList(this.tradesMap.keySet());

        List<Ticker> tickerList = response.first;
        Date lastUpdated = response.second;

        if(!tickerList.isEmpty())
        {
            // Update current price of each trade plan based on ticker
            for(Ticker ticker : tickerList)
            {
                // TODO: should consider weekend in days difference??? lastupdated will always be friday 3:20PM before market open
                Trade trade = this.tradesMap.get(ticker.getSymbol());
                trade.setCurrentPrice(ticker.getCurrentPrice());
                trade.setDaysToStopDate(this.calculatorService.getDaysBetween(lastUpdated, trade.getStopDate()));
                trade.setHoldingPeriod(this.calculatorService.getDaysBetween(lastUpdated, trade.getEntryDate()));
                trade.setGainLoss(this.calculatorService.getGainLossAmount(trade.getAveragePrice(), trade.getTotalShares(), trade.getCurrentPrice()));
                trade.setGainLossPercent(this.calculatorService.getPercentGainLoss(trade.getAveragePrice(), trade.getTotalShares(), trade.getCurrentPrice()));
                trade.setTotalAmount(this.calculatorService.getBuyNetAmount(trade.getCurrentPrice(), trade.getTotalShares()));
            }

            this.tradeList = new ArrayList<>(this.tradesMap.values());

            updateListOnUiThread(this.tradeList, this.formatService.formatLastUpdated(lastUpdated));
        }
    }

    @Override
    protected void saveListState()
    {
        getActivity().getIntent().putParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString(), this.tradeList);
    }

}
