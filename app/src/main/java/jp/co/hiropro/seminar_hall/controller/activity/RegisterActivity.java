package jp.co.hiropro.seminar_hall.controller.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.leolin.shortcutbadger.ShortcutBadger;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AccountUtils;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.HSSPreference;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.view.DialogEmail;
import jp.co.hiropro.seminar_hall.view.EditTextApp;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 7/20/2017.
 */

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient
        .OnConnectionFailedListener, DialogEmail.OnResult {
    @BindView(R.id.edt_email)
    EditTextApp mEdtEmail;
    @BindView(R.id.edt_password)
    EditTextApp mEdtPassword;
    @BindView(R.id.edt_re_password)
    EditTextApp mEdtRePassword;
    //    @BindView(R.id.rl_main)
//    RelativeLayout mRlMain;
    @BindView(R.id.rl_email)
    RelativeLayout mRlEmail;
    @BindView(R.id.rl_password)
    RelativeLayout mRlPassword;
    @BindView(R.id.rl_re_password)
    RelativeLayout mRlRePassword;
    @BindView(R.id.tv_error_re_password)
    TextView mTvErrorRePassword;
    @BindView(R.id.tv_error_password)
    TextView mTvErrorPassword;
    @BindView(R.id.tv_error_email)
    TextView mTvErrorEmail;
    @BindView(R.id.tv_skip)
    TextViewApp mTvSkip;
    @BindView(R.id.ll_input)
    LinearLayout ll_input;
    private AccountManager mManagerAccount;
    private ProgressDialog mPrg;
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 007;
    private ProgressDialog mProgressDialog;
    private String email;
    private String fbId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FacebookSdk.sdkInitialize(getApplicationContext());
        ButterKnife.bind(this);
        initView();
        callbackManager = CallbackManager.Factory.create();
        isLoginFacebook();
        mManagerAccount = AccountManager.get(this);
        // Progress Bar
        mPrg = new ProgressDialog(RegisterActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
        //Clear shortcut badger.
        ShortcutBadger.applyCount(getApplicationContext(), 0);
        // Using to check visibility of skip function
//        if (!User.getInstance().isSkipUser())
        checkSkipOption();
    }

    private void isLoginFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        fillDataFbLogin();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        HSSDialog.show(RegisterActivity.this, getString(R.string.msg_request_error_try_again), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HSSDialog.dismissDialog();
                            }
                        }).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
//        HSSDialog.show(RegisterActivity.this, "Values :" + result.getStatus() + "--" + result.isSuccess() + "--" + result.getSignInAccount(), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HSSDialog.dismissDialog();
//            }
//        }).show();
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            email = acct.getEmail();
            new RetrieveTokenTask().execute(acct.getAccount().name);
        }
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
            } catch (UserRecoverableAuthException e) {
            } catch (GoogleAuthException e) {
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            sendRequestRegisterSocial(AppConstants.StaticParam.TYPE_OF_GOOGLE,
                    email,
                    AppConstants.SERVER_PATH.LOGIN.toString(),
                    s);
        }
    }

    private void checkSkipOption() {
        showLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConstants
                .SERVER_PATH.CHECK_CONFIG_SKIP.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismissLoading();
                if (response.length() > 0) {
                    try {
                        JSONObject objectData = new JSONObject(response);
                        int status = objectData.optInt(AppConstants.KEY_PARAMS.STATUS.toString(),
                                1);
                        String msg = objectData.optString(AppConstants.KEY_PARAMS.MESSAGE
                                .toString(), "");
                        if (status == AppConstants.REQUEST_SUCCESS) {
                            dismissLoading();
                            try {
                                JSONObject data = objectData.getJSONObject(AppConstants
                                        .KEY_PARAMS.DATA.toString());
                                int statusSkip = data.optInt(AppConstants.KEY_PARAMS
                                        .REGISTER_REQUIRE.toString(), 1);
                                mTvSkip.setVisibility(statusSkip == 0 ? View.VISIBLE : View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                            goMaintainScreen(RegisterActivity.this, msg);
                        } else {
                            mTvSkip.setVisibility(View.GONE);
                            dismissLoading();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mTvSkip.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                mTvSkip.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf
                        (AppConstants.CLIENT_ID));
                return params;
            }
        };
        ForestApplication.getInstance().addToRequestQueue(stringRequest);

    }

    private void initView() {
        ll_input.requestFocus();
        mEdtEmail.clearFocus();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        if (AppConstants.StaticParam.mGoogleApiClient == null) {
            AppConstants.StaticParam.mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
//        mRlMain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
// .OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                mRlMain.getWindowVisibleDisplayFrame(r);
//                int screenHeight = mRlMain.getRootView().getHeight();
//                int keypadHeight = screenHeight - r.bottom;
//                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to
// determine keypad height.
//                    mRlMain.animate().translationY(-230f).start();
//                } else {
//                    mRlMain.animate().translationY(0f).start();
//                }
//            }
//        });

        mEdtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    clearError();
            }
        });
        mEdtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mRlEmail.setBackgroundResource(R.drawable
                            .bg_edittext_transparent_border_select);
                } else {
                    mRlEmail.setBackgroundResource(R.drawable
                            .bg_edittext_transparent_border);
                }
            }
        });
        mEdtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mRlPassword.setBackgroundResource(R.drawable
                            .bg_edittext_transparent_border_select);
                } else {
                    mRlPassword.setBackgroundResource(R.drawable
                            .bg_edittext_transparent_border);
                }
            }
        });
        mEdtRePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mRlRePassword.setBackgroundResource(R.drawable
                            .bg_edittext_transparent_border_select);
                } else {
                    mRlRePassword.setBackgroundResource(R.drawable
                            .bg_edittext_transparent_border);
                }
            }
        });
        mEdtRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    clearError();
            }
        });

        mEdtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    clearError();
            }
        });
    }

    @OnClick({R.id.btn_submit, R.id.btn_login, R.id.tv_skip, R.id.tv_privacy, R.id.btn_facebook,
            R.id.btn_google})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                String email = mEdtEmail.getText().toString();
                String password = mEdtPassword.getText().toString();
                String rePassword = mEdtRePassword.getText().toString();
                if (checkValidate(email, password, rePassword)) {
                    registerAccount(email, password);
                }
                break;
            case R.id.btn_login:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.tv_skip:
                // Create skip user.
                if (User.getInstance().isSkipUser())
                    finish();
                else
                    sendRequestLoginSkipUser(AppConstants.TEST.USER_NAME, AppConstants.TEST
                            .PASSWORD);
                break;
            case R.id.tv_privacy:
                startActivity(new Intent(RegisterActivity.this, PolicyActivity.class).putExtra
                        (AppConstants.KEY_SEND.KEY_DATA, false));
                break;
            case R.id.btn_facebook:
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                if (loggedIn) {//chua login accesstoken = null
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList
                            ("public_profile", "email"));
                } else {
                    fillDataFbLogin();
                }
                break;
            case R.id.btn_google:
                signIn();
                break;
        }
    }

    private void fillDataFbLogin() {
        getUserDetailsFromFB(AccessToken.getCurrentAccessToken());
    }

    public void getUserDetailsFromFB(AccessToken accessToken) {
        GraphRequest req = GraphRequest.newMeRequest(accessToken, new GraphRequest
                .GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    email = object.getString("email");
                } catch (JSONException e) {
                    email = "";
                }
                try {
                    fbId = object.getString("id");
                } catch (JSONException e) {
                    fbId = "";
                }
                HSSPreference.getInstance(RegisterActivity.this).putString(AppConstants
                        .KEY_PREFERENCE.FB_ID.toString(), fbId);
                //save fbID vao perfer
                sendRequestRegisterSocial(AppConstants.StaticParam.TYPE_OF_FACEBOOK,
                        email,
                        AppConstants.SERVER_PATH.LOGIN.toString(),
                        AccessToken.getCurrentAccessToken().getToken());
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        req.setParameters(parameters);
        req.executeAsync();
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearError();
    }

    private void sendRequestRegisterSocial(final int type, String _email, String url, final
    String token) {//1-- fb 2-- google
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants
                .CLIENT_ID));//TEST
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID
                (RegisterActivity.this));//TEST
        params.put(AppConstants.KEY_PARAMS.LOGIN_TYPE.toString(), String.valueOf(type));//TEST
        params.put(AppConstants.KEY_PARAMS.ACCESS_TOKEN.toString(), token);//TEST
        params.put(AppConstants.KEY_PARAMS.EMAIL.toString(), _email);//TEST
        if (AppConstants.StaticParam.TYPE_OF_FACEBOOK == type) {
            params.put(AppConstants.KEY_PARAMS.FB_ID.toString(), fbId);//TEST
        }
        RequestDataUtils.requestData(Request.Method.POST, RegisterActivity.this, url,
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        boolean isSkipUser = User.getInstance().isSkipUser();
                        try {
                            String auth = object.optString(AppConstants.KEY_PARAMS.AUTH_TOKEN
                                    .toString(), "");
                            if (auth.length() > 0) {
                                HSSPreference.getInstance(RegisterActivity.this).putString
                                        (AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), auth);
                            }
                            if (AppConstants.StaticParam.TYPE_OF_FACEBOOK == type) {
                                HSSPreference.getInstance(RegisterActivity.this).putInt
                                        (AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(),
                                                AppConstants.StaticParam.TYPE_OF_FACEBOOK);
                            } else if (AppConstants.StaticParam.TYPE_OF_GOOGLE == type) {
                                HSSPreference.getInstance(RegisterActivity.this).putInt
                                        (AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(),
                                                AppConstants.StaticParam.TYPE_OF_GOOGLE);
                            }
                            HSSPreference.getInstance(RegisterActivity.this).putString
                                    (AppConstants.KEY_PREFERENCE.ACCESS_TOKEN.toString(), token);
                            User user = User.parse(object.getJSONObject("info"));
                            user.setIsSocialType(type);
                            User.getInstance().setCurrentUser(user);
                            email = object.getJSONObject("info").optString("email");
                            AccountUtils.saveAccountInformation(RegisterActivity.this,
                                    mManagerAccount, email, token);
                            dismissLoading();
                            if (!isSkipUser)
                                startActivity(new Intent(RegisterActivity.this, TopActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent
                                                .FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(int error) {
                        if (error == AppConstants.STATUS_REQUEST.REQUEST_REQUIRE_EMAIL) {
                            HSSDialog.show(RegisterActivity.this, getString(R.string.msg_need_input_email_to_complete_register), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HSSDialog.dismissDialog();
                                    DialogEmail cdd = new DialogEmail(RegisterActivity.this);
                                    cdd.setCallBack(RegisterActivity.this);
                                    cdd.show();
                                }
                            }).show();
                        } else if (error == AppConstants.STATUS_REQUEST.EMAIL_HAS_REGISTER) {
                            HSSDialog.show(RegisterActivity.this, "このメールアドレスは既に他のFacebookアカウントに紐付いています。", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HSSDialog.dismissDialog();
                                }
                            }).show();
                        } else {
                            HSSDialog.show(RegisterActivity.this, getString(R.string.msg_request_error_try_again), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HSSDialog.dismissDialog();
                                }
                            }).show();
                        }

                    }
                });
    }

    private void sendRequestLoginSkipUser(final String email, final String password) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants
                .CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID
                (RegisterActivity.this));
        params.put(AppConstants.KEY_PARAMS.EMAIL.toString(), email);
        params.put(AppConstants.KEY_PARAMS.PASSWORD.toString(), password);
        JSONObject parameters = new JSONObject(params);
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants
                .SERVER_PATH.LOGIN.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString
                                    (), 1);
                            String msg = response.optString(AppConstants.KEY_PARAMS.MESSAGE
                                    .toString(), "");
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants
                                            .KEY_PARAMS.DATA.toString());
                                    String auth = objectData.optString(AppConstants.KEY_PARAMS
                                            .AUTH_TOKEN.toString(), "");
                                    if (auth.length() > 0) {
                                        HSSPreference.getInstance(RegisterActivity.this)
                                                .putString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN
                                                        .toString(), auth);
                                    }
                                    User user = User.parse(objectData.getJSONObject("info"));
                                    User.getInstance().setCurrentUser(user);
                                    AccountUtils.saveAccountInformation(RegisterActivity.this,
                                            mManagerAccount, email, password);
                                    dismissLoading();
                                    startActivity(new Intent(RegisterActivity.this, TopActivity
                                            .class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                goMaintainScreen(RegisterActivity.this, msg);
                            } else if (status == AppConstants.STATUS_REQUEST.LIMIT_DEVICE) {
                                dismissLoading();
                                HSSDialog.show(RegisterActivity.this, getString(R.string
                                        .msg_error_limit_device));
                            } else {
                                dismissLoading();
                                HSSDialog.show(RegisterActivity.this, getString(R.string
                                        .msg_login_error_password_id_error));
                            }
                        }
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(RegisterActivity.this, error);
            }
        });
        request.setHeaders(header);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(AppConstants.StaticParam
                .mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void registerAccount(final String email, final String password) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants
                .CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.EMAIL.toString(), email);
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID
                (RegisterActivity.this));
        params.put(AppConstants.KEY_PARAMS.PASSWORD.toString(), password);
        JSONObject parameters = new JSONObject(params);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants
                .SERVER_PATH.REGISTER.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString
                                    (), 1);
                            String msg = response.optString(AppConstants.KEY_PARAMS.MESSAGE
                                    .toString(), "");
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                // Update authen token.
                                boolean isSkipUser = User.getInstance().isSkipUser();
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants
                                            .KEY_PARAMS.DATA.toString());
                                    String auth = objectData.optString(AppConstants.KEY_PARAMS
                                            .AUTH_TOKEN.toString(), "");
                                    if (auth.length() > 0) {
                                        HSSPreference.getInstance(RegisterActivity.this)
                                                .putString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN
                                                        .toString(), auth);
                                    }
                                    User user = User.parse(objectData.getJSONObject("info"));
                                    User.getInstance().setCurrentUser(user);
                                    AccountUtils.saveAccountInformation(RegisterActivity.this,
                                            mManagerAccount, email, password);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                AppUtils.setIsFirstLogin(RegisterActivity.this);
                                startActivity(new Intent(RegisterActivity.this,
                                        RegisterSuccessActivity.class).putExtra(AppConstants
                                        .KEY_INTENT.IS_SKIP_USER.toString(), isSkipUser));
                                finish();
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                goMaintainScreen(RegisterActivity.this, msg);
                            } else if (status == AppConstants.STATUS_REQUEST.EMAIL_HAS_REGISTED) {
                                HSSDialog.show(RegisterActivity.this, getString(R.string
                                        .msg_email_has_registed)).show();
                            } else {
                                sendAgainRequestRegister(email, password);
                            }
                        }
                        dismissLoading();
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(RegisterActivity.this, error, getString(R.string
                        .txt_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HSSDialog.dismissDialog();
                        registerAccount(email, password);
                    }
                }, getString(R.string.txt_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HSSDialog.dismissDialog();
                    }
                });

            }
        });
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    protected void sendAgainRequestRegister(final String email, final String password) {
        HSSDialog.show(RegisterActivity.this, getString(R.string.msg_login_error_try_again),
                getString(R.string.txt_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HSSDialog.dismissDialog();
                        registerAccount(email, password);
                    }
                }, getString(R.string.txt_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HSSDialog.dismissDialog();
                    }
                }).show();
    }

    private boolean checkValidate(String email, String password, String rePassword) {
        boolean valuesEmail = checkErrorEmail(email);
        boolean valuePassword = checkErrorPassword(password);
        boolean valueRePassword = checkErrorRePassword(password, rePassword);
        return valuesEmail && valuePassword && valueRePassword;
    }

    private boolean checkErrorEmail(String email) {
        if (email.length() == 0) {
            setErrorMsg(getString(R.string.msg_email_empty));
            return false;
        } else if (email.length() > 0 && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setErrorMsg(getString(R.string.msg_email_not_validate));
            return false;
        } else {
            setErrorMsg("");
            return true;
        }
    }

    protected void setErrorMsg(String msg) {
        mRlEmail.setBackgroundResource(msg.length() > 0 ? R.drawable.bg_edittext_error : R
                .drawable.bg_edittext_transparent_border);
        mTvErrorEmail.setVisibility(msg.length() > 0 ? View.VISIBLE : View.GONE);
        if (msg.length() > 0) {
            mTvErrorEmail.setText(msg);
        }
    }

    private boolean checkErrorPassword(String password) {
        if (password.length() == 0) {
            setErrorMsgPassword(getString(R.string.msg_password_empty));
            return false;
        } else if (password.length() > 0 && password.length() < 6) {
            setErrorMsgPassword(getString(R.string.msg_password_not_enough_length));
            return false;
        } else if (password.length() > 0 && password.length() > 12) {
            setErrorMsgPassword(getString(R.string.msg_password_too_long));
            return false;
        } else {
            setErrorMsgPassword("");
            return true;
        }
    }

    protected void setErrorMsgPassword(String msg) {
        mRlPassword.setBackgroundResource(msg.length() > 0 ? R.drawable.bg_edittext_error : R
                .drawable.bg_edittext_transparent_border);
        mTvErrorPassword.setVisibility(msg.length() > 0 ? View.VISIBLE : View.GONE);
        if (msg.length() > 0) {
            mTvErrorPassword.setText(msg);
        }
    }

    private boolean checkErrorRePassword(String password, String rePassword) {
        if (rePassword.length() == 0) {
            setErrorReMsgPassword(getString(R.string.msg_re_password_empty));
            return false;
        } else if (rePassword.length() > 0 && password.length() < 6) {
            setErrorReMsgPassword(getString(R.string.msg_password_not_enough_length));
            return false;
        } else if (!password.equalsIgnoreCase(rePassword)) {
            setErrorReMsgPassword(getString(R.string.msg_password_not_equal_re_password));
            return false;
        } else {
            setErrorReMsgPassword("");
            return true;
        }
    }

    protected void setErrorReMsgPassword(String msg) {
        mRlRePassword.setBackgroundResource(msg.length() > 0 ? R.drawable.bg_edittext_error : R
                .drawable.bg_edittext_transparent_border);
        mTvErrorRePassword.setVisibility(msg.length() > 0 ? View.VISIBLE : View.GONE);
        if (msg.length() > 0) {
            mTvErrorRePassword.setText(msg);
        }
    }

    protected void clearError() {
        mRlRePassword.setBackgroundResource(R.drawable.bg_edittext_transparent_border);
        mRlPassword.setBackgroundResource(R.drawable.bg_edittext_transparent_border);
        mRlEmail.setBackgroundResource(R.drawable.bg_edittext_transparent_border);
        mTvErrorRePassword.setVisibility(View.GONE);
        mTvErrorPassword.setVisibility(View.GONE);
        mTvErrorEmail.setVisibility(View.GONE);
    }

    public void showLoading() {
        if (!mPrg.isShowing() && mPrg != null)
            mPrg.show();
    }

    public void dismissLoading() {
        if (mPrg.isShowing())
            mPrg.dismiss();
    }

    public void goMaintainScreen(Activity activity, String msg) {
        startActivity(new Intent(activity, MaintainActivity.class).putExtra(AppConstants.KEY_SEND
                .KEY_MSG_MAINTAIN, msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void isCallBack(String strValue) {
        email = strValue;
        sendRequestRegisterSocial(AppConstants.StaticParam.TYPE_OF_FACEBOOK,
                strValue,
                AppConstants.SERVER_PATH.LOGIN.toString(),
                AccessToken.getCurrentAccessToken().getToken());
    }
}
