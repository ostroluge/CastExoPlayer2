package com.ostro.castexoplayer2.ui.player;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ostro.castexoplayer2.BR;
import com.ostro.castexoplayer2.R;

/**
 * Created by Thomas Ostrowski
 * on 03/11/2016.
 */

public class CustomPlayerViewModel extends BaseObservable implements ExoPlayer.EventListener {

    private Activity mActivity;
    private String mUrl;
    private boolean loadingComplete = false;
    private boolean shouldAutoPlay = true;
    private ObservableBoolean controlsVisible = new ObservableBoolean(true);
    private boolean mPausable = true;

    private SimpleExoPlayerView mSimpleExoPlayerView;
    private SimpleExoPlayer mExoPlayer;

    public CustomPlayerViewModel(Activity activity) {
        mActivity = activity;
    }

    public void onStart(SimpleExoPlayerView simpleExoPlayerView,
                        String url) {
        mSimpleExoPlayerView = simpleExoPlayerView;
        mUrl = url;
        initPlayer();
        mSimpleExoPlayerView.setPlayer(mExoPlayer);
        preparePlayer();
    }

    private void initPlayer() {
        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mActivity,
                trackSelector, loadControl);
    }

    private void preparePlayer() {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mActivity,
                Util.getUserAgent(mActivity, "ExoPlayer2"), bandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        Uri videoUri = Uri.parse(mUrl);
        MediaSource videoSource = new ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source.
        mExoPlayer.prepare(videoSource);
        mExoPlayer.addListener(this);
        mExoPlayer.setPlayWhenReady(shouldAutoPlay);
    }

    public void onPlayerClicked(final View view) {
        controlsVisible.set(true);
    }

    @Bindable
    public int getPlaybackImageRes() {
        if (!isPlaying()) {
            return R.drawable.ic_media_play;
        }
        if (!isPausable()) {
            return R.drawable.ic_media_stop;
        } else {
            return R.drawable.ic_media_pause;
        }
    }

    public void onPlayPauseClicked(final View view) {
        if (mExoPlayer == null) {
            return;
        }
        if (isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    private void play() {
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.setPlayWhenReady(true);
    }

    private void pause() {
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.setPlayWhenReady(false);
    }

    private boolean isPlaying() {
        return mExoPlayer != null && mExoPlayer.getPlayWhenReady();
    }

    private boolean isPausable() {
        return mPausable;
    }

    public void setPausable(boolean pausable) {
        mPausable = pausable;
//        notifyPropertyChanged(BR.pausable);
    }

    @Bindable
    public boolean getLoadingComplete() {
        return loadingComplete;
    }

    private void setLoadingComplete(boolean complete) {
        loadingComplete = complete;
        notifyPropertyChanged(BR.loadingComplete);
    }

    @Bindable
    public boolean getControlsVisible() {
        return controlsVisible.get();
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        setLoadingComplete(!isLoading);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        if (playWhenReady) {
//            MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
//
//            mediaMetadata.putString(MediaMetadata.KEY_TITLE, "Test");
//
//            MediaInfo mediaInfo = new MediaInfo.Builder(mUrl)
//                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
//                    .setContentType("videos/mp4")
//                    .setMetadata(mediaMetadata)
//                    .build();
//
//            RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
//            remoteMediaClient.load(mediaInfo, shouldAutoPlay, 0);
//        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
}