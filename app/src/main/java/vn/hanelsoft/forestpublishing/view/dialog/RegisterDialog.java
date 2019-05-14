package vn.hanelsoft.forestpublishing.view.dialog;

import android.content.Context;
import android.view.View;

import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by dinhdv on 10/20/2017.
 */

public class RegisterDialog extends DialogBase {
    TextViewApp mTvCancel, mTvOk;
    private onDialogAction mAction;

    public RegisterDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_register;
    }

    @Override
    protected void inflateView() {
        super.inflateView();
        mTvCancel = (TextViewApp) findViewById(R.id.tv_cancel);
        mTvOk = (TextViewApp) findViewById(R.id.tv_register);
    }

    @Override
    protected void registerEvent() {
        super.registerEvent();
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mAction != null)
                    mAction.onRegister();
            }
        });
    }

    public void setAction(onDialogAction action) {
        mAction = action;
    }

    public interface onDialogAction {
        void onRegister();
    }
}
