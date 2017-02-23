package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.TradePlanListAdapter;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.bean.TradeEntry;
import com.aaron.pseplanner.service.PSEClientService;
import com.aaron.pseplanner.service.implementation.PSEClientServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class TradePlanListFragment extends AbstractListFragment
{
    public static final String CLASS_NAME = TradePlanListFragment.class.getSimpleName();
    private ArrayList<Trade> tradeList;
    private PSEClientService client;

    /**
     * Initializes non-fragment user interface.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.client = new PSEClientServiceImpl();

        // TODO: retrieve and parse data from database
        this.tradeList = new ArrayList<>();

        for(int i = 0; i < 5; i++)
        {
            this.tradeList.add(new Trade("CPG",
                    new Date(), 1, 0.53, 0.55, 300_000, 156_000, 0.567, 0.6, -28_020, -3.86, 53_543, 0.51, -10_000.7, new Date(), 23, 2.34, 500_000, 30.42,
                    Arrays.asList(new TradeEntry("CPG", 0.54, 150_000, 50), new TradeEntry("CPG", 0.55, 150_000, 50))));
            this.tradeList.add(new Trade("CYBR",
                    new Date(), 14, 0.56, 0.55, 500_000, 256_000, 0.577, 0.6, 98_120, 2.86, 53_543, 0.53, -40_000, new Date(), 56, 5.8, 500_000, 20.42,
                    Arrays.asList(new TradeEntry("CYBR", 0.54, 200_000, 37), new TradeEntry("CYBR", 0.55, 150_000, 31.5), new TradeEntry("CYBR", 0.56, 150_000, 31.5))));
            this.tradeList.add(new Trade("GERI",
                    new Date(), 63, 1.02, 0.98, 800_000, 891_020, 0.989, 1.1, 138_020, 6.86, 53_543, 0.91, -7_000.34, new Date(), 87, 8.14, 700_000, 57.42,
                    Arrays.asList(new TradeEntry("GERI", 1.02, 800_000, 100))));
        }

        updateListOnUiThread(this.tradeList);
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.list_fragment_trade_plan, parent, false);

        TextView lastUpdated = (TextView) view.findViewById(R.id.textview_last_updated);
        lastUpdated.setText(getActivity().getString(R.string.last_updated, client.getLastUpdated()));

        return view;
    }

    @Override
    protected ArrayAdapter getArrayAdapter()
    {
        return new TradePlanListAdapter(getActivity(), tradeList);
    }
}
