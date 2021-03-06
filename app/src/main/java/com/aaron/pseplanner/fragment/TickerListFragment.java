package com.aaron.pseplanner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.FilterableArrayAdapter;
import com.aaron.pseplanner.adapter.TickerListAdapter;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.PSEPlannerPreference;
import com.aaron.pseplanner.service.LogManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class TickerListFragment extends AbstractListFragment<TickerDto>
{
    public static final String CLASS_NAME = TickerListFragment.class.getSimpleName();
    private ArrayList<TickerDto> tickerDtoList;
    private Set<String> tradeDtoSymbols;
    private TickerListAdapter tickerListAdapter;
    private WeakReference<Activity> activityRef;

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

        initializeTradeDtoList(savedInstanceState);

        this.tradeDtoSymbols = this.pseService.getTradeSymbolsFromTradeDtos(this.tradeDtoList);

        initializeTickerDtoList(savedInstanceState);

        this.activityRef = new WeakReference<Activity>(getActivity());
    }

    private void initializeTradeDtoList(Bundle savedInstanceState)
    {
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
            this.tradeDtoList = initTradePlanListFromDatabase();
        }
    }

    private void initializeTickerDtoList(Bundle savedInstanceState)
    {
        if(getArguments() != null && getArguments().containsKey(DataKey.EXTRA_TICKER_LIST.toString()))
        {
            this.tickerDtoList = getArguments().getParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString());
            setTickerListAdapter();
        }
        else if(savedInstanceState != null && savedInstanceState.containsKey(DataKey.EXTRA_TICKER_LIST.toString()))
        {
            this.tickerDtoList = savedInstanceState.getParcelableArrayList(DataKey.EXTRA_TICKER_LIST.toString());
            setTickerListAdapter();
        }
        else
        {
            tickerDtoList = pseService.getTickerListFromDatabase().blockingGet();
            pseService.setTickerDtoListHasTradePlan(tickerDtoList, tradeDtoSymbols);
            setTickerListAdapter();
        }
    }

    private void setTickerListAdapter()
    {
        this.tickerListAdapter = new TickerListAdapter(getActivity(), this.tickerDtoList);
        this.setListAdapter(this.tickerListAdapter);
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.list_fragment_ticker, parent, false);
        this.unbinder = ButterKnife.bind(this, view);

        updateListView(this.tickerDtoList, this.pseService.getLastUpdated(PSEPlannerPreference.LAST_UPDATED_TICKER.toString()));

        LogManager.debug(CLASS_NAME, "onCreateView", "");

        return view;
    }

    /**
     * Saves current state in memory.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
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
    protected FilterableArrayAdapter<TickerDto> getArrayAdapter()
    {
        return this.tickerListAdapter;
    }

    /**
     * Updates the ticker list from web server.
     *
     * @param doAfterSubscribe the action that will be executed after executing this observable
     */
    @Override
    public void updateListFromWeb(Action doAfterSubscribe)
    {
        Disposable disposable = this.pseService.getAllTickerList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(doAfterSubscribe)
                .subscribeWith(updateListFromWebObserver());

        this.compositeDisposable.add(disposable);
    }

    /**
     * Updates the ticker list by getting the latest data from the database.
     */
    @Override
    public void updateListFromDatabase()
    {
        Disposable disposable = this.pseService.getTickerListFromDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(updateListFromDatabaseObserver());

        this.compositeDisposable.add(disposable);
    }

    private DisposableSingleObserver<ArrayList<TickerDto>> updateListFromDatabaseObserver()
    {
        return new DisposableSingleObserver<ArrayList<TickerDto>>()
        {
            @Override
            public void onSuccess(ArrayList<TickerDto> tickerDtos)
            {
                LogManager.debug(CLASS_NAME, "updateListFromDatabaseObserver.onSuccess", "TickerDto list count: " + tickerDtos.size());

                if(!tickerDtoList.isEmpty())
                {
                    tickerDtoList = tickerDtos;
                    updateListView(tickerDtoList, pseService.getLastUpdated(PSEPlannerPreference.LAST_UPDATED_TICKER.toString()));
                }
            }

            @Override
            public void onError(Throwable e)
            {
                LogManager.error(CLASS_NAME, "updateListFromDatabase", "Error retrieving Ticker list from database.", e);

                Activity activity = activityRef.get();
                if(activity != null)
                {
                    Toast.makeText(activity, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private DisposableSingleObserver<Pair<List<TickerDto>, Date>> updateListFromWebObserver()
    {
        return new DisposableSingleObserver<Pair<List<TickerDto>, Date>>()
        {
            @Override
            public void onSuccess(Pair<List<TickerDto>, Date> pair)
            {
                tickerDtoList = (ArrayList<TickerDto>) pair.first;
                Date lastUpdated = pair.second;

                pseService.setTickerDtoListHasTradePlan(tickerDtoList, tradeDtoSymbols);
                pseService.updateTickerList(tickerDtoList, lastUpdated);

                updateListView(tickerDtoList, formatService.formatLastUpdated(pair.second));
            }

            @Override
            public void onError(Throwable e)
            {
                LogManager.error(CLASS_NAME, "updateListFromWeb", "Error retrieving Ticker list from web.", e);
            }
        };
    }
}
