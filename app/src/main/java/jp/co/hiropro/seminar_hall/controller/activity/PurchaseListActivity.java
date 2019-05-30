package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.NewsItem;
import jp.co.hiropro.seminar_hall.model.PurchaseItem;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.view.adapter.PurchaseAdapter;
import jp.co.hiropro.seminar_hall.view.dialog.DialogNotification;
import jp.co.hiropro.seminar_hall.view.dialog.DialogRetryConnection;

import static jp.co.hiropro.seminar_hall.ForestApplication.LICENSE_KEY;
import static jp.co.hiropro.seminar_hall.ForestApplication.MERCHANT_ID;

/**
 * Created by dinhdv on 7/21/2017.
 */

public class PurchaseListActivity extends BaseActivity {
    private ArrayList<PurchaseItem> mListNews;
    private PurchaseAdapter mAdapterNews;
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
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private LinearLayoutManager linearLayoutManager;
    private NewsItem mItemNew = null;
    private int mPosition = -1;




    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(PurchaseListActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(linearLayoutManager);
        mListNews = new ArrayList<>();
        mAdapterNews = new PurchaseAdapter(PurchaseListActivity.this, mListNews);
        mList.setAdapter(mAdapterNews);
        mAdapterNews.setActionClick(new PurchaseAdapter.onActionClick() {
            @Override
            public void onItemClick(final PurchaseItem purchase, int pos) {
                DialogNotification dialogNotification = new DialogNotification(PurchaseListActivity.this, purchase.getDescription());
                dialogNotification.show();
            }

            @Override
            public void onPurchaseAction(final PurchaseItem purchase, int post) {
                if (AppUtils.isInternetAvailable(PurchaseListActivity.this)) {
                    DialogRetryConnection dialogRetryConnection = new DialogRetryConnection(PurchaseListActivity.this, getResources().getString(R.string.txt_buy_videos));
                    dialogRetryConnection.setListener(new DialogRetryConnection.onDialogChoice() {
                        @Override
                        public void onDone() {
                            if (AppConstants.isTestMode)
                                bp.purchase(PurchaseListActivity.this, "android.test.purchased");
                            else
                                bp.purchase(PurchaseListActivity.this, purchase.getCode());
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    dialogRetryConnection.show();
                    Toast.makeText(PurchaseListActivity.this, purchase.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PurchaseListActivity.this, getResources().getString(R.string.txt_lost_connect), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && mPage > 0) {
                    // TODO : Load more here....
                    isLoading = true;
                    // Add load more item.
                    mListNews.add(null);
                    mAdapterNews.notifyItemInserted(mListNews.size() - 1);
                    // End Add
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getMoreNewsFromServer();
                        }
                    });
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNewsFromServer();
                    }
                });
            }
        });
        mBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewsFromServer();
            }
        });
        btnBack.setVisibility(View.VISIBLE);
        initBilling();
        getNewsFromServer();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_purchase_list;
    }

    public void goDetailNews(NewsItem newsItem) {
        startActivity(new Intent(PurchaseListActivity.this, NewDetailActivity.class).putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, newsItem));
    }

    private void getNewsFromServer() {
        for (int i = 0; i < 10; i++) {
            PurchaseItem objNew = new PurchaseItem();
            objNew.setName("New " + i);
            objNew.setCode("2016/11/11 10:10");
            objNew.setCost("100" + i);
            objNew.setDescription("Description :" + i);
            objNew.setImage(i % 2 == 0 ? "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQnyBen7GvJCNWhSkO1BMWVdHTd1wbHHUxQ10MaXsovUo8aFGlIQg"
                    : "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSO7R1b4R1Kk2HMa7mkPWI9VB948Kmvb4ZJJWdpmh7cFEyhq6YqFw");
            mListNews.add(objNew);
        }
        mAdapterNews.notifyDataSetChanged();
        dismissBGLoading();
        Log.e("GET", "getNewsFromServer");
//        showBGLoading();
//        isLoading = true;
//        RequestParams params = new RequestParams();
//        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceId(getActivity()));
//        ServerConnection.getWithParams(ServerKeyPath.getUrlNews() + mPage, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                JSONArray arrayData;
//                try {
//                    arrayData = response.getJSONArray("list");
//                    if (arrayData.length() > 0) {
//                        for (int i = 0; i < arrayData.length(); i++) {
//                            try {
//                                JSONObject objectNews = arrayData.getJSONObject(i);
//                                NewsItem objNew = NewsItem.parser(objectNews);
//                                mListNews.add(objNew);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        dismissBGLoading();
//                        mAdapterNews.notifyDataSetChanged();
//                    } else {
//                        mTvNoData.setVisibility(View.GONE);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                String page = response.optString("next_page", "");
//                mPage = page.length() > 0 ? Integer.valueOf(page) : 0;
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                dismissBgWithFail();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//                dismissBgWithFail();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//                dismissBgWithFail();
//            }
//
//        });
    }

    private void getMoreNewsFromServer() {
        Log.e("GET", "getMoreNewsFromServer");
        isLoading = true;
//        ServerConnection.getNoParams(ServerKeyPath.getUrlNews() + mPage, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                //Remove loading item
//                mListNews.remove(mListNews.size() - 1);
//                mAdapterNews.notifyItemRemoved(mListNews.size());
//                JSONArray arrayData;
//                try {
//                    arrayData = response.getJSONArray("list");
//                    if (arrayData.length() > 0) {
//                        for (int i = 0; i < arrayData.length(); i++) {
//                            try {
//                                JSONObject objectNews = arrayData.getJSONObject(i);
//                                NewsItem objNew = NewsItem.parser(objectNews);
//                                mListNews.add(objNew);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        dismissBGLoading();
//                        mAdapterNews.notifyDataSetChanged();
//                    } else {
//                        mTvNoData.setVisibility(View.GONE);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                String page = response.optString("next_page", "");
//                mPage = page.length() > 0 ? Integer.valueOf(page) : 0;
//                isLoading = false;
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                dismissBgWithFail();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//                dismissBgWithFail();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//                dismissBgWithFail();
//            }
//
//        });
    }

    private void initBilling() {
        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                bp.consumePurchase(productId);

                DialogNotification dialogNotification = new DialogNotification(PurchaseListActivity.this, getResources().getString(R.string.txt_buy_video_success));
                dialogNotification.setOnListenner(new DialogNotification.onDoneClick() {
                    @Override
                    public void onClick() {

                    }
                });
                dialogNotification.setCanceledOnTouchOutside(false);
                dialogNotification.show();

            }

            @Override
            public void onPurchaseHistoryRestored() {
                for (String sku : bp.listOwnedProducts())
                    Log.d("FOREST", "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d("FOREST", "Owned Subscription: " + sku);
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                Toast.makeText(PurchaseListActivity.this, getResources().getString(R.string.txt_cancel_purchase), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingInitialized() {

            }
        });
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
}
