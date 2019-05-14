package vn.hanelsoft.forestpublishing.util;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;
import vn.hanelsoft.dialog.HSSDialog;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.controller.activity.LoginActivity;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.utils.NetworkUtils;

/**
 * Created by dinhdv on 11/14/2017.
 */

public class RequestDataUtils {
    private static final int MY_SOCKET_TIMEOUT_MS = 30000;

    /**
     * Using request data from server
     *
     * @param activity
     * @param params
     * @param action
     */
    public static void requestData(int method, final Activity activity, String url, final Map<String, String> params, final onResult action) {
        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONObject objectResponse = new JSONObject(response);
                        int status = objectResponse.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                        String msg = objectResponse.optString(AppConstants.KEY_PARAMS.MESSAGE.toString());
                        if (status == AppConstants.REQUEST_SUCCESS) {
                            try {
                                JSONObject objectData = objectResponse.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                if (action != null) {
                                    action.onSuccess(objectData, msg);
                                }
                            } catch (JSONException e) {
                                if (action != null)
                                    action.onFail(status);
                                e.printStackTrace();
                            }
                        } else {
                            if (status == AppConstants.STATUS_REQUEST.ACCOUNT_BLOCKED) {
                                HSSDialog.show(activity, activity.getString(R.string.msg_account_has_been_block), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        logoutSocial();
                                        AccountManager mManagerAccount = AccountManager.get(activity);
                                        AccountUtils.clearAllAccountOfThisApplication(activity, mManagerAccount);
                                        HSSPreference.getInstance(activity).putInt(AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(), 0);
                                        HSSPreference.getInstance(activity).putString(AppConstants.KEY_PREFERENCE.ACCESS_TOKEN.toString(), AppConstants.StaticParam.EMPTY_VALUE_STRING);
                                        //Clear shortcut badger.
                                        ShortcutBadger.applyCount(activity, 0);
                                        activity.startActivity(new Intent(activity, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        HSSDialog.dismissDialog();
                                        activity.finish();
                                    }
                                }).show();
                            } else {
                                if (action != null)
                                    action.onFail(status);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (action != null)
                        action.onFail(AppConstants.STATUS_REQUEST.REQUEST_ERROR);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (action != null)
                            action.onFail(AppConstants.STATUS_REQUEST.REQUEST_ERROR);
                        NetworkUtils.showDialogError(activity, error, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HSSDialog.dismissDialog();
                            }
                        });
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        request.setHeaders(getAuthHeader());
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    /**
     * Service with authen.
     *
     * @param method
     * @param activity
     * @param url
     * @param params
     * @param action
     */
    public static void requestDataWithAuthen(int method, final Activity activity, String url, final Map<String, String> params, final onResult action) {
        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONObject objectResponse = new JSONObject(response);
                        int status = objectResponse.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                        String msg = objectResponse.optString(AppConstants.KEY_PARAMS.MESSAGE.toString());
                        if (status == AppConstants.REQUEST_SUCCESS) {
                            try {
                                JSONObject objectData = objectResponse.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                if (action != null) {
                                    action.onSuccess(objectData, msg);
                                }
                            } catch (JSONException e) {
                                if (action != null)
                                    action.onFail(AppConstants.STATUS_REQUEST.REQUEST_ERROR);
                                e.printStackTrace();
                            }
                        } else {
//                            if (status != AppConstants.STATUS_REQUEST.CONTACT_HAS_EXISTS)
//                                HSSDialog.show(activity, msg);
                            if (status == AppConstants.STATUS_REQUEST.ACCOUNT_BLOCKED) {
                                HSSDialog.show(activity, activity.getString(R.string.msg_account_has_been_block), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        logoutSocial();
                                        AccountManager mManagerAccount = AccountManager.get(activity);
                                        AccountUtils.clearAllAccountOfThisApplication(activity, mManagerAccount);
                                        HSSPreference.getInstance(activity).putInt(AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(), 0);
                                        HSSPreference.getInstance(activity).putString(AppConstants.KEY_PREFERENCE.ACCESS_TOKEN.toString(), AppConstants.StaticParam.EMPTY_VALUE_STRING);
                                        //Clear shortcut badger.
                                        ShortcutBadger.applyCount(activity, 0);
                                        activity.startActivity(new Intent(activity, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        HSSDialog.dismissDialog();
                                        activity.finish();
                                    }
                                }).show();
                            } else {
                                if (action != null)
                                    action.onFail(status);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (action != null)
                        action.onFail(AppConstants.STATUS_REQUEST.REQUEST_ERROR);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (action != null)
                            action.onFail(AppConstants.STATUS_REQUEST.REQUEST_ERROR);
                        NetworkUtils.showDialogError(activity, error, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HSSDialog.dismissDialog();
                            }
                        });
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        request.setHeaders(getAuthHeader());
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private static void logoutSocial() {
        if (null == User.getInstance() || null == User.getInstance().getCurrentUser())
            return;
        if (User.getInstance().getCurrentUser().getIsSocialType() == AppConstants.StaticParam.TYPE_OF_FACEBOOK) {
            LoginManager.getInstance().logOut();
        } else if (User.getInstance().getCurrentUser().getIsSocialType() == AppConstants.StaticParam.TYPE_OF_GOOGLE) {
            signOut();
        }
    }

    private static void signOut() {
        AppConstants.StaticParam.mGoogleApiClient.connect();
        AppConstants.StaticParam.mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (AppConstants.StaticParam.mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(AppConstants.StaticParam.mGoogleApiClient);
                    AppConstants.StaticParam.mGoogleApiClient = null;
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        });
    }

    //    protected abstract void registerEvent();
    protected static HashMap<String, String> getAuthHeader() {
        HashMap<String, String> header = new HashMap<>();
        String auth = HSSPreference.getInstance().getString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), "");
        header.put("Authorization", auth);
        return header;
    }


    public interface onResult {
        void onSuccess(JSONObject object, String msg);

        void onFail(int error);
    }
}
