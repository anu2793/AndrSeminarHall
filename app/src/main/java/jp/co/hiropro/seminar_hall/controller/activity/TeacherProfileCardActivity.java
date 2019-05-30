package jp.co.hiropro.seminar_hall.controller.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.ButtonApp;
import jp.co.hiropro.seminar_hall.view.CircleImageView;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.ContentDetailHorizontalAdapter;
import jp.co.hiropro.seminar_hall.view.adapter.ProfileContactAdapter;
import jp.co.hiropro.seminar_hall.view.popupview.TooltipWindow;

/**
 * Created by dinhdv on 1/23/2018.
 */

public class TeacherProfileCardActivity extends BaseYoutubeActivity {
    @BindView(R.id.rcy_seminars)
    RecyclerView mRcySeminar;
    @BindView(R.id.imv_avatar)
    CircleImageView mAvatar;
    @BindView(R.id.tv_name)
    TextViewApp mTvName;
    @BindView(R.id.tv_tag_teacher)
    TextViewApp mTvTagTeacher;
    @BindView(R.id.imv_edit)
    ImageView mImvEdit;
    @BindView(R.id.tv_description)
    TextViewApp mTvDescription;
    @BindView(R.id.imv_youtube)
    ImageView mImvYoutube;
    @BindView(R.id.imv_google_plus)
    ImageView mImvGoogle;
    @BindView(R.id.imv_facebook)
    ImageView mImvFacebook;
    @BindView(R.id.imv_twitter)
    ImageView mImvTwitter;
    @BindView(R.id.imv_instagram)
    ImageView mImvInstagram;
    @BindView(R.id.rl_free_user)
    RelativeLayout mRlSkipUser;
    @BindView(R.id.btn_create_profile)
    ButtonApp mBtnCreateProfile;
    @BindView(R.id.rcy_contacts)
    RecyclerView mRcyContacts;
    @BindView(R.id.ll_seminar_list)
    LinearLayout mLlSeminarList;
    @BindView(R.id.ll_list_contact)
    LinearLayout mLlContact;
    @BindView(R.id.tv_empty_contact)
    TextViewApp mTvEmpty;
    @BindView(R.id.ll_social)
    RelativeLayout mLlSocial;
    @BindView(R.id.ll_footer)
    LinearLayout mLlFooter;
    @BindView(R.id.src_main)
    ScrollView mSrcMain;
    @BindView(R.id.tv_first_access)
    TextViewApp mTvFirstAccess;
    @BindView(R.id.tv_point)
    TextViewApp mTvPoint;
    @BindView(R.id.player_view)
    YouTubePlayerView mYoutubePlay;
//    @BindView(R.id.ll_point)
//    LinearLayout mLlPoint;

    private DividerItemDecoration mDivider = null;
    private ContentDetailHorizontalAdapter mAdapter;
    private ArrayList<VideoDetail> mListVideos = new ArrayList<>();
    private User mUser, mCurrentUser;
    private ArrayList<User> mListUser = new ArrayList<>();
    private ProfileContactAdapter mContactAdapter;
    private boolean iShowBack = false;
    private String mIdVideo = "";
    private YouTubePlayer mYouTubePlayer = null;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_profile_card;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen(getString(R.string.title_screen_teacher_profile));
        initListSeminar();
        mAdapter = new ContentDetailHorizontalAdapter(TeacherProfileCardActivity.this, mListVideos, false, false);
        mRcySeminar.setAdapter(mAdapter);
        mContactAdapter = new ProfileContactAdapter(mListUser, TeacherProfileCardActivity.this);
        mRcyContacts.setAdapter(mContactAdapter);
        updateLocation(null);
    }

    private void updateLocation(final Class<?> activity) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (activity == null) {
                            if (location != null) {
                                Log.e("Location", "VAlues :" + "Long : " + location.getLongitude() + "--lat :" + location.getLatitude());
                            }
                        } else {
                            if (null != location && location.getLatitude() > 0) {
                                Log.e("Location", "VAlues :" + "Long : " + location.getLongitude() + "--lat :" + location.getLatitude());
                                Intent intent = new Intent(TeacherProfileCardActivity.this, activity);
                                intent.putExtra(AppConstants.KEY_PARAMS.LATITUDE.toString(), location.getLatitude());
                                intent.putExtra(AppConstants.KEY_PARAMS.LONGITUDE.toString(), location.getLongitude());
                                startActivity(intent);
                            } else {
                                HSSDialog.show(TeacherProfileCardActivity.this, getString(R.string.msg_error_cannot_get_location), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        HSSDialog.dismissDialog();
                                    }
                                }).show();
                            }
                        }
                    }
                });
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        mCurrentUser = User.getInstance().getCurrentUser();

        if (bundle != null && bundle.getParcelable(AppConstants.KEY_SEND.KEY_DATA) != null) {
            iShowBack = true;
            mUser = bundle.getParcelable(AppConstants.KEY_SEND.KEY_DATA);
            mRlSkipUser.setVisibility(View.GONE);
            mSrcMain.setVisibility(View.VISIBLE);
            getData(mUser.getId() != mCurrentUser.getId());
        } else {
            iShowBack = false;
            mUser = User.getInstance().getCurrentUser();
            if (null != mUser && mUser.isSkipUser()) {
                mRlSkipUser.setVisibility(View.VISIBLE);
                mLlFooter.setVisibility(View.GONE);
                btnBack.setVisibility(View.INVISIBLE);
            } else {
                getData(false);
            }
            mRlSkipUser.setVisibility(mUser.isSkipUser() ? View.VISIBLE : View.GONE);
            mSrcMain.setVisibility(mUser.isSkipUser() ? View.GONE : View.VISIBLE);
        }

//        if (!mUser.isSkipUser())
//            getData();

    }

    private void configYouTube(String link) {
        mIdVideo = AppUtils.getIdYouTubeVideo(link);
        if (null != mYouTubePlayer) {
            mYouTubePlayer.cueVideo(mIdVideo);
        } else {
            mYoutubePlay.initialize(getString(R.string.youtube_developer), new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    if (!b) {
                        youTubePlayer.cueVideo(mIdVideo);
                        mYouTubePlayer = youTubePlayer;
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    Log.e("Error", "Values : " + youTubeInitializationResult.toString());
                }
            });
        }
    }

    @OnClick({R.id.tv_teacher_list, R.id.tv_screen_receive, R.id.imv_group_contact, R.id.imv_edit, R.id.tv_choice_type_share,
            R.id.imv_youtube, R.id.imv_google_plus, R.id.imv_facebook, R.id.imv_twitter, R.id.imv_instagram, R.id.btn_login, R.id.btn_register, R.id.btn_create_profile
            , R.id.tv_contact_list
    })
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_teacher_list:
                Intent intent = new Intent(TeacherProfileCardActivity.this, TeacherContentListActivity.class);
                intent.putExtra(AppConstants.KEY_INTENT.ID_USER.toString(), mUser.getId());
                startActivity(intent);
                break;
            case R.id.tv_screen_receive:
                showWindowChoice(mLlFooter, false);
                break;
            case R.id.imv_group_contact:

                break;
            case R.id.imv_edit:
                goEditCreateProfile();
                break;
            case R.id.tv_choice_type_share:
                showWindowChoice(mLlFooter, true);
                break;
            case R.id.imv_youtube:
                showUrlView(mUser.getYoutubeLink());
                break;
            case R.id.imv_google_plus:
                showUrlView(mUser.getGoogleLink());
                break;
            case R.id.imv_facebook:
                showUrlView(mUser.getFacebookLink());
                break;
            case R.id.imv_twitter:
                showUrlView(mUser.getTwitterLink());
                break;
            case R.id.imv_instagram:
                showUrlView(mUser.getInstagramLink());
                break;
            case R.id.btn_login:
                startActivity(new Intent(activity, LoginActivity.class).putExtra(AppConstants.KEY_INTENT.IS_REGISTER_USER.toString(), true));
                break;
            case R.id.btn_register:
                startActivity(new Intent(activity, RegisterActivity.class).putExtra(AppConstants.KEY_INTENT.IS_REGISTER_USER.toString(), true));
                break;
            case R.id.btn_create_profile:
                goEditCreateProfile();
                break;
            case R.id.tv_contact_list:
                startActivity(new Intent(TeacherProfileCardActivity.this, ContactListActivity.class));
                break;
        }
    }

    private void goEditCreateProfile() {
        Intent intent = new Intent(TeacherProfileCardActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    protected void showUrlView(String url) {
        if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + url));
            startActivity(browserIntent);
        }

    }

    private void showWindowChoice(View view, boolean isShowReceive) {
        TooltipWindow tipWindow = new TooltipWindow(TeacherProfileCardActivity.this, isShowReceive);
        tipWindow.setAction(new TooltipWindow.onAction() {
            @Override
            public void onQrCodeChoice() {
                startActivity(new Intent(TeacherProfileCardActivity.this, QrCodePreviewActivity.class));
            }

            @Override
            public void onReceiveScreenChoice() {
                startActivityWithPermission(CardReceiveActivity.class);
            }

            @Override
            public void onSwipeScreenChoice() {
                startActivityWithPermission(SwipeCardActivity.class);
            }

            @Override
            public void onScanQr() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Dexter.withActivity(TeacherProfileCardActivity.this)
                            .withPermission(Manifest.permission.CAMERA)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    startActivity(new Intent(TeacherProfileCardActivity.this, QrCodeScannerActivity.class));
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {

                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            })
                            .onSameThread()
                            .check();
                } else {
                    startActivity(new Intent(TeacherProfileCardActivity.this, QrCodeScannerActivity.class));
                }

            }
        });
        if (!tipWindow.isTooltipShown()) {
            tipWindow.showToolTip(view);
        }
    }

    private void startActivityWithPermission(final Class<?> activity) {
        if (AppUtils.checkEnableGPS(TeacherProfileCardActivity.this)) {
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                Dexter.withActivity(TeacherProfileCardActivity.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        updateLocation(activity);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        showDialogEnablePermissionGPS();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
            } else {
                updateLocation(activity);
            }
        } else {
            showDialogEnableGPS();
        }

    }

    private void showDialogEnablePermissionGPS() {
        HSSDialog.show(TeacherProfileCardActivity.this, getString(R.string.msg_need_enable_permssion_gps), getString(R.string.txt_turn_on_gps), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HSSDialog.dismissDialog();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, getString(R.string.txt_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HSSDialog.dismissDialog();
            }
        }).show();
    }

    private void showDialogEnableGPS() {
        HSSDialog.show(TeacherProfileCardActivity.this, getString(R.string.msg_need_enable_gps), getString(R.string.txt_turn_on_gps), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HSSDialog.dismissDialog();
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }, getString(R.string.txt_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HSSDialog.dismissDialog();
            }
        }).show();
    }

    /**
     * Enable social.
     *
     * @param isEnableFacebook
     * @param isEnableTwitter
     * @param isEnableInstagram
     * @param isEnableYoutube
     * @param isEnableGoogle
     */
    public void setEnableSocial(boolean isEnableFacebook, boolean isEnableTwitter, boolean isEnableInstagram, boolean isEnableYoutube, boolean isEnableGoogle) {
        //Facebook
        mImvFacebook.setEnabled(isEnableFacebook);
        mImvFacebook.setImageResource(isEnableFacebook ? R.mipmap.ic_facebook : R.mipmap.ic_facebook_disable);
        //Twitter.
        mImvTwitter.setEnabled(isEnableTwitter);
        mImvTwitter.setImageResource(isEnableTwitter ? R.mipmap.ic_twitter : R.mipmap.ic_twitter_disable);
        //Instagram.
        mImvInstagram.setEnabled(isEnableInstagram);
        mImvInstagram.setImageResource(isEnableInstagram ? R.mipmap.ic_instagram : R.mipmap.ic_instagram_disable);
        //Youtube.
        mImvYoutube.setEnabled(isEnableYoutube);
        mImvYoutube.setImageResource(isEnableYoutube ? R.mipmap.ic_youtube : R.mipmap.ic_youtube_disable);
        //Google plus.
        mImvGoogle.setEnabled(isEnableGoogle);
        mImvGoogle.setImageResource(isEnableGoogle ? R.mipmap.ic_google_plus : R.mipmap.ic_google_plus_disable);

    }

    private void getData(final boolean otherUser) {
        showLoading();
        HashMap<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.ID.toString(), String.valueOf(mUser.getId()));
        RequestDataUtils.requestDataWithAuthen(Request.Method.GET, TeacherProfileCardActivity.this, AppConstants.SERVER_PATH.GET_MY_PROFILE.toString(), params, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject object, String msg) {
                try {
                    JSONObject infoData = object.getJSONObject(AppConstants.KEY_PARAMS.INFO.toString());
                    if (infoData.length() > 0) {
                        User info = User.parse(infoData);
                        mUser = info;
                        mTvPoint.setText(Utils.formatNormalPrice(info.getPoint()));
//                        mLlPoint.setVisibility(otherUser ? View.GONE : View.VISIBLE);
                        if (!otherUser)
                            User.getInstance().setCurrentUser(info);
                        boolean isCreate = object.optBoolean(AppConstants.KEY_PARAMS.IS_CREATE.toString(), false);
                        loadDataToView(info, isCreate, otherUser);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray contactList = object.getJSONArray(AppConstants.KEY_PARAMS.CONTACT_LIST.toString());
                    mListUser.clear();
                    if (contactList.length() > 0) {
                        for (int i = 0; i < contactList.length(); i++) {
                            User user = User.parseContact(contactList.getJSONObject(i));
                            mListUser.add(user);
                        }
                    }
                    mContactAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mTvEmpty.setVisibility(mListUser.size() > 0 ? View.GONE : View.VISIBLE);
                mRcyContacts.setVisibility(mListUser.size() > 0 ? View.VISIBLE : View.VISIBLE);
                try {
                    JSONArray contactList = object.getJSONArray(AppConstants.KEY_PARAMS.CONTENT_LIST.toString());
                    mListVideos.clear();
                    if (contactList.length() > 0) {
                        for (int i = 0; i < contactList.length(); i++) {
                            VideoDetail video = VideoDetail.parse(contactList.getJSONObject(i));
                            mListVideos.add(video);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (otherUser) {
                    if (mUser.getId() != User.getInstance().getCurrentUser().getId())
                        mLlContact.setVisibility(View.GONE);
                }
                mLlSeminarList.setVisibility(mListVideos.size() > 0 ? View.VISIBLE : View.GONE);
                dismissLoading();
                mSrcMain.smoothScrollBy(0, 0);
            }

            @Override
            public void onFail(int error) {
                dismissLoading();
            }
        });
    }

    private void loadDataToView(User info, boolean isCreate, boolean otherUser) {
        if (info.getAvatar().length() > 0) {
            Glide.with(this).asBitmap().load(info.getAvatar()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    mAvatar.setImageBitmap(resource);
                }
            });
        } else {
            Utils.initImageView("", mAvatar, this);
        }
        mTvName.setText(info.getFullname().length() > 0 ? info.getFullname() : getString(R.string.title_screen_teacher_profile));
        mTvTagTeacher.setVisibility(info.getRoleUser() == AppConstants.TYPE_UER.TEACHER ? View.VISIBLE : View.GONE);

        setEnableSocial(!TextUtils.isEmpty(info.getFacebookLink()), !TextUtils.isEmpty(info.getTwitterLink()), !TextUtils.isEmpty(info.getInstagramLink()), !TextUtils.isEmpty(info.getYoutubeLink()), !TextUtils.isEmpty(info.getGoogleLink()));
        if (otherUser) {
            mBtnCreateProfile.setVisibility(View.GONE);
            mTvFirstAccess.setVisibility(View.GONE);
            mTvDescription.setVisibility(View.VISIBLE);
            mTvDescription.setText(info.getProfile().replace("\u2028", "\n"));
        } else {
            mBtnCreateProfile.setVisibility(isCreate ? View.VISIBLE : View.GONE);
            mTvFirstAccess.setVisibility(isCreate ? View.VISIBLE : View.GONE);
            mTvDescription.setVisibility(isCreate ? View.GONE : View.VISIBLE);
            if (!isCreate) {
                mTvDescription.setText(info.getProfile().replace("\u2028", "\n"));
            }

        }
        mLlFooter.setVisibility(otherUser ? View.GONE : View.VISIBLE);
        mImvEdit.setVisibility(otherUser ? View.GONE : View.VISIBLE);
        mLlSocial.setVisibility(isCreate ? View.GONE : View.VISIBLE);
        setupTitleScreen(otherUser ? (info.getFullname().length() > 0 ? info.getFullname() : getString(R.string.title_screen_teacher_profile)) : getString(R.string.title_screen_teacher_profile));
        btnBack.setVisibility(iShowBack ? View.VISIBLE : View.INVISIBLE);
        mYoutubePlay.setVisibility(info.getIntroductionVideoUrl().length() > 0 ? View.VISIBLE : View.GONE);
        if (info.getIntroductionVideoUrl().length() > 0)
            configYouTube(info.getIntroductionVideoUrl());

    }

    private void initListSeminar() {
        mRcySeminar.setLayoutManager(new LinearLayoutManager(TeacherProfileCardActivity.this, LinearLayout.HORIZONTAL, false));
        mRcyContacts.setLayoutManager(new LinearLayoutManager(TeacherProfileCardActivity.this, LinearLayout.HORIZONTAL, false));
        if (mDivider == null) {
            mDivider = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
            mDivider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider_10dp));
            mRcySeminar.addItemDecoration(mDivider);
            mRcyContacts.addItemDecoration(mDivider);
        }
        mRcySeminar.addOnItemTouchListener(new RecyclerItemClickListener(TeacherProfileCardActivity.this, mRcySeminar, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                VideoDetail item = mListVideos.get(position);
                startActivity(new Intent(TeacherProfileCardActivity.this, ContentDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), item));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        mRcyContacts.addOnItemTouchListener(new RecyclerItemClickListener(TeacherProfileCardActivity.this, mRcyContacts, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                User teacher = mListUser.get(position);
                if (teacher != null)
                    startActivity(new Intent(TeacherProfileCardActivity.this, TeacherProfileCardActivity.class).putExtra(AppConstants.KEY_SEND.KEY_DATA, teacher));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }
}
