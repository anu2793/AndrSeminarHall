package jp.co.hiropro.seminar_hall.view.dialog;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.OnSingleClickListener;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by Tuấn Sơn on 1/8/2017.
 */

public class DialogPurchaseVideo extends DialogBase {
    private TextViewApp tvPurchaseVideo, tvCancel, tvTitle, mTvPoint;
    private static DialogPurchaseVideo dialog;

    public DialogPurchaseVideo(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_purchase_video;
    }

    @Override
    protected void inflateView() {
        super.inflateView();
        tvPurchaseVideo = findViewById(R.id.tv_purchase_video);
        tvCancel = findViewById(R.id.tv_cancel);
        tvTitle = findViewById(R.id.tv_title);
        mTvPoint = findViewById(R.id.tv_point);
    }

    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    public static DialogPurchaseVideo showDialog(Context context, String money, String point, View.OnClickListener buy) {
        if (dialog != null && dialog.isShowing())
            return dialog;
        dialog = new DialogPurchaseVideo(context);
        dialog.tvTitle.setText(money);
        dialog.tvPurchaseVideo.setOnClickListener(buy);
        dialog.mTvPoint.setText(point);
        dialog.tvCancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismissDialog();
            }
        });
        dialog.show();
        return dialog;
    }

}
