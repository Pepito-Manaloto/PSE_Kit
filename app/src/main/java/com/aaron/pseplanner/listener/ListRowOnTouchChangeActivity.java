package com.aaron.pseplanner.listener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;

import java.util.ArrayList;

/**
 * Created by aaron.asuncion on 12/9/2016.
 * Class for handling List row selection and horizontal scrolling.
 */
public class ListRowOnTouchChangeActivity implements View.OnTouchListener
{
    private Activity activity;
    private Class<? extends Activity> activityClass;
    private DataKey extraKey;
    private Parcelable parcelableData;
    private DataKey extraListKey;
    private ArrayList<? extends Parcelable> parcelableListData;
    private IntentRequestCode intentRequestCode;
    private View view;
    private int highlightedColor;
    private double historicTouchXPosition;

    /**
     * Default constructor.
     *
     * @param activity          the current activity
     * @param activityClass     the activity class to transition to
     * @param extraKey          the extraKey of the parcelable data
     * @param parcelableData    the data that will be passed to the new activity
     * @param intentRequestCode the request code of the new activity
     * @param view              the view of the listener
     */
    public ListRowOnTouchChangeActivity(final Activity activity, final Class<? extends Activity> activityClass, final DataKey extraKey,
            final Parcelable parcelableData, final IntentRequestCode intentRequestCode, final View view)
    {
        this.activity = activity;
        this.activityClass = activityClass;
        this.extraKey = extraKey;
        this.parcelableData = parcelableData;
        this.intentRequestCode = intentRequestCode;
        this.view = view;
        this.highlightedColor = ContextCompat.getColor(activity, R.color.lightSkyBlue);
    }

    /**
     * Constructor with array list extra.
     *
     * @param activity           the current activity
     * @param activityClass      the activity class to transition to
     * @param parcelableKey      the extraKey of the parcelable data
     * @param parcelableData     the data that will be passed to the new activity
     * @param parcelableListKey  the extraKey of the parcelable list data
     * @param parcelableListData the data list that will be passed to the new activity
     * @param intentRequestCode  the request code of the new activity
     * @param view               the view of the listener
     */
    public ListRowOnTouchChangeActivity(final Activity activity, final Class<? extends Activity> activityClass, final DataKey parcelableKey,
            final Parcelable parcelableData, final DataKey parcelableListKey, final ArrayList<? extends Parcelable> parcelableListData,
            final IntentRequestCode intentRequestCode, final View view)
    {
        this(activity, activityClass, parcelableKey, parcelableData, intentRequestCode, view);

        this.extraListKey = parcelableListKey;
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
                historicTouchXPosition = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                this.view.setBackgroundColor(Color.WHITE);
                boolean touchMovedLessThan15Pixels = Math.abs(historicTouchXPosition - event.getX()) < 15;

                if(touchMovedLessThan15Pixels)
                {
                    addExtraDataToIntentAndStartNextActivity();
                }

                break;
            }
            default:
            {
                this.view.setBackgroundColor(Color.WHITE);
                // Removes compiler warning
                v.performClick();

                return false;
            }
        }

        // Removes compiler warning
        v.performClick();

        return true;
    }

    private void addExtraDataToIntentAndStartNextActivity()
    {
        Intent intent = new Intent(this.activity, this.activityClass);
        intent.putExtra(this.extraKey.toString(), this.parcelableData);

        if(this.extraListKey != null && this.parcelableListData != null)
        {
            intent.putParcelableArrayListExtra(this.extraListKey.toString(), this.parcelableListData);
        }

        this.activity.startActivityForResult(intent, this.intentRequestCode.code());
    }
}
