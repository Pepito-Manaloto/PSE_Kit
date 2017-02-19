package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.TradePlanAdapter;
import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.service.PSEClientService;
import com.aaron.pseplanner.service.implementation.PSEClientServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class TradePlanListFragment extends AbstractListFragment
{
    public static final String CLASS_NAME = TradePlanListFragment.class.getSimpleName();
    private List<Trade> tradeList;
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

        for(int i = 0; i < 15; i++)
        {
            this.tradeList.add(new Trade(
                    10_000.7, new Date(), 28, "CPG", 0.53, 0.55, 300_000, 156_000, 0.567, 0.6, -28_020, -3.86, 53_543, 0.51, new Date(), 23, 2.34, 500_000, 30.42, null));
            this.tradeList.add(new Trade(
                    40_000, new Date(), 14, "CYBR", 0.56, 0.55, 500_000, 256_000, 0.577, 0.6, 98_120, 2.86, 53_543, 0.53, new Date(), 56, 5.8, 500_000, 20.42, null));
            this.tradeList.add(new Trade(
                    7_000.34, new Date(), 63, "GERI", 1.02, 0.98, 800_000, 891_020, 0.989, 1.1, 138_020, 6.86, 53_543, 0.91, new Date(), 87, 8.14, 700_000, 57.42, null));
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
        return new TradePlanAdapter(getActivity(), tradeList);
    }
}
