package jp.co.hiropro.seminar_hall.controller.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.FieldSocial;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.ButtonApp;
import jp.co.hiropro.seminar_hall.view.CircleImageView;
import jp.co.hiropro.seminar_hall.view.EditTextApp;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

public class EditProfileActivity extends BaseActivity {
    @BindView(R.id.imv_avatar)
    CircleImageView imv_avatar;
    @BindView(R.id.value_youtube)
    TextViewApp value_youtube;
    @BindView(R.id.value_google)
    TextViewApp value_google;
    @BindView(R.id.value_facebook)
    TextViewApp value_facebook;
    @BindView(R.id.value_twitter)
    TextViewApp value_twitter;
    @BindView(R.id.value_instagram)
    TextViewApp value_instagram;
    @BindView(R.id.error_link_youtube)
    TextViewApp error_link_youtube;
    @BindView(R.id.error_name)
    TextViewApp error_name;
    @BindView(R.id.edt_name)
    EditTextApp edt_name;
    @BindView(R.id.edt_content)
    EditTextApp edt_content;
    @BindView(R.id.edt_content_link_youtube)
    EditTextApp edt_content_link_youtube;
    @BindView(R.id.scroll_view)
    NestedScrollView scroll_view;
    @BindView(R.id.btnSaveChange)
    ButtonApp btnSaveChange;
    private int type;
    private String valuechange;
    private String filePath = "";
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen(getString(R.string.title_screen_edit_profile));
        if (user == null)
            user = User.getInstance().getCurrentUser();
        loadDataServer(AppConstants.SERVER_PATH.GET_MY_PROFILE.toString());
        initEven();
        scroll_view.setSmoothScrollingEnabled(true);
    }

    private void loadDataServer(String url) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.ID.toString(), String.valueOf(user.getId()));//TEST
        RequestDataUtils.requestData(Request.Method.GET, EditProfileActivity.this, url,
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        try {
                            user = User.parseProfile(object.getJSONObject("info"), user);
                            fillDataUI();
                            dismissLoading();
                        } catch (JSONException e) {
                            dismissLoading();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(int error) {
                        dismissLoading();
                    }
                });
    }

    private void uiViewNotEdit() {
        edt_content.setEnabled(false);
        edt_name.setEnabled(false);
        edt_content_link_youtube.setEnabled(false);
        btnSaveChange.setVisibility(View.GONE);
    }

    private void fillDataUI() {
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(user.getAvatar())) {
            Utils.initImageView(user.getAvatar(), imv_avatar, this);
        } else {
            Utils.initImageView("", imv_avatar, this);
        }
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(user.getFullname())) {
            edt_name.setText(user.getFullname());
        }
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(user.getProfile())) {
            edt_content.setText(user.getProfile());
        }
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(user.getIntroductionVideoUrl())) {
            edt_content_link_youtube.setText(user.getIntroductionVideoUrl());
        }
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(user.getYoutubeLink())) {
            value_youtube.setText(user.getYoutubeLink());
        }
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(user.getGoogleLink())) {
            value_google.setText(user.getGoogleLink());
        }
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(user.getFacebookLink())) {
            value_facebook.setText(user.getFacebookLink());
        }
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(user.getTwitterLink())) {
            value_twitter.setText(user.getTwitterLink());
        }
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(user.getInstagramLink())) {
            value_instagram.setText(user.getInstagramLink());
        }
    }

    private void initEven() {
        edt_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_SCROLL:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(edt_content, InputMethodManager.SHOW_IMPLICIT);
                }
                return false;
            }
        });
        edt_content_link_youtube.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_content_link_youtube.setBackgroundResource(R.drawable.bg_border);
                if (error_link_youtube.getVisibility() == View.VISIBLE) {
                    error_link_youtube.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_name.setBackgroundResource(R.drawable.bg_border);
                if (error_name.getVisibility() == View.VISIBLE) {
                    error_name.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick(R.id.imv_avatar)
    public void captureImage() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            Dexter.withActivity(thisActivity())
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            showCamera();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            finish();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .onSameThread()
                    .check();
        } else {
            showCamera();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    public Activity thisActivity() {
        return EditProfileActivity.this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(thisActivity()).load(new File(resultUri.getPath())).into(imv_avatar);
                filePath = resultUri.getPath();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        } else if (AppConstants.StaticParam.REQUEST_CODE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                valuechange = data.getStringExtra(AppConstants.KEY_INTENT.VALUE_CHANGE
                        .toString());
                if (isUpdate(valuechange)) {
                    updateUI();
                }
            }
        }
    }

    private void updateUI() {
        switch (this.type) {
            case FieldSocial.YOUTUBE:
                value_youtube.setText(valuechange);
                break;
            case FieldSocial.GOOGLE:
                value_google.setText(valuechange);
                break;
            case FieldSocial.FACEBOOK:
                value_facebook.setText(valuechange);
                break;
            case FieldSocial.TWITTER:
                value_twitter.setText(valuechange);
                break;
            case FieldSocial.INSTAGRAM:
                value_instagram.setText(valuechange);
                break;
        }
    }

    private boolean isUpdate(String valueChange) {
        boolean isChange = true;
        switch (this.type) {
            case FieldSocial.YOUTUBE:
                if (valueChange.equals(value_youtube)) {
                    isChange = false;
                }
                break;
            case FieldSocial.GOOGLE:
                if (valueChange.equals(value_google)) {
                    isChange = false;
                }
                break;
            case FieldSocial.FACEBOOK:
                if (valueChange.equals(value_facebook)) {
                    isChange = false;
                }
                break;
            case FieldSocial.TWITTER:
                if (valueChange.equals(value_twitter)) {
                    isChange = false;
                }
                break;
            case FieldSocial.INSTAGRAM:
                if (valueChange.equals(value_instagram)) {
                    isChange = false;
                }
                break;
        }
        return isChange;
    }

    @OnClick({R.id.layout_youtube, R.id.layout_google, R.id.layout_facebook, R.id.layout_twitter, R.id.layout_diagram, R.id.btnSaveChange})
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.layout_youtube:
                goActivityEdit(FieldSocial.YOUTUBE, String.valueOf(value_youtube.getText()));
                break;
            case R.id.layout_google:
                goActivityEdit(FieldSocial.GOOGLE, String.valueOf(value_google.getText()));
                break;
            case R.id.layout_facebook:
                goActivityEdit(FieldSocial.FACEBOOK, String.valueOf(value_facebook.getText()));
                break;
            case R.id.layout_twitter:
                goActivityEdit(FieldSocial.TWITTER, String.valueOf(value_twitter.getText()));
                break;
            case R.id.layout_diagram:
                goActivityEdit(FieldSocial.INSTAGRAM, String.valueOf(value_instagram.getText()));
                break;
            case R.id.btnSaveChange:
//                    if (AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(edt_name.getText().toString())) {
//                        error_name.setVisibility(View.VISIBLE);
//                        edt_name.setBackgroundResource(R.drawable.bg_border_error);
//                        edt_name.requestFocus();
//                        Utils.scrollToView(scroll_view, edt_name);
//                    } else
                if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(edt_content_link_youtube.getText().toString()) &&
                        !Utils.formatYoutubeLink(edt_content_link_youtube.getText().toString())) {
                    error_link_youtube.setVisibility(View.VISIBLE);
                    edt_content_link_youtube.setBackgroundResource(R.drawable.bg_border_error);
                    edt_content_link_youtube.requestFocus();
                    Utils.scrollToView(scroll_view, edt_content_link_youtube);
                } else {
                    error_link_youtube.setVisibility(View.GONE);
                    sendRequestCreateUser(AppConstants.SERVER_PATH.CREATE_UPDATE_PROFILE.toString());
                }
                break;
        }
    }

    private void goActivityEdit(int type, String value) {
        this.type = type;
        Intent intent = new Intent(thisActivity(), EditSNSActivity.class);
        intent.putExtra(AppConstants.KEY_INTENT.TYPE_FIELD.toString(), type);
        intent.putExtra(AppConstants.KEY_INTENT.VALUE.toString(), value);
        startActivityForResult(intent, AppConstants.StaticParam.REQUEST_CODE);
    }

    private void sendRequestCreateUser(String url) {//1-- fb 2-- google
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.NAME.toString(), edt_name.getText().toString());//TEST
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(filePath)) {
            params.put(AppConstants.KEY_PARAMS.AVATAR.toString(), Utils.encodeBase64(filePath));//TEST
        }
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(edt_content.getText().toString()))
            params.put(AppConstants.KEY_PARAMS.DESCRIPTION.toString(), edt_content.getText().toString());//TEST
        if (!AppConstants.StaticParam.EMPTY_VALUE_STRING.equals(edt_content_link_youtube.getText().toString()))
            params.put(AppConstants.KEY_PARAMS.PRM_VIDEO_URL.toString(), edt_content_link_youtube.getText().toString());//TEST
        params.put(AppConstants.KEY_PARAMS.YOUTUBE.toString(), value_youtube.getText().toString());//TEST
        params.put(AppConstants.KEY_PARAMS.GOOGLE.toString(), value_google.getText().toString());//TEST
        params.put(AppConstants.KEY_PARAMS.FACEBOOK.toString(), value_facebook.getText().toString());//TEST
        params.put(AppConstants.KEY_PARAMS.TWITTER.toString(), value_twitter.getText().toString());//TEST
        params.put(AppConstants.KEY_PARAMS.INSTAGRAM.toString(), value_instagram.getText().toString());//TEST
        RequestDataUtils.requestData(Request.Method.POST, EditProfileActivity.this, url,
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        Toast.makeText(EditProfileActivity.this, "成功した", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFail(int error) {
                    }
                });
    }

    private void showCamera() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .setMaxZoom(4)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setRequestedSize(500, 500, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .start(thisActivity());
    }
}
