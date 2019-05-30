package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.Category;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 11/10/2017.
 */

public class CateDetailActivity extends BaseActivity {
    @BindView(R.id.imv_banner)
    ImageView mImvBanner;
    @BindView(R.id.tv_description)
    TextViewApp mTvDescription;
    private Category mCate;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cate_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            mCate = bundle.getParcelable(KeyParser.KEY.DATA.toString());
        if (mCate != null)
            getData(mCate.getId());
        btnBack.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.imv_go_list})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.imv_go_list:
                startActivity(new Intent(CateDetailActivity.this, CateListActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), mCate));
                break;
        }
    }

    private void getData(final int id) {
        showLoading();
        final HashMap<String, Object> params = new HashMap<>();
        params.put("cateid", id);
        params.put("page", "1");
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.JACK_LIST_VIDEO.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        dismissLoading();
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.optInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                JSONObject objectData = response.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                if (objectData.length() > 0) {
                                    JSONObject top = objectData.getJSONObject(AppConstants.KEY_PARAMS.TOP.toString());
                                    if (top.length() > 0){
                                        String title = top.optString(AppConstants.KEY_PARAMS.TITLE.toString());
                                        String description = top.optString(AppConstants.KEY_PARAMS.DESCRIPTION.toString());
                                        String image = top.optString(AppConstants.KEY_PARAMS.IMAGE.toString(), "");
                                        if (image.length() > 0)
                                            GlideApp.with(CateDetailActivity.this).load(image).into(mImvBanner);
                                        mTvDescription.setText(description);
                                        setupTitleScreen(title);
                                    }
                                }
                            } else if (status == 459) {
                                HSSDialog.show(activity, getString(R.string.msg_category_not_exist), "OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissLoading();
                        NetworkUtils.showDialogError(CateDetailActivity.this, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }
}
