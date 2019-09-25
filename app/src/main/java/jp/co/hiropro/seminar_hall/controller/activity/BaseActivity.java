package jp.co.hiropro.seminar_hall.controller.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.fragment.TopSeminarFragment;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AccountUtils;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.HSSPreference;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.dialog.AdviseDialog;
import jp.co.hiropro.utils.NetworkUtils;
import me.leolin.shortcutbadger.ShortcutBadger;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    DrawerLayout drawerLayout;
    FrameLayout layoutContentContainer;
    RelativeLayout layoutContainer;
    ImageView btnMenu, btnShop;
    ImageView btnBack;
    protected TextView tvTitle;
    protected ImageView ivLogo;
    TextViewApp menuHome, menuMyLibrary, menuProfile, menuHelp, menuTerm, menuSpcifiedTransaction, mTvDeviceManager, mTvLogin, mTvLogoutSocial;
    TextViewApp mTvPurchaseList, mTvHistory, mTvFavoriteList, mTvVersion, mTvPrivacy, mTvSetting, menuPoint, tvSendnews,tvListNews;
    protected ActionBarDrawerToggle mDrawerToggle;
    Activity activity;
    public static Point screenSize = null;
    public static User user = null;
    private LinearLayout mLlExpand;
    private boolean isShowExpand = false;
    private ImageView mImvArrow;
    private ProgressDialog mPrg;
    private AccountManager mManagerAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_right_to_left_enter,
                R.anim.anim_right_to_left_leave);
        setContentView(R.layout.activity_base);

        if (screenSize == null) {
            screenSize = new Point();
            getWindowManager().getDefaultDisplay().getSize(screenSize);
        }
        user = User.getInstance().getCurrentUser();
        activity = this;
        isShowExpand = false;
        inflateView();
        // Progress Bar
        mPrg = new ProgressDialog(activity);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
        mManagerAccount = AccountManager.get(this);
        if (user.getRoleUser() == 2) {
            tvSendnews.setVisibility(View.VISIBLE);
            tvListNews.setVisibility(View.VISIBLE);
        }
    }

    protected int getLayoutId() {
        return 0;
    }


    protected void inflateView() {
        initView();
        if (getLayoutId() > 0)
            View.inflate(activity, getLayoutId(), layoutContentContainer);
        initEvent();
    }

    protected void initView() {
        drawerLayout = findViewById(R.id.drawer);
        layoutContentContainer = findViewById(R.id.container);
        layoutContainer = findViewById(R.id.layout_container);
        btnMenu = findViewById(R.id.btn_menu);
        btnShop = findViewById(R.id.btn_shop);
        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title_screen);
        ivLogo = findViewById(R.id.iv_logo);

        menuHome = findViewById(R.id.tv_home);
        menuMyLibrary = findViewById(R.id.tv_library);
        menuProfile = findViewById(R.id.tv_profile);
        menuHelp = findViewById(R.id.tv_help);
        menuTerm = findViewById(R.id.tv_term);
        menuSpcifiedTransaction = findViewById(R.id.tv_specified_transaction);

        mLlExpand = findViewById(R.id.ll_expand_sub);
        mTvPurchaseList = findViewById(R.id.tv_purchase_list);
        mTvHistory = findViewById(R.id.tv_purchase_history);
        mTvFavoriteList = findViewById(R.id.tv_favorite_list);
        mImvArrow = findViewById(R.id.imv_arrow_down);
        mTvVersion = findViewById(R.id.tv_version);
        mTvDeviceManager = findViewById(R.id.tv_device_manager);
        mTvLogin = findViewById(R.id.tv_login);
        mTvPrivacy = findViewById(R.id.tv_privacy);
        mTvSetting = findViewById(R.id.tv_setting);
        mTvLogoutSocial = findViewById(R.id.tv_logout_social);
        menuPoint = findViewById(R.id.tv_menu_point);
        tvSendnews = findViewById(R.id.tv_menu_send_news);
        tvListNews = findViewById(R.id.tv_menu_listnews);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            mTvVersion.setText("version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void expandSubViewPurchase() {
        if (isShowExpand) {
            AppUtils.collapse(mLlExpand);
        } else {
            AppUtils.expand(mLlExpand);
        }
//        mTvVersion.animate().translationY(isShowExpand ? 0 : 120);
        mImvArrow.setRotation(isShowExpand ? 0 : 180);
        isShowExpand = !isShowExpand;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mTvDeviceManager.setVisibility(user.isSkipUser() ? View.GONE : View.VISIBLE);
//        if (user != null) {
//            mTvLogin.setVisibility(user.isSkipUser() ? View.VISIBLE : View.GONE);
//            mTvLogoutSocial.setVisibility(user.isSkipUser() ? View.GONE : View.GONE);
//        }

        // show dialog.
        // Show Advis.
        user = User.getInstance().getCurrentUser();
        if (HSSPreference.getInstance().getBoolean(AppConstants.KEY_PREFERENCE.IS_REGISTER_SUCCESS.toString(), false))
            showAdviseDialog();
    }

    private void showAdviseDialog() {
        if (user.isSkipUser())
            return;
        HSSPreference.getInstance().putBool(AppConstants.KEY_PREFERENCE.IS_REGISTER_SUCCESS.toString(), false);
        AdviseDialog dialog = new AdviseDialog(BaseActivity.this);
        dialog.setAction(new AdviseDialog.onAdviseAction() {
            @Override
            public void onCreateProfile() {
                goEditCreateProfile();
            }

            @Override
            public void onSkip() {

            }
        });
        dialog.show();
    }

    private void goEditCreateProfile() {
        Intent intent = new Intent(BaseActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    protected void setupTitleScreen(String title) {
        ivLogo.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
        btnBack.setVisibility(View.VISIBLE);
    }

    protected HashMap<String, String> getAuthHeader() {
        HashMap<String, String> header = new HashMap<>();
        String auth = HSSPreference.getInstance().getString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), "");
        header.put("Authorization", auth);
        return header;
    }

    private void initEvent() {
        btnMenu.setOnClickListener(this);
        btnShop.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ivLogo.setOnClickListener(this);
        tvTitle.setOnClickListener(this);

        menuHome.setOnClickListener(menu);
        menuMyLibrary.setOnClickListener(menu);
        menuProfile.setOnClickListener(menu);
        menuHelp.setOnClickListener(menu);
        menuTerm.setOnClickListener(menu);
        mTvDeviceManager.setOnClickListener(menu);
        menuSpcifiedTransaction.setOnClickListener(menu);
        mTvPurchaseList.setOnClickListener(menu);
        mTvHistory.setOnClickListener(menu);
        mTvFavoriteList.setOnClickListener(menu);
        mTvLogin.setOnClickListener(menu);
        mTvPrivacy.setOnClickListener(menu);
        mTvSetting.setOnClickListener(menu);
        mTvLogoutSocial.setOnClickListener(menu);
        menuPoint.setOnClickListener(menu);
        tvSendnews.setOnClickListener(menu);
        tvListNews.setOnClickListener(menu);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                layoutContainer.setTranslationX(-slideOffset * drawerView.getWidth());
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_menu:
                if (drawerLayout.isDrawerOpen(Gravity.END))
                    drawerLayout.closeDrawer(Gravity.END);
                else drawerLayout.openDrawer(Gravity.END);
                break;
            case R.id.iv_logo:
                if (activity.getClass().getName().equals(TopActivity.class.getName())) {
                    onLogoAppClick();
                    return;
                } else
                    startActivity(new Intent(activity, TopActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            case R.id.btn_shop:
//                if (activity.getClass().getName().equals(ShopContentActivity.class.getName()))
//                    return;
                TopSeminarFragment.INDEX_SELECT = 0;
                startActivity(new Intent(activity, ShopContentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.tv_title_screen:
                closeRightMenu();
                /*
                  Nga yeu cau load lai khi click vao.
                 */
                startActivity(new Intent(activity, TopActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            case R.id.rl_title:
                closeRightMenu();
                /*
                  Nga yeu cau load lai khi click vao.
                 */
                startActivity(new Intent(activity, TopActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
        }
    }

    View.OnClickListener menu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_home:
                    closeRightMenu();
//                    if (activity.getClass().getName().equals(TopActivity.class.getName()))
//                        return;
                    /*
                      Nga yeu cau load lai khi click vao.
                     */
                    startActivity(new Intent(activity, TopActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    break;
                case R.id.tv_library:
                    expandSubViewPurchase();
//                    if (activity.getClass().getName().equals(MyLibraryActivity.class.getName()))
//                        return;
//                    startActivity(new Intent(activity, MyLibraryActivity.class));
                    break;
                case R.id.tv_profile:
                    closeRightMenu();
//                    if (activity.getClass().getName().equals(TeacherProfileCardActivity.class.getName()))
//                    return;
//                    startActivity(new Intent(activity, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    startActivity(new Intent(activity, TeacherProfileCardActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_term:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(PolicyActivity.class.getName()))
                        return;
                    startActivity(new Intent(activity, TermOfServiceActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_help:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(PaidMemberGuiderActivity.class.getName()))
                        return;
                    startActivity(new Intent(activity, PaidMemberGuiderActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(PaidMemberGuiderActivity.KEY_SHOW_BACK, false));
                    break;
                case R.id.tv_specified_transaction:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(SpecifiedCommercialTransactionActivity.class.getName()))
                        return;
                    startActivity(new Intent(activity, SpecifiedCommercialTransactionActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_purchase_list:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(MyLibraryActivity.class.getName())) {
                        sendBroadcast(new Intent(AppConstants.BROAD_CAST.CHANGE_TAB).putExtra(AppConstants.KEY_SEND.KEY_SEND_TAB, 0));
                        return;
                    }
                    startActivity(new Intent(activity, MyLibraryActivity.class)
                            .putExtra(KeyParser.KEY.DATA.toString(), 0)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_purchase_history:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(MyLibraryActivity.class.getName())) {
                        sendBroadcast(new Intent(AppConstants.BROAD_CAST.CHANGE_TAB).putExtra(AppConstants.KEY_SEND.KEY_SEND_TAB, 1));
                        return;
                    }
                    startActivity(new Intent(activity, MyLibraryActivity.class)
                            .putExtra(KeyParser.KEY.DATA.toString(), 1)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_favorite_list:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(MyLibraryActivity.class.getName())) {
                        sendBroadcast(new Intent(AppConstants.BROAD_CAST.CHANGE_TAB).putExtra(AppConstants.KEY_SEND.KEY_SEND_TAB, 2));
                        return;
                    }
                    startActivity(new Intent(activity, MyLibraryActivity.class)
                            .putExtra(KeyParser.KEY.DATA.toString(), 2)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_device_manager:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(ManagerDeviceActivity.class.getName())) {
                        return;
                    }
                    startActivity(new Intent(activity, ManagerDeviceActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_login:
                    closeRightMenu();
                    startActivity(new Intent(activity, LoginActivity.class));
                    break;
                case R.id.tv_privacy:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(TermOfServiceActivity.class.getName()))
                        return;
                    startActivity(new Intent(activity, TermOfServiceActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_setting:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(SettingActivity.class.getName()))
                        return;
                    startActivity(new Intent(activity, SettingActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_menu_point:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(PointManagerActivity.class.getName()))
                        return;
                    startActivity(new Intent(activity, PointManagerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_menu_send_news:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(SendNewsActivity.class.getName()))
                        return;
                    startActivity(new Intent(activity, SendNewsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_menu_listnews:
                    closeRightMenu();
                    if (activity.getClass().getName().equals(ListnewsTeachActivity.class.getName()))
                        return;
                    startActivity(new Intent(activity, ListnewsTeachActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.tv_logout_social:
                    HSSDialog.show(BaseActivity.this, getString(R.string.msg_confirm_logout), getString(R.string.txt_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HSSDialog.dismissDialog();
                            sendRequestLogOut();
                        }
                    }, getString(R.string.txt_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HSSDialog.dismissDialog();
                        }
                    }).show();
                    break;
            }
        }
    };

    private void signOut(final onActionLogoutSocial action) {
        AppConstants.StaticParam.mGoogleApiClient.connect();
        AppConstants.StaticParam.mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (AppConstants.StaticParam.mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(AppConstants.StaticParam.mGoogleApiClient);
                    if (null != AppConstants.StaticParam.mGoogleApiClient && AppConstants.StaticParam.mGoogleApiClient.isConnected()) {
                        AppConstants.StaticParam.mGoogleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                AppConstants.StaticParam.mGoogleApiClient.disconnect();
                                AppConstants.StaticParam.mGoogleApiClient = null;
                                if (action != null)
                                    action.onSuccess();
                            }
                        });

                    }

                } else {
                    AppConstants.StaticParam.mGoogleApiClient.disconnect();
                    AppConstants.StaticParam.mGoogleApiClient = null;
                    if (action != null)
                        action.onSuccess();
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                if (action != null)
                    action.onFail();
            }
        });
    }

//    private void revokeAccess() {
//        Auth.GoogleSignInApi.revokeAccess(AppConstants.StaticParam.mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//
//                    }
//                });
//    }

    public void sendRequestLogOut() {
        showLoading();
        RequestDataUtils.requestData(Request.Method.GET, BaseActivity.this, AppConstants.SERVER_PATH.LOGOUT.toString(), null, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject object, String msg) {
                if (User.getInstance().getCurrentUser().getIsSocialType() == 0) {
                    dismissLoading();
                    logout();
                } else {
                    logoutSocial(new onActionLogoutSocial() {
                        @Override
                        public void onSuccess() {
                            dismissLoading();
                            logout();
                        }

                        @Override
                        public void onFail() {
                            dismissLoading();
                            logout();
                        }
                    });
                }
            }

            @Override
            public void onFail(int error) {
                dismissLoading();
                logoutAgain();
            }
        });
    }

    private void logout() {
        User.getInstance().logout(activity);
        BaseActivity.user = null;
        AccountUtils.clearAllAccountOfThisApplication(BaseActivity.this, mManagerAccount);
        HSSPreference.getInstance(BaseActivity.this).putInt(AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(), 0);
        HSSPreference.getInstance(BaseActivity.this).putString(AppConstants.KEY_PREFERENCE.ACCESS_TOKEN.toString(), AppConstants.StaticParam.EMPTY_VALUE_STRING);
        //Clear shortcut badger.
        ShortcutBadger.applyCount(getApplicationContext(), 0);
        startActivity(new Intent(BaseActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    private void logoutSocial(final onActionLogoutSocial action) {
        if (User.getInstance() == null)
            return;
        if (User.getInstance().getCurrentUser().getIsSocialType() == AppConstants.StaticParam.TYPE_OF_FACEBOOK) {
            if (AccessToken.getCurrentAccessToken() == null) {
                // already logged out
                if (action != null)
                    action.onSuccess();
                return;
            }
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    LoginManager.getInstance().logOut();
                    AccessToken.setCurrentAccessToken(null);
                    if (action != null)
                        action.onSuccess();
                }

            }).executeAsync();

        } else if (User.getInstance().getCurrentUser().getIsSocialType() == AppConstants.StaticParam.TYPE_OF_GOOGLE) {
            signOut(action);
        }
    }

    public interface onActionLogoutSocial {
        void onSuccess();

        void onFail();
    }

    private void logoutAgain() {
        HSSDialog.show(BaseActivity.this, getString(R.string.msg_login_error_try_again), getString(R.string.txt_ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSSDialog.dismissDialog();
                sendRequestLogOut();
            }
        }, getString(R.string.txt_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSSDialog.dismissDialog();
            }
        }).show();
    }

    protected void closeRightMenu() {
        drawerLayout.closeDrawer(Gravity.END);
        toggleExpand();
    }

    private void toggleExpand() {
        if (mLlExpand.getVisibility() == View.VISIBLE) {
            mImvArrow.setRotation(0);
        }
        isShowExpand = false;
        AppUtils.collapse(mLlExpand);
    }

    public void attachFragment(Fragment fragment, @IdRes int containerViewID, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerViewID, fragment);
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    protected View getFooterCopyRight() {
        return LayoutInflater.from(activity).inflate(R.layout.footer_copy_right, null, false);
    }

    private boolean isBackPressed;

    @Override
    public void onBackPressed() {
        if (!isTaskRoot()) {
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_left_to_right_enter, R.anim.anim_left_to_right_leave);
            return;
        }
        if (isBackPressed) {
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_left_to_right_enter, R.anim.anim_left_to_right_leave);
        } else {
            Toast.makeText(this, R.string.back_again_to_exit,
                    Toast.LENGTH_SHORT).show();
            isBackPressed = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isBackPressed = false;
                }
            }, 2000);
        }
    }

    public void goMaintainScreen(Activity activity, String msg) {
        startActivity(new Intent(activity, MaintainActivity.class).putExtra(AppConstants.KEY_SEND.KEY_MSG_MAINTAIN, msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goRegisterScreenUserFree() {
        startActivity(new Intent(activity, RegisterActivity.class).putExtra(AppConstants.KEY_INTENT.IS_REGISTER_USER.toString(), true));
    }

    public void sessionExpire() {
        HSSDialog.show(activity, getString(R.string.msg_session_expire), "Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestLoginSkipUser(AppConstants.TEST.USER_NAME, AppConstants.TEST.PASSWORD);
            }
        });
    }

    private void sendRequestLoginSkipUser(final String email, final String password) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID(activity));
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
                                        HSSPreference.getInstance(activity).putString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), auth);
                                    }
                                    User user = User.parse(objectData.getJSONObject("info"));
                                    User.getInstance().setCurrentUser(user);
                                    AccountUtils.saveAccountInformation(activity, mManagerAccount, email, password);
                                    dismissLoading();
                                    startActivity(new Intent(activity, TopActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                goMaintainScreen(activity, msg);
                            } else {
                                dismissLoading();
                                HSSDialog.show(activity, getString(R.string.msg_login_error_password_id_error));
                            }
                        }
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(activity, error);
            }
        });
        request.setHeaders(header);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    public void onLogoAppClick() {
    }

    public void showLoading() {
        if (null != mPrg && !mPrg.isShowing())
            mPrg.show();
    }

    public void dismissLoading() {
        if (null != mPrg && mPrg.isShowing())
            mPrg.dismiss();
    }
}
