package com.aaron.pseplanner.background;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.UUID;


/**
 * Created by Aaron on 7/13/2017.
 */
public class NotificationUtils
{
    private Context context;

    public NotificationUtils(Context context)
    {
        this.context = context;
    }

    /**
     * Creates a notification.
     *
     * @param activity the activity to start upon clicking the notification
     * @param icon     the small icon
     * @param title    the title
     * @param text     the content text
     */
    public int notify(Activity activity, int icon, String title, String text)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context).setSmallIcon(icon).setContentTitle(title).setContentText(text).setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this.context, activity.getClass());

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(activity.getClass());
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = UUID.randomUUID().hashCode();
        // mNotificationId is a unique integer your app uses to identify the notification.
        mNotificationManager.notify(notificationId, mBuilder.build());

        return notificationId;
    }

    public void cancelAllNotifications()
    {
        NotificationManager mNotificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    public void cancelNotification(int id)
    {
        NotificationManager mNotificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }
}

