package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.JackImageObject;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.JackImageAdapter;

/**
 * Created by dinhdv on 11/9/2017.
 */

public class JackProfileActivity extends BaseActivity {
    @BindView(R.id.imv_top)
    ImageView mImvTop;
    @BindView(R.id.tv_title)
    TextViewApp mTvTitle;
    @BindView(R.id.rcy_content)
    RecyclerView mRcyImage;

    private ArrayList<JackImageObject> mListImage = new ArrayList<>();
    private JackImageAdapter mImageAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_jack_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTitleScreen(getString(R.string.txt_jack_profile_screen));
        btnBack.setVisibility(View.VISIBLE);
        ButterKnife.bind(this);
        initListImage();
        loadProfile();
    }

    private void loadProfile() {
        showLoading();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, AppConstants.SERVER_PATH.JACK_PROFILE.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            String msg = response.optString(AppConstants.KEY_PARAMS.MESSAGE.toString(), "");
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                dismissLoading();
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                    if (objectData.length() > 0) {
                                        // Info  top.
                                        JSONObject info = objectData.getJSONObject(AppConstants.KEY_PARAMS.INFO.toString());
                                        if (info.length() > 0) {
                                            String title = info.optString(AppConstants.KEY_PARAMS.TITLE.toString(), "");
                                            setupTitleScreen(title);
                                            String image = info.optString(AppConstants.KEY_PARAMS.IMAGE.toString(), "");
                                            if (image.length() > 0)
                                                GlideApp.with(JackProfileActivity.this).load(image).into(mImvTop);
                                            String body = info.optString(AppConstants.KEY_PARAMS.BODY.toString(), "");
                                            mTvTitle.setText(body);
                                        }
                                        // Banner.
                                        JSONArray listBanner = objectData.optJSONArray(AppConstants.KEY_PARAMS.LIST_BANNER.toString());
                                        if (listBanner.length() > 0) {
                                            for (int i = 0; i < listBanner.length(); i++) {
                                                JackImageObject object = JackImageObject.parserData(listBanner.getJSONObject(i));
                                                mListImage.add(object);
                                            }
                                            mImageAdapter.notifyDataSetChanged();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                goMaintainScreen(activity, msg);
                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                                sessionExpire();
                            } else {
                                dismissLoading();
                                HSSDialog.show(JackProfileActivity.this, getString(R.string.msg_request_error_try_again));
                            }
                        }
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                HSSDialog.show(JackProfileActivity.this, getString(R.string.msg_request_error_try_again));
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void initListImage() {
        mImageAdapter = new JackImageAdapter(JackProfileActivity.this, mListImage, false, true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(JackProfileActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setAutoMeasureEnabled(true);
        mRcyImage.setLayoutManager(linearLayoutManager);
        mRcyImage.setAdapter(mImageAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider));
        mRcyImage.addItemDecoration(divider);
        mRcyImage.addOnItemTouchListener(new RecyclerItemClickListener(JackProfileActivity.this, mRcyImage, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                JackImageObject object = mListImage.get(position);
                if (object.getLink().length() > 0) {
                    startActivity(new Intent(JackProfileActivity.this, AppWebViewActivity.class).putExtra(AppConstants.KEY_SEND.KEY_DATA, object));
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }
}
