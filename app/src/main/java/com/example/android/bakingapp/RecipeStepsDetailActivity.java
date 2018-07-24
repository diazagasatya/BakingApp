package com.example.android.bakingapp;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.bakingapp.Fragment.RecipeStepsDetailFragment;
import com.example.android.bakingapp.Fragment.SimpleExoPlayerFragment;

public class RecipeStepsDetailActivity extends AppCompatActivity {

    private static final String RECIPE_STEPS = "recipe_steps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps_detail);

        // Only create new fragments when there is no previously saved state
        if(savedInstanceState == null) {

            // Create a bundle to send recipe steps data for exo and description
            Bundle bundle = new Bundle();
            bundle.putString(RECIPE_STEPS, getIntent().getStringExtra(RECIPE_STEPS));

            // Media Exo Player Fragment starts here
            SimpleExoPlayerFragment simpleExoPlayerFragment = new SimpleExoPlayerFragment();
            simpleExoPlayerFragment.setArguments(bundle);

            // Begin transaction here
            FragmentManager mediaFragmentManager = getSupportFragmentManager();
            mediaFragmentManager.beginTransaction()
                    .add((R.id.media_player_container), simpleExoPlayerFragment)
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
        }
    }
}
