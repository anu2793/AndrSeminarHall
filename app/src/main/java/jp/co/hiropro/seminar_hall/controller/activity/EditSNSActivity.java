package jp.co.hiropro.seminar_hall.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.FieldSocial;
import jp.co.hiropro.seminar_hall.view.EditTextApp;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

public class EditSNSActivity extends BaseActivity {
    private int typeEdit;
    private String strValueChange = "";
    private String strValue = "";
    @BindView(R.id.edt_Value)
    EditTextApp edt_Value;
    @BindView(R.id.tv_title)
    TextViewApp tv_title;
    @BindView(R.id.tv_error_link)
    TextViewApp mTvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initData();
        setTitle();
        setupTitleScreen(getTitleScreen());
        edt_Value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    mTvError.setVisibility(View.GONE);
            }
        });
    }

    private String getTitleScreen() {
        String value = "";
        switch (typeEdit) {
            case FieldSocial.YOUTUBE:
                value += "Youtube";
                break;
            case FieldSocial.FACEBOOK:
                value += "Facebook";
                break;
            case FieldSocial.GOOGLE:
                value += "Google+";
                break;
            case FieldSocial.INSTAGRAM:
                value += "Instagram";
                break;
            case FieldSocial.TWITTER:
                value += "Twitter";
                break;
        }
        return value + "を追加";
    }

    private void initData() {
        typeEdit = getIntent().getExtras().getInt(AppConstants.KEY_INTENT.TYPE_FIELD
                .toString());
        strValue = getIntent().getExtras().getString(AppConstants.KEY_INTENT.VALUE
                .toString());
        edt_Value.setText(strValue);
    }

    private void setTitle() {
        String value = "";
        switch (typeEdit) {
            case FieldSocial.YOUTUBE:
                value += "Youtube";
                break;
            case FieldSocial.FACEBOOK:
                value += "Facebook";
                break;
            case FieldSocial.GOOGLE:
                value += "Google+";
                break;
            case FieldSocial.INSTAGRAM:
                value += "Instagram";
                break;
            case FieldSocial.TWITTER:
                value += "Twitter";
                break;
        }
        tv_title.setText(value + "のアカウントのリンク入力");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_sns;
    }

    @OnClick({R.id.btn_save})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                strValueChange = edt_Value.getText().toString().trim();
                if (TextUtils.isEmpty(strValueChange)) {
                    finish();
                } else if (checkValidate(edt_Value.getText().toString())) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(AppConstants.KEY_INTENT.VALUE_CHANGE.toString(), strValueChange);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    mTvError.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private boolean checkValidate(String link) {
        return Patterns.WEB_URL.matcher(link.toLowerCase()).matches();
    }
}
