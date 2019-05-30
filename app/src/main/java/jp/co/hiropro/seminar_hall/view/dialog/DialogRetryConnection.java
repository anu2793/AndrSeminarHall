package jp.co.hiropro.seminar_hall.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import jp.co.hiropro.seminar_hall.R;


/**
 * Created by Hss on 8/28/2015.
 */
public class DialogRetryConnection extends Dialog {
    private TextView mTvContent;
    private String mContent;
    private onDialogChoice mChoice;
    private Button mBtnOk, mBtnCancel;

    public DialogRetryConnection(Context context, String msg) {
        super(context);
        this.mContent = msg;
    }

    public DialogRetryConnection(Context context, int theme) {
        super(context, theme);
    }

    protected DialogRetryConnection(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_loading);
        mTvContent = (TextView) findViewById(R.id.tv_loading);
        if (mContent.length() > 0)
            mTvContent.setText(mContent);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mChoice != null)
                    mChoice.onCancel();
            }
        });

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mChoice != null)
                    mChoice.onDone();
            }
        });
    }

    public void setListener(onDialogChoice choice) {
        mChoice = choice;
    }

    public interface onDialogChoice {
        void onDone();

        void onCancel();
    }
}
