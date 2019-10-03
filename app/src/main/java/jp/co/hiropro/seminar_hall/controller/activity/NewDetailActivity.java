package jp.co.hiropro.seminar_hall.controller.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.NewsItem;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.HSSPreference;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.view.AutoResizeTextView;
import jp.co.hiropro.seminar_hall.view.dialog.DialogRetryConnection;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 7/21/2017.
 */

public class NewDetailActivity extends BaseActivity {
    private NewsItem mNewsObject;
    private TextView mTvShortTitle, mTvTitleNormal, mTvTime;
    private AutoResizeTextView mTvTitle;
    private AutoLinkTextView mTvDescription;
    private ImageView mImvThumb;
    private RelativeLayout mRlImage;
    private ProgressDialog mPrg;
    private LinearLayout mLlContent;
    private ProgressBar mPrgLoading;
    private Boolean teachNews = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTitleScreen("ニュースの詳細");
        initViewControl();
        // Progress Bar
        mPrg = new ProgressDialog(NewDetailActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
        mNewsObject = getIntent().getParcelableExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT);
        teachNews = getIntent().getBooleanExtra(AppConstants.KEY_SEND.KEY_TEACH_NEWS, false);
        if (mNewsObject != null) {
            if (teachNews) {
                getNewDetailTeach(String.valueOf(mNewsObject.getId()));
            } else {
                getNewDetail(String.valueOf(mNewsObject.getId()));
            }
        }


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_detail;
    }

    private void initViewControl() {
        mTvTitle = (AutoResizeTextView) findViewById(R.id.tv_title);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvDescription = (AutoLinkTextView) findViewById(R.id.tv_description);
        mTvShortTitle = (TextView) findViewById(R.id.tv_short_content);
        mImvThumb = (ImageView) findViewById(R.id.imv_image);
        mRlImage = (RelativeLayout) findViewById(R.id.rl_img);
        mLlContent = (LinearLayout) findViewById(R.id.ll_content);
        mTvTitleNormal = (TextView) findViewById(R.id.tv_title_normal);
        mPrgLoading = (ProgressBar) findViewById(R.id.progressBar9);
        mNewsObject = new NewsItem();
    }

    private void loadDataToView(NewsItem object) {
        mTvTime.setText(object.getDateWithFomatJP());
        mTvDescription.addAutoLinkMode(AutoLinkMode.MODE_EMAIL, AutoLinkMode.MODE_URL, AutoLinkMode.MODE_PHONE);
        mTvDescription.setUrlModeColor(Color.BLUE);
        mTvDescription.setEmailModeColor(Color.BLUE);
        mTvDescription.setPhoneModeColor(Color.BLUE);
        mTvDescription.setAutoLinkText(object.getBody());
        mTvDescription.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
            @Override
            public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                if (autoLinkMode == AutoLinkMode.MODE_URL) {
                    startActivity(new Intent(NewDetailActivity.this, WebviewActivity.class)
                            .putExtra(AppConstants.KEY_SEND.KEY_URL_HYPERLINK, matchedText));
                } else if (autoLinkMode == AutoLinkMode.MODE_EMAIL) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{matchedText});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                    intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                    startActivity(Intent.createChooser(intent, ""));
                }
            }
        });

        mTvShortTitle.setText(object.getDescription());
        mTvTitleNormal.setText(object.getTitle());
        mTvTitle.setText(object.getTitle());
        int heightView = AppUtils.getScreenWidth() * 710 / 1242;
        if (object.getImage().length() > 0) {
            Glide.with(this).load(object.getImage()).into(mImvThumb);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            float dp = 23f;
            float fpixels = metrics.density * dp;
            int pixels = (int) (fpixels + 0.5f);

            // Image height.
            RelativeLayout.LayoutParams paramsImage = (RelativeLayout.LayoutParams) mImvThumb.getLayoutParams();
            paramsImage.height = heightView;
            mImvThumb.setLayoutParams(paramsImage);

            // Frame height.
            RelativeLayout.LayoutParams paramsTitle = (RelativeLayout.LayoutParams) mLlContent.getLayoutParams();
            paramsTitle.height = heightView;
            mLlContent.setLayoutParams(paramsTitle);

            // Content height/
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTvTitle.getLayoutParams();
            params.height = (int) (heightView - 2 * pixels - (mTvTitle.getLineSpacingExtra()));
            mTvTitle.setLayoutParams(params);
//            710 / 1242
        } else {
            mTvTitleNormal.setVisibility(View.VISIBLE);
            mTvTitle.setVisibility(View.GONE);
        }

    }

    private void getNewDetailTeach(String idnews) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.MEMBER_ID.toString(), user.getUserId());
        params.put(AppConstants.KEY_PARAMS.ID.toString(), idnews);
        params.put("memtype", "0");
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID(NewDetailActivity.this));
        RequestDataUtils.requestData(Request.Method.GET, NewDetailActivity.this, AppConstants.SERVER_PATH.NEW_DETAIL_TEACH.toString(),
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject response, String msg) {
                        if (response.length() > 0) {
                            try {
                                JSONObject objectNew = response.getJSONObject(AppConstants.KEY_PARAMS.DETAIL.toString());
                                if (user.getRoleUser() != 2) {
                                    int numberNotifi = objectNew.optInt(AppConstants.KEY_PARAMS.REMAIN.toString());
                                    HSSPreference.getInstance().putInt("number_remain", numberNotifi);
                                    AppUtils.setNumberNotification(NewDetailActivity.this, numberNotifi);
                                }
                                NewsItem item = NewsItem.parser(objectNew);
                                loadDataToView(item);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dismissLoading();
                    }

                    @Override
                    public void onFail(int error) {
                        dismissLoading();
                    }
                });
    }

    private void getNewDetail(final String idNew) {
        showLoading();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, AppConstants.SERVER_PATH.NEW_DETAIL.toString() + "?id=" + idNew, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mPrgLoading.setVisibility(View.GONE);
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                    int numberNotifi = objectData.optInt(AppConstants.KEY_PARAMS.REMAIN.toString());
                                    HSSPreference.getInstance().putInt("number_remain", numberNotifi);
                                    AppUtils.setNumberNotification(NewDetailActivity.this, numberNotifi);
                                    JSONObject objectNew = objectData.getJSONObject(AppConstants.KEY_PARAMS.DETAIL.toString());
                                    NewsItem item = NewsItem.parser(objectNew);
                                    loadDataToView(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (status == 459) {
                                HSSDialog.show(activity, getString(R.string.msg_news_deleted), getString(R.string.txt_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent returnIntent = new Intent();
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        onBackPressed();
                                    }
                                });
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
                                goMaintainScreen(activity, mess);
                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                                sessionExpire();
                            } else {
                                HSSDialog.show(activity, getString(R.string.error_io_exception));
                            }
                        }
                        dismissLoading();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPrgLoading.setVisibility(View.GONE);
                dismissLoading();
                NetworkUtils.showDialogError(activity, error);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    protected void getNewAgain(final String idNew) {
        DialogRetryConnection dialogRetryConnection = new DialogRetryConnection(NewDetailActivity.this, getString(R.string.msg_login_error_try_again));
        dialogRetryConnection.setListener(new DialogRetryConnection.onDialogChoice() {
            @Override
            public void onDone() {
                getNewDetail(idNew);
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
        dialogRetryConnection.show();

    }

    public void showLoading() {
        if (!mPrg.isShowing() && mPrg != null)
            mPrg.show();
    }

    public void dismissLoading() {
        if (mPrg.isShowing())
            mPrg.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (mNewsObject.isFromNotification()) {
            if (isTaskRoot()) {
                // is last activity.
                startActivity(new Intent(NewDetailActivity.this, TopActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
