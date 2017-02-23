package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.TickerListAdapter;
import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.service.PSEClientService;
import com.aaron.pseplanner.service.implementation.PSEClientServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class TickerListFragment extends AbstractListFragment
{
    public static final String CLASS_NAME = TickerListFragment.class.getSimpleName();
    private List<Ticker> tickerList;
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

        // TODO: retrieve and parse data from PSE
        this.tickerList = new ArrayList<>();

        for(int i = 0; i < 15; i++)
        {
            this.tickerList.add(new Ticker("CPG", 110.56, 0, 0));
            this.tickerList.add(new Ticker("CYBR", 2330.55, -0.01, -1.1));
            this.tickerList.add(new Ticker("TAE", 0.1234, 0.61, 2.65));
        }

        updateListOnUiThread(this.tickerList);
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.list_fragment_ticker, parent, false);

        TextView lastUpdated = (TextView) view.findViewById(R.id.textview_last_updated);
        lastUpdated.setText(getActivity().getString(R.string.last_updated, client.getLastUpdated()));

        return view;
    }


    @Override
    protected ArrayAdapter getArrayAdapter()
    {
        return new TickerListAdapter(getActivity(), tickerList);
    }
}
