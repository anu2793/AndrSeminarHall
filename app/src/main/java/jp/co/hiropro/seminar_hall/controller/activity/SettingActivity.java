
package jp.co.hiropro.seminar_hall.controller.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AccountUtils;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.HSSPreference;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.rl_unregister)
    RelativeLayout mRlUnregister;
    @BindView(R.id.rl_password)
    RelativeLayout rl_password;
    @BindView(R.id.title_value_id)
    TextViewApp title_value_id;
    @BindView(R.id.title_value_email)
    TextViewApp title_value_email;
    @BindView(R.id.title_value_pass)
    TextViewApp title_value_pass;
    @BindView(R.id.line_password)
    View mLinePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen(getString(R.string.title_screen_setting));
        btnBack.setVisibility(View.INVISIBLE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_user;
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = User.getInstance().getCurrentUser();
        mRlUnregister.setVisibility(user.isSkipUser() ? View.VISIBLE : View.GONE);
        int type = HSSPreference.getInstance(SettingActivity.this).getInt(AppConstants.KEY_PREFERENCE.IS_SOCIAL.toString(), 0);
        rl_password.setVisibility(type > 0 ? View.GONE : View.VISIBLE);
        mLinePassword.setVisibility(type > 0 ? View.GONE : View.VISIBLE);
        AccountManager manager = AccountManager.get(this);
        Account accountSave;
        accountSave = AccountUtils.getAccountUser(SettingActivity.this, manager);
        if (accountSave != null) {
            String password = manager.getPassword(accountSave);
            title_value_pass.setText(password);
        }
        title_value_id.setText(String.valueOf(user.getId()));
        title_value_email.setText(!TextUtils.isEmpty(user.getEmail()) ? user.getEmail() : "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (null != data && requestCode == ProfileActivity.REQUEST_CHANGE_EMAIL) {
            user.setEmail(data.getStringExtra(AppConstants.KEY_SEND.KEY_SEND_EMAIL));
            title_value_email.setText(user.getEmail());
        }
    }

    @OnClick({R.id.btn_register, R.id.rl_email, R.id.rl_password, R.id.btn_login, R.id.btn_logout, R.id.title_value_pass, R.id.rl_unregister})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                startActivity(new Intent(SettingActivity.this, RegisterActivity.class));
                break;
            case R.id.rl_email:
                Intent intent = new Intent(SettingActivity.this, ChangeEmailActivity.class);
                intent.putExtra(AppConstants.KEY_SEND.KEY_SEND_EMAIL, user.getEmail());
                startActivityForResult(intent, ProfileActivity.REQUEST_CHANGE_EMAIL);
                break;
            case R.id.rl_password:
                startActivity(new Intent(SettingActivity.this, ChangePasswordActivity.class));
                break;
            case R.id.btn_login:
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                break;
            case R.id.btn_logout:
                HSSDialog.show(SettingActivity.this, getString(R.string.msg_confirm_logout), getString(R.string.txt_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HSSDialog.dismissDialog();
                        sendRequestLogOut();
                    }
                }, getString(R.string.txt_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HSSDialog.dismissDialog();
                    }
                }).show();
                break;
            case R.id.title_value_pass:
                startActivity(new Intent(SettingActivity.this, ChangePasswordActivity.class));
                break;
            case R.id.rl_unregister:
                break;
        }
    }

}
