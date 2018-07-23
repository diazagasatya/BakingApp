package com.example.android.bakingapp.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.Data.RecipeContract;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeStepsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeStepsFragment extends Fragment implements RecipeStepsAdapter.RecipeStepsAdapterOnClickHandler{

    private RecyclerView mRecipeStepsRv;
    private RecipeStepsAdapter mRecipeStepsAdapter;
    private Uri mUri;
    private ArrayList<String> recipeShortDesc;

    private static final String RECIPE_URI = "recipe_uri";
    private static final String TAG = "master_list_fragment";

    public static final String[] RECIPE_PROJECTION = {
            RecipeContract.RecipeTable.COL_NAME,
            RecipeContract.RecipeTable.COL_INGREDIENTS,
            RecipeContract.RecipeTable.COL_STEPS,
            RecipeContract.RecipeTable.COL_SERVINGS,
    };

    public static final int INDEX_RECIPE_NAME = 0;
    public static final int INDEX_INGREDIENTS = 1;
    public static final int INDEX_STEPS = 2;
    public static final int INDEX_SERVINGS = 3;

    public static final String SHORT_DESCRIPTION = "shortDescription";

    private String rName, rIngredients, rSteps, rServings;

    /**
     * Mandatory Empty Constructor
     */
    public RecipeStepsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if(getArguments() != null) {
            mUri = Uri.parse(getArguments().getString(RECIPE_URI));
            // Debugging
            Log.v(TAG, "Uri passed from detail activity " + mUri);
        }

        final View rootView = inflater.inflate(R.layout.fragment_recipe_steps_list, container, false);

        // Get a reference of the recycler view in the fragment_master_recipe_list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // Get the reference of the recycler view for recipe steps
        mRecipeStepsRv = rootView.findViewById(R.id.rv_recipe_steps);
        // Set the layout of the recipe steps
        mRecipeStepsRv.setLayoutManager(linearLayoutManager);

        // If saved instance state is not null
        // Query the appropriate recipe and parse the steps and ingredients
        if(savedInstanceState == null) {
            getRecipeSteps();
        }

        // Create the adapter and set it to the recycler view
        mRecipeStepsAdapter = new RecipeStepsAdapter(getContext(),recipeShortDesc, this);
        mRecipeStepsRv.setAdapter(mRecipeStepsAdapter);

        return rootView;

    }

    /**
     * This function will get the recipe short descriptions
     */
    public void getRecipeSteps() {

        // Try grabbing the recipe with id
        try {
            // Get content resolver and query the appropriate recipe with id
            Cursor cursor = getContext().getContentResolver().query(mUri,
                    RECIPE_PROJECTION,
                    null,
                    null,
                    null);
            // Move to first in the cursor and grab the information
            cursor.moveToFirst();
            System.out.println("HOW MANY ROWS " + cursor.getCount());

            // Initialize ArrayList for holding recipe short descriptions
            recipeShortDesc = new ArrayList<>();

            /*********************
             *   GRAB DATA HERE  *
             *********************/
            rName = cursor.getString(INDEX_RECIPE_NAME);
            rIngredients = cursor.getString(INDEX_INGREDIENTS);
            rSteps = cursor.getString(INDEX_STEPS);
            rServings = cursor.getString(INDEX_SERVINGS);

            // Log.v(TAG, "Testing " + rName + rIngredients + rSteps + rServings);

            // Grab the steps short descriptions
            JSONArray rStepsArray = new JSONArray(rSteps);
            // Iterate to every array and insert the short descriptions
            for(int i = 0; i < rStepsArray.length(); i++) {
                JSONObject step = rStepsArray.getJSONObject(i);
                recipeShortDesc.add(step.getString(SHORT_DESCRIPTION));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void clickedRecipeSteps(int idNumber) {

    }
}
