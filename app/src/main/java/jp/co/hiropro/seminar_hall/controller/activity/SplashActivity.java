package jp.co.hiropro.seminar_hall.controller.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.BuildConfig;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.NewsItem;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AccountUtils;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.HSSPreference;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.utils.NetworkUtils;
import me.leolin.shortcutbadger.ShortcutBadger;

import static android.R.attr.name;

/**
 * Created by dinhdv on 7/20/2017.
 */

public class SplashActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {
    private String mIdNews = "";
    private Boolean teachNews = false;
    private String mIdCampaign = "";
    private ProgressDialog mPrg;
    private AccountManager mManagerAccount;
    private Uri dynamicLink;
    private String link = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d("AAAA", BuildConfig.BASE_URL);
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        // Progress Bar
        mPrg = new ProgressDialog(SplashActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
        mManagerAccount = AccountManager.get(this);
        // Deep LINK.
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical() && dynamicLink != null) {
            String uri = this.getIntent().getDataString();
            int index = uri.indexOf("?id=");
            mIdCampaign = uri.substring(index + 4, uri.length()).replace("/", "");
        }
        // END DEEP LINK.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            mIdNews = bundle.getString(AppConstants.KEY_SEND.KEY_ID_NEWS, "");
        teachNews = bundle.getBoolean(AppConstants.KEY_SEND.KEY_TEACH_NEWS, false);
        getDynamicLink();
//        getAccount();
//        sendToken();
    }

    private void getDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        if (pendingDynamicLinkData != null) {
                            dynamicLink = pendingDynamicLinkData.getLink();
                            link = dynamicLink.toString();
                            Log.d("DynamicLink", dynamicLink.toString());
                        } else {
                            Log.d("DynamicLink", "getDynamicLink: no link found");
                        }
                        getAccount();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        getAccount();
                        Log.w("DynamicLink", "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void getAccount() {
        AccountManager manager = AccountManager.get(this);
        Account accountSave = null;
        accountSave = AccountUtils.getAccountUser(SplashActivity.this, manager);
        if (accountSave != null) {
            String name = accountSave.name;
            String password = manager.getPassword(accountSave);
            /**
             * TODO : If user buy package purchase error. Put this id package here.
             * name
             * password
             * id_purchase error.
             */
            if (name.equalsIgnoreCase(AppConstants.TEST.USER_NAME)) {
                AccountUtils.clearAllAccountOfThisApplication(SplashActivity.this, manager);
                User.getInstance().setCurrentUser(null);
                BaseActivity.user = null;
                checkNextScreen();
            } else {
                if (HSSPreference.getInstance(SplashActivity.this).getInt(AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(), 0) == AppConstants.StaticParam.TYPE_OF_FACEBOOK
                        || HSSPreference.getInstance(SplashActivity.this).getInt(AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(), 0) == AppConstants.StaticParam.TYPE_OF_GOOGLE) {
                    if (HSSPreference.getInstance(SplashActivity.this).getInt(AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(), 0) == AppConstants.StaticParam.TYPE_OF_GOOGLE) {
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();
                        if (AppConstants.StaticParam.mGoogleApiClient == null) {
                            AppConstants.StaticParam.mGoogleApiClient = new GoogleApiClient.Builder(this)
                                    .enableAutoManage(this, this)
                                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                    .build();
                        }
                    }
                    logInSocial(HSSPreference.getInstance(SplashActivity.this).getInt(AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(), 0)
                            , accountSave.name, AppConstants.SERVER_PATH.LOGIN.toString(),
                            HSSPreference.getInstance(SplashActivity.this).getString(AppConstants.KEY_PREFERENCE.ACCESS_TOKEN.toString(), ""));
                } else {
                    login(name, password, "");
                }
            }
        } else {
            checkNextScreen();
        }
    }

    public void checkNextScreen() {
        String isLogin = HSSPreference.getInstance(SplashActivity.this).getString(AppConstants.KEY_PREFERENCE.IS_LOGIN.toString(), "");
        if (isLogin.length() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }, 2000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
                    finish();
                }
            }, 2000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void sendToken() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        if (refreshedToken != null && refreshedToken.length() > 0)
        sendTokenToServer(refreshedToken != null ? refreshedToken : "");
        Log.e("TOKEN", "Values : " + refreshedToken);
    }

    protected void sendTokenToServer(String token) {
        Map<String, String> params = new HashMap();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID(SplashActivity.this));
        params.put(AppConstants.KEY_PARAMS.TOKEN.toString(), token);
        params.put(AppConstants.KEY_PARAMS.TYPE.toString(), "2");
        params.put(AppConstants.KEY_PARAMS.DEVICE_NAME.toString(), AppUtils.getDeviceName(SplashActivity.this));
        params.put(AppConstants.KEY_PARAMS.OS_VERSION.toString(), AppUtils.getOsVersion());
        String isFirstRun = HSSPreference.getInstance().getString(AppConstants.KEY_PREFERENCE.IS_FIRST_RUN.toString(), "");
        if (isFirstRun.length() == 0)
            HSSPreference.getInstance().putString(AppConstants.KEY_PREFERENCE.IS_FIRST_RUN.toString(), "has_run");
        params.put(AppConstants.KEY_PARAMS.NEW_APP.toString(), isFirstRun.length() == 0 ? "1" : "2");
        JSONObject parameters = new JSONObject(params);
        Log.d("AAA", parameters.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.SPLASH.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());

            }
        });
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void logInSocial(final int type, final String _email, String url, final String token) {//1-- fb 2-- google
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));//TEST
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID(SplashActivity.this));//TEST
        params.put(AppConstants.KEY_PARAMS.LOGIN_TYPE.toString(), String.valueOf(type));//TEST
        params.put(AppConstants.KEY_PARAMS.ACCESS_TOKEN.toString(), token);//TEST
        params.put(AppConstants.KEY_PARAMS.EMAIL.toString(), _email);//TEST
        if (AppConstants.StaticParam.TYPE_OF_FACEBOOK == type) {
            params.put(AppConstants.KEY_PARAMS.FB_ID.toString(), HSSPreference.getInstance(SplashActivity.this).getString(AppConstants.KEY_PREFERENCE.FB_ID.toString(), ""));//TEST
        }
        RequestDataUtils.requestData(Request.Method.POST, SplashActivity.this, url,
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {

                        try {
                            String auth = object.optString(AppConstants.KEY_PARAMS.AUTH_TOKEN.toString(), "");
                            if (auth.length() > 0) {
                                HSSPreference.getInstance(SplashActivity.this).putString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), auth);
                            }
                            User user = User.parse(object.getJSONObject("info"));
                            user.setIsSocialType(type);
                            User.getInstance().setCurrentUser(user);
//                            AccountUtils.saveAccountInformation(SplashActivity.this, mManagerAccount, _email, token);
                            dismissLoading();
                            if (mIdNews.length() > 0) {
                                // Go from new notification.
                                NewsItem item = new NewsItem();
                                item.setId(Integer.valueOf(mIdNews));
                                item.setmIsFromNotification(true);
                                startActivity(new Intent(SplashActivity.this, NewDetailActivity.class).putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, item).putExtra(AppConstants.KEY_SEND.KEY_TEACH_NEWS, teachNews));
                                finish();
                            } else {
                                startActivity(new Intent(SplashActivity.this, TopActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(int error) {
                        if (error == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                            HSSDialog.show(SplashActivity.this, getString(R.string.msg_session_expire), getString(R.string.txt_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HSSDialog.dismissDialog();
                                    sendRequestLoginSkipUser(AppConstants.TEST.USER_NAME, AppConstants.TEST.PASSWORD);
                                }
                            });
                        } else if (error == AppConstants.STATUS_REQUEST.ACCOUNT_BLOCKED) {
                            HSSDialog.show(SplashActivity.this, getString(R.string.msg_account_has_been_block), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logoutSocial();
                                    AccountUtils.clearAllAccountOfThisApplication(SplashActivity.this, mManagerAccount);
                                    HSSPreference.getInstance(SplashActivity.this).putInt(AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(), 0);
                                    HSSPreference.getInstance(SplashActivity.this).putString(AppConstants.KEY_PREFERENCE.ACCESS_TOKEN.toString(), AppConstants.StaticParam.EMPTY_VALUE_STRING);
                                    //Clear shortcut badger.
                                    ShortcutBadger.applyCount(getApplicationContext(), 0);
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    HSSDialog.dismissDialog();
                                    finish();
                                }
                            }).show();
                        }
                    }
                });
    }

    protected void login(final String email, final String password, final String id_purchase) {
        Log.e("ACcount", "user-name : " + name + "--- password : " + password);
        Map<String, String> params = new HashMap();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID(SplashActivity.this));
        params.put(AppConstants.KEY_PARAMS.EMAIL.toString(), email);
        params.put(AppConstants.KEY_PARAMS.PASSWORD.toString(), password);
        if (id_purchase.length() > 0)
            params.put(AppConstants.KEY_PARAMS.ID_PURCHASE.toString(), password);
        JSONObject parameters = new JSONObject(params);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.LOGIN.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 0);
                            String mCurrentCodePurchase = "";
                            String msg = response.optString(AppConstants.KEY_PARAMS.MESSAGE.toString(), "");
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                    String auth = objectData.optString(AppConstants.KEY_PARAMS.AUTH_TOKEN.toString(), "");
                                    if (auth.length() > 0) {
                                        HSSPreference.getInstance(SplashActivity.this).putString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), auth);
                                    }
                                    User user = User.parse(objectData.getJSONObject("info"));
                                    User.getInstance().setCurrentUser(user);
                                    JSONObject objectPremiumInfo = objectData.getJSONObject(AppConstants.KEY_PARAMS.PREMIUM_INFO.toString());
                                    if (objectPremiumInfo.length() > 0) {
                                        mCurrentCodePurchase = objectPremiumInfo.optString(AppConstants.KEY_PARAMS.PRICE_CODE.toString(), "");
                                    }
                                    if (dynamicLink != null) {
                                        String stringUri = dynamicLink.toString();
                                        if (stringUri.contains("hiro-evergreen.video/links/receiveCard")) {
                                            String splits[] = stringUri.split("/");
                                            String last = splits[splits.length - 1];
                                            int id = Integer.parseInt(last.split("&")[0]);
                                            User userObject = new User();
                                            userObject.setId(id);
                                            if (userObject != null) {
                                                Log.d("DynamicLink", "1");
                                                startActivity(new Intent(SplashActivity.this, TeacherProfileCardActivity.class).putExtra(AppConstants.KEY_SEND.KEY_DATA, userObject));
                                                finish();
                                            } else {
                                                redirectLink(mCurrentCodePurchase);
                                            }
                                        }
                                    } else {
                                        redirectLink(mCurrentCodePurchase);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                goMaintainScreen(SplashActivity.this, msg);
                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                                HSSDialog.show(SplashActivity.this, getString(R.string.msg_session_expire), getString(R.string.txt_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        HSSDialog.dismissDialog();
                                        sendRequestLoginSkipUser(AppConstants.TEST.USER_NAME, AppConstants.TEST.PASSWORD);
                                    }
                                });
                            } else if (status == AppConstants.STATUS_REQUEST.ACCOUNT_BLOCKED) {
                                HSSDialog.show(SplashActivity.this, getString(R.string.msg_account_has_been_block), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        clearAllGoLogin();
                                    }
                                }).show();
                            } else {
                                HSSDialog.show(SplashActivity.this, getString(R.string.msg_request_error_try_again), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        clearAllGoLogin();
                                    }
                                });
                            }

                        } else {
                            HSSDialog.show(SplashActivity.this, getString(R.string.msg_account_has_been_deleted), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clearAllGoLogin();
                                }
                            }).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                NetworkUtils.showDialogError(SplashActivity.this, error, getString(R.string.txt_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HSSDialog.dismissDialog();
                        login(email, password, id_purchase);
                    }
                }, getString(R.string.txt_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HSSDialog.dismissDialog();
                        finish();
                    }
                });

            }
        });
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void redirectLink(String mCurrentCodePurchase) {
        if (mIdNews.length() > 0) {
            // Go from new notification.
            NewsItem item = new NewsItem();
            item.setId(Integer.valueOf(mIdNews));
            item.setmIsFromNotification(true);
            startActivity(new Intent(SplashActivity.this, NewDetailActivity.class).putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, item).putExtra(AppConstants.KEY_SEND.KEY_TEACH_NEWS, teachNews));
            finish();
        } else if (mIdCampaign.length() > 0) {
            startActivity(new Intent(SplashActivity.this, TopActivity.class)
                    .putExtra(AppConstants.KEY_SEND.KEY_ID_CAMPAIGN, mIdCampaign)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else {
            // Test send id purchase to  top. Need remove params when done.
            startActivity(mCurrentCodePurchase.length() > 0 ? new Intent(SplashActivity.this, TopActivity.class)
                    .putExtra(AppConstants.KEY_SEND.KEY_SEND_ID_PURCHASE, mCurrentCodePurchase).
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    : new Intent(SplashActivity.this, TopActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    private void clearAllGoLogin() {
        logoutSocial();
        AccountUtils.clearAllAccountOfThisApplication(SplashActivity.this, mManagerAccount);
        HSSPreference.getInstance(SplashActivity.this).putInt(AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(), 0);
        HSSPreference.getInstance(SplashActivity.this).putString(AppConstants.KEY_PREFERENCE.ACCESS_TOKEN.toString(), AppConstants.StaticParam.EMPTY_VALUE_STRING);
        //Clear shortcut badger.
        ShortcutBadger.applyCount(getApplicationContext(), 0);
        startActivity(new Intent(SplashActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        HSSDialog.dismissDialog();
        finish();
    }

    private void logoutSocial() {
        if (null == User.getInstance().getCurrentUser())
            return;
        if (User.getInstance().getCurrentUser().getIsSocialType() == AppConstants.StaticParam.TYPE_OF_FACEBOOK) {
            LoginManager.getInstance().logOut();
        } else if (User.getInstance().getCurrentUser().getIsSocialType() == AppConstants.StaticParam.TYPE_OF_GOOGLE) {
            signOut();
        }
    }

    private void signOut() {
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

    public void goMaintainScreen(Activity activity, String msg) {
        startActivity(new Intent(activity, MaintainActivity.class).putExtra(AppConstants.KEY_SEND.KEY_MSG_MAINTAIN, msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void sendRequestLoginSkipUser(final String email, final String password) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID(SplashActivity.this));
        params.put(AppConstants.KEY_PARAMS.EMAIL.toString(), email);
        params.put(AppConstants.KEY_PARAMS.PASSWORD.toString(), password);
        JSONObject parameters = new JSONObject(params);
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.LOGIN.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            String msg = response.optString(AppConstants.KEY_PARAMS.MESSAGE.toString(), "");
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                    String auth = objectData.optString(AppConstants.KEY_PARAMS.AUTH_TOKEN.toString(), "");
                                    if (auth.length() > 0) {
                                        HSSPreference.getInstance(SplashActivity.this).putString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), auth);
                                    }
                                    User user = User.parse(objectData.getJSONObject("info"));
                                    User.getInstance().setCurrentUser(user);
                                    AccountUtils.saveAccountInformation(SplashActivity.this, mManagerAccount, email, password);
                                    dismissLoading();
                                    startActivity(new Intent(SplashActivity.this, TopActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                goMaintainScreen(SplashActivity.this, msg);
                            } else {
                                dismissLoading();
                                HSSDialog.show(SplashActivity.this, getString(R.string.msg_login_error_password_id_error));
                            }
                        }
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(SplashActivity.this, error);
            }
        });
        request.setHeaders(header);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    public void showLoading() {
        if (!mPrg.isShowing() && mPrg != null)
            mPrg.show();
    }

    public void dismissLoading() {
        if (mPrg.isShowing())
            mPrg.dismiss();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
