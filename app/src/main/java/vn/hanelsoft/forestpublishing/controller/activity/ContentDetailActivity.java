package vn.hanelsoft.forestpublishing.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import butterknife.OnClick;
import vn.hanelsoft.dialog.HSSDialog;
import vn.hanelsoft.dialog.LoadingDialog;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.GlideApp;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.model.VideoDetail;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.AppUtils;
import vn.hanelsoft.forestpublishing.util.KeyParser;
import vn.hanelsoft.forestpublishing.util.OnSingleClickListener;
import vn.hanelsoft.forestpublishing.util.RequestDataUtils;
import vn.hanelsoft.forestpublishing.util.Utils;
import vn.hanelsoft.forestpublishing.view.CustomLoadingListItemCreator;
import vn.hanelsoft.forestpublishing.view.TextViewApp;
import vn.hanelsoft.forestpublishing.view.adapter.SubCategoryDetailAdapter;
import vn.hanelsoft.forestpublishing.view.dialog.DialogPurchaseVideo;
import vn.hanelsoft.forestpublishing.view.tagsview.Tag;
import vn.hanelsoft.forestpublishing.view.tagsview.TagView;
import vn.hanelsoft.utils.DateTimeUtils;
import vn.hanelsoft.utils.NetworkUtils;

public class ContentDetailActivity extends BaseActivity implements Paginate.Callbacks {
    private static final int MY_SOCKET_TIMEOUT_MS = 30000;
    public static int UNREGISTER_BUY_VIDEO = 223;
    @BindView(R.id.tag_group)
    TagView tagGroup;
    @BindView(R.id.lv_subcategory_detail)
    RecyclerView lvSubCategoryDetail;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.progressbar)
    ProgressBar loadingView;
    @BindView(R.id.layout_media_player)
    RelativeLayout layoutMediaPlayer;
    @BindView(R.id.tv_special)
    TextViewApp tvSpecial;
    @BindView(R.id.tv_type_of_content)
    TextViewApp tvPrice;
    @BindView(R.id.tv_title)
    TextViewApp tvTitle;
    @BindView(R.id.tv_time)
    TextViewApp tvTime;
    @BindView(R.id.tv_description)
    TextViewApp tvDescription;
    @BindView(R.id.tv_status_premium)
    TextViewApp tvStatusPremium;
    @BindView(R.id.iv_thumb)
    ImageView ivThumb;
    @BindView(R.id.tv_time_duration)
    TextViewApp mTvTimeDuration;
    @BindView(R.id.tv_cate_title)
    TextViewApp mTvCateTitle;
    @BindView(R.id.ic_point)
    ImageView mImvPoint;
    @BindView(R.id.ll_point)
    LinearLayout mLlPoint;
    @BindView(R.id.ll_container)
    LinearLayout mLlContainer;
    @BindView(R.id.tv_copyright)
    TextViewApp mTvCopyRight;
    @BindView(R.id.tv_copyright_bottom)
    TextViewApp mTvCopyRightBottom;
    List<VideoDetail> videoDetailList = new ArrayList<>();
    SubCategoryDetailAdapter adapter;
    VideoDetail item;
    int typeOfVideo;
    private int page = 1;
    private boolean isLoading;
    private boolean isVideoDeleted;
    private User mUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        item = getIntent().getParcelableExtra(KeyParser.KEY.DATA.toString());
        if (item != null) {
            setupTitleScreen(TextUtils.isEmpty(item.getTitle()) ? "詳細" : item.getTitle());
        } else {
            setupTitleScreen("詳細");
        }
        int heightOfView = screenSize.x * 718 / 1242;
        layoutMediaPlayer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightOfView));
        ivPlay.setEnabled(false);
        initListSubCategoryDetail();

        mLlContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Get height here
                        mLlContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int heightScreen = AppUtils.getScreenHigh() - (3 * getResources().getDimensionPixelOffset(R.dimen.value_height_action_bar));
                        showCopyRight(mLlContainer.getHeight() < heightScreen);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUser = User.getInstance().getCurrentUser();
        page = 1;
        videoDetailList.clear();
        onLoadMore();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            boolean isDeletedVideo = data.getBooleanExtra(KeyParser.KEY.DATA.toString(), false);
            if (requestCode == 100 && isDeletedVideo) {
                onBackPressed();
            }
        }
        if (resultCode == ContentDetailActivity.UNREGISTER_BUY_VIDEO) {
            checkValidateUser();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_content_detail;
    }

    @Override
    public void onBackPressed() {
        if (!isLoading) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(KeyParser.KEY.DATA.toString(), isVideoDeleted);
            returnIntent.putExtra("item", item);
            setResult(Activity.RESULT_OK, returnIntent);
            super.onBackPressed();
        }
    }

    @OnClick({R.id.iv_play, R.id.tv_type_of_content, R.id.iv_thumb})
    void click(View view) {
        switch (view.getId()) {
            case R.id.iv_thumb:
//            case R.id.tv_type_of_content:
            case R.id.iv_play:
                /**
                 * Neu la skip user thi chuyen sang man hinh dang ki.
                 * Con neu la user binh thuong thi van nhu phase 2.
                 */
                if (isLoading)
                    return;
                if (item.getType() == VideoDetail.VIDEO_FREE
                        || item.getType() == VideoDetail.VIDEO_LIMITED_FREE
                        || item.getType() == VideoDetail.VIDEO_FREE_TYPE_2
                        || item.getType() == VideoDetail.VIDEO_LIMITED_SPECIAL || item.getHasBoughtVideo() == 1) {
                    startActivityForResult(new Intent(activity, PlayVideoActivity.class)
                            .putExtra(KeyParser.KEY.DATA.toString(), item), 100);
                } else {
                    /**
                     * User not register , skip user go Purchase screen.
                     */
                    if (mUser.isSkipUser()) {
                        Intent intent = new Intent(ContentDetailActivity.this, PurchaseActivity.class);
                        intent.putExtra(AppConstants.KEY_SEND.KEY_VIDEO, item);
                        startActivityForResult(intent, ContentDetailActivity.UNREGISTER_BUY_VIDEO);
                    } else {
                        if (mUser.getId() == item.getIdOwner()) {
                            startActivityForResult(new Intent(activity, PlayVideoActivity.class)
                                    .putExtra(KeyParser.KEY.DATA.toString(), item), 100);
                        } else {
                            checkValidateUser();
                        }
                    }
                }
                break;
        }
    }

    private void checkValidateUser() {
        showLoading();
        HashMap<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.ID.toString(), String.valueOf(mUser.getId()));
        RequestDataUtils.requestDataWithAuthen(Request.Method.GET, ContentDetailActivity.this, AppConstants.SERVER_PATH.GET_MY_PROFILE.toString(), params, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject object, String msg) {
                dismissLoading();
                try {
                    JSONObject infoData = object.getJSONObject(AppConstants.KEY_PARAMS.INFO.toString());
                    if (infoData.length() > 0) {
                        User info = User.parse(infoData);
                        mUser = info;
                        User.getInstance().setCurrentUser(info);
                        if (mUser.getPoint() >= item.getPrice()) {
                            DialogPurchaseVideo.showDialog(ContentDetailActivity.this, getString(R.string.txt_content_point, Utils.formatPrice(item.getPrice())), Utils.formatPrice(mUser.getPoint()), new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    DialogPurchaseVideo.dismissDialog();
                                    purchaseVideo(item.getId());
                                }
                            }).show();
                        } else {
                            DialogPurchaseVideo.showDialog(ContentDetailActivity.this, getString(R.string.title_not_enough_point), Utils.formatPrice(mUser.getPoint()), new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    DialogPurchaseVideo.dismissDialog();
                                    startActivity(new Intent(ContentDetailActivity.this, PointManagerActivity.class).putExtra(AppConstants.KEY_INTENT.SHOW_BACK.toString(), true));
                                }
                            }).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(int error) {
                dismissLoading();
                Toast.makeText(ContentDetailActivity.this, getString(R.string.msg_cannot_buy_video), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoadMore() {
        getData(item.getId());
    }


    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == 0;
    }


    private void initTagView(String tag) {
        try {
            JSONArray arrayTag = new JSONArray(tag);
            if (arrayTag.length() > 0) {
                List<Tag> tagList = new ArrayList<>();
                for (int i = 0; i < arrayTag.length(); i++) {
                    tagList.add(new Tag(arrayTag.getString(i)));

                }
                tagGroup.addTags(tagList);
            }
            tagGroup.setVisibility(arrayTag.length() > 0 ? View.VISIBLE : View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                Intent intent = new Intent(ContentDetailActivity.this, VideoSameTagActivity.class);
                intent.putExtra("isTagSearch", true);
                intent.putExtra("tag", tag.text);
                startActivity(intent);

            }
        });
    }

    private void initListSubCategoryDetail() {
        adapter = new SubCategoryDetailAdapter(this, videoDetailList, false, false);
        lvSubCategoryDetail.setLayoutManager(new LinearLayoutManager(this));
        lvSubCategoryDetail.setAdapter(adapter);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider)));
        lvSubCategoryDetail.addItemDecoration(divider);

        adapter.setOnItemClickListener(new SubCategoryDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoDetail item) {
                startActivity(new Intent(ContentDetailActivity.this, ContentDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), item)
                        .putExtra("type", typeOfVideo));
            }
        });

        Paginate.with(lvSubCategoryDetail, this)
                .setLoadingTriggerThreshold(0)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
    }

    private void bindData(VideoDetail item) {
        setupTitleScreen(item.getTitle());
        User user = User.getInstance().getCurrentUser();
        GlideApp.with(activity).load(item.getImage()).override(621, 359).error(R.mipmap.imv_default_empty).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                loadingView.setVisibility(View.GONE);
                ivThumb.setImageResource(R.mipmap.imv_default_empty);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                loadingView.setVisibility(View.GONE);
                return false;
            }
        }).into(ivThumb);

        if (item.getType() == VideoDetail.VIDEO_SPECIAL) {
            tvSpecial.setVisibility(View.VISIBLE);
            mImvPoint.setVisibility(View.GONE);
            if (item.getHasBoughtVideo() == 1) {
                tvPrice.setText("購入済み");
                tvPrice.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_type_content));
                tvPrice.setTextColor(ContextCompat.getColorStateList(activity, R.color.color_type_content));
            } else {
//                tvPrice.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_price));
                mLlPoint.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_price));
                tvPrice.setTextColor(ContextCompat.getColorStateList(activity, R.color.color_price));
                tvPrice.setText(Utils.formatPrice(item.getPrice()));
                mImvPoint.setVisibility(View.VISIBLE);
            }

        } else if (item.getType() == VideoDetail.VIDEO_LIMITED_SPECIAL) {
            tvSpecial.setVisibility(View.VISIBLE);
            if (item.getHasBoughtVideo() == 1) {
                tvPrice.setText("購入済み");
//                tvPrice.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_type_content));
                mLlPoint.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_type_content));
                tvPrice.setTextColor(ContextCompat.getColorStateList(activity, R.color.color_type_content));
            } else {
                tvStatusPremium.setVisibility(View.VISIBLE);
                tvStatusPremium.setBackgroundColor(Color.parseColor("#f97e59"));
                tvStatusPremium.setText("期間限定");
//                tvPrice.setSelected(true);
                mLlPoint.setSelected(true);
                tvPrice.setText(getString(R.string.txt_free));
            }
        } else if (item.getType() == VideoDetail.VIDEO_PAID) {
            mImvPoint.setVisibility(View.GONE);
            if (item.getHasBoughtVideo() == 1) tvPrice.setText("購入済み");
            else {
                if (user.isPremiumUser()) {
                    tvPrice.setVisibility(View.VISIBLE);
                    tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvStatusPremium.setVisibility(View.VISIBLE);
                    tvPrice.setText(Utils.formatPrice(item.getPrice()));
//                    tvPrice.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_price));
                    tvPrice.setTextColor(ContextCompat.getColorStateList(activity, R.color.color_price));
                } else {
//                    tvPrice.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_price));
                    tvPrice.setTextColor(ContextCompat.getColorStateList(activity, R.color.color_price));
                    tvPrice.setText(Utils.formatPrice(item.getPrice()));
                }
                mLlPoint.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_price));
                mImvPoint.setVisibility(View.VISIBLE);
            }

        } else if (item.getType() == VideoDetail.VIDEO_FREE || item.getType() == VideoDetail.VIDEO_FREE_TYPE_2) {
            if (item.getHasBoughtVideo() == 1) {
                tvPrice.setText("購入済み");
            } else {
                tvPrice.setText(getString(R.string.txt_free));
//                tvPrice.setSelected(true);
                mLlPoint.setSelected(true);
            }
        } else if (item.getType() == VideoDetail.VIDEO_LIMITED_FREE) {
            mImvPoint.setVisibility(View.GONE);
            if (user.isPremiumUser()) {
                tvStatusPremium.setVisibility(View.VISIBLE);
                tvStatusPremium.setBackgroundColor(Color.parseColor("#686868"));
                tvStatusPremium.setText("会員無料");
                tvPrice.setText(Utils.formatPrice(item.getPrice()));
//                tvPrice.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_price));
                mLlPoint.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_price));
                tvPrice.setTextColor(ContextCompat.getColorStateList(activity, R.color.color_price));
                tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mImvPoint.setVisibility(View.VISIBLE);
            } else {
                if (item.getHasBoughtVideo() == 1) {
                    tvPrice.setText("購入済み");
                } else {
                    tvPrice.setText(getString(R.string.txt_free));
//                    tvPrice.setSelected(true);
                    mLlPoint.setSelected(true);
                    tvStatusPremium.setVisibility(View.VISIBLE);
                    tvStatusPremium.setBackgroundColor(Color.parseColor("#f97e59"));
                    tvStatusPremium.setText("期間限定");
                }
            }
        }
        tvTitle.setText(item.getTitle());
        tvDescription.setText(item.getDescription());
        tvTime.setText(DateTimeUtils.format(item.getDate(), "yyyy-MM-dd hh:mm:ss", "yyyy/MM/dd") + " に公開");
        mTvTimeDuration.setText(item.getDuration().length() > 0 ? item.getDuration() : "00:00:00");
    }

    private void getData(int id) {
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("page", page);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.CONTENT_DETAIL.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.optInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                ivPlay.setEnabled(true);
                                isVideoDeleted = false;
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());

                                ///Video
                                if (page == 1) {
                                    item = VideoDetail.parse(data.getJSONObject("video"));
                                    bindData(item);
                                }
                                //ListVideo
                                JSONArray listVideo = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                videoDetailList.clear();
                                if (listVideo.length() > 0) {
                                    for (int i = 0; i < listVideo.length(); i++) {
                                        videoDetailList.add(VideoDetail.parse(listVideo.getJSONObject(i)));
                                    }
                                }
                                lvSubCategoryDetail.getRecycledViewPool().clear();
                                adapter.notifyDataSetChanged();
                                mTvCateTitle.setVisibility(videoDetailList.size() > 0 ? View.VISIBLE : View.GONE);
                                ///Tags
                                String tag = data.getJSONObject("video").optString("tag");
                                initTagView(tag);

                                //Premium Info
//                                JSONObject premiumInfo = data.getJSONObject("premium_info");
//                                mIdPurchase = premiumInfo.optInt(KeyParser.KEY.ID.toString());
//                                premiumCode = premiumInfo.optString("code");

                                page = data.getInt("next_page");
                                isLoading = false;
                            } else if (status == 459) {
                                isVideoDeleted = true;
                                isLoading = false;
                                HSSDialog.show(activity, getString(R.string.msg_video_not_exist), "OK", new View.OnClickListener() {
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
                            page = 0;
                            isLoading = false;
                            adapter.notifyDataSetChanged();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkUtils.showDialogError(ContentDetailActivity.this, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void purchaseVideo(int videoId) {
        LoadingDialog.getDialog(this).show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("videoid", videoId);
        JsonObjectRequest request = new JsonObjectRequest(AppConstants.SERVER_PATH.PURCHASE_VIDEO.toString(), new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            LoadingDialog.getDialog(activity).dismiss();
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                tvPrice.setText("購入済み");
                                mImvPoint.setVisibility(View.GONE);
                                item.setHasBoughtVideo(1);
                                sendBroadcast(new Intent(AppConstants.BROAD_CAST.REFRESH));
                                startActivityForResult(new Intent(activity, PlayVideoActivity.class)
                                        .putExtra(KeyParser.KEY.DATA.toString(), item), 100);
                            } else if (status == AppConstants.STATUS_REQUEST.NOT_ENOUGH_POINT) {
                                HSSDialog.show(ContentDetailActivity.this, getString(R.string.msg_not_enough_point)).show();
                            } else if (status == AppConstants.STATUS_REQUEST.VIDEO_HAS_BUY) {
                                HSSDialog.show(ContentDetailActivity.this, getString(R.string.msg_video_has_buy), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        HSSDialog.dismissDialog();
                                        // Refresh view.
                                        page = 1;
                                        getData(item.getId());
                                        sendBroadcast(new Intent(AppConstants.BROAD_CAST.REFRESH));
                                    }
                                }).show();
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
                                goMaintainScreen(activity, mess);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
