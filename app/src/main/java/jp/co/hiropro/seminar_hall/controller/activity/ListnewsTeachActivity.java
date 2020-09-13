package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.NewsItem;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.adapter.ListnewsTeachAdapter;
import jp.co.hiropro.utils.NetworkUtils;

public class ListnewsTeachActivity extends BaseActivity implements Paginate.Callbacks {
    private ArrayList<NewsItem> mListNews;
    private ListnewsTeachAdapter mlistnewsTeachAdapter;
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
    protected int getLayoutId() {
        return R.layout.activity_listnews_teach;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen("ニュース一覧");
        initListNews();
        dismissBGLoading();
    }


    private void initListNews() {
        mList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(ListnewsTeachActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(linearLayoutManager);
        mListNews = new ArrayList<>();
        mlistnewsTeachAdapter = new ListnewsTeachAdapter(ListnewsTeachActivity.this, mListNews);
        mList.setAdapter(mlistnewsTeachAdapter);
        Paginate.with(mList, this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();


        mlistnewsTeachAdapter.setActionClick(new ListnewsTeachAdapter.onActionClick() {
            @Override
            public void onItemClick(NewsItem newsItem, int pos) {
                if (AppUtils.isInternetAvailable(ListnewsTeachActivity.this)) {
                    mPosRead = pos;
                    startActivityForResult(new Intent(ListnewsTeachActivity.this, NewDetailActivity.class)
                            .putExtra(AppConstants.KEY_SEND.KEY_TEACH_NEWS, true)
                            .putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, newsItem), NewsActivity.KEY_READ_NEW);
                } else {
                    Toast.makeText(ListnewsTeachActivity.this, getResources().getString(R.string.txt_lost_connect), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemdelete(final NewsItem newsItem, int pos) {
                HSSDialog.show(ListnewsTeachActivity.this, "このメッセージを削除してもよろしいですか？", "OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HSSDialog.dismissDialog();
                        deleteNews(newsItem);
                    }
                }, "キャンセル", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HSSDialog.dismissDialog();
                    }
                }).show();
            }

            @Override
            public void onItemEdit(NewsItem newsItem, int pos) {
                startActivity(new Intent(ListnewsTeachActivity.this, SendNewsActivity.class).putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, newsItem));
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

    private void dismissBGLoading() {
        mViewLoading.setVisibility(View.GONE);
        mList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        getData();
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return mPage == 0;
    }

    private void getData() {
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put("memberid", user.getUserId());
        params.put("page", mPage);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.LIST_TEACH_NEWS.toString(), params),
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
                                    mlistnewsTeachAdapter.notifyDataSetChanged();
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
                            mlistnewsTeachAdapter.notifyDataSetChanged();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        NetworkUtils.showDialogError(ListnewsTeachActivity.this, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        request.setShouldCache(false);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void deleteNews(NewsItem newsItem) {
        showLoading();
        HashMap<String, Object> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.ID.toString(), String.valueOf(newsItem.getId()));
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.MEMBER_ID.toString(), user.getUserId());
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.DELETE_NEWS.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {

                        System.out.println(strResponse);
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.optInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                dismissLoading();
                                mPage = 1;
                                mListNews.clear();
                                isLoading = true;
                                onLoadMore();
                            }

                        } catch (JSONException e) {
                            dismissLoading();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkUtils.showDialogError(ListnewsTeachActivity.this, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        request.setShouldCache(false);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

}
