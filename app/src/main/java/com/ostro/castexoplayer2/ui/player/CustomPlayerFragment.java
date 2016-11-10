package com.ostro.castexoplayer2.ui.player;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.gms.cast.framework.CastSession;
import com.ostro.castexoplayer2.R;
import com.ostro.castexoplayer2.databinding.FragmentPlayerBinding;

/**
 * Created by Thomas Ostrowski
 * on 03/11/2016.
 */

public class CustomPlayerFragment extends Fragment {

    private CustomPlayerViewModel mCustomPlayerViewModel;
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private CastSession mCastSession;

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

    @Nullable
    private String getUrlExtra() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle.getString("VIDEO_URL");
        }
        return null;
    }
}