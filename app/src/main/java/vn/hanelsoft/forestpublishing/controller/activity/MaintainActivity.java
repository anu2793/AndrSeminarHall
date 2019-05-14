package vn.hanelsoft.forestpublishing.controller.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by dinhdv on 8/14/2017.
 */

public class MaintainActivity extends FragmentActivity {
    private TextViewApp mTvMsgMaintain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain);
        mTvMsgMaintain = (TextViewApp) findViewById(R.id.tv_message);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String msg = bundle.getString(AppConstants.KEY_SEND.KEY_MSG_MAINTAIN, "");
            mTvMsgMaintain.setText(msg);
        }

    }
}
