package vn.hanelsoft.forestpublishing.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import vn.hanelsoft.forestpublishing.R;

/**
 * Created by dinhdv on 8/9/2017.
 */

public class DialogOpenBrowser extends DialogBase {
    private Button mBtnClose, mBtnCopy, mBtnOpen;
    private onActionDiloag mAction;

    public DialogOpenBrowser(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_open_browser;
    }

    @Override
    protected void inflateView() {
        super.inflateView();
        mBtnClose = (Button) findViewById(R.id.btn_close);
        mBtnCopy = (Button) findViewById(R.id.btn_copy);
        mBtnOpen = (Button) findViewById(R.id.btn_open_browser);
    }

    @Override
    protected void registerEvent() {
        super.registerEvent();
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mBtnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAction != null)
                    mAction.onCopy();
                dismiss();
            }
        });
        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAction != null)
                    mAction.onOpen();
                dismiss();
            }
        });
    }

    public void setActionDialog(onActionDiloag action) {
        mAction = action;
    }

    public interface onActionDiloag {
        void onOpen();

        void onCopy();
    }
}
