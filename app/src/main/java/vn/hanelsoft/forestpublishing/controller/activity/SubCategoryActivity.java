package vn.hanelsoft.forestpublishing.controller.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.hanelsoft.dialog.HSSDialog;
import vn.hanelsoft.dialog.LoadingDialog;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.Category;
import vn.hanelsoft.forestpublishing.model.SubCategory;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.KeyParser;
import vn.hanelsoft.forestpublishing.view.TextViewApp;
import vn.hanelsoft.forestpublishing.view.adapter.SubCategoryAdapter;
import vn.hanelsoft.utils.NetworkUtils;

public class SubCategoryActivity extends BaseActivity {
    @BindView(R.id.lv_subcategory)
    ListView lvSubcategory;

    List<SubCategory> subCategoryList = new ArrayList<>();

    SubCategoryAdapter adapter;
    Category category;
    SubCategory subCategoryIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        category = getIntent().getParcelableExtra(KeyParser.KEY.DATA.toString());
        if (category != null) {
            setupTitleScreen(category.getNameJp());
        } else {
            setupTitleScreen("大カテゴリー名");
        }
        initListSubCategory();
        getData(category.getId());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sub_category;
    }

    TextViewApp tvName, tvDescription;
    ImageView ivBanner;
    ProgressBar loadingView;

    private void initListSubCategory() {
        adapter = new SubCategoryAdapter(this);
        adapter.setVisibleFavoriteImage(false);
        lvSubcategory.setAdapter(adapter);

        int heightOfView = screenSize.x * 712 / 1242;
        View header = LayoutInflater.from(this).inflate(R.layout.header_sub_category_screen, null, false);

        tvName = header.findViewById(R.id.tv_name);
        tvDescription = header.findViewById(R.id.tv_description);
        ivBanner = header.findViewById(R.id.iv_banner_category);
        loadingView = header.findViewById(R.id.loading_view);
        ivBanner.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightOfView));

        lvSubcategory.addHeaderView(header, null, false);
        lvSubcategory.addFooterView(getFooterCopyRight(), null, false);
        adapter.setOnItemClick(new SubCategoryAdapter.OnItemClick() {
            @Override
            public void onItemClick(SubCategory subCategory) {
                startActivity(new Intent(SubCategoryActivity.this, SubSubCategoryActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                SubCategory result = data.getParcelableExtra(KeyParser.KEY.DATA.toString());
                subCategoryIntent.setIsFavorite(result.getIsFavorite());
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getData(int id) {
        LoadingDialog.getDialog(this).show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("cateid", id);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.SUBCATEGORY.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        LoadingDialog.getDialog(SubCategoryActivity.this).dismiss();
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());
                                //Banner
                                JSONObject top = data.getJSONObject("top");
                                tvName.setText(top.optString("title"));
                                tvName.setVisibility(View.GONE);
                                if (TextUtils.isEmpty(top.optString("description")))
                                    tvDescription.setVisibility(View.GONE);
                                else
                                    tvDescription.setText(top.optString("description"));

                                Glide.with(SubCategoryActivity.this).load(top.optString("image")).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        loadingView.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        loadingView.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(ivBanner);
                                //List subcategory
                                JSONArray subcategory = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                if (subcategory.length() > 0) {
                                    for (int i = 0; i < subcategory.length(); i++) {
                                        JSONObject item = subcategory.getJSONObject(i);
                                        subCategoryList.add(SubCategory.parse(item));
                                    }
                                    adapter.addAll(subCategoryList);
                                    adapter.notifyDataSetChanged();
                                }
                            } else if (status == 459) {
                                HSSDialog.show(activity, getString(R.string.msg_category_not_exist), "OK", new View.OnClickListener() {
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
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingDialog.getDialog(SubCategoryActivity.this).dismiss();
                NetworkUtils.showDialogError(SubCategoryActivity.this, error);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }


}
