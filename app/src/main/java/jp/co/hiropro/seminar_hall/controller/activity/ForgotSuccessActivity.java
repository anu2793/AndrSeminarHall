package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.view.ButtonApp;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by dinhdv on 7/20/2017.
 */

public class ForgotSuccessActivity extends FragmentActivity {
    @BindView(R.id.tv_alert)
    TextViewApp tv_alert;
    @BindView(R.id.tv_alert_failed)
    TextViewApp tv_alert_failed;
    @BindView(R.id.tv_alert_failed_note)
    TextViewApp tv_alert_failed_note;
    @BindView(R.id.imv_alert)
    ImageView imv_alert;
    @BindView(R.id.btn_send)
    ButtonApp btnSend;
    boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_result);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        result = bundle.getBoolean(AppConstants.KEY_INTENT.FORGOT_RESULT.toString(), true);
        if (result)
            isForgotSuccess();
        else
            isForgotFailed();
    }

    private void isForgotSuccess() {
        tv_alert.setVisibility(View.VISIBLE);
    }

    private void isForgotFailed() {
        imv_alert.setImageResource(R.mipmap.ic_failed);
        tv_alert_failed.setVisibility(View.VISIBLE);
        tv_alert_failed_note.setVisibility(View.VISIBLE);
        btnSend.setText("OK");
    }

    @OnClick(R.id.btn_send)
    public void nextScreen() {
        if (result) {
            Intent intent = new Intent(ForgotSuccessActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(ForgotSuccessActivity.this, ForgotPasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
