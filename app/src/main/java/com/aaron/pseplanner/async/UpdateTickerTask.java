package com.aaron.pseplanner.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.aaron.pseplanner.activity.MainFragmentActivity;

/**
 * Created by Aaron on 11/20/2016.
 */
public class UpdateTickerTask extends AsyncTask<Void, Void, String>
{
    private MainFragmentActivity callerActivity;

    public UpdateTickerTask(MainFragmentActivity callerActivity)
    {
        this.callerActivity = callerActivity;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        try {
            // Set a time to simulate a long update process.
            Thread.sleep(2000);

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(callerActivity, "Finished complex background function!",
                Toast.LENGTH_LONG).show();

        callerActivity.stopRefreshAnimation();
    }
}
