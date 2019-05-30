package jp.co.hiropro.seminar_hall.controller.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

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
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.controller.activity.ContentDetailActivity;
import jp.co.hiropro.seminar_hall.controller.activity.LoginActivity;
import jp.co.hiropro.seminar_hall.model.SearchResult;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.PurchaseHistoryAdapter;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PurchaseFragment extends FragmentBase implements Paginate.Callbacks {
    @BindView(R.id.lv_purchase)
    RecyclerView lvPurchase;
    @BindView(R.id.tv_empty)
    TextViewApp tvEmpty;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mRefreshView;
    @BindView(R.id.rl_free_user)
    RelativeLayout mRlUnRegister;
    private PurchaseHistoryAdapter adapter;
    private List<SearchResult> purchaseHistoryList = new ArrayList<>();
    private boolean isLoading = false;
    private int page = 1;
    private User mUser = null;
    private DividerItemDecoration mDivider = null;

    public PurchaseFragment() {
        // Required empty public constructor
    }

    public static PurchaseFragment newInstance(User user) {
        PurchaseFragment fragment = new PurchaseFragment();
        fragment.mUser = user;
        return fragment;
    }

    @Override
    protected void inflateData() {
        super.inflateData();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUser == null)
            mUser = User.getInstance().getCurrentUser();
        onLoadMore();
        initListHistory();
//        mRlUnRegister.setVisibility(mUser != null && mUser.isSkipUser() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_purchase;
    }

    @OnClick({R.id.btn_login, R.id.rl_free_user})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(activity, LoginActivity.class));
                break;
            case R.id.rl_free_user:
                break;
        }
    }

    private void initListHistory() {
        adapter = new PurchaseHistoryAdapter(purchaseHistoryList, false, false);
        lvPurchase.setLayoutManager(new LinearLayoutManager(activity));

        if (mDivider == null) {
            mDivider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            mDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider_gray));
            lvPurchase.addItemDecoration(mDivider);
        }
//        DividerItemDecoration divider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
//        lvPurchase.addItemDecoration(divider);
        lvPurchase.setAdapter(adapter);

        Paginate.with(lvPurchase, PurchaseFragment.this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
        lvPurchase.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), lvPurchase, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!isLoading) {
                    SearchResult item = purchaseHistoryList.get(lvPurchase.getChildAdapterPosition(view));
                    if (item != null) {
                        VideoDetail videoDetail = new VideoDetail();
                        videoDetail.setTitle(item.getTitle());
                        videoDetail.setId(item.getId());
                        startActivity(new Intent(activity, ContentDetailActivity.class)
                                .putExtra(KeyParser.KEY.DATA.toString(), videoDetail));
                    }
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                purchaseHistoryList.clear();
                getData();
            }
        });
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
        return page == 0;
    }

    private void getData() {
        isLoading = true;
        final HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.HISTORY_PURCHASE.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        mRefreshView.setRefreshing(false);
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                if (page == 1 || page == 0)
                                    purchaseHistoryList.clear();
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());
                                JSONArray list = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                if (list.length() > 0) {
                                    tvEmpty.setVisibility(View.GONE);
                                    for (int i = 0; i < list.length(); i++) {
                                        purchaseHistoryList.add(SearchResult.parse(list.getJSONObject(i)));
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                                isLoading = false;
                                page = data.getInt("next_page");
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
                                activity.goMaintainScreen(activity, mess);
                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                                ((BaseActivity) getActivity()).sessionExpire();
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
                        mRefreshView.setRefreshing(false);
                        NetworkUtils.showDialogError(activity, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }
}
