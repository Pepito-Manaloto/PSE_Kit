package com.aaron.pseplanner.async;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aaron.pseplanner.activity.MainActivity;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.fragment.AbstractListFragment;
import com.aaron.pseplanner.service.LogManager;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Aaron on 11/20/2016.
 * Retrieves and updates the current value of the list in the AbstractListFragment.
 */
public class UpdateFragmentListTask extends AsyncTask<Void, Void, String>
{
    public static final String CLASS_NAME = UpdateFragmentListTask.class.getSimpleName();
    private MainActivity callerActivity;
    private AbstractListFragment listFragment;

    public UpdateFragmentListTask(MainActivity callerActivity, AbstractListFragment listFragment)
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
            LogManager.error(CLASS_NAME, "doInBackground", "Error retrieving from Web API", e);
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
