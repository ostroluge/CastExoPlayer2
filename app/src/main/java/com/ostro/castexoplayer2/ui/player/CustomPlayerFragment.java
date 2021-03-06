package com.ostro.castexoplayer2.ui.player;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.ostro.castexoplayer2.R;
import com.ostro.castexoplayer2.databinding.FragmentPlayerBinding;
import com.ostro.castexoplayer2.event.CastSessionEndedEvent;
import com.ostro.castexoplayer2.event.CastSessionStartedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import timber.log.Timber;

/**
 * Created by Thomas Ostrowski
 * on 03/11/2016.
 */

public class CustomPlayerFragment extends Fragment {

    private CustomPlayerViewModel mCustomPlayerViewModel;
    private SimpleExoPlayerView mSimpleExoPlayerView;

    public static CustomPlayerFragment newInstance(String videoUrl) {
        CustomPlayerFragment customPlayerFragment = new CustomPlayerFragment();

        Bundle args = new Bundle();
        args.putString("VIDEO_URL", videoUrl);
        customPlayerFragment.setArguments(args);

        return customPlayerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentPlayerBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_player, container, false);
        View view = binding.getRoot();
        mCustomPlayerViewModel = new CustomPlayerViewModel(getActivity());
        binding.setPlayerVm(mCustomPlayerViewModel);
        mSimpleExoPlayerView = binding.videoView;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCustomPlayerViewModel.onStart(mSimpleExoPlayerView, getUrlExtra());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onCastSessionStartedEvent(CastSessionStartedEvent event) {
        Timber.d("onCastSessionStartedEvent");
        ExoPlayer mExoPlayer = mCustomPlayerViewModel.getExoPlayer();
        if (mExoPlayer != null) {
            long currentPosition = mExoPlayer.getCurrentPosition();
            if (currentPosition > 0) {
                int position = (int) currentPosition;
                mCustomPlayerViewModel.loadMedia(position, true);
            }
        }
    }

    @Subscribe
    public void onCastSessionEndedEvent(CastSessionEndedEvent event) {
        ExoPlayer mExoPlayer = mCustomPlayerViewModel.getExoPlayer();
        SimpleExoPlayerView mSimpleExoPlayerView = mCustomPlayerViewModel.getSimpleExoPlayerView();
        if (mExoPlayer != null) {
            long time = mExoPlayer.getDuration() - event.getSessionRemainingTime();
            if (time > 0) {
                mCustomPlayerViewModel.setIsInProgress(true);
                mExoPlayer.seekTo(time);
                mExoPlayer.setPlayWhenReady(true);
            }
        }
        if (mSimpleExoPlayerView != null) {
            if (!mSimpleExoPlayerView.getUseController()) {
                mSimpleExoPlayerView.setUseController(true);
            }
        }
    }

    @Nullable
    private String getUrlExtra() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle.getString("VIDEO_URL");
        }
        return null;
    }
}
