package vn.hanelsoft.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import vn.hanelsoft.mylibrary.R;

public class HSSDialog extends Dialog {

    private static HSSDialog dialog;
    private static Context mContext;
    private Button btnAccept, btnCancel;
    private View viewDevide;
    private TextView lblMsg;

    public HSSDialog(Context context) {
        super(context);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_hss);
        lblMsg = (TextView) findViewById(R.id.lblMessage);
        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        viewDevide = findViewById(R.id.viewDivide);
    }

    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    public static HSSDialog show(Context context, String message) {
        return show(context, message, null, null);
    }

    public static HSSDialog show(Context context, String message, String txtAccept) {
        return show(context, message, txtAccept, null);
    }

    public static HSSDialog show(Context context, String message, View.OnClickListener accept) {
        return show(context, message, null, accept);
    }

    public static HSSDialog show(Context context, String message, String txtAccept, View.OnClickListener accept) {
        return show(context, message, txtAccept, accept, null, null);
    }

    public static HSSDialog show(Context context, String message,
                                 String txtAccept, View.OnClickListener accept,
                                 String txtCancel, View.OnClickListener cancel) {
        if (dialog != null && mContext == context && dialog.isShowing())
            return dialog;
        dialog = new HSSDialog(context);
        dialog.lblMsg.setText(message);
        if (TextUtils.isEmpty(txtAccept))
            txtAccept = context.getString(R.string.btn_ok);
        dialog.btnAccept.setText(txtAccept);
        if (accept == null) {
            accept = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            };
        }
        dialog.btnAccept.setOnClickListener(accept);
        if (cancel == null) {
            dialog.btnCancel.setVisibility(View.GONE);
            dialog.viewDevide.setVisibility(View.GONE);
            dialog.btnAccept.setBackgroundResource(R.drawable.btn_hss_dialog);
        } else {
            dialog.btnCancel.setText(txtCancel);
            dialog.btnCancel.setOnClickListener(cancel);
        }
        dialog.show();
        return dialog;
    }

    public static void clear() {
        dialog = null;
        mContext = null;
    }
}
