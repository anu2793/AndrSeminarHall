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
 * Created by Hss on 9/11/2015.
 */
public class DialogNotification extends Dialog {
    private TextView mTvMsg;
    private Button mBtnOk;
    private String mMsgNotification = "" , mTitle = "";
    private onDoneClick mClick;

    public DialogNotification(Context context, String noti) {
        super(context);
        this.mMsgNotification = noti;
    }

    public DialogNotification(Context context, String title, String noti) {
        super(context);
        this.mMsgNotification = noti;
        mTitle = title;
    }

    public DialogNotification(Context context, int theme) {
        super(context, theme);
    }

    protected DialogNotification(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_notification);
        mTvMsg = (TextView) findViewById(R.id.tv_notification);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        if (mTvMsg != null) {
            mTvMsg.setText(mMsgNotification);
        }
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClick != null) {
                    mClick.onClick();
                }
                dismiss();
            }
        });
    }

    public void setOnListenner(onDoneClick click) {
        mClick = click;
    }

    public interface onDoneClick {
        void onClick();
    }
}
