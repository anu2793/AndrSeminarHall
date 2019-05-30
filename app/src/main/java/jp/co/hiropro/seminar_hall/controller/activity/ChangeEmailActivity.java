package jp.co.hiropro.seminar_hall.controller.activity;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import jp.co.hiropro.seminar_hall.view.EditTextApp;
import jp.co.hiropro.seminar_hall.view.dialog.DialogRetryConnection;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 7/24/2017.
 */

public class ChangeEmailActivity extends BaseActivity {
    @BindView(R.id.edt_email)
    EditTextApp mEdtEmail;
    @BindView(R.id.edt_old_email)
    EditTextApp mEdtOldEmail;
    @BindView(R.id.tv_error_email)
    TextView mTvError;
    String mEmail = "";
    private ProgressDialog mPrg;
    private AccountManager mManagerAccount;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_email;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        btnBack.setVisibility(View.VISIBLE);
        setupTitleScreen(getString(R.string.title_change_email));
        mManagerAccount = AccountManager.get(this);
        // Progress Bar
        mPrg = new ProgressDialog(ChangeEmailActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            mEmail = bundle.getString(AppConstants.KEY_SEND.KEY_SEND_EMAIL, "");
        mEdtOldEmail.setText(mEmail);
        mEdtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    mTvError.setVisibility(View.GONE);

            }
        });
    }

    @OnClick({R.id.btn_change_email, R.id.btn_cancel})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_email:
                String email = mEdtEmail.getText().toString().trim();
                if (checkValidate(email)) {
                    sendRequestChangEmail(email);
                }
                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
        }
    }

    private void sendRequestChangEmail(final String email) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.EMAIL.toString(), email);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.CHANGE_EMAIL.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            String msg = response.optString(AppConstants.KEY_PARAMS.MESSAGE.toString(), "");
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                /**
                                 * TODO : Update user email.
                                 */
                                AccountUtils.changeEmailAddress(ChangeEmailActivity.this, mManagerAccount, email);
                                setResult(ProfileActivity.REQUEST_CHANGE_EMAIL, new Intent().putExtra(AppConstants.KEY_SEND.KEY_SEND_EMAIL, email));
                                Toast.makeText(ChangeEmailActivity.this, getString(R.string.msg_change_email_success), Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (status == AppConstants.STATUS_REQUEST.EMAIL_HAS_REGISTED) {
                                dismissLoading();
                                HSSDialog.show(ChangeEmailActivity.this, getString(R.string.msg_email_has_registed)).show();
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                dismissLoading();
                                goMaintainScreen(activity, msg);
                            } else {
                                dismissLoading();
                                HSSDialog.show(ChangeEmailActivity.this, getString(R.string.msg_request_error_try_again));
                            }
                        }
                        System.out.println(response);
                        dismissLoading();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(ChangeEmailActivity.this, error);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    protected void requestAgain(final String email) {
        DialogRetryConnection dialogRetryConnection = new DialogRetryConnection(ChangeEmailActivity.this, getString(R.string.msg_request_error_try_again));
        dialogRetryConnection.setListener(new DialogRetryConnection.onDialogChoice() {
            @Override
            public void onDone() {
                sendRequestChangEmail(email);
            }

            @Override
            public void onCancel() {

            }
        });
        dialogRetryConnection.show();
    }

    private boolean checkValidate(String email) {
        if (email.length() == 0) {
            showError(true, getString(R.string.msg_email_empty));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(true, getString(R.string.msg_email_not_validate));
            return false;
        }
        return true;
    }

    private void showError(boolean isShow, String error) {
        mTvError.setVisibility(View.VISIBLE);
        mTvError.setText(error);
//        Toast.makeText(ChangeEmailActivity.this, error, Toast.LENGTH_SHORT).show();
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
