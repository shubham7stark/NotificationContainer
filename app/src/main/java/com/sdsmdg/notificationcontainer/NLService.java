package com.sdsmdg.notificationcontainer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * @author shubham
 * @since 17/12/16.
 */
public class NLService extends NotificationListenerService{
    private String TAG = this.getClass().getSimpleName();
    private NLServiceReceiver nlservicereciver;

    @Override
    public void onCreate(){
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.sdsmdg.notificationcontainer.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver,filter);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn.getPackageName());
        Intent i = new  Intent("com.sdsmdg.notificationcontainer.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event","onNotificationPosted :" + sbn.getPackageName());
        sendBroadcast(i);
        Notification notification = sbn.getNotification();
        Bundle extras = NotificationCompat.getExtras(notification);
        String package_name = sbn.getPackageName();
        String title = extras.getString(Notification.EXTRA_TITLE);
        String title_big = extras.getString(Notification.EXTRA_TITLE_BIG);
        Bitmap bmp = getNotificationIcon(extras);
        String text_content = getNotificationText(extras);
        String text_extended_content = getNotificationExtendedText(extras);
        PendingIntent pendingIntent = notification.contentIntent;

        ArrayList<NotificationCompat.Action> actions = new ArrayList<>();
        int size = NotificationCompat.getActionCount(notification);
        for (int j = 0; j < size; j++)
            actions.add(NotificationCompat.getAction(notification, j));

        if (bmp != null) {
            Log.i(TAG, package_name+"|"+title+"|"+title_big+"|"+bmp.getHeight()+"|"+text_content+"|"+text_extended_content+"|"+pendingIntent.toString());
        }
    }

    private String getNotificationText(Bundle extras) {
        CharSequence chars =
                extras.getCharSequence(Notification.EXTRA_TEXT);
        if(!TextUtils.isEmpty(chars))
            return chars.toString();
        else if(!TextUtils.isEmpty((chars = extras.getString(Notification.EXTRA_SUMMARY_TEXT))))
            return chars.toString();
        else
            return "";
    }

    private String getNotificationExtendedText (Bundle extras) {
        CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
        if(lines != null && lines.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (CharSequence msg : lines)
                if (!TextUtils.isEmpty(msg)) {
                    sb.append(msg.toString());
                    sb.append('\n');
                }
            return sb.toString().trim();
        }
        CharSequence chars = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            chars = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        }
        if(!TextUtils.isEmpty(chars))
            return chars.toString();
        return "";
    }

    private Bitmap getNotificationIcon(Bundle extras) {
        Bitmap icon;
        icon = extras.getParcelable(Notification.EXTRA_LARGE_ICON);
        if (icon != null ) {
            return icon;
        }
        icon = extras.getParcelable(Notification.EXTRA_LARGE_ICON_BIG);
        if (icon != null) {
            return icon;
        }
        icon = extras.getParcelable(Notification.EXTRA_SMALL_ICON);
        if (icon != null) {
            return icon;
        }
        return null;
//        icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        return icon;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNOtificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText +"t" + sbn.getPackageName());
        Intent i = new  Intent("com.sdsmdg.notificationcontainer.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "n");
        i.putExtra("object",sbn.getNotification().contentIntent);
        sendBroadcast(i);
    }

    class NLServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("command").equals("clearall")){
                NLService.this.cancelAllNotifications();
            }
            else if(intent.getStringExtra("command").equals("list")){
                Intent i1 = new  Intent("com.sdsmdg.notificationcontainer.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event","=====================");
                sendBroadcast(i1);
                int i=1;
                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                    Intent i2 = new  Intent("com.sdsmdg.notificationcontainer.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event",i +" " + sbn.getPackageName() + "n");
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new  Intent("com.sdsmdg.notificationcontainer.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event","===== Notification List ====");
                sendBroadcast(i3);
            }
        }
    }
}