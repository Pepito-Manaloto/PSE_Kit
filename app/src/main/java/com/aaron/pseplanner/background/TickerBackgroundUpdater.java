package com.aaron.pseplanner.background;

import android.os.Handler;

import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.fragment.AbstractListFragment;

import java.util.concurrent.TimeUnit;

/**
 * Created by Aaron on 31/10/2017.
 */
public class TickerBackgroundUpdater implements Runnable
{
    private AbstractListFragment selectedListFragment;
    private int interval;
    private final Handler handler;

    public TickerBackgroundUpdater(int interval)
    {
        this.interval = interval;
        this.handler = new Handler();
    }

    /**
     * Updates the ticker list on every interval. Note that updating the interval will only take effect on the next ticker refresh.
     */
    @Override
    public void run()
    {
        // TODO: update
        try
        {
            selectedListFragment.updateListFromWeb();
        }
        catch(HttpRequestException e)
        {
            e.printStackTrace();
        }

        this.handler.postDelayed(this, TimeUnit.SECONDS.toMillis(this.interval));
    }

    /**
     * Starts the ticker updater.
     */
    public void start()
    {
        this.handler.post(this);
    }

    /**
     * Stops the ticker updater.
     */
    public void stop()
    {
        this.handler.removeCallbacks(this);
    }

    public int getInterval()
    {
        return this.interval;
    }

    public void setInterval(int interval)
    {
        this.interval = interval;
    }
}
