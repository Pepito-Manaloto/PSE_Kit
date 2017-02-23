package com.aaron.pseplanner.listener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;

import java.util.ArrayList;

/**
 * Created by aaron.asuncion on 12/9/2016.
 * Class for handling List row selection and horizontal scrolling.
 */
public class ListRowOnTouchChangeActivity implements View.OnTouchListener
{
    private float historicX;
    private Activity activity;
    private Class<? extends Activity> activityClass;
    private DataKey key;
    private Parcelable parcelableData;
    private DataKey keyList;
    private ArrayList<? extends Parcelable> parcelableListData;
    private IntentRequestCode requestCode;
    private View view;
    private int highlightedColor;

    /**
     * Default constructor.
     *
     * @param activity       the current activity
     * @param activityClass  the activity class to transition to
     * @param key            the key of the parcelable data
     * @param parcelableData the data that will be passed to the new activity
     * @param requestCode    the request code of the new activity
     * @param view           the view of the listener
     */
    public ListRowOnTouchChangeActivity(final Activity activity, final Class<? extends Activity> activityClass, final DataKey key, final Parcelable parcelableData, final IntentRequestCode requestCode, final View view)
    {
        this.activity = activity;
        this.activityClass = activityClass;
        this.key = key;
        this.parcelableData = parcelableData;
        this.requestCode = requestCode;
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
     * Constructor with array list extra.
     *
     * @param activity           the current activity
     * @param activityClass      the activity class to transition to
     * @param parcelableKey      the key of the parcelable data
     * @param parcelableData     the data that will be passed to the new activity
     * @param parcelableListKey  the key of the parcelable list data
     * @param parcelableListData the data list that will be passed to the new activity
     * @param requestCode        the request code of the new activity
     * @param view               the view of the listener
     */
    public ListRowOnTouchChangeActivity(final Activity activity, final Class<? extends Activity> activityClass, final DataKey parcelableKey, final Parcelable parcelableData, final DataKey parcelableListKey, final ArrayList<? extends Parcelable> parcelableListData, final IntentRequestCode requestCode, final View view)
    {
        this(activity, activityClass, parcelableKey, parcelableData, requestCode, view);

        this.keyList = parcelableListKey;
        this.parcelableListData = parcelableListData;
    }

    /**
     * If the touch moves MORE than 15 pixels horizontally then the gesture will be treated as a scrolling event,
     * else it will be treated as selecting the row which will start CreateTradePlanActivity.
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
                boolean touchMovedLessThan15Pixels = Math.abs(this.historicX - event.getX()) < 15;

                if(touchMovedLessThan15Pixels)
                {
                    Intent intent = new Intent(this.activity, this.activityClass);
                    intent.putExtra(this.key.toString(), this.parcelableData);

                    if(this.keyList != null && this.parcelableListData != null)
                    {
                        intent.putParcelableArrayListExtra(this.keyList.toString(), this.parcelableListData);
                    }

                    this.activity.startActivityForResult(intent, this.requestCode.code());
                }

                // Removes compiler warning
                //v.performClick();

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
