package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.paginate.Paginate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.Category;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.SubCategoryDetailAdapter;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 11/10/2017.
 */

public class CateListActivity extends BaseActivity implements Paginate.Callbacks {
    @BindView(R.id.imv_banner)
    ImageView mImvBanner;
    @BindView(R.id.tv_description)
    TextViewApp mTvDescription;
    @BindView(R.id.rcy_cate)
    RecyclerView mRcyCate;
    private Category mCate;
    private List<VideoDetail> videoDetailList = new ArrayList<>();
    private SubCategoryDetailAdapter adapter;
    private int page = 1;
    private boolean isLoading;
    private BroadcastReceiver mReceive;
    private DividerItemDecoration divider;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cate_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            mCate = bundle.getParcelable(KeyParser.KEY.DATA.toString());
        adapter = new SubCategoryDetailAdapter(CateListActivity.this, videoDetailList, false, true);
        mRcyCate.setLayoutManager(new LinearLayoutManager(this));
        mRcyCate.setAdapter(adapter);
        if (divider == null) {
            divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider));
            mRcyCate.addItemDecoration(divider);
        }
        adapter.setOnItemClickListener(new SubCategoryDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoDetail item) {
                startActivity(new Intent(CateListActivity.this, ContentDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), item));
            }
        });
        Paginate.with(mRcyCate, this)
                .setLoadingTriggerThreshold(0)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
        btnBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        getData(mCate.getId());
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == 0;
    }

    private void getData(final int id) {
        isLoading = true;
        final HashMap<String, Object> params = new HashMap<>();
        params.put("cateid", id);
        params.put("page", page);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.JACK_LIST_VIDEO.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.optInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                if (page == 1)
                                    videoDetailList.clear();
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());
                                if (data.length() > 0) {
                                    JSONObject top = data.getJSONObject(AppConstants.KEY_PARAMS.TOP.toString());
                                    if (top.length() > 0) {
                                        String title = top.optString(AppConstants.KEY_PARAMS.TITLE.toString());
                                        String description = top.optString(AppConstants.KEY_PARAMS.DESCRIPTION.toString());
                                        String image = top.optString(AppConstants.KEY_PARAMS.IMAGE.toString(), "");
                                        if (image.length() > 0)
                                            GlideApp.with(CateListActivity.this).load(image).into(mImvBanner);
                                        mTvDescription.setText(description);
                                        setupTitleScreen(title);
                                    }
                                }
                                JSONArray arrayData = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                if (arrayData.length() > 0) {
                                    for (int i = 0; i < arrayData.length(); i++) {
                                        JSONObject objectVideos = arrayData.getJSONObject(i);
                                        videoDetailList.add(VideoDetail.parse(objectVideos));
                                    }
                                }
                                page = data.getInt("next_page");
                                isLoading = false;
                                adapter.notifyDataSetChanged();
                            } else if (status == 459) {
                                isLoading = false;
                                HSSDialog.show(activity, getString(R.string.msg_video_not_exist), "OK", new View.OnClickListener() {
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
                            page = 0;
                            isLoading = false;
                            adapter.notifyDataSetChanged();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        HSSDialog.dismissDialog();
                        NetworkUtils.showDialogError(CateListActivity.this, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadCast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadCast();
    }

    private void registerBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.BROAD_CAST.SEARCH);
        intentFilter.addAction(AppConstants.BROAD_CAST.REFRESH);
        if (mReceive == null)
            mReceive = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(AppConstants.BROAD_CAST.REFRESH)) {
                        videoDetailList.clear();
                        page = 1;
                        getData(mCate.getId());
                    }
                }
            };
        registerReceiver(mReceive, intentFilter);
    }

    private void unregisterBroadCast() {
        if (mReceive != null)
            unregisterReceiver(mReceive);
    }
}
