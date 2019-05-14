package vn.hanelsoft.forestpublishing.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.hanelsoft.dialog.HSSDialog;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.SpeedObject;
import vn.hanelsoft.forestpublishing.model.VideoDetail;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.ExoPlayerVideoHandler;
import vn.hanelsoft.forestpublishing.util.HSSPreference;
import vn.hanelsoft.forestpublishing.util.KeyParser;
import vn.hanelsoft.forestpublishing.util.RecyclerItemClickListener;
import vn.hanelsoft.forestpublishing.view.TextViewApp;
import vn.hanelsoft.forestpublishing.view.adapter.SpeedAdapter;
import vn.hanelsoft.utils.NetworkUtils;

public class PlayVideoActivity extends AppCompatActivity implements ExoPlayer.EventListener {
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
    private SpeedAdapter mAdapter;
    private ArrayList<SpeedObject> mListSpeed = new ArrayList<>();
    private boolean isVideoDeleted;
    private boolean isPlayVideo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        ButterKnife.bind(this);

        VideoDetail item = getIntent().getParcelableExtra(KeyParser.KEY.DATA.toString());
        tvTitle.setText(item.getTitle());

        Uri mp4VideoUri = Uri.parse(item.getVideo());
        ExoPlayerVideoHandler.getInstance()
                .prepareExoPlayerForUri(this,
                        mp4VideoUri, playerView);
        ExoPlayerVideoHandler.getInstance().getPlayer().setPlayWhenReady(true);
        ExoPlayerVideoHandler.getInstance().getPlayer().addListener(this);
//        GlideApp.with(PlayVideoActivity.this).load(item.getImage()).into(ivThumb);
        setViewVideo(item.getId());
        //Update list video on search result
        sendBroadcast(new Intent(AppConstants.BROAD_CAST.REFRESH));
        initListSpeed();
        playerView.hideController();
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
                        mRlSpeed.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        // 0
        mListSpeed.add(new SpeedObject(4, getString(R.string.spn_speed_normal), 1));
        // 1.25
        mListSpeed.add(new SpeedObject(5, "1.25 倍速", 1.25));
        // 1.5
        mListSpeed.add(new SpeedObject(6, "1.5 倍速", 1.5));
        // 2
        mListSpeed.add(new SpeedObject(7, "2 倍速", 2));
        mAdapter = new SpeedAdapter(PlayVideoActivity.this, mListSpeed);
        mRecySpeed.setAdapter(mAdapter);
    }

    @OnClick({R.id.btn_back, R.id.imv_close, R.id.imv_menu, R.id.rl_speed, R.id.rl_top, R.id.iv_replay})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.rl_top:
                break;
            case R.id.rl_speed:
                mRlSpeed.setVisibility(View.GONE);
                break;
            case R.id.imv_close:
                mRlSpeed.setVisibility(View.GONE);
                break;
            case R.id.imv_menu:
                mRlSpeed.setVisibility(View.VISIBLE);
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
        }
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
            loadingView.setVisibility(View.GONE);
            isPlayVideo = true;
        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            ivPlay.setVisibility(View.GONE);
            ivPause.setVisibility(View.GONE);
            ivReplay.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
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
}
