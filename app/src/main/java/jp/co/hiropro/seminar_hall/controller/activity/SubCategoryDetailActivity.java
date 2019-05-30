package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.dialog.LoadingDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.SubCategoryDetailAdapter;
import jp.co.hiropro.utils.NetworkUtils;

public class SubCategoryDetailActivity extends BaseActivity implements Paginate.Callbacks {
    @BindView(R.id.lv_subcategory_detail)
    RecyclerView lvSubcategory;
    @BindView(R.id.tv_copyright)
    TextViewApp mTvCopyRight;
    @BindView(R.id.tv_copyright_bottom)
    TextViewApp mTvCopyRightBottom;
    List<VideoDetail> subCategoryList = new ArrayList<>();
    SubCategoryDetailAdapter adapter;
    SubCategory subCategory;
    private int page = 1;
    private boolean isLoading;
    private boolean isTagSearch;
    private String tag;
    private User mTeacher = null;
    private DividerItemDecoration divider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        if (user == null)
            user = User.getInstance().getCurrentUser();
        isTagSearch = getIntent().getBooleanExtra("isTagSearch", false);
        subCategory = getIntent().getParcelableExtra(KeyParser.KEY.DATA.toString());
        tag = getIntent().getStringExtra("tag");
        if (isTagSearch)
            setupTitleScreen(tag);
        else {
            if (subCategory != null) {
                setupTitleScreen(subCategory.getTitle());
            } else {
                setupTitleScreen("サブカテゴリー名");
            }
        }
        initSubCategoryList();
        lvSubcategory.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Get height here
                        int heightScreen = (int) (AppUtils.getScreenHigh() - ((3 * getResources().getDimensionPixelOffset(R.dimen.value_height_action_bar))) +
                                getResources().getDimensionPixelOffset(R.dimen.value_65dp));
                        showCopyRight(lvSubcategory.getHeight() < heightScreen);
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sub_category_detail;
    }

    private void initSubCategoryList() {
        if (isTagSearch)
            adapter = new SubCategoryDetailAdapter(this, subCategoryList, false, false);
        else {
            adapter = new SubCategoryDetailAdapter(this, subCategoryList, true, false);
        }
        lvSubcategory.setLayoutManager(new LinearLayoutManager(this));
        lvSubcategory.setAdapter(adapter);
        if (divider == null) {
            divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider)));
            lvSubcategory.addItemDecoration(divider);
        }
        adapter.setOnItemClickListener(new SubCategoryDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoDetail item) {
                startActivity(new Intent(SubCategoryDetailActivity.this, ContentDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), item));
            }
        });
        adapter.setOnFavoriteListener(new SubCategoryDetailAdapter.OnFavoriteListener() {
            @Override
            public void onFavoriteChange(ImageView ivStatus) {
                if (user.isSkipUser()) {
                    Toast.makeText(SubCategoryDetailActivity.this, getString(R.string.msg_need_login), Toast.LENGTH_SHORT).show();
                } else {
                    int status = subCategory.getIsFavorite() == 1 ? 0 : 1;
                    subCategory.setIsFavorite(status);
                    ivStatus.setSelected(status == 1);
                    setFavorite(subCategory);
                }
            }

            @Override
            public void onTeacherClick() {
                startActivity(new Intent(SubCategoryDetailActivity.this, TeacherProfileCardActivity.class).putExtra(AppConstants.KEY_SEND.KEY_DATA, mTeacher));
            }
        });

        Paginate.with(lvSubcategory, this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        if (subCategoryList.size() > 0) {
            subCategoryList.clear();
            adapter.notifyDataSetChanged();
        } else
            onLoadMore();
    }

    @Override
    public void onLoadMore() {
        if (isTagSearch)
            searchTag(tag);
        else
            getData(subCategory.getId());
    }


    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendBroadcast(new Intent(AppConstants.BROAD_CAST.REFRESH));
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == 0;
    }

    private void getData(int id) {
        isLoading = true;
        final HashMap<String, Object> params = new HashMap<>();
        params.put("seminarid", id);
        params.put("page", page);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.LIST_SEMINAR_TOP.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());
                                ///List
                                JSONArray list = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                //Top
                                if (!isTagSearch) {
                                    JSONObject top = data.getJSONObject("top");
                                    JSONObject teacher = top.getJSONObject(AppConstants.KEY_PARAMS.TEACHER.toString());
                                    if (teacher.length() > 0) {
                                        mTeacher = new User();
                                        mTeacher = User.parse(teacher);
                                    }
                                    adapter.setDataHeader(top.optString("image"), top.optInt("is_favorite"),
                                            top.optString(KeyParser.KEY.DESCRIPTION.toString()), mTeacher, list.length() > 0);

                                }
                                if (list.length() > 0) {
                                    for (int i = 0; i < list.length(); i++) {
                                        JSONObject item = list.getJSONObject(i);
                                        subCategoryList.add(VideoDetail.parse(item));
                                    }
                                    lvSubcategory.getRecycledViewPool().clear();
                                    adapter.notifyDataSetChanged();
                                }

                                isLoading = false;
                                page = data.getInt("next_page");

                            } else if (status == 459) {
                                HSSDialog.show(activity, getString(R.string.msg_book_not_exist), "OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onBackPressed();
                                    }
                                });
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
                                goMaintainScreen(activity, mess);
                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                                sessionExpire();
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
                NetworkUtils.showDialogError(SubCategoryDetailActivity.this, error);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void setFavorite(SubCategory subCategory) {
        LoadingDialog.getDialog(SubCategoryDetailActivity.this).show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", subCategory.getId());
        params.put("status", subCategory.getIsFavorite());
        params.put("page", page);
        JsonObjectRequest request = new JsonObjectRequest(AppConstants.SERVER_PATH.FAVORITE_SUBCATEGORY.toString(), new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialog.getDialog(SubCategoryDetailActivity.this).dismiss();
                        sendBroadcast(new Intent(AppConstants.BROAD_CAST.REFRESH));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingDialog.getDialog(SubCategoryDetailActivity.this).dismiss();
                        NetworkUtils.showDialogError(SubCategoryDetailActivity.this, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void searchTag(String tag) {
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", tag);
        params.put("page", page);
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
                            } else {
                                HSSDialog.show(activity, getString(R.string.msg_session_expire), "Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(activity, LoginActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    }
                                });
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
     *
     * @param showBottom
     */
    private void showCopyRight(boolean showBottom) {
        mTvCopyRightBottom.setVisibility(showBottom ? View.VISIBLE : View.GONE);
        mTvCopyRight.setVisibility(showBottom ? View.GONE : View.VISIBLE);
    }

}