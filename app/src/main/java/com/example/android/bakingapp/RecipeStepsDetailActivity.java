package com.example.android.bakingapp;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.bakingapp.Fragment.RecipeStepsDetailFragment;

public class RecipeStepsDetailActivity extends AppCompatActivity {

    private static final String RECIPE_STEPS = "recipe_steps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps_detail);

        // Only create new fragments when there is no previously saved state
        if(savedInstanceState == null) {

            // TODO(1) Create EXO Media Player Here

            // Create new Recipe Fragment
            RecipeStepsDetailFragment recipeStepsDetailFragment = new RecipeStepsDetailFragment();

            // Create a bundle to send recipe steps data
            Bundle bundle = new Bundle();
            bundle.putString(RECIPE_STEPS, getIntent().getStringExtra(RECIPE_STEPS));
            recipeStepsDetailFragment.setArguments(bundle);

            // Use FragmentManager and transaction to add the fragment to the screen
            FragmentManager fragmentManager = getSupportFragmentManager();
            // Fragment transaction
            fragmentManager.beginTransaction()
                    .add((R.id.recipe_steps_instruction_container), recipeStepsDetailFragment)
                    .commit();
        }
    }
}
