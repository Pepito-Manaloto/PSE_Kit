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
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.PSEPlannerPreference;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.implementation.DefaultCalculatorService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class TradePlanListFragment extends AbstractListFragment<TradeDto>
{
    public static final String CLASS_NAME = TradePlanListFragment.class.getSimpleName();

    // This storage needs to be thread-safe because this will be modified in AsyncTask
    private ConcurrentHashMap<String, TradeDto> tradesMap;
    private ArrayList<TradeDto> tradeDtoList;
    private CalculatorService calculatorService;

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
            this.tradeDtoList = pseService.getTradePlanListFromDatabase();
            this.tradesMap = new ConcurrentHashMap<>();

            for(TradeDto dto : this.tradeDtoList)
            {
                this.tradesMap.put(dto.getSymbol(), dto);
            }
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
        updateListOnUiThread(this.tradeDtoList, this.pseService.getLastUpdated(PSEPlannerPreference.LAST_UPDATED_TRADE_PLAN.toString()));

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

        outState.putParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString(), this.tradeDtoList);

        LogManager.debug(CLASS_NAME, "onSaveInstanceState", "Trade plan list count: " + this.tradeDtoList.size());
    }

    @Override
    protected ArrayAdapter getArrayAdapter(List<TradeDto> tradeDtoList)
    {
        return new TradePlanListAdapter(getActivity(), tradeDtoList);
    }

    /**
     * Http request is blocking, this method MUST be called in an AsyncTask.
     *
     * @throws HttpRequestException if the http request failed, does not update the list
     */
    @Override
    public void updateListFromWeb() throws HttpRequestException
    {
        Pair<List<TickerDto>, Date> response = this.pseService.getTickerList(this.tradesMap.keySet());

        List<TickerDto> tickerDtoList = response.first;
        Date lastUpdated = response.second;

        if(!tickerDtoList.isEmpty())
        {
            // Update current price of each trade plan based on ticker
            for(TickerDto tickerDto : tickerDtoList)
            {
                // TODO: should consider weekend in days difference??? lastupdated will always be friday 3:20PM before market open
                TradeDto tradeDto = this.tradesMap.get(tickerDto.getSymbol());
                tradeDto.setCurrentPrice(tickerDto.getCurrentPrice());
                tradeDto.setDaysToStopDate(this.calculatorService.getDaysBetween(lastUpdated, tradeDto.getStopDate()));
                tradeDto.setHoldingPeriod(this.calculatorService.getDaysBetween(lastUpdated, tradeDto.getEntryDate()));
                tradeDto.setGainLoss(this.calculatorService.getGainLossAmount(tradeDto.getAveragePrice(), tradeDto.getTotalShares(), tradeDto.getCurrentPrice()));
                tradeDto.setGainLossPercent(this.calculatorService.getPercentGainLoss(tradeDto.getAveragePrice(), tradeDto.getTotalShares(), tradeDto.getCurrentPrice()));
                tradeDto.setTotalAmount(this.calculatorService.getBuyNetAmount(tradeDto.getCurrentPrice(), tradeDto.getTotalShares()));
            }

            this.tradeDtoList = new ArrayList<>(this.tradesMap.values());

            updateListOnUiThread(this.tradeDtoList, this.formatService.formatLastUpdated(lastUpdated));
        }
    }

    /**
     * Updates the trade list by getting the latest data from the database.
     */
    @Override
    public void updateListFromDatabase()
    {
        this.tradeDtoList = this.pseService.getTradePlanListFromDatabase();

        if(!this.tradeDtoList.isEmpty())
        {
            updateListOnUiThread(this.tradeDtoList, this.pseService.getLastUpdated(PSEPlannerPreference.LAST_UPDATED_TRADE_PLAN.toString()));
        }
    }

    @Override
    protected void saveListState()
    {
        getActivity().getIntent().putParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString(), this.tradeDtoList);
    }

}
