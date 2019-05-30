package jp.co.hiropro.seminar_hall.controller.fragment;

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
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.paginate.Paginate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import jp.co.hiropro.dialog.LoadingDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.SubCategoryDetailActivity;
import jp.co.hiropro.seminar_hall.model.CateSeminarObject;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.RecyclerSubCategoryNoDescriptionAdapter;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 1/26/2018.
 */

public class SeminarListFragment extends FragmentBase implements Paginate.Callbacks {
    public static String TAG = SeminarListFragment.class.getName();
    public static String TYPE_SEMINAR_TOP = "TYPE_SEMINAR_TOP";

    @BindView(R.id.rcy_seminars)
    RecyclerView mRcySeminar;
    @BindView(R.id.tv_copyright)
    TextViewApp mTvCopyRight;
    @BindView(R.id.tv_copyright_bottom)
    TextViewApp mTvCopyRightBottom;

    int mType = 0;
    private CateSeminarObject mCateObject = new CateSeminarObject();
    private ArrayList<SubCategory> subCategoryList = new ArrayList<>();
    private RecyclerSubCategoryNoDescriptionAdapter adapter;
    private DividerItemDecoration mTop = null;
    private int page = 1;
    private boolean isLoading;
    private BroadcastReceiver mReceive;

    public SeminarListFragment() {
    }

    public static SeminarListFragment newInstance(int type, CateSeminarObject object) {
        SeminarListFragment fragment = new SeminarListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SeminarListFragment.TYPE_SEMINAR_TOP, type);
        bundle.putParcelable(SeminarListFragment.TAG, object);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_seminar_list;
    }

    @Override
    protected void inflateView() {
        super.inflateView();

    }

    @Override
    protected void inflateData() {
        super.inflateData();
        // Get data.
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCateObject = bundle.getParcelable(SeminarListFragment.TAG);
            mType = bundle.getInt(SeminarListFragment.TYPE_SEMINAR_TOP);
        }
        initListSeminar();
        initListSubCategory();
        mRcySeminar.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Get height here
                        int heightScreen = (int) (AppUtils.getScreenHigh() - ((4 * getResources().getDimensionPixelOffset(R.dimen.value_height_action_bar))) +
                                getResources().getDimensionPixelOffset(R.dimen.value_65dp));
                        showCopyRight(mRcySeminar.getHeight() < heightScreen);
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
    public void onResume() {
        super.onResume();
        page = 1;
        Paginate.with(mRcySeminar, this)
                .setLoadingTriggerThreshold(0)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == 0;
    }

    private void getData() {
        isLoading = true;
        final HashMap<String, String> params = new HashMap<>();
        params.put("cateid", mCateObject.getId());
        params.put("sorttype", String.valueOf(mType));
        params.put("page", String.valueOf(page));
        RequestDataUtils.requestDataWithAuthen(Request.Method.GET, getActivity(), AppConstants.SERVER_PATH.GET_SHOP_SEMINAR.toString(), params, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject object, String msg) {
                if (page == 1)
                    subCategoryList.clear();
                JSONArray arraySeminar = object.optJSONArray(AppConstants.KEY_PARAMS.LIST.toString());
                if (arraySeminar.length() > 0) {
                    for (int i = 0; i < arraySeminar.length(); i++) {
                        try {
                            SubCategory cate = SubCategory.parse(arraySeminar.getJSONObject(i));
                            subCategoryList.add(cate);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                page = object.optInt(AppConstants.KEY_PARAMS.NEXT_PAGE.toString(), 0);
                isLoading = false;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(int error) {
                isLoading = false;
                page = 0;
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initListSeminar() {
        mRcySeminar.setLayoutManager(new LinearLayoutManager(activity, LinearLayout.VERTICAL, false));
        if (mTop == null) {
            mTop = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
            mTop.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(activity, R.drawable.line_divider)));
            mRcySeminar.addItemDecoration(mTop);
        }
    }

    private void initListSubCategory() {
        adapter = new RecyclerSubCategoryNoDescriptionAdapter(activity, subCategoryList, User.getInstance().getCurrentUser());
        adapter.setVisibleFavoriteImage(true);
        mRcySeminar.setAdapter(adapter);
        adapter.setOnFavoriteListener(new RecyclerSubCategoryNoDescriptionAdapter.FavoriteListener() {
            @Override
            public void onFavorite(SubCategory subCategory) {
                setFavorite(subCategory);
            }
        });
        adapter.setOnItemClick(new RecyclerSubCategoryNoDescriptionAdapter.OnItemClick() {
            @Override
            public void onItemClick(SubCategory subCategory) {
                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }
        });
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
                        if (getActivity() != null)
                            getActivity().sendBroadcast(new Intent(AppConstants.BROAD_CAST.REFRESH));
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
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadCast();
    }

    protected void registerBroadCast() {
        if (getActivity() == null)
            return;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.BROAD_CAST.UPDATE_SEMINAR_LIST);
        intentFilter.addAction(AppConstants.BROAD_CAST.REFRESH);
        if (mReceive == null)
            mReceive = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (null != intent.getAction()) {
                        if (intent.getAction().equalsIgnoreCase(AppConstants.BROAD_CAST.UPDATE_SEMINAR_LIST)) {
                            // update data.
                            if (intent.getExtras() != null) {
                                page = 1;
                                mCateObject = intent.getExtras().getParcelable(AppConstants.KEY_SEND.KEY_DATA);
                                getData();
                            }
                        }
                        if (intent.getAction().equalsIgnoreCase(AppConstants.BROAD_CAST.REFRESH)) {
                            page = 1;
                            getData();
                        }
                    }
                }
            };
        getActivity().registerReceiver(mReceive, intentFilter);
    }


    protected void unRegisterBroadCast() {
        if (getActivity() == null || mReceive == null)
            return;
        getActivity().unregisterReceiver(mReceive);
    }

    /**
     * Show copy right in bottom with 2 case :
     * 1 >  empty list.
     * 2 > item not full screen.
     *
     * @param showBottom
     */
    private void showCopyRight(boolean showBottom) {
        mTvCopyRightBottom.setVisibility(showBottom ? View.VISIBLE : View.GONE);
        mTvCopyRight.setVisibility(showBottom ? View.GONE : View.VISIBLE);
    }
}
