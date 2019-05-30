package jp.co.hiropro.seminar_hall.controller.fragment;


import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
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
import jp.co.hiropro.dialog.LoadingDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.controller.activity.LoginActivity;
import jp.co.hiropro.seminar_hall.controller.activity.RegisterActivity;
import jp.co.hiropro.seminar_hall.controller.activity.SubCategoryDetailActivity;
import jp.co.hiropro.seminar_hall.model.Favorite;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.HSSPreference;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.FavoriteAdapter;
import jp.co.hiropro.utils.NetworkUtils;

public class FavoriteFragment extends FragmentBase implements Paginate.Callbacks {
    @BindView(R.id.lv_favorite)
    RecyclerView lvFavorite;
    @BindView(R.id.tv_empty)
    TextViewApp tvEmpty;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mRefreshView;
    @BindView(R.id.rl_unregister)
    RelativeLayout mRlunregister;
    private List<Favorite> favoriteList = new ArrayList<>();
    private FavoriteAdapter adapter;
    private boolean isLoading = true;
    private int page = 1;
    private User mUser = null;
    private DividerItemDecoration divider = null;

    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        return fragment;
    }

    @Override
    protected void inflateData() {
        super.inflateData();
//        onLoadMore();
        initListFavorite();
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                favoriteList.clear();
                page = 1;
                getData();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUser == null)
            mUser = User.getInstance().getCurrentUser();
        mRlunregister.setVisibility(mUser.isSkipUser() ? View.VISIBLE : View.GONE);
        page = 1;
        getData();
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


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_favorite;
    }

    @OnClick({R.id.btn_register, R.id.btn_login, R.id.rl_unregister})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                startActivity(new Intent(getActivity(), RegisterActivity.class));
                break;
            case R.id.btn_login:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_unregister:
                break;
        }
    }

    private void initListFavorite() {
        adapter = new FavoriteAdapter(favoriteList, false, false);
        lvFavorite.setLayoutManager(new LinearLayoutManager(activity));
        if (divider == null) {
            divider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
            lvFavorite.addItemDecoration(divider);
        }
        lvFavorite.setAdapter(adapter);
        Paginate.with(lvFavorite, FavoriteFragment.this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();

        adapter.setOnUnfavoriteListener(new FavoriteAdapter.OnUnfavoriteListener() {
            @Override
            public void onUnfavorite(Favorite item, int position) {
                favoriteList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                disLikeSubcategory(item.getId());
                if (favoriteList.size() == 0)
                    tvEmpty.setVisibility(View.VISIBLE);
            }
        });
        adapter.setOnItemClick(new FavoriteAdapter.OnItemClick() {
            @Override
            public void onItemClick(Favorite item) {
                SubCategory subCategory = new SubCategory();
                subCategory.setId(item.getId());
                subCategory.setTitle(item.getTitle());
                subCategory.setIsFavorite(1);
                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }
        });
//        adapter.setOnCategoryClick(new FavoriteAdapter.OnItemClick() {
//            @Override
//            public void onItemClick(Favorite item) {
//                Category category = new Category();
//                category.setId(item.getCateId());
//                category.setNameJp(item.getCateTitle());
//                startActivity(new Intent(activity, SubCategoryActivity.class)
//                        .putExtra(KeyParser.KEY.DATA.toString(), category));
//            }
//        });
//        adapter.setOnSubCategoryClick(new FavoriteAdapter.OnItemClick() {
//            @Override
//            public void onItemClick(Favorite item) {
//                SubCategory subCategory = new SubCategory();
//                subCategory.setId(item.getSubCateId());
//                subCategory.setTitle(item.getSubcateTitle());
//                startActivity(new Intent(activity, SubSubCategoryActivity.class)
//                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
//            }
//        });
    }

    private void getData() {
        isLoading = true;
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.FAVORITE.toString(), new HashMap<String, Object>()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                if (page == 1)
                                    favoriteList.clear();
                                JSONArray data = response.getJSONArray(KeyParser.KEY.DATA.toString());
                                if (data.length() > 0) {
                                    for (int i = 0; i < data.length(); i++) {
                                        Favorite favorite = Favorite.parse(data.getJSONObject(i));
                                        favorite.setFavorite(1);
                                        favoriteList.add(favorite);
                                    }
                                }
                                tvEmpty.setVisibility(favoriteList.size() > 0 ? View.GONE : View.VISIBLE);
                                adapter.notifyDataSetChanged();
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
                        }
                        isLoading = false;
                        mRefreshView.setRefreshing(false);
                        page = 0;
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                page = 0;
                isLoading = false;
                mRefreshView.setRefreshing(false);
                adapter.notifyDataSetChanged();
                NetworkUtils.showDialogError(activity, error);
            }
        });
        HashMap<String, String> header = new HashMap<>();
        String auth = HSSPreference.getInstance().getString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), "");
        header.put("Authorization", auth);
        request.setHeaders(header);
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void disLikeSubcategory(int id) {
        LoadingDialog.getDialog(activity).show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", 0);
        JsonObjectRequest request = new JsonObjectRequest(AppConstants.SERVER_PATH.FAVORITE_SUBCATEGORY.toString(), new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialog.getDialog(activity).dismiss();
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

}
