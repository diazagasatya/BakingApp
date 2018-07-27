package com.example.android.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.bakingapp.Fragment.SimpleExoPlayerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FullScreenActivity extends AppCompatActivity {

    private long cVideoPosition;
    private int cStepPosition;
    private SimpleExoPlayerFragment simpleExoPlayerFragment;
    private JSONObject mStepRecipe;
    private int mStepPosition;
    private String mJsonString;

    // Intent Extra Strings
    private static final String RECIPE_STEPS = "recipe_steps";
    private static final String STEPS_POSITION = "steps_position";
    private static final String CURRENT_POSITION = "current_position";
    private static final String JSON_STRING = "json_string";
    private static final String SIMPLE_EXO_PLAYER_POSITION = "simple_exo_player_position";
    private static final String RECIPE_STEPS_ARRAY = "steps_array";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        // Start RecipeStepsDetailActivity Intent
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                && savedInstanceState != null) {
            // Create intent to RecipeStepsDetailActivity
            Intent portraitActivity = new Intent(this, RecipeStepsDetailActivity.class);
            portraitActivity.putExtra(SIMPLE_EXO_PLAYER_POSITION,
                    savedInstanceState.getLong(SIMPLE_EXO_PLAYER_POSITION));
            portraitActivity.putExtra(RECIPE_STEPS, savedInstanceState.getString(RECIPE_STEPS));
            portraitActivity.putExtra(STEPS_POSITION, savedInstanceState.getInt(STEPS_POSITION));
            portraitActivity.putExtra(RECIPE_STEPS_ARRAY, savedInstanceState.getString(RECIPE_STEPS_ARRAY));

            startActivity(portraitActivity);
        }

        // Get video position JSON STRING
        cVideoPosition = getIntent().getLongExtra(SIMPLE_EXO_PLAYER_POSITION,0);
        // Get current video position
        cStepPosition = getIntent().getIntExtra(CURRENT_POSITION, 0);
        // Get steps position
        mStepPosition = getIntent().getIntExtra(STEPS_POSITION, 0);
        // Get JSON String
        mJsonString = getIntent().getStringExtra(JSON_STRING);

        // Get the recipe steps
        try {
            String steps = getIntent().getStringExtra(JSON_STRING);
            JSONArray stepsArray = new JSONArray(steps);
            mStepRecipe = stepsArray.getJSONObject(cStepPosition);

            // Create bundle for fragment here
            Bundle stepBundle = new Bundle();
            stepBundle.putString(RECIPE_STEPS, mStepRecipe.toString());

            // Initialize Fragments here
            initializeFragment(stepBundle);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Save outstate needed for configuration changes / onResume()
     * @param outState          Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SIMPLE_EXO_PLAYER_POSITION, simpleExoPlayerFragment.getVideoPosition());
        outState.putString(RECIPE_STEPS, mStepRecipe.toString());
        outState.putInt(STEPS_POSITION, mStepPosition);
        outState.putString(RECIPE_STEPS_ARRAY, mJsonString);
    }

    /**
     * Will initialize fragment to display SimpleExoPlayer
     * @param stepBundle            Bundle RecipeSteps
     */
    private void initializeFragment(Bundle stepBundle) {

        // Instantiate Fragment & Begin Transaction
        simpleExoPlayerFragment = new SimpleExoPlayerFragment();
        simpleExoPlayerFragment.setArguments(stepBundle);
        simpleExoPlayerFragment.setVideoPosition(cVideoPosition);
        // Begin transaction here
        getSupportFragmentManager().beginTransaction()
                .add((R.id.landscape_exo_player_container), simpleExoPlayerFragment)
                .commit();
    }
}
