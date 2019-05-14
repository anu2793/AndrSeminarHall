package vn.hanelsoft.forestpublishing.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.view.ButtonApp;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by dinhdv on 2/8/2018.
 */

public class AdviseDialog extends DialogBase {
    private ButtonApp mBtnCreateProfile;
    private TextViewApp mSkip;
    private onAdviseAction mAction;
    private ImageView mImvAdvise;

    public AdviseDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_advise;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        mBtnCreateProfile = findViewById(R.id.btn_create_profile);
        mImvAdvise = findViewById(R.id.imv_advise);
        mSkip = findViewById(R.id.tv_skip);
        mBtnCreateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null)
                    mAction.onCreateProfile();
                dismiss();
            }
        });

        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null)
                    mAction.onSkip();
                dismiss();
            }
        });
        Glide.with(getContext()).asGif().load(R.drawable.advise_cr).into(mImvAdvise);
    }

    public void setAction(onAdviseAction action) {
        mAction = action;
    }

    public interface onAdviseAction {
        void onCreateProfile();

        void onSkip();
    }
}
