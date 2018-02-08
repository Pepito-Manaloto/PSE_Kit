package com.aaron.pseplanner.background;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.aaron.pseplanner.service.LogManager;

/**
 * Created by Aaron on 7/13/2017.
 */
@Deprecated
public class NotificationIntentService extends IntentService
{
    public static final String CLASS_NAME = NotificationIntentService.class.getSimpleName();
    private static final String ACTION_START = "ACTION_START";

    private NotificationUtils notificationUtils;

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public NotificationIntentService()
    {
        super(CLASS_NAME);

        this.notificationUtils = new NotificationUtils(this);
    }

    public static Intent createIntentStartNotificationService(Context context)
    {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        try
        {
            String action = null;
            if(intent != null)
            {
                action = intent.getAction();
            }
            LogManager.debug(CLASS_NAME, "onHandleIntent", "Started handling a notification event action = " + action);

            if(ACTION_START.equals(action))
            {
                Activity activity = null;
                String title = null;
                String body = null;
                //int notificationId = this.notificationUtils.notify(activity, R.mipmap.ic_launcher, title, body);
            }
        }
        finally
        {
            if(intent != null)
            {
                WakefulBroadcastReceiver.completeWakefulIntent(intent);
            }
        }
    }
}
