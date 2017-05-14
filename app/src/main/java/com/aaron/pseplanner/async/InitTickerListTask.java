package com.aaron.pseplanner.async;

import android.os.AsyncTask;

import com.aaron.pseplanner.activity.MainActivity;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.PSEPlannerPreference;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.fragment.TickerListFragment;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Aaron on 3/25/2017.
 * Initializes the current value of the ticker list.
 */
public class InitTickerListTask extends AsyncTask<Void, Void, String>
{
    public static final String CLASS_NAME = InitTickerListTask.class.getSimpleName();
    private MainActivity callerActivity;
    private PSEPlannerService service;
    private Set<String> tradeDtoSymbols;

    public InitTickerListTask(MainActivity callerActivity, PSEPlannerService service, ArrayList<TradeDto> tradeDtoList)
    {
        this.callerActivity = callerActivity;
        this.service = service;
        this.tradeDtoSymbols = this.service.getTradeSymbolsFromTradeDtos(tradeDtoList);
    }

    @Override
    protected String doInBackground(Void... params)
    {
        LogManager.debug(CLASS_NAME, "doInBackground", "Start");
        try
        {
            ArrayList<TickerDto> tickerDtoList;
            if(!this.service.isTickerListSavedInDatabase() || !this.service.isUpToDate(PSEPlannerPreference.LAST_UPDATED_TICKER))
            {
                tickerDtoList = (ArrayList<TickerDto>) this.service.getAllTickerList().first;
                this.service.insertTickerList(tickerDtoList);

                LogManager.debug(CLASS_NAME, "doInBackground", "Retrieved from Web API and saved to database, count: " + tickerDtoList.size());
            }
            else
            {
                tickerDtoList = this.service.getTickerListFromDatabase();
                LogManager.debug(CLASS_NAME, "doInBackground", "Retrieved from database, count: " + tickerDtoList.size());
            }

            if(!tickerDtoList.isEmpty())
            {
                this.service.setTickerDtoListHasTradePlan(tickerDtoList, this.tradeDtoSymbols);
                this.callerActivity.setTickerDtoList(tickerDtoList);
            }

            return "";
        }
        catch(HttpRequestException e)
        {
            LogManager.error(CLASS_NAME, "doInBackground", "Error retrieving from Web API", e);
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        if(StringUtils.isBlank(result))
        {
            // Update ticker view if it is the current selected fragment
            if(this.callerActivity.getSelectedListFragment() instanceof TickerListFragment)
            {
                LogManager.debug(CLASS_NAME, "onPostExecute", "Updating TickerListFragment.");
                this.callerActivity.getSelectedListFragment().updateListFromDatabase();
            }
        }
    }
}
