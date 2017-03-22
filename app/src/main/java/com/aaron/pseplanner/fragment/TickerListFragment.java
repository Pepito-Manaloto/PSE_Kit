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
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.service.LogManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class TickerListFragment extends AbstractListFragment<TickerDto>
{
    public static final String CLASS_NAME = TickerListFragment.class.getSimpleName();
    private ArrayList<TickerDto> tickerDtoList;

    /**
     * Gets a new instance of TickerListFragment with the TickerDto list.
     *
     * @param list the list of Tickers
     * @return TickerListFragment
     */
    public static TickerListFragment newInstance(ArrayList<TickerDto> list)
    {
        TickerListFragment tickerListFragment = new TickerListFragment();

        if(list != null && !list.isEmpty())
        {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString(), list);
            tickerListFragment.setArguments(bundle);
        }

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

        if(getArguments() != null && getArguments().containsKey(DataKey.EXTRA_TICKER_LIST.toString()))
        {
            this.tickerDtoList = getArguments().getParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString());
        }
        else if(savedInstanceState != null && savedInstanceState.containsKey(DataKey.EXTRA_TICKER_LIST.toString()))
        {
            this.tickerDtoList = savedInstanceState.getParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString());
        }
        else
        {
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
        updateListOnUiThread(this.tickerDtoList, this.client.getLastUpdated());

        return view;
    }

    /**
     * Saves current state in memory.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString(), this.tickerDtoList);
        LogManager.debug(CLASS_NAME, "onSaveInstanceState", "");
    }

    @Override
    protected ArrayAdapter getArrayAdapter(List<TickerDto> tickerDtoList)
    {
        return new TickerListAdapter(getActivity(), tickerDtoList);
    }

    /**
     * Http request is blocking, this method MUST be called in an AsyncTask.
     *
     * @throws HttpRequestException if the http request failed, does not update the list
     */
    @Override
    public void updateList() throws HttpRequestException
    {
        Pair<List<TickerDto>, Date> response = this.client.getAllTickerList();
        this.tickerDtoList = (ArrayList<TickerDto>) response.first;

        updateListOnUiThread(response.first, this.formatService.formatLastUpdated(response.second));
    }

    @Override
    protected void saveListState()
    {
        getActivity().getIntent().putParcelableArrayListExtra(DataKey.EXTRA_TICKER_LIST.toString(), this.tickerDtoList);
    }

}
