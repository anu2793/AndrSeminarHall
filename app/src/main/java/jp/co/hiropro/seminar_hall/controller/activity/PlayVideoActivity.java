package jp.co.hiropro.seminar_hall.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.SpeedObject;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.ExoPlayerVideoHandler;
import jp.co.hiropro.seminar_hall.util.HSSPreference;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.SpeedAdapter;
import jp.co.hiropro.utils.NetworkUtils;

public class PlayVideoActivity extends AppCompatActivity implements ExoPlayer.EventListener {
    @BindView(R.id.imv_thumb_video)
    ImageView ivThumb;
    @BindView(R.id.mediaplayer)
    SimpleExoPlayerView playerView;
    @BindView(R.id.progressbar)
    ProgressBar loadingView;
    @BindView(R.id.exo_play)
    ImageView ivPlay;
    @BindView(R.id.exo_pause)
    ImageView ivPause;
    @BindView(R.id.iv_replay)
    ImageView ivReplay;
    @BindView(R.id.tv_title)
    TextViewApp tvTitle;
    @BindView(R.id.btn_back)
    ImageView ivBack;
    @BindView(R.id.rcy_speed)
    RecyclerView mRecySpeed;
    @BindView(R.id.rl_speed)
    LinearLayout mRlSpeed;
    @BindView(R.id.ll_video_menu)
    LinearLayout Main_menu;
    @BindView(R.id.imv_volume)
    ImageView mImvVolume;
    @BindView(R.id.seek_volume)
    VerticalSeekBar mSeekVolume;
    @BindView(R.id.volume)
    VerticalSeekBarWrapper mViewVolume;
    @BindView(R.id.imv_control)
    ImageView mImvControls;
    RelativeLayout layout_doublespeed;
    private SpeedAdapter mAdapter;
    private ArrayList<SpeedObject> mListSpeed = new ArrayList<>();
    private ArrayList<String> mListMenu = new ArrayList<>();
    public int currentPos = 0;
    private boolean isVideoDeleted;
    private boolean isPlayVideo = true;
    private Boolean isMultypleType = false;
    private AudioManager mAudioManager;
    private Timer mTimer = new Timer();
    private User mUser;
    private double mSpeed = 1;
    public int albumId = 0;
    private int teachalbumId = 0;
    private Boolean isUserTestFlagMode = false;
    private boolean statusVideo = false;
    private boolean mIsVideoRun = false;
    private boolean isMute = false;
    VideoDetail item;
    public int positionTemp = 0;
    List<VideoDetail> videoDetailList = new ArrayList<>();
    List<VideoDetail> videoDetailListReceive = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        // layout_doublespeed= (RelativeLayout) findViewById(R.id.layout_doublespeed);
        ButterKnife.bind(this);
        albumId = getIntent().getIntExtra(AppConstants.KEY_SEND.KEY_ALBUM_ID, 0);
        teachalbumId = getIntent().getIntExtra(AppConstants.KEY_SEND.KEY_TEACH_ALBUM_ID, 0);
        item = getIntent().getParcelableExtra(KeyParser.KEY.DATA.toString());
        tvTitle.setText(item.getTitle());
        isMultypleType = getIntent().getBooleanExtra(AppConstants.KEY_SEND.KEY_MULTYPLE_TYPE, false);
        isUserTestFlagMode = getIntent().getBooleanExtra(AppConstants.KEY_SEND.KEY_USER_TEST, false);
        videoDetailListReceive = getIntent().getParcelableArrayListExtra(KeyParser.KEY.ALBUM.toString());

        Uri mp4VideoUri = Uri.parse(item.getVideo());
        ExoPlayerVideoHandler.getInstance()
                .prepareExoPlayerForUri(this,
                        mp4VideoUri, playerView);
        ExoPlayerVideoHandler.getInstance().getPlayer().setPlayWhenReady(true);
        ExoPlayerVideoHandler.getInstance().getPlayer().addListener(this);

        //GlideApp.with(PlayVideoActivity.this).load(item.getImage()).into(ivThumb);
        setViewVideo(item.getId());
        //Update list video on search result
        sendBroadcast(new Intent(AppConstants.BROAD_CAST.REFRESH));
        initListSpeed();
     //   menuList();

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mSeekVolume.setMax(Objects.requireNonNull(mAudioManager).getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mSeekVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        mSeekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mSeekVolume.setProgress(progress);
                    mImvVolume.setImageResource(progress == 0 ? R.drawable.sound_of : R.drawable.sound_on);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    createTimerHiddenVolume();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (null != playerView)
            playerView.hideController();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                updateSeekBar();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                updateSeekBar();
                return true;
            default:
                // return false;
                // Update based on @Rene comment below:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void initListSpeed() {
        mRecySpeed.setLayoutManager(new LinearLayoutManager(this));
        mRecySpeed.addOnItemTouchListener(
                new RecyclerItemClickListener(PlayVideoActivity.this, mRecySpeed, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        SpeedObject speed = mListSpeed.get(mRecySpeed.getChildAdapterPosition(view));
                        ExoPlayerVideoHandler.getInstance().getPlayer().setPlayWhenReady(isPlayVideo);
                        PlaybackParameters playbackParameters = ExoPlayerVideoHandler.getInstance().getPlayer().getPlaybackParameters();
                        PlaybackParameters params = new PlaybackParameters((float) Objects.requireNonNull(speed).getValues(), playbackParameters.pitch);
                        ExoPlayerVideoHandler.getInstance().getPlayer().setPlaybackParameters(params);
                        ExoPlayerVideoHandler.getInstance().getPlayer().getPlaybackState();
                        mAdapter.setIndexChoice(position);
                       /* if(position==5){*//*
                        mRlSpeed.setVisibility(View.GONE);
                       // }*/
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        // 0.5
        mListSpeed.add(new SpeedObject(7, "0.5 倍速", 0.5));
        //0.75
        mListSpeed.add(new SpeedObject(6, "0.75 倍速", 0.75));
        // 0
        mListSpeed.add(new SpeedObject(4, getString(R.string.spn_speed_normal), 1));
        // 1.5
        mListSpeed.add(new SpeedObject(8, "1.5 倍速", 1.5));
        // 2
        mListSpeed.add(new SpeedObject(9, "2 倍速", 2));

        mAdapter = new SpeedAdapter(PlayVideoActivity.this, mListSpeed);
        mRecySpeed.setAdapter(mAdapter);
    }

    private void menuList(){
        mRecySpeed.setLayoutManager(new LinearLayoutManager(this));
        mRecySpeed.addOnItemTouchListener(
                new RecyclerItemClickListener(PlayVideoActivity.this, mRecySpeed, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        mListMenu.add(0,"Video Play");
        mListMenu.add(1,"Close");
    }


    @OnClick({R.id.btn_back, R.id.speedlist_close, R.id.mainlist_close, R.id.imv_Mainmenu, R.id.imv_speedList,R.id.imv_skip_ahead, R.id.imv_skip_back, R.id.rl_speed, R.id.rl_top, R.id.iv_replay, R.id.imv_control, R.id.imv_volume, R.id.rl_control})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.rl_top:
                break;
            case R.id.rl_speed:
               // mRlSpeed.setVisibility(View.GONE);
                break;
            case R.id.speedlist_close:
                mRlSpeed.setVisibility(View.GONE);
                break;
            case R.id.imv_speedList:
                mRlSpeed.setVisibility(View.VISIBLE);
                Main_menu.setVisibility(View.GONE);
                break;
            case R.id.mainlist_close:
                Main_menu.setVisibility(View.GONE);
                break;
            case R.id.imv_Mainmenu:
                Main_menu.setVisibility(View.VISIBLE);
                break;
            case R.id.imv_skip_ahead:
                skipForSecondsAhead();
                break;
            case R.id.imv_skip_back:
                skipForSecondsBack();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.iv_replay:
                // ivThumb.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.VISIBLE);
                ivReplay.setVisibility(View.GONE);
                ExoPlayerVideoHandler.getInstance().getPlayer().seekTo(1);
                ExoPlayerVideoHandler.getInstance().getPlayer().setPlayWhenReady(true);
                break;
            case R.id.imv_volume:
//                mImvVolume.setImageResource(isMute ? R.drawable.ic_volume_on : R.drawable.ic_volume_off);
                mViewVolume.setVisibility(isMute ? View.VISIBLE : View.GONE);
                if (isMute)
                    createTimerHiddenVolume();
                isMute = !isMute;
//                ExoPlayerVideoHandler.getInstance().setMute(isMute);
                break;
            case R.id.imv_control:
//                mImvControls.setImageResource(isPlayVideo ? R.drawable.ic_play : R.drawable.ic_pause);
                if (mIsVideoRun) {
                    stopPlayer();
                    mIsVideoRun = false;
                } else {
                    runPlayer();
                    mIsVideoRun = true;
                }
                break;
            case R.id.rl_control:
                // statusvVideo = true : Video pause.
                if (statusVideo) {
                    if (null != playerView) {
                        if (playerView.getControllerHideOnTouch())
                            playerView.hideController();
                        else playerView.showController();
                    }
                }
                break;

        }
    }

    private void runPlayer() {
        ExoPlayerVideoHandler.getInstance().getPlayer().setPlayWhenReady(false);
        ExoPlayerVideoHandler.getInstance().getPlayer().getPlaybackState();
    }

    private void stopPlayer() {
        ExoPlayerVideoHandler.getInstance().getPlayer().setPlayWhenReady(true);
        ExoPlayerVideoHandler.getInstance().getPlayer().getPlaybackState();
    }

    private void updateSeekBar() {
        int volume = Objects.requireNonNull(mAudioManager).getStreamVolume(AudioManager.STREAM_MUSIC);
        mSeekVolume.setProgress(volume);
        mImvVolume.setImageResource(volume == 0 ? R.drawable.sound_of : R.drawable.sound_on);
    }
    @Override
    protected void onDestroy() {
        ExoPlayerVideoHandler.getInstance().releaseVideoPlayer();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KeyParser.KEY.DATA.toString(), isVideoDeleted);
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        if (isLoading) {
            ivPlay.setVisibility(View.GONE);
            ivPause.setVisibility(View.GONE);
        }
    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_BUFFERING) {
            loadingView.setVisibility(View.VISIBLE);
        } else if (playbackState == ExoPlayer.STATE_READY) {
            mImvControls.setVisibility(View.VISIBLE);
            mImvControls.setImageResource(playWhenReady ? R.drawable.pauseicon : R.drawable.playicon);
            loadingView.setVisibility(View.GONE);
            isPlayVideo = true;
        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            ivPlay.setVisibility(View.GONE);
            ivPause.setVisibility(View.GONE);
            ivReplay.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            mIsVideoRun = false;
            isPlayVideo = false;
        } else if (playbackState == ExoPlayer.STATE_IDLE) {
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        loadingView.setVisibility(View.GONE);
        isVideoDeleted = true;
        HSSDialog.show(PlayVideoActivity.this, getString(R.string.msg_video_not_exist), "OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HSSDialog.dismissDialog();
                onBackPressed();

            }
        });
    }

    @Override
    protected void onPause() {
        pausePlayer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startPlayer();
        super.onResume();
    }

    private void pausePlayer() {
        isPlayVideo = ExoPlayerVideoHandler.getInstance().getPlayer().getPlayWhenReady();
        ExoPlayerVideoHandler.getInstance().getPlayer().setPlayWhenReady(false);
        ExoPlayerVideoHandler.getInstance().getPlayer().getPlaybackState();
    }

    private void startPlayer() {
        ExoPlayerVideoHandler.getInstance().getPlayer().setPlayWhenReady(isPlayVideo);
        ExoPlayerVideoHandler.getInstance().getPlayer().getPlaybackState();
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    private void skipForSecondsAhead() {
         long getCurrentPosition = ExoPlayerVideoHandler.getInstance().getPlayer().getCurrentPosition();
         long getDuration = ExoPlayerVideoHandler.getInstance().getPlayer().getDuration();
            if (getCurrentPosition + 15000 >= getDuration) {
                ExoPlayerVideoHandler.getInstance().getPlayer().seekTo(getDuration);
            } else {
                ExoPlayerVideoHandler.getInstance().getPlayer().seekTo(getCurrentPosition + 15000);

            }
        }

    private void skipForSecondsBack() {
        long getCurrentPosition = ExoPlayerVideoHandler.getInstance().getPlayer().getCurrentPosition();
        System.out.println("currentPosition"+getCurrentPosition);
        long getDuration = ExoPlayerVideoHandler.getInstance().getPlayer().getDuration();
        System.out.println("getDuration"+getDuration);
        if (getCurrentPosition - 15000 >= getDuration) {
            ExoPlayerVideoHandler.getInstance().getPlayer().seekTo(getDuration);
        } else if (getCurrentPosition - 15000 > 0) {
            ExoPlayerVideoHandler.getInstance().getPlayer().seekTo(getCurrentPosition - 15000);
        }
    }
/*
    public VideoDetail getPreItemVideo() {
        int step = 1;
        if (currentPos - step >= 0) {
            currentPos = currentPos - step;
            imvNext.setVisibility(View.VISIBLE);
            if (currentPos == 0) {
                imvPrev.setVisibility(View.GONE);
            }
            VideoDetail videoDetail = videoDetailList.get(currentPos);
            return videoDetail;
        } else {
            currentPos = positionTemp;
            return null;
        }
    }

    private VideoDetail getNextItemVideo() {
        int step = 1;
        if (currentPos + step < videoDetailList.size()) {
            currentPos += step;
            imvPrev.setVisibility(View.VISIBLE);
            if (currentPos == videoDetailList.size() - 1) {
                imvNext.setVisibility(View.GONE);
            }
            VideoDetail videoDetail = videoDetailList.get(currentPos);
            return videoDetail;
        } else {
            currentPos = positionTemp;
            return null;
        }
    }*/

    private void setViewVideo(int id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        JsonObjectRequest request = new JsonObjectRequest(AppConstants.SERVER_PATH.VIEWED_VIDEO.toString(), new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkUtils.showDialogError(PlayVideoActivity.this, error);
                    }
                });
        HashMap<String, String> header = new HashMap<>();
        String auth = HSSPreference.getInstance().getString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), "");
        header.put("Authorization", auth);
        request.setHeaders(header);
        ForestApplication.getInstance().addToRequestQueue(request);
    }


    protected HashMap<String, String> getAuthHeader() {
        HashMap<String, String> header = new HashMap<>();
        String auth = HSSPreference.getInstance().getString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), "");
        header.put("Authorization", auth);
        return header;
    }

    private void createTimerHiddenVolume() {
        if (mTimer != null) mTimer.cancel();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mViewVolume.setVisibility(View.GONE);
                    }
                });
            }
        }, 3000);
    }

    protected void callApiVideoPlay(VideoDetail videoDetail) {
        if (!isUserTestFlagMode) {
            String memberPremium = "0";
            if (mUser.isPremiumUser()) {
                memberPremium = "1";
            }
            Map<String, String> params = new HashMap<>();
            params.put(AppConstants.KEY_PARAMS.MEMBER_ID.toString(), String.valueOf(mUser.getUserId()));
            params.put(AppConstants.KEY_PARAMS.VIDEO_ID.toString(), String.valueOf(videoDetail.getId()));
            params.put(AppConstants.KEY_PARAMS.TEACHER_ID.toString(), teachalbumId + "");
            params.put(AppConstants.KEY_PARAMS.MEMBERPREMIUMFLG.toString(), memberPremium);
            params.put(AppConstants.KEY_PARAMS.ALBUM_ID.toString(), albumId + "");

            JSONObject parameters = new JSONObject(params);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.VIDEOSHOW_ALBUM.toString(), parameters,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkUtils.showDialogError(PlayVideoActivity.this, error);
                }
            });
            request.setHeaders(getAuthHeader());
            ForestApplication.getInstance().addToRequestQueue(request);
        }
    }

}
