package jp.co.hiropro.seminar_hall.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.RelativeLayout;

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
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.view.EditTextApp;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by dinhdv on 7/20/2017.
 */

public class ForgotPasswordActivity extends FragmentActivity {
    @BindView(R.id.edt_email)
    EditTextApp mEdtEmail;
    @BindView(R.id.tv_error_email)
    TextViewApp mTvErrorEmail;
    private ProgressDialog mPrg;
    @BindView(R.id.rl_email)
    RelativeLayout rl_email;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        // Progress Bar
        mPrg = new ProgressDialog(ForgotPasswordActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
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
                    rl_email.setBackgroundResource(R.drawable
                            .bg_edittext_transparent_border_select);
                } else {
                    rl_email.setBackgroundResource(R.drawable
                            .bg_edittext_transparent_border);
                }
            }
        });
    }

    @OnClick({R.id.btn_send, R.id.imv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String email = mEdtEmail.getText().toString();
                if (checkValidate(email)) {
                    sendRequestForgot(email);
                }
                break;
            case R.id.imv_back:
                finish();
                break;
        }
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
        rl_email.setBackgroundResource(msg.length() > 0 ? R.drawable.bg_edittext_error : R.drawable.bg_edittext_transparent_border);
        mTvErrorEmail.setVisibility(msg.length() > 0 ? View.VISIBLE : View.GONE);
        if (msg.length() > 0) {
            mTvErrorEmail.setText(msg);
        }
    }

    protected void clearError() {
        rl_email.setBackgroundResource(R.drawable.bg_edittext_transparent_border_select);
        mTvErrorEmail.setVisibility(View.GONE);
    }

    private void sendRequestForgot(final String email) {
        showLoading();
        Map<String, String> params = new HashMap();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.EMAIL.toString(), email);
        JSONObject parameters = new JSONObject(params);
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.FORGOT_PASSWORD.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            Intent intent = new Intent(ForgotPasswordActivity.this, ForgotSuccessActivity.class);
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                intent.putExtra(AppConstants.KEY_INTENT.FORGOT_RESULT.toString(), true);
                            } else {
                                intent.putExtra(AppConstants.KEY_INTENT.FORGOT_RESULT.toString(), false);
                            }
                            startActivity(intent);
                            dismissLoading();
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                sendRequestLoginAgain(email);
            }
        });
        request.setHeaders(header);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    protected void sendRequestLoginAgain(final String email) {
        HSSDialog.show(ForgotPasswordActivity.this, getString(R.string.msg_login_error_try_again), getString(R.string.txt_ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSSDialog.dismissDialog();
                sendRequestForgot(email);
            }
        }, getString(R.string.txt_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSSDialog.dismissDialog();
            }
        }).show();
    }

    private boolean checkValidate(String email) {
        return checkErrorEmail(email);
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
