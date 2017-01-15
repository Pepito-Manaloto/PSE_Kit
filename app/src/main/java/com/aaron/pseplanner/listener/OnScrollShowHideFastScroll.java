package com.aaron.pseplanner.listener;

import android.os.Handler;
import android.widget.AbsListView;

/**
 * Created by aaron.asuncion on 12/9/2016.
 * Show/hides the fast scroll of a ListView depending on the user's gesture.
 */
public class OnScrollShowHideFastScroll implements AbsListView.OnScrollListener
{
    private static final int DELAY = 1000;
    private AbsListView view;

    private Handler handler = new Handler();
    // Runnable for handler object.
    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            view.setFastScrollAlwaysVisible(false);
            view = null;
        }
    };

    /**
     * Show fast-scroll thumb if scrolling, and hides fast-scroll thumb if not scrolling.
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        if(scrollState != SCROLL_STATE_IDLE)
        {
            view.setFastScrollAlwaysVisible(true);

            // Removes the runnable from the message queue.
            // Stops the handler from hiding the fast-scroll.
            this.handler.removeCallbacks(this.runnable);
        }else
        {
            this.view = view;

            // Adds the runnable to the message queue, will run after the DELAY.
            // Hides the fast-scroll after two seconds of no scrolling.
            this.handler.postDelayed(this.runnable, DELAY);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
    }
}
