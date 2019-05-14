package vn.hanelsoft.forestpublishing.util;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import vn.hanelsoft.forestpublishing.ForestApplication;

/**
 * Created by Tuấn Sơn on 27/7/2017.
 */

public class ExoPlayerVideoHandler {
    private static ExoPlayerVideoHandler instance;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    public static ExoPlayerVideoHandler getInstance() {
        if (instance == null) {
            instance = new ExoPlayerVideoHandler();
        }
        return instance;
    }

    private SimpleExoPlayer player;
    private Uri playerUri;
    private boolean isPlayerPlaying;

    public ExoPlayerVideoHandler() {
    }

    public void prepareExoPlayerForUri(Context context, Uri uri, SimpleExoPlayerView exoPlayerView) {
        if (context != null && uri != null && exoPlayerView != null) {
            if (!uri.equals(playerUri) || player == null) {
                // Create a new player if the player is null or
                // we want to play a new video
                playerUri = uri;
                // Do all the standard ExoPlayer code here...
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
                player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                        Util.getUserAgent(context, "vn.hanelsoft.forestpublishing"), defaultBandwidthMeter);
                MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, new DefaultExtractorsFactory(),
                        null, null);
                // Prepare the player with the source.
                player.prepare(videoSource);
            }
            player.clearVideoSurface();
            player.setVideoSurfaceView((SurfaceView) exoPlayerView.getVideoSurfaceView());
            player.seekTo(player.getCurrentPosition() + 1);
            exoPlayerView.setPlayer(player);
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return (ForestApplication.getInstance()
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null));
    }

    public void releaseVideoPlayer() {
        if (player != null) {
            player.release();
        }
        isPlayerPlaying = false;
        player = null;
    }

    public void goToBackground() {
        if (player != null) {
            isPlayerPlaying = player.getPlayWhenReady();
            player.setPlayWhenReady(false);
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void goToForeground() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }
}
