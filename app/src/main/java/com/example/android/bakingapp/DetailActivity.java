package com.example.android.bakingapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.bakingapp.Data.RecipeContract;
import com.example.android.bakingapp.Fragment.RecipeStepsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity implements RecipeStepsFragment.OnStepsClickListener{

    // Get the URI as a string to pass in to bundle
    private static final String RECIPE_URI = "recipe_uri";
    private static final String[] RECIPE_PROJECTION = {
            RecipeContract.RecipeTable.COL_INGREDIENTS,
            RecipeContract.RecipeTable.COL_STEPS,
    };
    private static final String RECIPE_STEPS = "recipe_steps";
    private static final String RECIPE_INGREDIENTS = "recipe_ingredients";
    private static Uri mUri;

    public static final int INDEX_INGREDIENTS = 0;
    public static final int INDEX_STEPS = 1;

    private RecyclerView mRecipeIngredientsRv;
    private RecipeIngredientsAdapter mRecipeIngredientsAdapter;
    private JSONArray recipeIngredients;

    // Reference to data
    private String rIngredients, rSteps;

    // Two-pane to larger tablet screens
    private boolean twoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content layout
        setContentView(R.layout.activity_detail);

        // Pass on the URI to the RecipeStepsFragment in a Bundle
        Bundle bundle = new Bundle();
        // Grab the URI from Main Activity
        mUri = getIntent().getData();

        // Manage layout for Recipe Ingredients
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // Initialize Recycler view for the recipe ingredients
        mRecipeIngredientsRv = findViewById(R.id.rv_recipe_ingredients);
        // Set the layout of the recipe ingredients
        mRecipeIngredientsRv.setLayoutManager(linearLayoutManager);

        // If saved instance state is null query the database
        if(savedInstanceState == null) {
            getRecipeData();
        } else {
            String ingredients = savedInstanceState.getString(RECIPE_INGREDIENTS);
            try {
                recipeIngredients = new JSONArray(ingredients);
                rSteps = savedInstanceState.getString(RECIPE_STEPS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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

        } else {

            // Try Catch Block to grab JSONObject
            try {

                // Grab the clicked JSONObject from the rSteps
                JSONArray stepArray = new JSONArray(rSteps);
                JSONObject stepDetail = stepArray.getJSONObject(position);

                // Put the data steps data into a Bundle to run Intent to RecipeStepsDetailActivity
                Bundle bundle = new Bundle();
                bundle.putString(RECIPE_STEPS, stepDetail.toString());

                // Initiate Intent here
                final Intent intent = new Intent(this, RecipeStepsDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
