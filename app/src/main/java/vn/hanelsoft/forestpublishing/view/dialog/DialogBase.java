package vn.hanelsoft.forestpublishing.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.Window;

/**
 * Created by Tuấn Sơn on 1/8/2017.
 */

public abstract class DialogBase extends Dialog {
    protected Context mContext;

    public DialogBase(Context context) {
        super(context);
        this.mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setCancelable(false);
        inflateView();
        registerEvent();
    }

    protected abstract @LayoutRes int getLayoutId();

    protected void inflateView() {
    }

    protected void registerEvent() {
    }

    protected <T> T getViewById(@IdRes int viewId) {
        return (T) findViewById(viewId);
    }
}
