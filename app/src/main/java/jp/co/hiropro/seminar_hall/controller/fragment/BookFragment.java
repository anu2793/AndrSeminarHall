package jp.co.hiropro.seminar_hall.controller.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

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
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.dialog.LoadingDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.controller.activity.MaintainActivity;
import jp.co.hiropro.seminar_hall.controller.activity.SubCategoryActivity;
import jp.co.hiropro.seminar_hall.controller.activity.SubCategoryDetailActivity;
import jp.co.hiropro.seminar_hall.controller.activity.SubSubCategoryActivity;
import jp.co.hiropro.seminar_hall.model.Category;
import jp.co.hiropro.seminar_hall.model.Favorite;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.HSSPreference;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.adapter.FavoriteAdapter;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 9/26/2017.
 */

public class BookFragment extends FragmentBase implements Paginate.Callbacks {
    @BindView(R.id.lv_subcategory)
    RecyclerView lvFavorite;
    private List<Favorite> favoriteList = new ArrayList<>();
    private FavoriteAdapter adapter;
    private int mId = 0;
    private int page = 1;
    private boolean isLoading = false;
    private BroadcastReceiver mReceive;

    public BookFragment() {
    }

    public static BookFragment newInstance(int id) {
        BookFragment fragment = new BookFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_book;
    }

    @Override
    protected void inflateData() {
        super.inflateData();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mId = bundle.getInt("id");
        }
        onLoadMore();
        initSearchResult();
    }

    public void initSearchResult() {
        adapter = new FavoriteAdapter(favoriteList, false, true);
        lvFavorite.setLayoutManager(new LinearLayoutManager(activity));
        DividerItemDecoration divider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        lvFavorite.addItemDecoration(divider);
        lvFavorite.setAdapter(adapter);

        adapter.setOnUnfavoriteListener(new FavoriteAdapter.OnUnfavoriteListener() {
            @Override
            public void onUnfavorite(Favorite item, int position) {
                disLikeSubcategory(item.getId(), item.isFavorite(), position);
            }
        });
        adapter.setOnItemClick(new FavoriteAdapter.OnItemClick() {
            @Override
            public void onItemClick(Favorite item) {
                SubCategory subCategory = new SubCategory();
                subCategory.setId(item.getId());
                subCategory.setTitle(item.getTitle());
                subCategory.setIsFavorite(item.isFavorite() ? 1 : 0);
                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }
        });
        adapter.setOnCategoryClick(new FavoriteAdapter.OnItemClick() {
            @Override
            public void onItemClick(Favorite item) {
                Category category = new Category();
                category.setId(item.getCateId());
                category.setNameJp(item.getCateTitle());
                startActivity(new Intent(activity, SubCategoryActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), category));
            }
        });
        adapter.setOnSubCategoryClick(new FavoriteAdapter.OnItemClick() {
            @Override
            public void onItemClick(Favorite item) {
                SubCategory subCategory = new SubCategory();
                subCategory.setId(item.getSubCateId());
                subCategory.setTitle(item.getSubcateTitle());
                startActivity(new Intent(activity, SubSubCategoryActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }
        });
        Paginate.with(lvFavorite, this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
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
                        favoriteList.get(position).setFavorite(isFavorite ? 0 : 1);
                        adapter.notifyItemChanged(position);
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


    private void getData(int id) {
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("subcateid", id);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.FAVORITE_BOOK.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == 200) {
                                favoriteList.clear();
                                JSONArray data = response.getJSONArray(KeyParser.KEY.DATA.toString());
                                if (data.length() > 0) {
                                    for (int i = 0; i < data.length(); i++) {
                                        favoriteList.add(Favorite.parse(data.getJSONObject(i)));
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                isLoading = false;
                                page = response.getInt("next_page");
                            } else if (status == 459) {
                                HSSDialog.show(activity, getString(R.string.msg_sub_category_not_exist), "OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getActivity().finish();
                                    }
                                });
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
                                goMaintainScreen(activity, mess);
                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                                ((BaseActivity) getActivity()).sessionExpire();
                            } else {
                                HSSDialog.show(activity, getString(R.string.error_io_exception));
                            }

                        } catch (JSONException e) {
                            page = 0;
                            isLoading = false;
                            adapter.notifyDataSetChanged();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                page = 0;
                isLoading = false;
                adapter.notifyDataSetChanged();
                NetworkUtils.showDialogError(getActivity(), error, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    public void goMaintainScreen(Activity activity, String msg) {
        startActivity(new Intent(activity, MaintainActivity.class).putExtra(AppConstants.KEY_SEND.KEY_MSG_MAINTAIN, msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void setFavorite(SubCategory subCategory) {
        LoadingDialog.getDialog(getActivity()).show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", subCategory.getId());
        params.put("status", subCategory.getIsFavorite());
        JsonObjectRequest request = new JsonObjectRequest(AppConstants.SERVER_PATH.FAVORITE_SUBCATEGORY.toString(), new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialog.getDialog(getActivity()).dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingDialog.getDialog(getActivity()).dismiss();
                        NetworkUtils.showDialogError(getActivity(), error);
                    }
                });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onLoadMore() {
        getData(mId);
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
                        favoriteList.clear();
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
