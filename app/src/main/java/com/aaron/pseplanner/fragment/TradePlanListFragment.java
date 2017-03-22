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
            // TODO: retrieve and parse data from database
            this.tradesMap = new ConcurrentHashMap<>();
            for(int i = 0; i < 5; i++)
            {
                this.tradesMap.put("CPG", new TradeDto("CPG", new Date(), 1, 0.53, 0.55, 300_000, 156_000, 0.567, 0.6, -28_020, -3.86, 53_543, 0.51, -10_000.7, new Date(), 23, 2.34, 500_000, 30.42, Arrays.asList(new TradeEntryDto("CPG", 0.54, 150_000, 50), new TradeEntryDto("CPG", 0.55, 150_000, 50))));
                this.tradesMap.put("CYBR", new TradeDto("CYBR", new Date(), 14, 0.56, 0.55, 500_000, 256_000, 0.577, 0.6, 98_120, 2.86, 53_543, 0.53, -40_000, new Date(), 56, 5.8, 500_000, 20.42, Arrays.asList(new TradeEntryDto("CYBR", 0.54, 200_000, 37), new TradeEntryDto("CYBR", 0.55, 150_000, 31.5), new TradeEntryDto("CYBR", 0.56, 150_000, 31.5))));
                this.tradesMap.put("GERI", new TradeDto("GERI", new Date(), 63, 1.02, 0.98, 800_000, 891_020, 0.989, 1.1, 138_020, 6.86, 53_543, 0.91, -7_000.34, new Date(), 87, 8.14, 700_000, 57.42, Arrays.asList(new TradeEntryDto("GERI", 1.02, 800_000, 100))));
            }

            this.tradeDtoList = new ArrayList<>(this.tradesMap.values());
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
        updateListOnUiThread(this.tradeDtoList, this.client.getLastUpdated());

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

        LogManager.debug(CLASS_NAME, "onSaveInstanceState", "");
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
    public void updateList() throws HttpRequestException
    {
        Pair<List<TickerDto>, Date> response = this.client.getTickerList(this.tradesMap.keySet());

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

    @Override
    protected void saveListState()
    {
        getActivity().getIntent().putParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString(), this.tradeDtoList);
    }

}
