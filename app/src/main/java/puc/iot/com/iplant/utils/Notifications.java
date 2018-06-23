package puc.iot.com.iplant.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import puc.iot.com.iplant.LaunchActivity;
import puc.iot.com.iplant.PlantActivity;
import puc.iot.com.iplant.R;
import puc.iot.com.iplant.models.Plant;

public final class Notifications {

    private static final int TAP_CLOSED_ID = 4,TAP_OPENED_ID = 3,
            CAN_TURN_OFF_ID = 2,NEED_TURN_OFF_ID = 1,NEED_TURN_ON_ID = 0;

    private static void expanded(Context context, String[] contentText, int iconID, int titleID, int notificationId){
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), iconID);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context,contentText[0])
                        .setSmallIcon(R.drawable.ic_notification)
                        .setColorized(true)
                        .setLargeIcon(icon)
                        .setColor(ContextCompat.getColor(context,R.color.black))
                        .setContentTitle(context.getString(titleID));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle(context.getString(titleID));
        for (String plantName : contentText) {

            inboxStyle.addLine(plantName);
        }
        mBuilder.setStyle(inboxStyle);

        Intent resultIntent = new Intent(context, LaunchActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PlantActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(notificationId, mBuilder.build());
        }
    }

    private static void simple(Context context, String plantId, String contentText,int iconID,int titleID){
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), iconID);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context,plantId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(icon)
                        .setColor(ContextCompat.getColor(context,R.color.black))
                        .setContentTitle(context.getString(titleID))
                        .setContentText(contentText);
        Intent resultIntent = new Intent(context, PlantActivity.class);
        resultIntent.putExtra(Plant._ID,plantId);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PlantActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(plantId.hashCode(), mBuilder.build());
        }
    }

    public static void needTurnOnWater(Context context, String plantId, String name){
        simple(context,
                plantId,
                name,
                R.drawable.notf_d,
                R.string.I_want_water);
    }

    public static void needTurnOnWater(Context context, String[] plantNames){
        if (plantNames.length>0)
        expanded(context,
                plantNames,
                R.drawable.notf_d,
                R.string.I_want_water,NEED_TURN_ON_ID);
        else cancel(context, NEED_TURN_ON_ID);
    }

    public static void needTurnOffWater(Context context, String plantId, String name){
        simple(context,
                plantId,
                name,
                R.drawable.notf_w,
                R.string.someone_is_drowning);
    }
    public static void needTurnOffWater(Context context, String plantNames[]){
        if (plantNames.length>0)
        expanded(context,
                plantNames,
                R.drawable.notf_w,
                R.string.someone_is_drowning,NEED_TURN_OFF_ID);
        else cancel(context, NEED_TURN_OFF_ID);
    }

    public static void canTurnOffWater(Context context, String plantId, String name){
        simple(context,
                plantId,
                name,
                R.drawable.notf_n,
                R.string.someone_is_satisfied);
    }
    public static void canTurnOffWater(Context context, String plantNames[]){
        if (plantNames.length>0)
        expanded(context,
                plantNames,
                R.drawable.notf_n,
                R.string.someone_is_satisfied,CAN_TURN_OFF_ID);
        else cancel(context, CAN_TURN_OFF_ID);
    }
    public static void tapOpenedBy(Context context, String plantId, String plantName, String byWho){
        simple(context,
                plantId,
                plantName + ": "+byWho,
                R.drawable.notf_n,
                R.string.someone_watered_your_plants);
    }
    public static void tapOpenedBy(Context context, String[] plantNames){
        if (plantNames.length>0)
        expanded(context,
                plantNames,
                R.drawable.notf_n,
                R.string.someone_watered_your_plants,TAP_OPENED_ID);
        else cancel(context, TAP_OPENED_ID);
    }

    public static void tapClosedBy(Context context, String plantId, String plantName, String byWho) {
        simple(context,
                plantId,
                plantName + ": "+byWho,
                R.drawable.notf_n,
                R.string.Someone_turned_off_the_tap);
    }

    public static void tapClosedBy(Context context, String[] plantNames){
        if (plantNames.length>0)
        expanded(context,
                plantNames,
                R.drawable.notf_n,
                R.string.Someone_turned_off_the_tap, TAP_CLOSED_ID);
        else cancel(context, TAP_CLOSED_ID);
    }
    private static void cancel(Context context,int id){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(id);
    }
}
