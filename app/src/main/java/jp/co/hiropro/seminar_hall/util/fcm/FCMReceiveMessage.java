package jp.co.hiropro.seminar_hall.util.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import jp.co.hiropro.seminar_hall.util.HSSPreference;
import me.leolin.shortcutbadger.ShortcutBadger;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.LoginActivity;
import jp.co.hiropro.seminar_hall.controller.activity.SplashActivity;
import jp.co.hiropro.seminar_hall.util.AppConstants;

/**
 * Created by dinhdv on 7/20/2017.
 */

public class FCMReceiveMessage extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From999: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData() + " Notification ---" + remoteMessage.getData().get("data"));
            if (remoteMessage.getData().containsKey("data")) {
                String jsonData = remoteMessage.getData().get("data");
                if (jsonData.length() > 0) {
                    try {
                        JSONObject object = new JSONObject(jsonData);
//                        JSONObject objectData = object.getJSONObject("data");
                        if (object != null) {
                            int badge = object.optInt("remain", 0);
                            if (getApplicationContext() != null){
                                ShortcutBadger.applyCount(getApplicationContext(), badge);
                                HSSPreference.getInstance().putInt("number_remain",badge);
                            }
                            String msg = object.optString("title", "");
                            String newId = object.optString("news_id", "");
                            int type = 0;
                            if (object.has("notify_type")) {
                                type = object.optInt("notify_type", 0);
                            }
                            sendNotification(type, msg, newId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param newId       Id of new notification.
     * @param messageBody FCM message body received.
     */
    private void sendNotification(int type, String messageBody, String newId) {
        if (type == 1) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(AppConstants.KEY_SEND.KEY_ID_NEWS, newId);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis() /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setColor(getResources().getColor(R.color.green))
                    .setContentTitle(getString(R.string.app_name) + "からお知らせがあります。")
                    .setContentIntent(pendingIntent)
                    .setContentText(messageBody)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(getApplicationContext().getString(R.string.app_name),
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }
            if (notificationManager != null) {
                notificationManager.notify((int) System.currentTimeMillis() /* ID of notification */, builder.build());
            }
        } else {
//        Intent intent = new Intent(this, ActivityNewsDetail.class);
            Intent intent = new Intent(this, SplashActivity.class);
            if (type == 2){
                intent.putExtra(AppConstants.KEY_SEND.KEY_TEACH_NEWS, true);
            }
            intent.putExtra(AppConstants.KEY_SEND.KEY_ID_NEWS, newId);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis() /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name) + "からお知らせがあります。")
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(getApplicationContext().getString(R.string.app_name),
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }
            if (notificationManager != null) {
                notificationManager.notify((int) System.currentTimeMillis() /* ID of notification */, notificationBuilder.build());
            }
        }
    }
}