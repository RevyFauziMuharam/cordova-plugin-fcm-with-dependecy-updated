package com.gae.scaffolder.plugin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.Map;
import java.util.HashMap;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


// add revy
import android.app.NotificationChannel;
import android.app.Notification;
import android.content.Intent;

/**
 * Created by Felipe Echanique on 08/06/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMPlugin";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
      Log.d(TAG, "==> MyFirebaseMessagingService onMessageReceived ENAK 1");

  		if( remoteMessage.getNotification() != null){
  			Log.d(TAG, "\tNotification Title: " + remoteMessage.getNotification().getTitle());
  			Log.d(TAG, "\tNotification Message: " + remoteMessage.getNotification().getBody());
  		}

  		Map<String, Object> data = new HashMap<String, Object>();
  		data.put("wasTapped", false);
  		for (String key : remoteMessage.getData().keySet()) {
        String value = remoteMessage.getData().get(key);
        Log.d(TAG, "\tKey: " + key + " Value: " + value);
        data.put(key, value);
      }

  		Log.d(TAG, "\tNotification Data: " + data.toString());
      FCMPlugin.sendPushPayload( data );
      // sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), data);
      createNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), data, this);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, Map<String, Object> data) {
        Intent intent = new Intent(this, FCMPluginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		for (String key : data.keySet()) {
			intent.putExtra(key, data.get(key).toString());
		}
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private static final String default_channel_id = "1";
    private static final String default_channel = "Jaga-Channel";
    private NotificationManager notifManager;
    public void createNotification(String aTitle, String aMessage, Map<String, Object> data, Context context) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = default_channel_id; // default_channel_id
        String title = default_channel; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, FCMPluginActivity.class);
            for (String key : data.keySet()) {
        			intent.putExtra(key, data.get(key).toString());
        		}
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aTitle)                            // required
                   .setContentText(aMessage)
                   .setSmallIcon(getApplicationInfo().icon)   // required
                   .setDefaults(Notification.DEFAULT_ALL)
                   .setAutoCancel(true)
                   .setContentIntent(pendingIntent)
                   .setTicker(aMessage)
                   .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, FCMPluginActivity.class);
            for (String key : data.keySet()) {
        			intent.putExtra(key, data.get(key).toString());
        		}
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aTitle)                            // required
                   .setContentText(aMessage)
                   .setSmallIcon(getApplicationInfo().icon)   // required
                   .setDefaults(Notification.DEFAULT_ALL)
                   .setAutoCancel(true)
                   .setContentIntent(pendingIntent)
                   .setTicker(aMessage)
                   .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                   .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }
}
