package jp.co.hiropro.seminar_hall.util.fcm;

import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.HSSPreference;

/**
 * Created by dinhdv on 7/20/2017.
 */

public class ServiceFCMInstanceID extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendTokenToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendTokenToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Map<String, String> params = new HashMap();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), Settings.Secure.getString(ForestApplication.getInstance().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        params.put(AppConstants.KEY_PARAMS.TOKEN.toString(), token);
        params.put(AppConstants.KEY_PARAMS.TYPE.toString(), "2");
        params.put(AppConstants.KEY_PARAMS.DEVICE_NAME.toString(), AppUtils.getDeviceName(getApplicationContext()));
        params.put(AppConstants.KEY_PARAMS.OS_VERSION.toString(), AppUtils.getOsVersion());
        String isFirstRun = HSSPreference.getInstance().getString(AppConstants.KEY_PREFERENCE.IS_FIRST_RUN.toString(), "");
        if (isFirstRun.length() == 0)
            HSSPreference.getInstance().putString(AppConstants.KEY_PREFERENCE.IS_FIRST_RUN.toString(), "has_run");
        params.put(AppConstants.KEY_PARAMS.NEW_APP.toString(), isFirstRun.length() == 0 ? "1" : "2");
        JSONObject parameters = new JSONObject(params);
        Log.d("AAA",parameters.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.SPLASH.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Log.d("AAA",response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());

            }
        });
        ForestApplication.getInstance().addToRequestQueue(request);
    }

}