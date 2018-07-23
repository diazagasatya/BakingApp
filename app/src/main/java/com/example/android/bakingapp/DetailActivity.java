package com.example.android.bakingapp;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.bakingapp.Data.RecipeContract;
import com.example.android.bakingapp.ui.RecipeStepsFragment;

import org.json.JSONArray;

public class DetailActivity extends AppCompatActivity {

    // Get the URI as a string to pass in to bundle
    private static final String RECIPE_URI = "recipe_uri";
    private static final String[] RECIPE_PROJECTION = {
            RecipeContract.RecipeTable.COL_NAME,
            RecipeContract.RecipeTable.COL_INGREDIENTS,
            RecipeContract.RecipeTable.COL_STEPS,
            RecipeContract.RecipeTable.COL_SERVINGS,
    };
    private static Uri mUri;

    public static final int INDEX_RECIPE_NAME = 0;
    public static final int INDEX_INGREDIENTS = 1;
    public static final int INDEX_STEPS = 2;
    public static final int INDEX_SERVINGS = 3;

    private RecyclerView mRecipeIngredientsRv;
    private RecipeIngredientsAdapter mRecipeIngredientsAdapter;
    private JSONArray recipeIngredients;

    private String rName, rIngredients, rSteps, rServings;

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
            getRecipeIngredients();
        }

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
    public void getRecipeIngredients() {
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
            rName = cursor.getString(INDEX_RECIPE_NAME);
            rIngredients = cursor.getString(INDEX_INGREDIENTS);
            rSteps = cursor.getString(INDEX_STEPS);
            rServings = cursor.getString(INDEX_SERVINGS);

            recipeIngredients = new JSONArray(rIngredients);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
