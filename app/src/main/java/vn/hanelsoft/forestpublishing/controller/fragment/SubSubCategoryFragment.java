package vn.hanelsoft.forestpublishing.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

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
import java.util.Objects;

import butterknife.BindView;
import vn.hanelsoft.dialog.HSSDialog;
import vn.hanelsoft.dialog.LoadingDialog;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.controller.activity.BaseActivity;
import vn.hanelsoft.forestpublishing.controller.activity.SubCategoryDetailActivity;
import vn.hanelsoft.forestpublishing.controller.activity.SubSubCategoryActivity;
import vn.hanelsoft.forestpublishing.model.SubCategory;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.AppUtils;
import vn.hanelsoft.forestpublishing.util.KeyParser;
import vn.hanelsoft.forestpublishing.view.TextViewApp;
import vn.hanelsoft.forestpublishing.view.adapter.RecyclerSubCategoryAdapter;
import vn.hanelsoft.utils.NetworkUtils;

public class SubSubCategoryFragment extends FragmentBase implements Paginate.Callbacks {
    @BindView(R.id.lv_subcategory)
    RecyclerView lvSubcategory;
    @BindView(R.id.tv_copyright)
    TextViewApp mTvCopyRight;
    @BindView(R.id.tv_copyright_bottom)
    TextViewApp mTvCopyRightBottom;
    ArrayList<SubCategory> subCategoryList = new ArrayList<>();
    RecyclerSubCategoryAdapter adapter;
    SubCategory subCategoryIntent;
    int mType = AppConstants.TYPE_SEARCH.DEFAULT;
    private BroadcastReceiver mReceive;
    private int page = 1;
    private boolean isLoading;
    private User mUser;
    private DividerItemDecoration mDivider;

    public SubSubCategoryFragment() {
    }

    public static SubSubCategoryFragment newInstance(SubCategory category) {
        SubSubCategoryFragment fragment = new SubSubCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("category", category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void inflateData() {
        super.inflateData();
        Bundle bundle = getArguments();
        if (mUser == null)
            mUser = User.getInstance().getCurrentUser();
        if (bundle != null) {
            subCategoryIntent = bundle.getParcelable("category");
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        lvSubcategory.setLayoutManager(linearLayoutManager);
        lvSubcategory.setHasFixedSize(true);
        if (mDivider == null) {
            mDivider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
            mDivider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(activity, R.drawable.line_divider)));
            lvSubcategory.addItemDecoration(mDivider);
        }
        initListSubCategory();
//        adapter.clear();
        subCategoryList.clear();
        onLoadMore();
        lvSubcategory.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Get height here
                        int heightScreen = AppUtils.getScreenHigh() - (2 * getResources().getDimensionPixelOffset(R.dimen.value_height_action_bar));
                        showCopyRight(lvSubcategory.getHeight() < heightScreen);
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragmet_sub_sub_category;
    }

    @Override
    public void onLoadMore() {
        getData(subCategoryIntent.getId());
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == 0;
    }

    private void initListSubCategory() {
        adapter = new RecyclerSubCategoryAdapter(activity, subCategoryList, mUser);
        adapter.setVisibleFavoriteImage(true);
        lvSubcategory.setAdapter(adapter);
//        lvSubcategory.addFooterView(getFooterCopyRight(), null, false);

        adapter.setOnFavoriteListener(new RecyclerSubCategoryAdapter.FavoriteListener() {
            @Override
            public void onFavorite(SubCategory subCategory) {
                setFavorite(subCategory);
            }
        });
        adapter.setOnItemClick(new RecyclerSubCategoryAdapter.OnItemClick() {
            @Override
            public void onItemClick(SubCategory subCategory) {
                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }
        });
    }

    private void getData(int id) {
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("cateid", id);
        params.put("sorttype", mType);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.LIST_VIDEO_CATEGORY_TOP.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        isLoading = false;
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());
                                //Banner
                                JSONObject top = data.getJSONObject("top");
                                if (top.length() > 0)
                                    ((SubSubCategoryActivity) Objects.requireNonNull(getActivity())).setDataHeader(top);
                                //List subcategory
                                JSONArray subcategory = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                if (subcategory.length() > 0) {
                                    for (int i = 0; i < subcategory.length(); i++) {
                                        JSONObject item = subcategory.getJSONObject(i);
                                        subCategoryList.add(SubCategory.parse(item));
                                    }
//                                    adapter.addAll(subCategoryList);
                                    adapter.notifyDataSetChanged();
                                }
                            } else if (status == 459) {
                                HSSDialog.show(activity, getString(R.string.msg_sub_category_not_exist), "OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Objects.requireNonNull(getActivity()).onBackPressed();
                                    }
                                });
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
                                ((SubSubCategoryActivity) Objects.requireNonNull(getActivity())).goMaintainScreen(activity, mess);
                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                                ((BaseActivity) Objects.requireNonNull(getActivity())).sessionExpire();
                            } else {
                                HSSDialog.show(activity, getString(R.string.error_io_exception));
                            }
                            page = 0;
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            isLoading = false;
                            page = 0;
                            adapter.notifyDataSetChanged();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                page = 0;
                adapter.notifyDataSetChanged();
                NetworkUtils.showDialogError(activity, error);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void setFavorite(SubCategory subCategory) {
        LoadingDialog.getDialog(activity).show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", subCategory.getId());
        params.put("status", subCategory.getIsFavorite());
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

    @Override
    public void onStart() {
        super.onStart();
        registerBroadCast();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterBroadCast();
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
                    if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(AppConstants.BROAD_CAST.REFRESH)) {
//                        adapter.clear();
                        subCategoryList.clear();
                        getData(subCategoryIntent.getId());
                    }
                }
            };
        getActivity().registerReceiver(mReceive, intentFilter);
    }

    private void unregisterBroadCast() {
        if (getActivity() != null && mReceive != null)
            getActivity().unregisterReceiver(mReceive);
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
