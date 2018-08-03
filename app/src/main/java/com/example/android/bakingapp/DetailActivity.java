package com.example.android.bakingapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.android.bakingapp.Data.RecipeContract;
import com.example.android.bakingapp.Fragment.NavigationStepsFragment;
import com.example.android.bakingapp.Fragment.RecipeStepsDetailFragment;
import com.example.android.bakingapp.Fragment.RecipeStepsFragment;
import com.example.android.bakingapp.Fragment.SimpleExoPlayerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements RecipeStepsFragment.OnStepsClickListener,
        NavigationStepsFragment.NavigationOnClickListener{

    // Get the URI as a string to pass in to bundle
    private static final String RECIPE_URI = "recipe_uri";
    private static final String[] RECIPE_PROJECTION = {
            RecipeContract.RecipeTable.COL_INGREDIENTS,
            RecipeContract.RecipeTable.COL_STEPS,
    };

    private static final String TAG = "detail_activity";

    private static final String RECIPE_STEPS = "recipe_steps";
    private static final String RECIPE_INGREDIENTS = "recipe_ingredients";
    private static final String STEPS_POSITION = "steps_position";
    private static final String RECIPE_STEPS_ARRAY = "steps_array";
    private static final String SIMPLE_EXO_PLAYER_POSITION = "simple_exo_player_position";
    private static final int NEXT_BUTTON = 1;

    private static Uri mUri;

    public static final int INDEX_INGREDIENTS = 0;
    public static final int INDEX_STEPS = 1;

    private RecyclerView mRecipeIngredientsRv;
    private RecipeIngredientsAdapter mRecipeIngredientsAdapter;
    private JSONArray recipeIngredients, stepsJsonArray;
    private SimpleExoPlayerFragment mSimpleExoPlayerFragment;

    // Reference to data
    private String rIngredients, rSteps, rStepDetail;
    private int rStepPosition;
    private long rVideoPosition;

    // Two-pane to larger tablet screens
    private boolean twoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content layout
        setContentView(R.layout.activity_detail);

        // Grab the URI from Main Activity
        mUri = getIntent().getData();

        // If saved instance state is null query the database
        if(savedInstanceState == null) {
            getRecipeData();
            rStepPosition = 0;
            rVideoPosition = 0;
        } else {
            String ingredients = savedInstanceState.getString(RECIPE_INGREDIENTS);
            try {
                recipeIngredients = new JSONArray(ingredients);
                rSteps = savedInstanceState.getString(RECIPE_STEPS);
                stepsJsonArray = new JSONArray(rSteps);
                rStepPosition = savedInstanceState.getInt(STEPS_POSITION);
                rVideoPosition = savedInstanceState.getLong(SIMPLE_EXO_PLAYER_POSITION);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // If the client is using a tablet, the recipe steps layout shouldn't be null
        if(findViewById(R.id.recipe_detail_layout) != null) {

            // Two pane mode is true
            twoPane = true;

            try {
                rStepDetail = new JSONArray(rSteps).getJSONObject(rStepPosition).toString();
            } catch (JSONException e) {
                Log.v(TAG, "BAD DATA Please check");
            }

            // Only create new fragments if savedInstanceState is null
            if(savedInstanceState == null) {
                // Show the SimpleExoPlayer
                Bundle bundle = new Bundle();
                bundle.putString(RECIPE_STEPS, rStepDetail);

                // Media Exo Player Fragment starts here
                mSimpleExoPlayerFragment = new SimpleExoPlayerFragment();
                mSimpleExoPlayerFragment.setArguments(bundle);
                mSimpleExoPlayerFragment.setVideoPosition(rVideoPosition);

                // Begin Transaction Here
                getSupportFragmentManager().beginTransaction()
                        .add((R.id.media_player_container), mSimpleExoPlayerFragment)
                        .commit();

                // Show the Recipe short description
                RecipeStepsDetailFragment recipeStepsDetailFragment = new RecipeStepsDetailFragment();
                recipeStepsDetailFragment.setArguments(bundle);

                // Begin transaction
                getSupportFragmentManager().beginTransaction()
                        .add((R.id.recipe_steps_instruction_container), recipeStepsDetailFragment)
                        .commit();

                // Show the navigation button
                NavigationStepsFragment navStepsFragment = new NavigationStepsFragment();
                // Pass in arguments here
                Bundle positionBundle = new Bundle();
                positionBundle.putInt(STEPS_POSITION, rStepPosition);
                navStepsFragment.setArguments(positionBundle);

                // Start transactions here
                FragmentManager navFragManager = getSupportFragmentManager();
                navFragManager.beginTransaction()
                        .add((R.id.previous_or_next_container), navStepsFragment)
                        .commit();
            }

        }

        // Pass on the URI to the RecipeStepsFragment in a Bundle
        Bundle bundle = new Bundle();

        // Manage layout for Recipe Ingredients
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // Initialize Recycler view for the recipe ingredients
        mRecipeIngredientsRv = findViewById(R.id.rv_recipe_ingredients);
        // Set the layout of the recipe ingredients
        mRecipeIngredientsRv.setLayoutManager(linearLayoutManager);


        // Fragment for recipe steps starts here
        // Create the adapter and set it to the recycler view
        mRecipeIngredientsAdapter = new RecipeIngredientsAdapter(this,recipeIngredients);
        mRecipeIngredientsRv.setAdapter(mRecipeIngredientsAdapter);

        // Insert the URI to a bundle for Fragment
        bundle.putString(RECIPE_URI, mUri.toString());

        // Create a new RecipeStepsFragment and display it using the Fragment Manager
        RecipeStepsFragment recipeStepsFragment = new RecipeStepsFragment();
        recipeStepsFragment.setArguments(bundle);

        // Use FragmentManager and transaction to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Fragment transaction
        fragmentManager.beginTransaction()
                .add((R.id.recipe_steps_container), recipeStepsFragment)
                .commit();

    }

    /**
     * This method will query the database and grab the JSONArray of ingredients for adapter
     */
    public void getRecipeData() {
        // Try grabbing the recipe ingredients with id
        try {
            Cursor cursor = getContentResolver().query(mUri,
                    RECIPE_PROJECTION,
                    null,
                    null,
                    null);
            // Move to first in the cursor and grab the information
            cursor.moveToFirst();
            /*********************
             *   GRAB DATA HERE  *
             *********************/
            rIngredients = cursor.getString(INDEX_INGREDIENTS);
            rSteps = cursor.getString(INDEX_STEPS);
            stepsJsonArray = new JSONArray(rSteps);
            recipeIngredients = new JSONArray(rIngredients);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Save the recipe data for configuration changes
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RECIPE_INGREDIENTS, recipeIngredients.toString());
        outState.putString(RECIPE_STEPS, rSteps);
        outState.putInt(STEPS_POSITION,rStepPosition);
        // Make sure that the simpleExoPlayer is not null
        if(mSimpleExoPlayerFragment != null) {
            outState.putLong(SIMPLE_EXO_PLAYER_POSITION, mSimpleExoPlayerFragment.getVideoPosition());
        }
    }

    /**
     * This function will grab the recipe steps JSON data and pass it appropriately
     * depending on phone screen sizes.
     * @param position
     */
    @Override
    public void onStepSelected(int position) {

        // Set the appropriate layout based on screen
        if(twoPane) {

            // Update the current position of the fragment
            rStepPosition = position;

            // Get the correct recipe step
            try {
                rStepDetail = stepsJsonArray.getJSONObject(position).toString();
            } catch (JSONException e) {
                Log.v(TAG, "BAD DATA");
            }

            // Replace the mExoplayer
            Bundle bundle = new Bundle();
            bundle.putString(RECIPE_STEPS, rStepDetail);

            mSimpleExoPlayerFragment = new SimpleExoPlayerFragment();
            mSimpleExoPlayerFragment.setArguments(bundle);
            mSimpleExoPlayerFragment.setVideoPosition(0);

            getSupportFragmentManager().beginTransaction()
                    .replace((R.id.media_player_container), mSimpleExoPlayerFragment)
                    .commit();

            // Replace the recipe
            RecipeStepsDetailFragment recipeStepsDetailFragment = new RecipeStepsDetailFragment();
            recipeStepsDetailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace((R.id.recipe_steps_instruction_container), recipeStepsDetailFragment)
                    .commit();

            // Replace the Navigation Fragment
            Bundle bundlePosition = new Bundle();
            bundlePosition.putInt(STEPS_POSITION, position);
            NavigationStepsFragment navStepsFragment = new NavigationStepsFragment();
            navStepsFragment.setArguments(bundlePosition);
            getSupportFragmentManager().beginTransaction()
                    .replace((R.id.previous_or_next_container),navStepsFragment)
                    .commit();

        } else {

            // Try Catch Block to grab JSONObject
            try {

                // Grab the clicked JSONObject from the rSteps
                JSONArray stepArray = new JSONArray(rSteps);
                JSONObject stepDetail = stepArray.getJSONObject(position);

                // Put the data steps data into a Bundle to run Intent to RecipeStepsDetailActivity
                Bundle bundle = new Bundle();
                bundle.putString(RECIPE_STEPS, stepDetail.toString());
                bundle.putInt(STEPS_POSITION, position);
                bundle.putString(RECIPE_STEPS_ARRAY, rSteps);

                // Initiate Intent here
                final Intent intent = new Intent(this, RecipeStepsDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * This function will handle the fragment if a navigation is clicked
     * @param navigationButton
     */
    @Override
    public void navigationClicked(int navigationButton) {
        // Handle bad position
        if(rStepPosition == -1) {
            return;
        }

        // If reached the end of the recipe steps initialize position to 0
        if((rStepPosition == stepsJsonArray.length() - 1) && navigationButton == NEXT_BUTTON) {
            rStepPosition = 0;
        } else {
            // Get the position of after navigation button clicked
            rStepPosition += navigationButton;
        }

        // Get the recipeStep here
        try {

            // Grab the appropriate recipe steps
            JSONObject step = stepsJsonArray.getJSONObject(rStepPosition);

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
            bundlePosition.putInt(STEPS_POSITION, rStepPosition);
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
