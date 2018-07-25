package com.example.android.bakingapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.android.bakingapp.Fragment.NavigationStepsFragment;
import com.example.android.bakingapp.Fragment.RecipeStepsDetailFragment;
import com.example.android.bakingapp.Fragment.SimpleExoPlayerFragment;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeStepsDetailActivity extends AppCompatActivity implements NavigationStepsFragment.NavigationOnClickListener{

    // Arguments passed
    private static final String RECIPE_STEPS = "recipe_steps";
    private static final String STEPS_POSITION = "steps_position";
    private static final String RECIPE_STEPS_ARRAY = "steps_array";

    // Reference for buttons
    private static final int NEXT_BUTTON = 1;
    private static final String CURRENT_POSITION = "current_position";
    private static final String JSON_STRING = "json_string";
    private static final String SIMPLE_EXO_PLAYER_POSITION = "simple_exo_player_position";

    // Steps array
    private JSONArray stepsArray;
    private int currentPosition;
    private String jsonString;
    private SimpleExoPlayerFragment mSimpleExoPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_steps_detail);

        // If configuration changes or paused
        if(savedInstanceState != null) {

            // Grab JSON String
            jsonString = savedInstanceState.getString(JSON_STRING);
            // Grab the previous position
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION);

            // Get the stepDetail
            try {
                // Save the stepsArray
                stepsArray = new JSONArray(jsonString);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Check if orientation is Landscape, if so start FullScreenActivity
            if((getResources().getConfiguration().orientation)
                    == Configuration.ORIENTATION_LANDSCAPE) {

                // Start full activity intent here
                Intent fcActivityIntent = new Intent(this, FullScreenActivity.class);
                fcActivityIntent.putExtra(JSON_STRING, jsonString);
                fcActivityIntent.putExtra(CURRENT_POSITION,currentPosition);
                fcActivityIntent.putExtra(SIMPLE_EXO_PLAYER_POSITION,
                        savedInstanceState.getLong(SIMPLE_EXO_PLAYER_POSITION));
                fcActivityIntent.putExtra(STEPS_POSITION, currentPosition);

                // Start Intent Here
                startActivity(fcActivityIntent);
            }

        } else {

            // Get video position if any
            long videoPosition = getIntent().getLongExtra(SIMPLE_EXO_PLAYER_POSITION,0);

            // Initialize the fragments with the passed in intent
            initializeFragments(getIntent().getIntExtra(STEPS_POSITION,-1),
                    getIntent().getStringExtra(RECIPE_STEPS), videoPosition);

            // Get the Steps Array & position of the current step
            try {
                jsonString = getIntent().getStringExtra(RECIPE_STEPS_ARRAY);
                stepsArray = new JSONArray(jsonString);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            currentPosition = getIntent().getIntExtra(STEPS_POSITION, -1);
        }

    }

    /**
     * This function will initialize the fragments with its corresponding position
     * @param recipeStepsPosition           Current Position
     * @param stepDetail                    Step Detail
     */
    public void initializeFragments(int recipeStepsPosition, String stepDetail, long videoPosition) {

        // Create a bundle to send recipe steps data for exo and description
        Bundle bundle = new Bundle();
        bundle.putString(RECIPE_STEPS, stepDetail);

        // Media Exo Player Fragment starts here
        mSimpleExoPlayerFragment = new SimpleExoPlayerFragment();
        mSimpleExoPlayerFragment.setArguments(bundle);
        mSimpleExoPlayerFragment.setVideoPosition(videoPosition);

        // Begin transaction here
        FragmentManager mediaFragmentManager = getSupportFragmentManager();
        mediaFragmentManager.beginTransaction()
                .add((R.id.media_player_container), mSimpleExoPlayerFragment)
                .commit();

        // Recipe Detail Fragment starts here
        // Create new Recipe Fragment
        RecipeStepsDetailFragment recipeStepsDetailFragment = new RecipeStepsDetailFragment();
        recipeStepsDetailFragment.setArguments(bundle);

        // Use FragmentManager and transaction to add the fragment to the screen
        FragmentManager recipeFragmentManager = getSupportFragmentManager();
        // Fragment transaction
        recipeFragmentManager.beginTransaction()
                .add((R.id.recipe_steps_instruction_container), recipeStepsDetailFragment)
                .commit();

        // Next & Previous Button Fragment Here
        NavigationStepsFragment navStepsFragment = new NavigationStepsFragment();
        // Pass in arguments here
        Bundle positionBundle = new Bundle();
        positionBundle.putInt(STEPS_POSITION, recipeStepsPosition);
        navStepsFragment.setArguments(positionBundle);

        // Start transactions here
        FragmentManager navFragManager = getSupportFragmentManager();
        navFragManager.beginTransaction()
                .add((R.id.previous_or_next_container), navStepsFragment)
                .commit();
    }

    /**
     * Configuration changes
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, currentPosition);
        outState.putString(JSON_STRING, jsonString);
        if(mSimpleExoPlayerFragment != null) {
            outState.putLong(SIMPLE_EXO_PLAYER_POSITION, mSimpleExoPlayerFragment.getVideoPosition());
        }
    }

    @Override
    public void navigationClicked(int navigationButton) {
        // Handle bad position
        if(currentPosition == -1) {
            return;
        }

        // If reached the end of the recipe steps initialize position to 0
        if((currentPosition == stepsArray.length() - 1) && navigationButton == NEXT_BUTTON) {
            currentPosition = 0;
        } else {
            // Get the position of after navigation button clicked
            currentPosition += navigationButton;
        }

        // Get the recipeStep here
        try {

            // Grab the appropriate recipe steps
            JSONObject step = stepsArray.getJSONObject(currentPosition);

            // Insert the new steps to a bundle
            Bundle bundle = new Bundle();
            bundle.putString(RECIPE_STEPS, step.toString());

            // Replace the fragments for Media Player
            mSimpleExoPlayerFragment = new SimpleExoPlayerFragment();
            mSimpleExoPlayerFragment.setArguments(bundle);

            // Begin transaction here
            getSupportFragmentManager().beginTransaction()
                    .replace((R.id.media_player_container), mSimpleExoPlayerFragment)
                    .commit();

            // Replace the Step Description
            RecipeStepsDetailFragment recipeStepsDetailFragment = new RecipeStepsDetailFragment();
            recipeStepsDetailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace((R.id.recipe_steps_instruction_container), recipeStepsDetailFragment)
                    .commit();

            // Replace the Navigation Fragment
            Bundle bundlePosition = new Bundle();
            bundlePosition.putInt(STEPS_POSITION, currentPosition);
            NavigationStepsFragment navStepsFragment = new NavigationStepsFragment();
            navStepsFragment.setArguments(bundlePosition);
            getSupportFragmentManager().beginTransaction()
                    .replace((R.id.previous_or_next_container),navStepsFragment)
                    .commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
