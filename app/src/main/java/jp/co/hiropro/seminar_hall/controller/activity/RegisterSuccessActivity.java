package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.HSSPreference;

/**
 * Created by dinhdv on 7/20/2017.
 */

public class RegisterSuccessActivity extends FragmentActivity {
    private boolean isSkipUser = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsiter_success);
        ButterKnife.bind(this);
        initControls();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            isSkipUser = bundle.getBoolean(AppConstants.KEY_INTENT.IS_SKIP_USER.toString(), false);
    }

    private void initControls() {

    }

    @OnClick({R.id.btn_go_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_go_top:
                HSSPreference.getInstance().putBool(AppConstants.KEY_PREFERENCE.IS_REGISTER_SUCCESS.toString(), true);
                if (!isSkipUser)
                    startActivity(new Intent(RegisterSuccessActivity.this, TopActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra(AppConstants.KEY_SEND.KEY_SHOW_ADVISE, true));
                finish();
                break;
        }
    }
}
