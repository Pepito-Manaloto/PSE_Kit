package com.aaron.pseplanner.async;

import android.os.AsyncTask;
import android.widget.Toast;

import com.aaron.pseplanner.activity.MainActivity;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.fragment.AbstractListFragment;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Aaron on 11/20/2016.
 * Retrieves the current value of all stocks traded in the PSE.
 */
public class UpdateTickerTask extends AsyncTask<Void, Void, String>
{
    private MainActivity callerActivity;
    private AbstractListFragment listFragment;

    public UpdateTickerTask(MainActivity callerActivity, AbstractListFragment listFragment)
    {
        this.callerActivity = callerActivity;
        this.listFragment = listFragment;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        try
        {
            this.listFragment.updateList();
            return "";
        }
        catch(HttpRequestException e)
        {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        if(StringUtils.isNotBlank(result))
        {
            Toast.makeText(this.callerActivity, "Update failed: " + result, Toast.LENGTH_LONG).show();
        }

        this.callerActivity.stopRefreshAnimation();
    }
}
