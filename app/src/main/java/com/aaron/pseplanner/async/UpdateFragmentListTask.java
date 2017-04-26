package com.aaron.pseplanner.async;

import android.os.AsyncTask;
import android.widget.Toast;

import com.aaron.pseplanner.activity.MainActivity;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.fragment.AbstractListFragment;
import com.aaron.pseplanner.service.LogManager;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Aaron on 11/20/2016.
 * Retrieves and updates the current value of the list in the AbstractListFragment.
 */
public class UpdateFragmentListTask extends AsyncTask<Void, Void, String>
{
    public static final String CLASS_NAME = UpdateFragmentListTask.class.getSimpleName();
    private MainActivity callerActivity;
    private AbstractListFragment listFragment;
    private AtomicBoolean isUpdating;

    public UpdateFragmentListTask(MainActivity callerActivity, AbstractListFragment listFragment, AtomicBoolean isUpdating)
    {
        this.callerActivity = callerActivity;
        this.listFragment = listFragment;
        this.isUpdating = isUpdating;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        try
        {
            this.listFragment.updateListFromWeb();

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
        this.isUpdating.set(false);
    }
}
