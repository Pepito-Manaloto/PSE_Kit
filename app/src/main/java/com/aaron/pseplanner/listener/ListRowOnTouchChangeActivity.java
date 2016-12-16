package com.aaron.pseplanner.listener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.Ticker;

/**
 * Created by aaron.asuncion on 12/9/2016.
 * Class for handling List row selection and horizontal scrolling.
 */
public class ListRowOnTouchChangeActivity implements View.OnTouchListener
{
    private float historicX;
    private Activity activity;
    private Class<? extends Activity> activityClass;
    private Ticker ticker;
    private View view;
    private int highlightedColor;

    /**
     * Default constructor.
     *
     * @param activity      the current activity
     * @param activityClass the activity class to transition to
     * @param ticker        the selected ticker
     * @param view          the view of the listener
     */
    public ListRowOnTouchChangeActivity(final Activity activity, final Class<? extends Activity> activityClass, final Ticker ticker, final View view)
    {
        this.activity = activity;
        this.activityClass = activityClass;
        this.ticker = ticker;
        this.view = view;

        if(android.os.Build.VERSION.SDK_INT >= 23)
        {
            this.highlightedColor = activity.getColor(R.color.lightSkyBlue);
        }
        else
        {
            this.highlightedColor = activity.getResources().getColor(R.color.lightSkyBlue);
        }
    }

    /**
     * If the touch moves MORE than 15 pixels horizontally then the gesture will be treated as a scrolling event,
     * else it will be treated as selecting the row which will start RecipeActivity.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                this.view.setBackgroundColor(this.highlightedColor);
                this.historicX = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                this.view.setBackgroundColor(Color.WHITE);
                boolean touchMovedLessThan10Pixels = Math.abs(this.historicX - event.getX()) < 15;

                if(touchMovedLessThan10Pixels)
                {

                    // TODO: start activity
                }

                // Removes compiler warning
                v.performClick();

                break;
            }
            default:
            {
                this.view.setBackgroundColor(Color.WHITE);
                return false;
            }
        }

        return true;
    }

}
