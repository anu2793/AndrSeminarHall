package jp.co.hiropro.seminar_hall.service.socket;



import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.HSSPreference;

/**
 * Created by dinhdv on 2/26/2018.
 */

public class SocketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    public static String TAG = SocketListener.class.getName();
    private onActionRequest mAction;
    private double mLatitude = 0;
    private double mLongitude = 0;

    public SocketListener(double latitude, double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        if (mAction != null)
            mAction.onStart();
//        Gson gson = new Gson();
//        {"Authorization":"236704b0-0897-11e8-8d30-a14420d80227","isSend":0}
        JSONObject objectData = new JSONObject();
        try {
            String auth = HSSPreference.getInstance().getString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), "");
            objectData.put("Authorization", auth);
            objectData.put("isSend", "0");
            objectData.put(AppConstants.KEY_PARAMS.LATITUDE.toString(), String.valueOf(mLatitude));
            objectData.put(AppConstants.KEY_PARAMS.LONGITUDE.toString(), String.valueOf(mLongitude));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webSocket.send(objectData.toString());
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
            mAction.onDone(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
//        if (mAction != null)
//            mAction.onDone();
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        if (mAction != null)
            mAction.onClose();
//        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if (mAction != null)
            mAction.onFail();
    }

    public void setActionRequest(onActionRequest action) {
        mAction = action;
    }

    public interface onActionRequest {
        void onStart();

        void onDone(String result);

        void onFail();

        void onClose();
    }
}