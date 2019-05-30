package jp.co.hiropro.seminar_hall.controller.activity;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.AccountUtils;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.view.dialog.DialogRetryConnection;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 7/24/2017.
 */

public class ChangePasswordActivity extends BaseActivity {
    @BindView(R.id.edt_old_password)
    EditText mEdtOldPassword;
    @BindView(R.id.edt_password)
    EditText mEdtNewPassword;
    @BindView(R.id.edt_re_password)
    EditText mEdtReNewPassword;
    @BindView(R.id.tv_error_new_password)
    TextView mTvErrorNewpassword;
    @BindView(R.id.tv_error_old_password)
    TextView mTvOldPassword;
    private ProgressDialog mPrg;
    private AccountManager mManagerAccount;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        btnBack.setVisibility(View.VISIBLE);
        mManagerAccount = AccountManager.get(this);
        setupTitleScreen("パスワード変更");
        // Progress Bar
        mPrg = new ProgressDialog(ChangePasswordActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
        mEdtNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    clearError();
            }
        });
        mEdtReNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    clearError();
            }
        });

        mEdtOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    clearError();
            }
        });

    }

    @OnClick({R.id.btn_submit, R.id.btn_cancel})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                changePassword();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    private void changePassword() {
        String oldPassword = mEdtOldPassword.getText().toString().trim();
        String newPassword = mEdtNewPassword.getText().toString().trim();
        String reNewPassword = mEdtReNewPassword.getText().toString().trim();
        if (checkValidate(oldPassword, newPassword, reNewPassword)) {
            sendRequestChangePassword(oldPassword, newPassword);
        }
    }

    private void sendRequestChangePassword(final String oldPassword, final String newPassword) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.OLD_PASSWORD.toString(), oldPassword);
        params.put(AppConstants.KEY_PARAMS.NEW_PASSWORD.toString(), newPassword);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.CHANGE_EMAIL.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                /**
                                 * TODO : Update user email.
                                 */
                                AccountUtils.changePasswordDefault(ChangePasswordActivity.this, mManagerAccount, newPassword);
                                setResult(ProfileActivity.REQUEST_CHANGE_PASSWORD, new Intent().putExtra(AppConstants.KEY_SEND.KEY_SEND_PASSWORD, newPassword));
                                HSSDialog.show(ChangePasswordActivity.this, getString(R.string.msg_change_password_success), getString(R.string.txt_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onBackPressed();
                                    }
                                });
//                                Toast.makeText(ChangePasswordActivity.this, getString(R.string.msg_change_password_success), Toast.LENGTH_SHORT).show();
//                                finish();
                            } else {
                                dismissLoading();
                                HSSDialog.show(ChangePasswordActivity.this, getString(R.string.msg_request_error_try_again));
                            }
                        }
                        System.out.println(response);
                        dismissLoading();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(ChangePasswordActivity.this, error);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    protected void requestAgain(final String oldPassword, final String newPassword) {
        DialogRetryConnection dialogRetryConnection = new DialogRetryConnection(ChangePasswordActivity.this, getString(R.string.msg_request_error_try_again));
        dialogRetryConnection.setListener(new DialogRetryConnection.onDialogChoice() {
            @Override
            public void onDone() {
                sendRequestChangePassword(oldPassword, newPassword);
            }

            @Override
            public void onCancel() {

            }
        });
        dialogRetryConnection.show();
    }

    private boolean checkValidate(String oldPassword, String password, String rePassword) {
        if (oldPassword.length() == 0) {
            mTvOldPassword.setVisibility(View.VISIBLE);
            mTvOldPassword.setText(getString(R.string.msg_password_empty));
//            showError(true, getString(R.string.msg_password_empty));
            return false;
        }
        if (password.length() == 0) {
            showError(true, getString(R.string.msg_password_empty));
            return false;
        }
        if (password.length() > 0 && password.length() < 6) {
            showError(true, getString(R.string.msg_password_not_enough_length));
            return false;
        }
        if (password.length() > 0 && password.length() > 12) {
            showError(true, getString(R.string.msg_password_too_long));
            return false;
        }
        if (!password.equalsIgnoreCase(rePassword)) {
            showError(true, getString(R.string.msg_password_not_equal_re_password));
            return false;
        }
        return true;
    }

    private void showError(boolean isShow, String error) {
        mTvErrorNewpassword.setVisibility(View.VISIBLE);
        mTvErrorNewpassword.setText(error);
//        Toast.makeText(ChangePasswordActivity.this, error, Toast.LENGTH_SHORT).show();
    }

    private void clearError() {
        mTvErrorNewpassword.setVisibility(View.GONE);
        mTvOldPassword.setVisibility(View.GONE);
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
