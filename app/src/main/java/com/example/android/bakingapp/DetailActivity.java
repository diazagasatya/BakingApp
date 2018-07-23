package com.example.android.bakingapp;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.bakingapp.ui.RecipeStepsFragment;

public class DetailActivity extends AppCompatActivity {

    // Get the URI as a string to pass in to bundle
    private static final String RECIPE_URI = "recipe_uri";
    private static Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content layout
        setContentView(R.layout.activity_detail);

        // Pass on the URI to the RecipeStepsFragment in a Bundle
        Bundle bundle = new Bundle();
        // Convert the uri to String to pass in the bundle
        mUri = getIntent().getData();
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
}
