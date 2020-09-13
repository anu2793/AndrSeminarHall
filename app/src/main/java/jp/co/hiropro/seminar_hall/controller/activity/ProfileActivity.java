package jp.co.hiropro.seminar_hall.controller.activity;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.LevelItem;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AccountUtils;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.view.NonScrollListView;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.LevelAdapter;

/**
 * Created by dinhdv on 7/24/2017.
 */

public class ProfileActivity extends BaseActivity {
    public static int GO_BUY_PREMIUM = 468;
    public static int GO_PAID_MEMBER = 357;
    @BindView(R.id.list_level)
    NonScrollListView mListLevel;
    @BindView(R.id.tv_go_purchase)
    ImageView mImvGoPurchase;
    @BindView(R.id.tv_time_left)
    TextViewApp mTvTimeLeft;
    @BindView(R.id.tv_name_purchase)
    TextViewApp mTvStatus;
    @BindView(R.id.src_main)
    NestedScrollView mSrcMain;
    @BindView(R.id.tv_email)
    TextViewApp mTvEmail;
    @BindView(R.id.tv_go_paid_guide)
    TextViewApp mTvPaidGuide;
    @BindView(R.id.tv_user_id)
    TextViewApp mTvUserId;
    @BindView(R.id.ll_purchase_view)
    LinearLayout mLlPurchase;
    @BindView(R.id.edt_password)
    EditText mEdtPassword;
    @BindView(R.id.ll_content_login)
    LinearLayout mLlContentLogin;
    @BindView(R.id.rl_free_user)
    RelativeLayout mRlFreeUser;
    @BindView(R.id.pr_rl_password)
    RelativeLayout pr_rl_password;
    private ProgressDialog mPrg;
    private AccountManager mManagerAccount;
    private ArrayList<LevelItem> mList = new ArrayList<>();
    private LevelAdapter mAdatper;
    public static int REQUEST_CHANGE_EMAIL = 121;
    public static int REQUEST_CHANGE_PASSWORD = 123;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTitleScreen(getString(R.string.txt_profile_screen));
        btnBack.setVisibility(View.GONE);
        ButterKnife.bind(this);
        // Progress Bar
        mPrg = new ProgressDialog(ProfileActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
        mAdatper = new LevelAdapter(ProfileActivity.this, mList);
        mListLevel.setAdapter(mAdatper);
        mManagerAccount = AccountManager.get(this);
        String password = AccountUtils.getPassword(ProfileActivity.this, mManagerAccount);
        mEdtPassword.setText(password);
        if (User.getInstance().getCurrentUser().getIsSocialType() > 0) {//neu tai khoan la tk login qua mang xa hoi
            pr_rl_password.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Dùng để check người dùng đã cancel gói mua đấy hay chưa.
         * Chúy ý : Nếu chưa mua gói hàng đó lần nào . hàm getSubscriptionTransactionDetails trả về giá trị null.
         */
        if (!user.isSkipUser())
            loadProfile();
        showViewNotRegister(user.isSkipUser());
    }

    private void loadProfile() {
        showLoading();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, AppConstants.SERVER_PATH.PROFILE.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            String msg = response.optString(AppConstants.KEY_PARAMS.MESSAGE.toString(), "");
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                dismissLoading();
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                    if (objectData.length() > 0) {
                                        JSONObject objectUserData = objectData.getJSONObject(AppConstants.KEY_PARAMS.USER_INFO.toString());
                                        loadUserData(objectUserData);
                                        JSONArray listLevel = objectData.getJSONArray(AppConstants.KEY_PARAMS.PROGRESS_WATCHES.toString());
                                        if (listLevel.length() > 0)
                                            loadLevelToView(listLevel);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                goMaintainScreen(activity, msg);
                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                                sessionExpire();
                            } else {
                                dismissLoading();
                                requestAgain();
                            }
                        }
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                requestAgain();
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void loadLevelToView(JSONArray listLevel) {
        mList.clear();
        for (int i = 0; i < listLevel.length(); i++) {
            try {
                JSONObject objectLevel = listLevel.getJSONObject(i);
                LevelItem item = LevelItem.parser(objectLevel);
                mList.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdatper.notifyDataSetChanged();
        mSrcMain.smoothScrollTo(0, 0);
    }

    private void loadUserData(JSONObject objectUserData) {
        String email = objectUserData.optString(AppConstants.KEY_PARAMS.EMAIL.toString());
        mTvEmail.setText(email);
        int premiumRemain = objectUserData.optInt(AppConstants.KEY_PARAMS.PREMIUM_REMAIN.toString(), -1);
        setStatusUser(premiumRemain >= 0);
        String dateTo = objectUserData.optString(AppConstants.KEY_PARAMS.PREMIUM_TO_DATE.toString(), "");
        if (dateTo.length() > 0) {
            String date = AppUtils.formateDateFromstring("yyyy-MM-dd", "yyyy/MM/dd", dateTo);
            mTvTimeLeft.setText(getString(R.string.txt_time_left, date));
        }
        mTvUserId.setText(objectUserData.optString(AppConstants.KEY_PARAMS.ID.toString()));
    }

    protected void setStatusUser(boolean isPremiumUser) {
        mTvStatus.setEnabled(isPremiumUser);
        mTvStatus.setText(isPremiumUser ? getString(R.string.title_paid_user) : getString(R.string.title_free_user));
//        mImvGoPurchase.setVisibility(isPremiumUser ? View.GONE : View.VISIBLE);
        mTvTimeLeft.setVisibility(isPremiumUser ? View.VISIBLE : View.GONE);
//        mTvPaidGuide.setVisibility(isPremiumUser ? View.GONE : View.VISIBLE);
        mLlPurchase.setVisibility(isPremiumUser ? View.GONE : View.GONE);
    }

    protected void requestAgain() {
        HSSDialog.show(ProfileActivity.this, getString(R.string.msg_request_error_try_again), getString(R.string.txt_ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSSDialog.dismissDialog();
                loadProfile();
            }
        }, getString(R.string.txt_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSSDialog.dismissDialog();
                finish();
            }
        }).show();
    }

    @OnClick({R.id.rl_email, R.id.rl_password, R.id.tv_go_paid_guide, R.id.btn_logout, R.id.ll_purchase_view, R.id.btn_login})
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.rl_email:
                startActivityForResult(new Intent(ProfileActivity.this, ChangeEmailActivity.class).putExtra(AppConstants.KEY_SEND.KEY_SEND_EMAIL, mTvEmail.getText().toString()), REQUEST_CHANGE_EMAIL);
                break;
            case R.id.rl_password:
                startActivityForResult(new Intent(ProfileActivity.this, ChangePasswordActivity.class).putExtra(AppConstants.KEY_SEND.KEY_SEND_EMAIL, mTvEmail.getText().toString()), REQUEST_CHANGE_PASSWORD);
                break;
            case R.id.tv_go_paid_guide:
                startActivityForResult(new Intent(ProfileActivity.this, PaidMemberGuiderActivity.class), ProfileActivity.GO_PAID_MEMBER);
                break;
            case R.id.btn_logout:
                HSSDialog.show(ProfileActivity.this, getString(R.string.msg_confirm_logout), getString(R.string.txt_ok), new View.OnClickListener() {
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
            case R.id.ll_purchase_view:
                startActivity(new Intent(ProfileActivity.this, BuyMonthPremiumActivity.class));
                break;
            case R.id.btn_login:
                goRegisterScreenUserFree();
                break;
        }
    }

//    private void sendRequestLogOut() {
//        showLoading();
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, AppConstants.SERVER_PATH.LOGOUT.toString(), null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        if (response.length() > 0) {
//                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
//                            if (status == AppConstants.REQUEST_SUCCESS) {
//                                AccountUtils.clearAllAccountOfThisApplication(ProfileActivity.this, mManagerAccount);
//                                dismissLoading();
//                                //Clear shortcut badger.
//                                ShortcutBadger.applyCount(getApplicationContext(), 0);
//                                startActivity(new Intent(ProfileActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                                finish();
//                            } else {
//                                dismissLoading();
//                                logoutAgain();
//                            }
//                        }
//                        System.out.println(response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dismissLoading();
//                logoutAgain();
//            }
//        });
//        request.setHeaders(getAuthHeader());
//        ForestApplication.getInstance().addToRequestQueue(request);
//    }

    private void logoutAgain() {
        HSSDialog.show(ProfileActivity.this, getString(R.string.msg_login_error_try_again), getString(R.string.txt_ok), new View.OnClickListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANGE_EMAIL) {
            if (data != null && data.hasExtra(AppConstants.KEY_SEND.KEY_SEND_EMAIL)) {
                String newEmail = data.getStringExtra(AppConstants.KEY_SEND.KEY_SEND_EMAIL);
                mTvEmail.setText(newEmail);
            }
        }

        if (requestCode == REQUEST_CHANGE_PASSWORD) {
            if (data != null && data.hasExtra(AppConstants.KEY_SEND.KEY_SEND_PASSWORD)) {
                String newPassword = data.getStringExtra(AppConstants.KEY_SEND.KEY_SEND_PASSWORD);
                mEdtPassword.setText(newPassword);
            }
        }

        if (requestCode == ProfileActivity.GO_BUY_PREMIUM || requestCode == ProfileActivity.GO_PAID_MEMBER) {
            setStatusUser(user.isPremiumUser());
        }
    }

    private void showViewNotRegister(boolean isShow) {
        mRlFreeUser.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mLlContentLogin.setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    public void showLoading() {
        if (!mPrg.isShowing() && mPrg != null)
            mPrg.show();
    }

    public void dismissLoading() {
        if (mPrg.isShowing())
            mPrg.dismiss();
    }
}
