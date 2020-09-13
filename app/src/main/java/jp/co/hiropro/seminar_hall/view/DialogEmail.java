package jp.co.hiropro.seminar_hall.view;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.Utils;

/**
 * Created by Kienmt on 1/31/2018.
 */

public class DialogEmail extends Dialog implements
        android.view.View.OnClickListener {
    private EditTextApp edt_email;
    private ButtonApp btnRegister;

    public DialogEmail(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_email);
        edt_email = findViewById(R.id.edt_email);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
    }

    private boolean validate() {
        if (AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(getEmailValue())) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.msg_email_not_validate_new), Toast.LENGTH_LONG).show();
            return false;
        } else if (!Utils.isEmailValid(getEmailValue())) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.msg_email_not_validate_new), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private String getEmailValue() {
        return edt_email.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                if (validate()) {
                    result.isCallBack(edt_email.getText().toString());
                    dismiss();
                }
                break;
            default:
                break;
        }
        dismiss();
    }

    public OnResult result;

    public void setCallBack(OnResult _result) {
        result = _result;
    }

    public interface OnResult {
        void isCallBack(String strValue);
    }
}
