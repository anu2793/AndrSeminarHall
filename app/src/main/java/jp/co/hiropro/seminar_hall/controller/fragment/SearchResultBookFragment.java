package jp.co.hiropro.seminar_hall.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.paginate.Paginate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import jp.co.hiropro.dialog.LoadingDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.ContentDetailActivity;
import jp.co.hiropro.seminar_hall.controller.activity.SubCategoryActivity;
import jp.co.hiropro.seminar_hall.controller.activity.SubCategoryDetailActivity;
import jp.co.hiropro.seminar_hall.controller.activity.SubSubCategoryActivity;
import jp.co.hiropro.seminar_hall.model.Category;
import jp.co.hiropro.seminar_hall.model.SearchResult;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.HSSPreference;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.adapter.SearchResultAdapter;
import jp.co.hiropro.utils.NetworkUtils;

public class SearchResultBookFragment extends FragmentBase implements Paginate.Callbacks {
    @BindView(R.id.lv_search_result)
    RecyclerView lvSearchResult;
    private String keySearch;
    private List<SearchResult> searchResultList = new ArrayList<>();
    private SearchResultAdapter adapter;
    private boolean isLoading;
    private int page = 1;
    private int mType = AppConstants.TYPE_SEARCH.BOOK;
    private BroadcastReceiver mReceive;

    public SearchResultBookFragment() {
    }

    public static SearchResultBookFragment newInstance(String keySearch) {
        SearchResultBookFragment fragment = new SearchResultBookFragment();
        Bundle args = new Bundle();
        args.putString("search", keySearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keySearch = getArguments().getString("search");
        }
    }

    @Override
    protected void inflateData() {
        super.inflateData();
        page = 1;
        onLoadMore();
        initSearchResult();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_result_detail;
    }

    public void initSearchResult() {
        adapter = new SearchResultAdapter(getActivity(), searchResultList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvSearchResult.setLayoutManager(linearLayoutManager);
        lvSearchResult.setAdapter(adapter);
        lvSearchResult.setNestedScrollingEnabled(false);
        initAdapterEvent();
        Paginate.with(lvSearchResult, this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
    }

    private void initAdapterEvent() {
        adapter.setOnItemSearchClick(new SearchResultAdapter.OnItemSearchClick() {
            @Override
            public void onClick(SearchResult item) {
                if (item.getType() == 0) {//Video
                    VideoDetail videoDetail = new VideoDetail();
                    videoDetail.setTitle(item.getTitle());
                    videoDetail.setId(item.getId());
                    startActivityForResult(new Intent(activity, ContentDetailActivity.class)
                            .putExtra(KeyParser.KEY.DATA.toString(), videoDetail), 103);
                } else if (item.getType() == 1) { //Sub-subcategory
                    SubCategory subCategory = new SubCategory();
                    subCategory.setId(item.getId());
                    subCategory.setTitle(item.getTitle());
                    subCategory.setDescription(item.getDescription());
                    subCategory.setImage(item.getSearchSubCategory().getImage());
                    subCategory.setIsFavorite(item.getSearchSubCategory().getIsFavorite());
                    startActivity(new Intent(activity, SubCategoryDetailActivity.class)
                            .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
                } else {
                    SubCategory subCategory = new SubCategory();
                    subCategory.setId(item.getId());
                    subCategory.setTitle(item.getTitle());
                    subCategory.setDescription(item.getDescription());
                    subCategory.setImage(item.getSearchSubCategory().getImage());
                    startActivity(new Intent(activity, SubSubCategoryActivity.class)
                            .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
                }
            }
        });
        adapter.setOnUnfavoriteListener(new SearchResultAdapter.OnUnfavoriteListener() {
            @Override
            public void onUnfavorite(SearchResult item, int position) {
                disLikeSubcategory(item.getId(), item.getSearchSubCategory().getIsFavorite() == 1, position);
            }
        });
        adapter.setOnCategoryClick(new SearchResultAdapter.OnCategoryClick() {
            @Override
            public void onCategoryClick(SearchResult item) {
                Category category = new Category();
                category.setId(item.getSearchSubCategory().getCateId());
                category.setNameJp(item.getSearchSubCategory().getCategory());
                startActivity(new Intent(activity, SubCategoryActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), category));
            }
        });
        adapter.setOnSubCategoryClick(new SearchResultAdapter.OnSubCategoryClick() {
            @Override
            public void onSubCategoryClick(SearchResult item) {
                SubCategory subCategory = new SubCategory();
                subCategory.setId(item.getSearchSubCategory().getSubCateId());
                subCategory.setTitle(item.getSearchSubCategory().getSubCategory());
                startActivity(new Intent(activity, SubSubCategoryActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }
        });
        adapter.setOnSubSubCategoryClick(new SearchResultAdapter.OnSubSubCategoryClick() {
            @Override
            public void onSubSubCategoryClick(SearchResult item) {
                SubCategory subCategory = new SubCategory();
                subCategory.setId(item.getSearchSubCategory().getBookId());
                subCategory.setTitle(item.getSearchSubCategory().getBook());
                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }
        });
    }

    @Override
    public void onLoadMore() {
        search(keySearch);
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == 0;
    }

    private void search(String querry) {
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("str", querry);
        params.put("page", page);
        params.put("sorttype", mType);
        if (page == 1)
            searchResultList.clear();
        JsonObjectRequest request = new JsonObjectRequest(AppConstants.SERVER_PATH.SEARCH.toString(), new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == 200) {
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());
                                JSONArray listData = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                if (listData.length() > 0) {
                                    for (int i = 0; i < listData.length(); i++) {
                                        searchResultList.add(SearchResult.parse(listData.getJSONObject(i)));
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                                isLoading = false;
                                page = data.getInt("next_page");
                            } else {
                                page = 0;
                                isLoading = false;
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            page = 0;
                            isLoading = false;
                            adapter.notifyDataSetChanged();
                            e.printStackTrace();
                        }
                        System.out.println(response);
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

    private void disLikeSubcategory(int id, final boolean isFavorite, final int position) {
        LoadingDialog.getDialog(activity).show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", isFavorite ? 0 : 1);
        JsonObjectRequest request = new JsonObjectRequest(AppConstants.SERVER_PATH.FAVORITE_SUBCATEGORY.toString(), new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        searchResultList.get(position).getSearchSubCategory().setIsFavorite(isFavorite ? 0 : 1);
                        adapter.notifyItemChanged(position);
                        LoadingDialog.getDialog(activity).dismiss();
                        if (getActivity() != null)
                            getActivity().sendBroadcast(new Intent(AppConstants.BROAD_CAST.REFRESH));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingDialog.getDialog(activity).dismiss();
                        NetworkUtils.showDialogError(activity, error);

                    }
                });
        HashMap<String, String> header = new HashMap<>();
        String auth = HSSPreference.getInstance().getString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), "");
        header.put("Authorization", auth);
        request.setHeaders(header);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void registerBroadCast() {
        if (getActivity() == null)
            return;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.BROAD_CAST.SEARCH);
        intentFilter.addAction(AppConstants.BROAD_CAST.REFRESH);
        if (mReceive == null)
            mReceive = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(AppConstants.BROAD_CAST.SEARCH)) {
                        keySearch = intent.getStringExtra(AppConstants.KEY_INTENT.SEARCH_VALUES.toString());
                        page = 1;
                        onLoadMore();
                    }
                    if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(AppConstants.BROAD_CAST.REFRESH)) {
                        page = 1;
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
