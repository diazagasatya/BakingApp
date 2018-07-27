package com.example.android.bakingapp.Fragment;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeStepsDetailActivity;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class SimpleExoPlayerFragment extends Fragment {

    // Static string name for recipe steps
    private static final String RECIPE_STEPS = "recipe_steps";
    private static final String VIDEO_URL = "videoURL";
    private static final String THUMBNAIL_URL = "thumbnailURL";
    private static final String TAG = "exo_player_fragment";
    private static final String PLAYBACK_POSITION = "playback_position";

    private static String mRecipeDetail;

    // Create an instance of SimpleExoPlayer and the View
    private SimpleExoPlayerView mExoPlayerView;
    private SimpleExoPlayer mSimplePlayer;
    private PlaybackStateCompat.Builder mStateBuilder;
    private MediaSessionCompat mMediaSessionCompat;
    private long videoPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Grab passed in data from RecipeStepsDetailActivity
        if(getArguments() != null) {
            mRecipeDetail = getArguments().getString(RECIPE_STEPS);
            // Log.v(TAG, "step detail passed from detail activity " + stepDetail);
        }

        // Initialize Media Sessions
        mediaSessionInitializer();

        // Inflate the the layout of the fragment to rootview
        View rootView = inflater.inflate(R.layout.fragment_simple_exo_player,container,false);
        // Reference the ExoPlayerView
        mExoPlayerView = rootView.findViewById(R.id.simple_exo_player);

        // Get videoURL and thumbnailURL from argument
        // Convert the String into a JSONObject
        try {

            JSONObject recipeDetails = new JSONObject(mRecipeDetail);
            String videoUrl, thumbnailUrl;
            videoUrl = recipeDetails.getString(VIDEO_URL);
            thumbnailUrl = recipeDetails.getString(THUMBNAIL_URL);
            // Log.v(TAG, "TEST" + videoUrl + " " + thumbnailUrl);
            // Set default artwork to below
            mExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(
                    getResources(),R.drawable.ic_no_video));

            // If there's video URL
            if(videoUrl.length() != 0 || videoUrl.equals("")) {
                // If there's no thumbnail present
                if(thumbnailUrl.length() != 0
                        && thumbnailUrl
                        .subSequence(thumbnailUrl.length() - 3, thumbnailUrl.length())
                        .equals("mp4")) {
                    // Bad DATA - video URL in thumbnail
                    videoUrl = thumbnailUrl;
                    // Create thumbnail from the video
                    Bitmap thumbnailVideo = ThumbnailUtils.createVideoThumbnail(thumbnailUrl,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    mExoPlayerView.setDefaultArtwork(thumbnailVideo);
                }
            }
            // Initialize the player here
            initializePlayer(videoUrl, rootView, savedInstanceState);

        } catch (JSONException e) {
            Log.v(TAG,"Please check for BAD DATA");
        }
        return rootView;
    }

    /**
     * Create Media Session to handle media functions outside the UI.
     * Create the MediaSessionCompat object, set the flags for external clients.
     * Set available actions you want to support, and start the session
     */
    private void mediaSessionInitializer() {
        mMediaSessionCompat = new MediaSessionCompat(getContext(), TAG);

        // Set flags for external clients in a car and media buttons
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Don't let external client restart the player when not in UI
        mMediaSessionCompat.setMediaButtonReceiver(null);

        // Set available actions to support outside UI
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        // Set the playback state for compat
        mMediaSessionCompat.setPlaybackState(mStateBuilder.build());

        // Handle callbacks from media controller
        mMediaSessionCompat.setCallback(new SessionCallbacks());

        // Start the Media Session since the activity is active
        mMediaSessionCompat.setActive(true);
    }


    /**
     * Override onPlay, onPause, and onSkipToPrevious()
     */
    private class SessionCallbacks extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mSimplePlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mSimplePlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mSimplePlayer.seekTo(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSimplePlayer.stop();
        mSimplePlayer.release();
        mSimplePlayer = null;
        mMediaSessionCompat.setActive(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mSimplePlayer != null) {
            mSimplePlayer.setPlayWhenReady(false);
        }
    }

    /**
     * This function will initialize the exo player with trackSelector
     * and LoadControl build in.
     * @param videoUrl          Video Url
     */
    private void initializePlayer(String videoUrl, View rootView, Bundle savedInstanceState) {

        // Make sure that the Player is not initiated before
        if(mSimplePlayer == null) {
            // Parse the url string to Uri
            Uri mediaUri = Uri.parse(videoUrl);

            // Create an instance TrackSelector and LoadControl
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            // Create an instance of the simple player
            mSimplePlayer = ExoPlayerFactory.newSimpleInstance(rootView.getContext(),
                    trackSelector,loadControl);
            // Set the player to the player view
            mExoPlayerView.setPlayer(mSimplePlayer);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(rootView.getContext(),"bakingapp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    rootView.getContext(), userAgent), new DefaultExtractorsFactory(),
                    null, null);
            // Prepare the simple player and set play when ready
            mSimplePlayer.prepare(mediaSource);

            // If it's only a rotation
            if(savedInstanceState != null) {
                long position = savedInstanceState.getLong(PLAYBACK_POSITION);
                // Prepare the simple player and set play when ready
                mSimplePlayer.seekTo(position);
                mSimplePlayer.setPlayWhenReady(true);
            } else {
                if(videoPosition != 0) {
                    mSimplePlayer.seekTo(videoPosition);
                    mSimplePlayer.setPlayWhenReady(true);
                }
            }

            // If orientation is Landscape Play
            if(getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                mSimplePlayer.setPlayWhenReady(true);
            }
        }
    }
    /**
     * Handle rotation here and saving playback position
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        long playbackPosition = mSimplePlayer.getCurrentPosition();
        outState.putLong(PLAYBACK_POSITION, playbackPosition);
        videoPosition = mSimplePlayer.getCurrentPosition();
    }

    /**
     * Helper function to return the video position
     * @return videoPosition
     */
    public long getVideoPosition() {
        return videoPosition;
    }

    /**
     * Helper function to return to the video position
     * @param videoPosition
     */
    public void setVideoPosition(long videoPosition) {
        this.videoPosition = videoPosition;
    }
}
