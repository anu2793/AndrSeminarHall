package vn.hanelsoft.forestpublishing.util.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dinhdv on 9/25/2017.
 */

public class CustomUniversalFCM extends BroadcastReceiver {

    private static final String ACTION_REGISTRATION
            = "com.google.android.c2dm.intent.REGISTRATION";
    private static final String ACTION_RECEIVE
            = "com.google.android.c2dm.intent.RECEIVE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_REGISTRATION:
                // TODO: 2016-08-06
                break;

            case ACTION_RECEIVE:
                convertData(context, intent);
                // TODO: 2016-08-06
                break;

            default:
        }
        abortBroadcast();
    }

    private void convertData(Context context, Intent intent) {
        String message = "";
        if (intent.getExtras().getString("message") != null)
            message = intent.getExtras().getString("message");
        String title = "";
        String data = intent.getExtras().getString("data");
        if (data != null && data.length() > 0) {
            try {
                JSONObject object = new JSONObject(data);
                title = object.optString("title");
                String id = object.optString("id");
                int type = object.optInt("type_notify", 1);
                int numberNotification = object.optInt("totalunread", 0);
                int numberBill = object.optInt("totalbill", 0);
                int idBill = object.optInt("id", 0);
//                PreferenceUtils.getInstance(context).putint(AppConstants.PREFERENCE_KEY.NUMBER_NOTIFICATION, numberNotification);
//                PreferenceUtils.getInstance(context).putint(AppConstants.PREFERENCE_KEY.NUMBER_BILL, numberBill);
//                AppUtils.setIconAppNotification(context, numberNotification + numberBill);
//                context.sendBroadcast(new Intent(AppConstants.BROAD_CAST_ACTION.BROAD_CAST_NUMBER_NOTIFICATION));
//                createNotificationNews(context, title, message == null ? "" : message, id, type, idBill);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    private void createNotificationNews(Context context, String title, String content, String id, int type, int idBill) {
//        Intent resultIntent = null;
//        if (type == 1) {
//            //message
//            resultIntent = new Intent(context, DetailMessageActivity.class);
//            resultIntent.putExtra(AppConstants.KEY_SEND_VIA_INTENT.ID, id);
//        } else {
//            resultIntent = new Intent(context, DetailBillActivity.class);
//            resultIntent.putExtra(AppConstants.KEY_SEND_VIA_INTENT.TYPE, type);
//            BillObject object = new BillObject();
//            object.setId(String.valueOf(idBill));
//            resultIntent.putExtra(AppConstants.KEY_SEND_VIA_INTENT.DATA, object);
//        }
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(context)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setContentTitle(title)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentText(content)
//                        .setAutoCancel(true);
//        PendingIntent resultPendingIntent =
//                PendingIntent.getActivity(
//                        context,
//                        (int) System.currentTimeMillis(),
//                        resultIntent,
//                        PendingIntent.FLAG_ONE_SHOT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
//                R.drawable.ic_launcher);
//        mBuilder.setLargeIcon(icon);
//        NotificationManager mNotifyMgr =
//                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        mNotifyMgr.notify((int) System.currentTimeMillis(), mBuilder.build());
//    }
}