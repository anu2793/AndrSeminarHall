package jp.co.hiropro.seminar_hall.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

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
import java.util.Objects;

import butterknife.BindView;
import jp.co.hiropro.dialog.LoadingDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.controller.activity.ContentDetailActivity;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.SubCategoryDetailAdapter;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 10/3/2017.
 */

public class TagDefaultFragment extends FragmentBase implements Paginate.Callbacks {
    @BindView(R.id.lv_subcategory_detail)
    RecyclerView lvSubcategory;
    @BindView(R.id.tv_copyright)
    TextViewApp mTvCopyRight;
    @BindView(R.id.tv_copyright_bottom)
    TextViewApp mTvCopyRightBottom;
    private List<VideoDetail> subCategoryList = new ArrayList<>();
    private SubCategoryDetailAdapter adapter;
    private SubCategory subCategory;
    private int page = 1;
    private boolean isLoading;
    private String tag;
    private int type = AppConstants.TYPE_SEARCH.DEFAULT;
    private DividerItemDecoration divider = null;

    public TagDefaultFragment() {
    }

    public static TagDefaultFragment newInstance(String tag, int type, SubCategory cate) {
        TagDefaultFragment fragment = new TagDefaultFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putInt("type", type);
        bundle.putParcelable(KeyParser.KEY.DATA.toString(), cate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tag;
    }

    @Override
    protected void inflateData() {
        super.inflateData();
        Bundle bundle = getArguments();
        if (bundle != null) {
            subCategory = bundle.getParcelable(KeyParser.KEY.DATA.toString());
            tag = bundle.getString("tag");
            type = bundle.getInt("type");
        }
        initSubCategoryList();
        lvSubcategory.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Get height here
                        int heightScreen = (int) (AppUtils.getScreenHigh() - (2 *  getResources().getDimensionPixelOffset(R.dimen.value_height_action_bar)));
                        showCopyRight(lvSubcategory.getHeight() < heightScreen);
                    }
                });
    }

    private void initSubCategoryList() {
        adapter = new SubCategoryDetailAdapter(activity, subCategoryList, false, false);
        lvSubcategory.setLayoutManager(new LinearLayoutManager(activity));
        lvSubcategory.setAdapter(adapter);
        if (divider == null) {
            divider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
            divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(activity, R.drawable.line_divider)));
            lvSubcategory.addItemDecoration(divider);
        }
        adapter.setOnItemClickListener(new SubCategoryDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoDetail item) {
                startActivity(new Intent(activity, ContentDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), item));
            }
        });
        adapter.setOnFavoriteListener(new SubCategoryDetailAdapter.OnFavoriteListener() {
            @Override
            public void onFavoriteChange(ImageView ivStatus) {
                int status = subCategory.getIsFavorite() == 1 ? 0 : 1;
                subCategory.setIsFavorite(status);
                ivStatus.setSelected(status == 1);
                setFavorite(subCategory);
            }

            @Override
            public void onTeacherClick() {

            }
        });

        Paginate.with(lvSubcategory, this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMore() {
        searchTag(tag);
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == 0;
    }

    private void setFavorite(SubCategory subCategory) {
        LoadingDialog.getDialog(activity).show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", subCategory.getId());
        params.put("status", subCategory.getIsFavorite());
        params.put("page", page);
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
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void searchTag(String tag) {
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", tag);
        if (page == 1)
            subCategoryList.clear();
        params.put("page", page);
        params.put("sorttype", type);
        JsonObjectRequest request = new JsonObjectRequest(AppConstants.SERVER_PATH.SEARCH_TAG.toString(), new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        try {
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == 200) {
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());
                                JSONArray list = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                if (list.length() > 0) {
                                    for (int i = 0; i < list.length(); i++) {
                                        subCategoryList.add(VideoDetail.parse(list.getJSONObject(i)));
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                                isLoading = false;
                                page = data.getInt("next_page");
                            } else if (status == 404) {

                            } else {
                                ((BaseActivity) Objects.requireNonNull(getActivity())).sessionExpire();
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
                        isLoading = false;
                        adapter.notifyDataSetChanged();
                    }
                });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    /**
     * Show copy right in bottom with 2 case :
     * 1 >  empty list.
     * 2 > item not full screen.
     */
    private void showCopyRight(boolean showBottom) {
        mTvCopyRightBottom.setVisibility(showBottom ? View.VISIBLE : View.GONE);
        mTvCopyRight.setVisibility(showBottom ? View.GONE : View.VISIBLE);
    }
}
