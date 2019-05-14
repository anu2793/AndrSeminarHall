package vn.hanelsoft.forestpublishing.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.controller.activity.ContentDetailActivity;
import vn.hanelsoft.forestpublishing.model.SearchResult;
import vn.hanelsoft.forestpublishing.model.VideoDetail;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.KeyParser;
import vn.hanelsoft.forestpublishing.util.RecyclerItemClickListener;
import vn.hanelsoft.forestpublishing.view.CustomLoadingListItemCreator;
import vn.hanelsoft.forestpublishing.view.adapter.HistoryAdapter;
import vn.hanelsoft.utils.NetworkUtils;

/**
 * Created by dinhdv on 9/26/2017.
 */

public class VideoPaidFragment extends FragmentBase implements Paginate.Callbacks {
    @BindView(R.id.lv_history)
    RecyclerView lvHistory;
    private HistoryAdapter adapter;
    private List<SearchResult> historyList = new ArrayList<>();
    private boolean isLoading = false;
    private int page = 1;
    private BroadcastReceiver mReceive;

    public VideoPaidFragment() {
    }

    public static VideoPaidFragment newInstance() {
        return new VideoPaidFragment();
    }

    @Override
    protected void inflateData() {
        super.inflateData();
        onLoadMore();
        initListHistory();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_free;
    }

    private void initListHistory() {
        adapter = new HistoryAdapter(historyList, false, true);
        lvHistory.setLayoutManager(new LinearLayoutManager(activity));
        DividerItemDecoration divider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        lvHistory.addItemDecoration(divider);
        lvHistory.setAdapter(adapter);

        Paginate.with(lvHistory, VideoPaidFragment.this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();

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
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("sorttype", AppConstants.TYPE_SEARCH.VIDEO_BUY);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.FAVORITE_VIDEOS.toString(), params),
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
                                if (list.length() > 0) {
                                    for (int i = 0; i < list.length(); i++) {
                                        historyList.add(SearchResult.parse(list.getJSONObject(i)));
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                                isLoading = false;
                                page = data.getInt("next_page");
                            }
                        } catch (JSONException e) {
                            page = 0;
                            isLoading = false;
                            adapter.notifyDataSetChanged();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkUtils.showDialogError(activity, error);
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
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadCast();
    }

    private void registerBroadCast() {
        if (getActivity() == null)
            return;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.BROAD_CAST.REFRESH);
        if (mReceive == null)
            mReceive = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(AppConstants.BROAD_CAST.REFRESH)) {
                        page = 1;
                        historyList.clear();
                        onLoadMore();
                    }
                }
            };
        getActivity().registerReceiver(mReceive, intentFilter);
    }

    private void unregisterBroadCast() {
        if (getActivity() != null && mReceive != null)
            getActivity().unregisterReceiver(mReceive);
    }
}

