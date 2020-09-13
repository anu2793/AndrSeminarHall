package jp.co.hiropro.seminar_hall.controller.fragment;

import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

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
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.controller.activity.ContentDetailActivity;
import jp.co.hiropro.seminar_hall.model.SearchResult;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.HistoryAdapter;
import jp.co.hiropro.utils.NetworkUtils;

public class HistoryFragment extends FragmentBase implements Paginate.Callbacks {
    @BindView(R.id.lv_history)
    RecyclerView lvHistory;
    @BindView(R.id.tv_empty)
    TextViewApp tvEmpty;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mRefreshView;
    private HistoryAdapter adapter;
    private List<SearchResult> historyList = new ArrayList<>();
    private boolean isLoading = false;
    private int page = 1;
    private DividerItemDecoration mDivider = null;

    public HistoryFragment() {
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    protected void inflateData() {
        super.inflateData();

    }

    @Override
    public void onResume() {
        super.onResume();
        onLoadMore();
        initListHistory();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history;
    }

    private void initListHistory() {
        adapter = new HistoryAdapter(historyList, false, false);
        lvHistory.setLayoutManager(new LinearLayoutManager(activity));
        if (mDivider == null) {
            mDivider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            mDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider_gray));
            lvHistory.addItemDecoration(mDivider);
        }
//        DividerItemDecoration divider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
//        lvHistory.addItemDecoration(divider);
        lvHistory.setAdapter(adapter);

        lvHistory.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), lvHistory, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!isLoading) {
                    SearchResult item = historyList.get(lvHistory.getChildAdapterPosition(view));
                    if (item != null) {
                        VideoDetail videoDetail = new VideoDetail();
                        videoDetail.setId(item.getId());
                        videoDetail.setTitle(item.getTitle());
                        startActivity(new Intent(activity, ContentDetailActivity.class)
                                .putExtra(KeyParser.KEY.DATA.toString(), videoDetail));
                    }
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

//        adapter.setOnItemClick(new HistoryAdapter.OnItemClick() {
//            @Override
//            public void onItemClick(SearchResult item) {
//                VideoDetail videoDetail = new VideoDetail();
//                videoDetail.setId(item.getId());
//                videoDetail.setTitle(item.getTitle());
//                startActivity(new Intent(activity, ContentDetailActivity.class)
//                        .putExtra(KeyParser.KEY.DATA.toString(), videoDetail));
//            }
//        });
//        adapter.setOnCategoryClick(new HistoryAdapter.OnItemClick() {
//            @Override
//            public void onItemClick(SearchResult item) {
//                Category category = new Category();
//                category.setId(item.getSearchSubCategory().getCateId());
//                category.setNameJp(item.getSearchSubCategory().getCategory());
//                startActivity(new Intent(activity, SubCategoryActivity.class)
//                        .putExtra(KeyParser.KEY.DATA.toString(), category));
//            }
//        });
//        adapter.setOnSubCategoryClick(new HistoryAdapter.OnItemClick() {
//            @Override
//            public void onItemClick(SearchResult item) {
//                SubCategory subCategory = new SubCategory();
//                subCategory.setId(item.getSearchSubCategory().getSubCateId());
//                subCategory.setTitle(item.getSearchSubCategory().getSubCategory());
//                startActivity(new Intent(activity, SubSubCategoryActivity.class)
//                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
//            }
//        });
//        adapter.setOnSubSubCategoryClick(new HistoryAdapter.OnItemClick() {
//            @Override
//            public void onItemClick(SearchResult item) {
//                SubCategory subCategory = new SubCategory();
//                subCategory.setId(item.getSearchSubCategory().getBookId());
//                subCategory.setTitle(item.getSearchSubCategory().getBook());
//                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
//                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
//            }
//        });
        Paginate.with(lvHistory, HistoryFragment.this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lvHistory.stopScroll();
                if (!isLoading) {
                    page = 1;
                    onLoadMore();
                }
            }
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//    }

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
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.HISTORY.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                if (page == 1 || page == 0)
                                    historyList.clear();
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());
                                JSONArray list = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                tvEmpty.setVisibility(list.length() > 0 ? View.GONE : View.VISIBLE);
                                if (list.length() > 0) {
                                    for (int i = 0; i < list.length(); i++) {
                                        historyList.add(SearchResult.parse(list.getJSONObject(i)));
                                    }
                                }
                                lvHistory.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                isLoading = false;
                                page = data.getInt("next_page");
                            } else {
                                ((BaseActivity) getActivity()).sessionExpire();
                            }
                        } catch (JSONException e) {
                            page = 0;
                            isLoading = false;
                            lvHistory.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            e.printStackTrace();
                        }
                        mRefreshView.setRefreshing(false);
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
