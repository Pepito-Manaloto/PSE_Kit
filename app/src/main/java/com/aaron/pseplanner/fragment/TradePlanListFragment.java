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
import com.aaron.pseplanner.adapter.TradePlanListAdapter;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.PSEPlannerPreference;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.implementation.DefaultCalculatorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class TradePlanListFragment extends AbstractListFragment<TradeDto>
{
    public static final String CLASS_NAME = TradePlanListFragment.class.getSimpleName();

    // This storage needs to be thread-safe because this will be modified in AsyncTask
    private ConcurrentHashMap<String, TradeDto> tradesMap;
    private CalculatorService calculatorService;
    private TradePlanListAdapter tradePlanListAdapter;

    /**
     * Gets a new instance of TradePlanListFragment with the TradeDto list.
     *
     * @param list the list of TradeDto
     * @return TickerListFragment
     */
    public static TradePlanListFragment newInstance(ArrayList<TradeDto> list)
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

        LogManager.debug(CLASS_NAME, "onCreate", "");

        this.calculatorService = new DefaultCalculatorService();

        initializeTradePlanList(savedInstanceState);
        initializeTradesMap();

        setTradePlanListAdapter();
    }

    private void setTradePlanListAdapter()
    {
        this.tradePlanListAdapter = new TradePlanListAdapter(getActivity(), this.tradeDtoList);
        this.setListAdapter(this.tradePlanListAdapter);
    }

    private void initializeTradePlanList(Bundle savedInstanceState)
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

        LogManager.debug(CLASS_NAME, "initializeTradePlanList", "TradeDto list count = " + (tradeDtoList != null ? tradeDtoList.size() : 0));
    }

    private void initializeTradesMap()
    {
        this.tradesMap = new ConcurrentHashMap<>();
        for(TradeDto dto : this.tradeDtoList)
        {
            this.tradesMap.put(dto.getSymbol(), dto);
        }
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.list_fragment_trade_plan, parent, false);
        this.unbinder = ButterKnife.bind(this, view);

        updateListView(this.tradeDtoList, this.pseService.getLastUpdated(PSEPlannerPreference.LAST_UPDATED_TRADE_PLAN.toString()));

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

        if(this.tradeDtoList != null && !this.tradeDtoList.isEmpty())
        {
            outState.putParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString(), this.tradeDtoList);
            LogManager.debug(CLASS_NAME, "onSaveInstanceState", "Trade plan list count: " + this.tradeDtoList.size());
        }
    }

    @Override
    protected FilterableArrayAdapter<TradeDto> getArrayAdapter()
    {
        return this.tradePlanListAdapter;
    }

    /**
     * Retrieves ticker list from web api.
     *
     * @param doAfterSubscribe the action that will be executed after executing this observable
     */
    @Override
    public void updateListFromWeb(Action doAfterSubscribe)
    {
        Disposable disposable = this.pseService.getTickerList(this.tradesMap.keySet())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(doAfterSubscribe)
                .subscribeWith(updateListFromWebObserver());

        this.compositeDisposable.add(disposable);
    }

    /**
     * Updates the trade list by getting the latest data from the database.
     */
    @Override
    public void updateListFromDatabase()
    {
        Disposable disposable = this.pseService.getTradePlanListFromDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(updateListFromDatabaseObserver());

        this.compositeDisposable.add(disposable);
    }

    private DisposableSingleObserver<Pair<List<TickerDto>, Date>> updateListFromWebObserver()
    {
        final Activity activity = getActivity();
        return new DisposableSingleObserver<Pair<List<TickerDto>, Date>>()
        {
            @Override
            public void onSuccess(Pair<List<TickerDto>, Date> response)
            {
                List<TickerDto> tickerDtoList = response.first;
                Date lastUpdated = response.second;

                LogManager.debug(CLASS_NAME, "updateListFromWebObserver.onSuccess", "TickerDto list count: " + tickerDtoList.size());

                if(!tickerDtoList.isEmpty())
                {
                    // Update current price of each trade plan based on ticker
                    for(TickerDto tickerDto : tickerDtoList)
                    {
                        updateTradeDtoListInTradesMap(tickerDto, lastUpdated);
                    }

                    tradeDtoList = new ArrayList<>(tradesMap.values());
                    Collections.sort(tradeDtoList);
                    updateListView(tradeDtoList, formatService.formatLastUpdated(lastUpdated));
                }
            }

            private void updateTradeDtoListInTradesMap(TickerDto tickerDto, Date lastUpdated)
            {
                // TODO: should consider weekend in days difference??? lastupdated will always be friday 3:20PM before market open
                TradeDto tradeDto = tradesMap.get(tickerDto.getSymbol());
                tradeDto.setCurrentPrice(tickerDto.getCurrentPrice());
                tradeDto.setDaysToStopDate(calculatorService.getDaysBetween(lastUpdated, tradeDto.getStopDate()));
                if(tradeDto.getEntryDate() != null)
                {
                    tradeDto.setHoldingPeriod(calculatorService.getDaysBetween(lastUpdated, tradeDto.getEntryDate()));
                }
                else
                {
                    tradeDto.setHoldingPeriod(0);
                }
                tradeDto.setDaysSincePlanned(calculatorService.getDaysBetween(lastUpdated, tradeDto.getDatePlanned()));
                tradeDto.setGainLoss(
                        calculatorService.getGainLossAmount(tradeDto.getAveragePrice(), tradeDto.getTotalShares(), tradeDto.getCurrentPrice()));
                tradeDto.setGainLossPercent(
                        calculatorService.getPercentGainLoss(tradeDto.getAveragePrice(), tradeDto.getTotalShares(), tradeDto.getCurrentPrice()));
                tradeDto.setTotalAmount(calculatorService.getBuyNetAmount(tradeDto.getCurrentPrice(), tradeDto.getTotalShares()));
            }

            @Override
            public void onError(Throwable e)
            {
                LogManager.error(CLASS_NAME, "updateListFromWeb", "Error retrieving Ticker list from web.", e);
                Toast.makeText(activity, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private DisposableSingleObserver<ArrayList<TradeDto>> updateListFromDatabaseObserver()
    {
        return new DisposableSingleObserver<ArrayList<TradeDto>>()
        {
            @Override
            public void onSuccess(ArrayList<TradeDto> tradeDtos)
            {
                if(!tradeDtos.isEmpty())
                {
                    tradeDtoList = tradeDtos;
                    updateListView(tradeDtoList, pseService.getLastUpdated(PSEPlannerPreference.LAST_UPDATED_TRADE_PLAN.toString()));
                }
            }

            @Override
            public void onError(Throwable e)
            {
                LogManager.error(CLASS_NAME, "updateListFromDatabase", "Error retrieving Ticker list from database.", e);
            }
        };
    }
}
