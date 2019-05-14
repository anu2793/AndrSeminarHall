package vn.hanelsoft.forestpublishing.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by dinhdv on 2/22/2018.
 */

public class WaringPurchaseDialog extends Dialog {
    private TextViewApp mTvOk, mTvCancel;
    private onDialogAction mAction;

    public WaringPurchaseDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_warning_purchase);
        mTvOk = findViewById(R.id.tv_ok);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null)
                    mAction.onOk();
                dismiss();
            }
        });

        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null)
                    mAction.onCancel();
                dismiss();
            }
        });
    }

    public void setListener(onDialogAction action) {
        mAction = action;
    }

    public interface onDialogAction {
        void onOk();

        void onCancel();
    }
}
