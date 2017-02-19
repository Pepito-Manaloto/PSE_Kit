package com.aaron.pseplanner.async;

import android.os.AsyncTask;
import android.widget.Toast;

import com.aaron.pseplanner.activity.MainActivity;

/**
 * Created by Aaron on 11/20/2016.
 * Retrieves the current value of all stocks traded in the PSE.
 */
public class UpdateTickerTask extends AsyncTask<Void, Void, String>
{
    private MainActivity callerActivity;

    public UpdateTickerTask(MainActivity callerActivity)
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
