package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.TickerListAdapter;
import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.exception.HttpRequestException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class TickerListFragment extends AbstractListFragment<Ticker>
{
    public static final String CLASS_NAME = TickerListFragment.class.getSimpleName();
    private ArrayList<Ticker> tickerList;

    /**
     * Gets a new instance of TickerListFragment with the Ticker list.
     *
     * @param list the list of Tickers
     * @return TickerListFragment
     */
    public static TickerListFragment newInstance(ArrayList<Ticker> list)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString(), list);

        TickerListFragment tickerListFragment = new TickerListFragment();
        tickerListFragment.setArguments(bundle);

        return tickerListFragment;
    }

    /**
     * Initializes non-fragment user interface.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(DataKey.EXTRA_TICKER_LIST.toString()))
        {
            this.tickerList = getArguments().getParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString());

        }
        else if(savedInstanceState.containsKey(DataKey.EXTRA_TICKER_LIST.toString()))
        {
            this.tickerList = savedInstanceState.getParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString());
        }
        else
        {
            this.tickerList = new ArrayList<>();
            //TODO: retrieve from database
        }
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.list_fragment_ticker, parent, false);
        this.lastUpdatedTextView = (TextView) view.findViewById(R.id.textview_last_updated);
        updateListOnUiThread(this.tickerList, this.client.getLastUpdated());

        return view;
    }

    /**
     * Saves current state in memory.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString(), this.tickerList);
    }

    @Override
    protected ArrayAdapter getArrayAdapter(List<Ticker> tickerList)
    {
        return new TickerListAdapter(getActivity(), tickerList);
    }

    /**
     * Http request is blocking, this method MUST be called in an AsyncTask.
     *
     * @throws HttpRequestException if the http request failed, does not update the list
     */
    @Override
    public void updateList() throws HttpRequestException
    {
        Pair<List<Ticker>, Date> response = this.client.getTickerList();

        updateListOnUiThread(response.first, this.formatService.formatLastUpdated(response.second));
    }
}
