package vn.hanelsoft.forestpublishing.controller.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
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
import vn.hanelsoft.dialog.HSSDialog;
import vn.hanelsoft.dialog.LoadingDialog;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.util.AccountUtils;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.AppUtils;
import vn.hanelsoft.forestpublishing.util.HSSPreference;
import vn.hanelsoft.forestpublishing.util.RequestDataUtils;
import vn.hanelsoft.forestpublishing.view.DialogEmail;
import vn.hanelsoft.forestpublishing.view.EditTextApp;
import vn.hanelsoft.forestpublishing.view.TextViewApp;
import vn.hanelsoft.utils.NetworkUtils;

/**
 * Created by dinhdv on 7/20/2017.
 */

public class LoginActivity extends FragmentActivity implements GoogleApiClient
        .OnConnectionFailedListener, DialogEmail.OnResult {
    //    @BindView(R.id.rl_main)
//    RelativeLayout mRlMain;
    @BindView(R.id.edt_email)
    EditTextApp mEdtEmail;
    @BindView(R.id.edt_password)
    EditTextApp mEdtPassword;
    @BindView(R.id.tv_error_password)
    TextView mTvErrorPassword;
    @BindView(R.id.tv_error_email)
    TextView mTvErrorEmail;
    @BindView(R.id.rl_email)
    RelativeLayout mRlEmail;
    @BindView(R.id.rl_password)
    RelativeLayout mRlPassword;
    @BindView(R.id.cl_main)
    ConstraintLayout cl_main;
    @BindView(R.id.tv_skip)
    TextViewApp mTvSkip;
    private AccountManager mManagerAccount;
    private ProgressDialog mPrg;
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 7;
    private String email;
    private String fbId;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initControls();
        mManagerAccount = AccountManager.get(this);
        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        AppConstants.StaticParam.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        isLoginFacebook();
        // Progress Bar
        mPrg = new ProgressDialog(LoginActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
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
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                        // App code
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string
                                .error), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fillDataFbLogin() {
        getUserDetailsFromFB(AccessToken.getCurrentAccessToken());
    }

    public void getUserDetailsFromFB(AccessToken accessToken) {
        GraphRequest req = GraphRequest.newMeRequest(accessToken, new GraphRequest
                .GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (null != object && object.length() > 0) {
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
                    HSSPreference.getInstance(LoginActivity.this).putString(AppConstants
                            .KEY_PREFERENCE.FB_ID.toString(), fbId);
                    sendRequestRegisterSocial(AppConstants.StaticParam.TYPE_OF_FACEBOOK,
                            email,
                            AppConstants.SERVER_PATH.LOGIN.toString(),
                            AccessToken.getCurrentAccessToken().getToken());
                } else {
                    HSSDialog.show(LoginActivity.this, getString(R.string.msg_login_error_try_again), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HSSDialog.dismissDialog();
                        }
                    }).show();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        req.setParameters(parameters);
        req.executeAsync();
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
            if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(s)) {
                sendRequestRegisterSocial(AppConstants.StaticParam.TYPE_OF_GOOGLE,
                        email,
                        AppConstants.SERVER_PATH.LOGIN.toString(),
                        s);
            } else {
                Toast.makeText(LoginActivity.this, "Token null", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * If has account on device. Insert information to form login.
     */
    private void setupDefaultAccount() {
        AccountManager manager = AccountManager.get(this);
        Account accountSave = null;
        accountSave = AccountUtils.getAccountUser(LoginActivity.this, manager);
        if (accountSave != null) {
            String name = accountSave.name;
            String password = manager.getPassword(accountSave);
            /**
             * TODO : If user buy package purchase error. Put this id package here.
             * name
             * password
             * id_purchase error.
             */
            mEdtEmail.setText(name);
            mEdtPassword.setText(password);
        }
    }

    private void initControls() {
        cl_main.requestFocus();
        mEdtEmail.clearFocus();
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

    @OnClick({R.id.btn_create_account, R.id.btn_login, R.id.tv_forgot_password, R.id.tv_privacy,
            R.id.btn_facebook, R.id.btn_google, R.id.tv_skip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_account:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
                break;
            case R.id.btn_login:
                String email = mEdtEmail.getText().toString();
                String password = mEdtPassword.getText().toString();
                if (checkValidate(email, password)) {
                    sendRequestLogin(email, password, "");
                }
                break;
            case R.id.tv_forgot_password:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;
            case R.id.tv_privacy:
                startActivity(new Intent(LoginActivity.this, PolicyActivity.class).putExtra
                        (AppConstants.KEY_SEND.KEY_DATA, false));
                break;
            case R.id.btn_google:
                signIn();
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
            case R.id.tv_skip:
                // Create skip user.
                if (User.getInstance().isSkipUser())
                    finish();
                else
                    sendRequestLoginSkipUser(AppConstants.TEST.USER_NAME, AppConstants.TEST
                            .PASSWORD);
                break;
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
                            goMaintainScreen(LoginActivity.this, msg);
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

    private void sendRequestLoginSkipUser(final String email, final String password) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants
                .CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID
                (LoginActivity.this));
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
                                        HSSPreference.getInstance(LoginActivity.this).putString
                                                (AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString
                                                        (), auth);
                                    }
                                    User user = User.parse(objectData.getJSONObject("info"));
                                    User.getInstance().setCurrentUser(user);
                                    AccountUtils.saveAccountInformation(LoginActivity.this,
                                            mManagerAccount, email, password);
                                    dismissLoading();
                                    startActivity(new Intent(LoginActivity.this, TopActivity
                                            .class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                goMaintainScreen(LoginActivity.this, msg);
                            } else if (status == AppConstants.STATUS_REQUEST.LIMIT_DEVICE) {
                                dismissLoading();
                                HSSDialog.show(LoginActivity.this, getString(R.string
                                        .msg_error_limit_device));
                            } else {
                                dismissLoading();
                                HSSDialog.show(LoginActivity.this, getString(R.string
                                        .msg_login_error_password_id_error));
                            }
                        }
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(LoginActivity.this, error);
            }
        });
        request.setHeaders(header);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void signIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(AppConstants.StaticParam.mGoogleApiClient);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void sendRequestRegisterSocial(final int type, String _email, String url, final String token) {//1-- fb 2-- google
        LoadingDialog.getDialog(this).show();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants
                .CLIENT_ID));//TEST
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID
                (LoginActivity.this));//TEST
        params.put(AppConstants.KEY_PARAMS.LOGIN_TYPE.toString(), String.valueOf(type));//TEST
        params.put(AppConstants.KEY_PARAMS.ACCESS_TOKEN.toString(), token);//TEST
        params.put(AppConstants.KEY_PARAMS.EMAIL.toString(), _email);//TEST
        if (AppConstants.StaticParam.TYPE_OF_FACEBOOK == type) {
            params.put(AppConstants.KEY_PARAMS.FB_ID.toString(), fbId);//TEST
        }
        RequestDataUtils.requestData(Request.Method.POST, LoginActivity.this, url,
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        boolean isSkipUser = User.getInstance().isSkipUser();
                        try {
                            String auth = object.optString(AppConstants.KEY_PARAMS.AUTH_TOKEN
                                    .toString(), "");
                            if (auth.length() > 0) {
                                HSSPreference.getInstance(LoginActivity.this).putString
                                        (AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), auth);
                            }
                            if (AppConstants.StaticParam.TYPE_OF_FACEBOOK == type) {
                                HSSPreference.getInstance(LoginActivity.this).putInt(AppConstants
                                        .KEY_PREFERENCE.IS_SOCIAL.toString(), AppConstants
                                        .StaticParam.TYPE_OF_FACEBOOK);
                            } else if (AppConstants.StaticParam.TYPE_OF_GOOGLE == type) {
                                HSSPreference.getInstance(LoginActivity.this).putInt(AppConstants
                                        .KEY_PREFERENCE.IS_SOCIAL.toString(), AppConstants
                                        .StaticParam.TYPE_OF_GOOGLE);
                            }
                            HSSPreference.getInstance(LoginActivity.this).putString(AppConstants
                                    .KEY_PREFERENCE.ACCESS_TOKEN.toString(), token);
                            User user = User.parse(object.getJSONObject("info"));
                            user.setIsSocialType(type);
                            User.getInstance().setCurrentUser(user);
                            email = object.getJSONObject("info").optString("email");
                            AccountUtils.saveAccountInformation(LoginActivity.this,
                                    mManagerAccount, email, token);
                            if (!isSkipUser)
                                startActivity(new Intent(LoginActivity.this, TopActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent
                                                .FLAG_ACTIVITY_NEW_TASK));
                            LoadingDialog.getDialog(LoginActivity.this).dismiss();
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(int error) {
                        if (error == AppConstants.STATUS_REQUEST.REQUEST_REQUIRE_EMAIL) {
                            HSSDialog.show(LoginActivity.this, getString(R.string.msg_need_input_email_to_complete_register), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HSSDialog.dismissDialog();
                                    DialogEmail cdd = new DialogEmail(LoginActivity.this);
                                    cdd.setCallBack(LoginActivity.this);
                                    cdd.show();
                                }
                            }).show();
                        } else if (error == AppConstants.STATUS_REQUEST.EMAIL_HAS_REGISTER) {
                            HSSDialog.show(LoginActivity.this, "このメールアドレスは既に他のFacebookアカウントに紐付いています。", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HSSDialog.dismissDialog();
                                }
                            }).show();
                        } else {
                            HSSDialog.show(LoginActivity.this, getString(R.string.msg_request_error_try_again), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HSSDialog.dismissDialog();
                                }
                            }).show();
                        }
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            email = acct.getEmail();
            new RetrieveTokenTask().execute(acct.getAccount().name);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        clearError();
    }

    private void sendRequestLogin(final String email, final String password, final String id_purchase) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants
                .CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID
                (LoginActivity.this));
        params.put(AppConstants.KEY_PARAMS.EMAIL.toString(), email);
        params.put(AppConstants.KEY_PARAMS.PASSWORD.toString(), password);
        if (id_purchase.length() > 0)
            params.put(AppConstants.KEY_PARAMS.ID_PURCHASE.toString(), password);
        JSONObject parameters = new JSONObject(params);
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants
                .SERVER_PATH.LOGIN.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            boolean isSkipUser = User.getInstance().isSkipUser();
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString
                                    (), 1);
                            String msg = response.optString(AppConstants.KEY_PARAMS.MESSAGE
                                    .toString(), "");
                            String mCurrentCodePurchase = "";
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants
                                            .KEY_PARAMS.DATA.toString());
                                    String auth = objectData.optString(AppConstants.KEY_PARAMS
                                            .AUTH_TOKEN.toString(), "");
                                    if (auth.length() > 0) {
                                        HSSPreference.getInstance(LoginActivity.this).putString
                                                (AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString
                                                        (), auth);
                                    }
                                    User user = User.parse(objectData.getJSONObject("info"));
                                    User.getInstance().setCurrentUser(user);
                                    JSONObject objectPremiumInfo = objectData.getJSONObject
                                            (AppConstants.KEY_PARAMS.PREMIUM_INFO.toString());
                                    if (objectPremiumInfo.length() > 0) {
                                        mCurrentCodePurchase = objectPremiumInfo.optString
                                                (AppConstants.KEY_PARAMS.PRICE_CODE.toString(), "");
                                    }
                                    AccountUtils.saveAccountInformation(LoginActivity.this,
                                            mManagerAccount, email, password);
                                    dismissLoading();
                                    AppUtils.setIsFirstLogin(LoginActivity.this);
                                    /**
                                     * Neu a skip user chi chi can finish activity de quay tro
                                     * lai man hinh truoc do.
                                     */
                                    if (!isSkipUser)
                                        startActivity(mCurrentCodePurchase.length() > 0 ? new
                                                Intent(LoginActivity.this, TopActivity.class)
                                                .putExtra(AppConstants.KEY_SEND
                                                                .KEY_SEND_ID_PURCHASE,
                                                        mCurrentCodePurchase) : new Intent
                                                (LoginActivity.this, TopActivity.class));
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                goMaintainScreen(LoginActivity.this, msg);
                            } else if (status == AppConstants.STATUS_REQUEST.LIMIT_DEVICE) {
                                dismissLoading();
                                HSSDialog.show(LoginActivity.this, getString(R.string
                                        .msg_error_limit_device));
                            } else {
                                dismissLoading();
                                HSSDialog.show(LoginActivity.this, getString(R.string
                                        .msg_login_error_password_id_error));
                            }
                        }
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(LoginActivity.this, error);
            }
        });
        request.setHeaders(header);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private boolean checkValidate(String email, String password) {
        boolean valuesEmail = checkErrorEmail(email);
        boolean valuePassword = checkErrorPassword(password);
        return valuesEmail && valuePassword;
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

    protected void clearError() {
        mRlPassword.setBackgroundResource(R.drawable.bg_edittext_transparent_border);
        mRlEmail.setBackgroundResource(R.drawable.bg_edittext_transparent_border);
        mTvErrorEmail.setVisibility(View.GONE);
        mTvErrorPassword.setVisibility(View.GONE);
    }

    public void showLoading() {
        if (!LoginActivity.this.isFinishing() && null != mPrg && !mPrg.isShowing())
            mPrg.show();
    }

    public void dismissLoading() {
        if (!LoginActivity.this.isFinishing() && null != mPrg && mPrg.isShowing())
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
    public void onStart() {
        super.onStart();
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn
// (mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be
// "done"
//            // and the GoogleSignInResult will be available instantly.
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }


    @Override
    public void isCallBack(String strValue) {
        email = strValue;
        sendRequestRegisterSocial(AppConstants.StaticParam.TYPE_OF_FACEBOOK,
                email,
                AppConstants.SERVER_PATH.LOGIN.toString(),
                AccessToken.getCurrentAccessToken().getToken());
    }
}
