package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.FilterableArrayAdapter;
import com.aaron.pseplanner.adapter.TickerListAdapter;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.PSEPlannerPreference;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.listener.SearchOnQueryTextListener;
import com.aaron.pseplanner.service.LogManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class TickerListFragment extends AbstractListFragment<TickerDto>
{
    public static final String CLASS_NAME = TickerListFragment.class.getSimpleName();
    private ArrayList<TickerDto> tickerDtoList;
    private ArrayList<TradeDto> tradeDtoList;
    private Set<String> tradeDtoSymbols;

    /**
     * Gets a new instance of TickerListFragment with the TickerDto list.
     *
     * @param tickerDtoArgList the list of Tickers
     * @param tradeDtoArgList  the list of Trade Plans
     * @return TickerListFragment
     */
    public static TickerListFragment newInstance(ArrayList<TickerDto> tickerDtoArgList, ArrayList<TradeDto> tradeDtoArgList)
    {
        TickerListFragment tickerListFragment = new TickerListFragment();

        Bundle bundle = new Bundle();

        if(tickerDtoArgList != null && !tickerDtoArgList.isEmpty())
        {
            bundle.putParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString(), tickerDtoArgList);
        }

        if(tradeDtoArgList != null && !tradeDtoArgList.isEmpty())
        {
            bundle.putParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString(), tradeDtoArgList);
        }

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

        LogManager.debug(CLASS_NAME, "onCreate", "");

        if(getArguments() != null && getArguments().containsKey(DataKey.EXTRA_TRADE_LIST.toString()))
        {
            this.tradeDtoList = getArguments().getParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString());
        }
        else if(savedInstanceState != null && savedInstanceState.containsKey(DataKey.EXTRA_TRADE_LIST.toString()))
        {
            this.tradeDtoList = savedInstanceState.getParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString());
        }
        else
        {
            this.tradeDtoList = this.pseService.getTradePlanListFromDatabase();
        }
        this.tradeDtoSymbols = this.pseService.getTradeSymbolsFromTradeDtos(this.tradeDtoList);

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
            this.tickerDtoList = this.pseService.getTickerListFromDatabase();
            this.pseService.setTickerDtoListHasTradePlan(this.tickerDtoList, this.tradeDtoSymbols);
        }

        if(this.tickerDtoList != null && !this.tickerDtoList.isEmpty())
        {
            setListAdapter(getArrayAdapter(this.tickerDtoList));
            if(lastUpdatedTextView != null)
            {
                lastUpdatedTextView.setText(getActivity().getString(R.string.last_updated, this.pseService.getLastUpdated(PSEPlannerPreference.LAST_UPDATED_TICKER.toString())));
            }
        }

        searchListener.setSearchListAdapater(getListAdapter());
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.list_fragment_ticker, parent, false);
        this.unbinder = ButterKnife.bind(this, view);

        updateListOnUiThread(this.tickerDtoList, this.pseService.getLastUpdated(PSEPlannerPreference.LAST_UPDATED_TICKER.toString()));
        LogManager.debug(CLASS_NAME, "onCreateView", "");

        return view;
    }

    /**
     * Saves current state in memory.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(this.tickerDtoList != null && !this.tickerDtoList.isEmpty())
        {
            outState.putParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString(), this.tickerDtoList);
            LogManager.debug(CLASS_NAME, "onSaveInstanceState", "Ticker list count: " + this.tickerDtoList.size());
        }

        if(this.tradeDtoList != null && !this.tradeDtoList.isEmpty())
        {
            outState.putParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString(), this.tradeDtoList);
            LogManager.debug(CLASS_NAME, "onSaveInstanceState", "Trade Plan list count: " + this.tradeDtoList.size());
        }
    }

    @Override
    protected FilterableArrayAdapter<TickerDto> getArrayAdapter(List<TickerDto> tickerDtoList)
    {
        return new TickerListAdapter(getActivity(), tickerDtoList);
    }

    /**
     * Http request is blocking, this method MUST be called in an AsyncTask.
     *
     * @throws HttpRequestException if the http request failed, does not update the list
     */
    @Override
    public void updateListFromWeb() throws HttpRequestException
    {
        Pair<List<TickerDto>, Date> response = this.pseService.getAllTickerList();
        this.tickerDtoList = (ArrayList<TickerDto>) response.first;

        this.pseService.setTickerDtoListHasTradePlan(tickerDtoList, this.tradeDtoSymbols);
        this.pseService.updateTickerList(this.tickerDtoList);

        updateListOnUiThread(response.first, this.formatService.formatLastUpdated(response.second));
    }

    /**
     * Updates the ticker list by getting the latest data from the database.
     */
    @Override
    public void updateListFromDatabase()
    {
        this.tickerDtoList = this.pseService.getTickerListFromDatabase();

        if(!this.tickerDtoList.isEmpty())
        {
            updateListOnUiThread(this.tickerDtoList, this.pseService.getLastUpdated(PSEPlannerPreference.LAST_UPDATED_TICKER.toString()));
        }
    }
}
