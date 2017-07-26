package com.aaron.pseplanner.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.aaron.pseplanner.service.LogManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aaron on 7/13/2017.
 *
 * https://stackoverflow.com/questions/20501225/using-service-to-run-background-and-create-notification
 * https://stackoverflow.com/questions/25009195/how-to-edit-reset-alarm-manager
 */
@Deprecated
public class NotificationEventReceiver extends WakefulBroadcastReceiver
{
    public static final String CLASS_NAME = NotificationEventReceiver.class.getSimpleName();
    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final int NOTIFICATIONS_INTERVAL_IN_HOURS = 1;

    public static void setupAlarm(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntent(context);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTriggerAt(new Date()), NOTIFICATIONS_INTERVAL_IN_HOURS * AlarmManager.INTERVAL_HOUR, alarmIntent);
    }

    private static long getTriggerAt(Date now)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR, NOTIFICATIONS_INTERVAL_IN_HOURS);
        return calendar.getTimeInMillis();
    }

    /**
     * Receive broadcast and execute service depending on the broadcast's action.
     *
     * @param context the current context in which the receiver is running
     * @param intent  the intent broadcasted and is being received
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        Intent serviceIntent = null;
        if(ACTION_START_NOTIFICATION_SERVICE.equals(action))
        {
            LogManager.debug(CLASS_NAME, "onReceive", "Starting notification service.");
            serviceIntent = NotificationIntentService.createIntentStartNotificationService(context);
        }

        if(serviceIntent != null)
        {
            WakefulBroadcastReceiver.startWakefulService(context, serviceIntent);
        }
    }

    private static PendingIntent getStartPendingIntent(Context context)
    {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
