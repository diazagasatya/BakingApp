package com.example.android.bakingapp.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RecipeStepsDetailFragment extends Fragment {

    // Final strings to retrieve from bundle
    private static final String RECIPE_STEPS = "recipe_steps";
    private static final String TAG = "recipe_detail_frag";

    private static final String DESCRIPTION = "description";

    private String stepDetail;

    private TextView mRecipeDescription;
    private JSONObject recipeDetailObject;

    /**
     * Essential empty constructor
     */
    public RecipeStepsDetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments() != null) {
            stepDetail = getArguments().getString(RECIPE_STEPS);
            // Log.v(TAG, "step detail passed from detail activity " + stepDetail);
        }

        final View rootView = inflater.inflate(R.layout.fragment_recipe_instruction, container, false);
        mRecipeDescription = rootView.findViewById(R.id.tv_recipe_instruction);

        // Initialize strings to N/A to handle bad data
        String recipeDescription = "N/A";

        // Convert the string to JSONObject for easy key value pair
        try {
            recipeDetailObject = new JSONObject(stepDetail);
            if(recipeDetailObject.getString(DESCRIPTION).length() == 0
                    || recipeDetailObject.getString(DESCRIPTION).equals(null)) {
                recipeDescription = "N/A";
            } else {
                recipeDescription = recipeDetailObject.getString(DESCRIPTION);
            }

        } catch (JSONException e) {
            Log.v(TAG,"Please check for BAD DATA");
        }

        mRecipeDescription.setText(recipeDescription);

        return rootView;
    }
}
