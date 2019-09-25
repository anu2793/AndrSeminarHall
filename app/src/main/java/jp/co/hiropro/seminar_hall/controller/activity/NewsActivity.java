package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.paginate.Paginate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.NewsItem;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.adapter.NewsAdapter;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 7/21/2017.
 */

public class NewsActivity extends BaseActivity implements Paginate.Callbacks {
    private ArrayList<NewsItem> mListNews;
    private NewsAdapter mAdapterNews;
    @BindView(R.id.bg_loading)
    View mViewLoading;
    @BindView(R.id.pg_loading)
    ProgressBar mPrgLoading;
    @BindView(R.id.btn_retry)
    ImageButton mBtnRetry;
    @BindView(R.id.tv_no_data)
    TextView mTvNoData;
    @BindView(R.id.tv_error)
    TextView mTvError;
    @BindView(R.id.list_news)
    RecyclerView mList;
    private int mPage = 1;
    private boolean isLoading;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    public static int KEY_READ_NEW = 111;
    private int mPosRead = -1;
    private String mIsFromNotification = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen("ニュース一覧");
        initListNews();
        dismissBGLoading();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIsFromNotification = bundle.getString(AppConstants.KEY_SEND.KEY_CHECK_OPEN_NEW_FROM_NOTIFICATION, "");
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_news;
    }

    @OnClick({R.id.btn_retry})
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_retry:
                mPage = 1;
                getData();
                break;
        }
    }

    @Override
    public synchronized void onLoadMore() {
        getData();
    }

    @Override
    public synchronized boolean isLoading() {
        return isLoading;
    }

    @Override
    public synchronized boolean hasLoadedAllItems() {
        return mPage == 0;
    }

    private void initListNews() {
        mList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(NewsActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(linearLayoutManager);
        mListNews = new ArrayList<>();
        mAdapterNews = new NewsAdapter(NewsActivity.this, mListNews);
        mList.setAdapter(mAdapterNews);
//        mList.addOnItemTouchListener(
//                new RecyclerItemClickListener(NewsActivity.this, mList, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        // do whatever
//                        NewsItem newsItem = mListNews.get(mList.getChildAdapterPosition(view));
//                        if (AppUtils.isInternetAvailable(NewsActivity.this) && newsItem != null) {
//                            mPosRead = position;
//                            startActivityForResult(new Intent(NewsActivity.this, NewDetailActivity.class)
//                                    .putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, newsItem), NewsActivity.KEY_READ_NEW);
//                        } else {
//                            Toast.makeText(NewsActivity.this, getResources().getString(R.string.txt_lost_connect), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );
        Paginate.with(mList, this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();


        mAdapterNews.setActionClick(new NewsAdapter.onActionClick() {
            @Override
            public void onItemClick(NewsItem newsItem, int pos) {
                if (AppUtils.isInternetAvailable(NewsActivity.this)) {
                    mPosRead = pos;
                    if (newsItem.getNewtype() == 1) {
                        startActivityForResult(new Intent(NewsActivity.this, NewDetailActivity.class)
                                .putExtra(AppConstants.KEY_SEND.KEY_TEACH_NEWS, true)
                                .putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, newsItem), NewsActivity.KEY_READ_NEW);
                    } else {
                        startActivityForResult(new Intent(NewsActivity.this, NewDetailActivity.class)
                                .putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, newsItem), NewsActivity.KEY_READ_NEW);
                    }
                } else {
                    Toast.makeText(NewsActivity.this, getResources().getString(R.string.txt_lost_connect), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Hidden refresh view.
                mRefreshLayout.setRefreshing(false);
                mPage = 1;
                mListNews.clear();
                isLoading = true;
                onLoadMore();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NewsActivity.KEY_READ_NEW) {
            if (mPosRead >= 0) {
                mListNews.get(mPosRead).setRead(AppConstants.STATUS_NEW.READ);
                mAdapterNews.notifyItemChanged(mPosRead);
            }
        }
    }

    public void goDetailNews(NewsItem newsItem) {
        startActivity(new Intent(NewsActivity.this, NewDetailActivity.class).putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, newsItem));
    }

    private void showBGLoading() {
        mViewLoading.setVisibility(View.VISIBLE);
        mPrgLoading.setVisibility(View.VISIBLE);
        mBtnRetry.setVisibility(View.GONE);
        mList.setVisibility(View.GONE);
        mTvError.setVisibility(View.GONE);
    }

    private void dismissBGLoading() {
        mViewLoading.setVisibility(View.GONE);
        mList.setVisibility(View.VISIBLE);
    }

    private void dismissBgWithFail() {
        mViewLoading.setVisibility(View.VISIBLE);
        mPrgLoading.setVisibility(View.GONE);
        mBtnRetry.setVisibility(View.VISIBLE);
        mList.setVisibility(View.GONE);
        mTvError.setVisibility(View.VISIBLE);
        isLoading = false;
    }


    private void getData() {
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", mPage);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.NEWS.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {

                        System.out.println(strResponse);
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.optInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());

                                JSONArray newsList = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                if (newsList.length() > 0) {
                                    for (int i = 0; i < newsList.length(); i++) {
                                        JSONObject item = newsList.getJSONObject(i);
                                        mListNews.add(NewsItem.parser(item));
                                    }
                                    mAdapterNews.notifyDataSetChanged();
                                }
                                isLoading = false;
                                mPage = data.getInt(KeyParser.KEY.NEXT_PAGE.toString());
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
                                goMaintainScreen(activity, mess);
                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                                sessionExpire();
                            } else {
                                HSSDialog.show(activity, getString(R.string.error_io_exception));
                            }

                        } catch (JSONException e) {
                            isLoading = false;
                            mPage = 0;
                            e.printStackTrace();
                            mAdapterNews.notifyDataSetChanged();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        NetworkUtils.showDialogError(NewsActivity.this, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        request.setShouldCache(false);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onBackPressed() {
        if (mIsFromNotification.length() > 0) {
            startActivity(new Intent(NewsActivity.this, TopActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            super.onBackPressed();
        }
    }
}
