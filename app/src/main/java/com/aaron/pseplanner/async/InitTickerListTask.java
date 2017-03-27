package com.aaron.pseplanner.async;

import android.os.AsyncTask;
import android.widget.Toast;

import com.aaron.pseplanner.activity.MainActivity;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by Aaron on 3/25/2017.
 * Initializes the current value of the ticker list.
 */
public class InitTickerListTask extends AsyncTask<Void, Void, String>
{
    public static final String CLASS_NAME = InitTickerListTask.class.getSimpleName();
    private MainActivity callerActivity;
    private PSEPlannerService service;

    public InitTickerListTask(MainActivity callerActivity, PSEPlannerService service)
    {
        this.callerActivity = callerActivity;
        this.service = service;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        try
        {
            ArrayList<TickerDto> tickerDtoList = (ArrayList<TickerDto>) this.service.getAllTickerList().first;
            this.callerActivity.getIntent().putParcelableArrayListExtra(DataKey.EXTRA_TICKER_LIST.toString(), tickerDtoList);
            this.service.saveTickerList(tickerDtoList);

            LogManager.debug(CLASS_NAME, "doInBackground", "Retrieved from Web API and saved to database, count: " + tickerDtoList.size());

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
        // Do something?
    }
}
