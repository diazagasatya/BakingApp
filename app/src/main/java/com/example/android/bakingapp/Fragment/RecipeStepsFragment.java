package com.example.android.bakingapp.Fragment;

import android.content.Context;
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

    private static final String DESCRIPTION_LIST = "short_description_list";
    private RecyclerView mRecipeStepsRv;
    private RecipeStepsAdapter mRecipeStepsAdapter;
    private Uri mUri;
    private ArrayList<String> recipeShortDesc;

    private static final String RECIPE_URI = "recipe_uri";

    public static final String[] RECIPE_PROJECTION = {
            RecipeContract.RecipeTable.COL_STEPS,
    };

    public static final int INDEX_STEPS = 0;

    public static final String SHORT_DESCRIPTION = "shortDescription";

    private String rSteps;

    // Define a new interface, calls a method in the host activity named onStepsSelected
    private OnStepsClickListener mCallback;

    /**
     * Mandatory Empty Constructor
     */
    public RecipeStepsFragment() {}

    /**
     * OnStepsClickListener interface, calls a method in the host activity named onStepSelected
     */
    public interface OnStepsClickListener {
        void onStepSelected(int position);
    }

    /**
     * Override onAttach to make sure that the container activity has implemented the callback
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Make sure the host activity implement the callback interface
        try {
            mCallback = (OnStepsClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " implement OnStepsClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if(getArguments() != null) {
            mUri = Uri.parse(getArguments().getString(RECIPE_URI));
            // Debugging
            // Log.v(TAG, "Uri passed from detail activity " + mUri);
        }

        final View rootView = inflater.inflate(R.layout.fragment_recipe_steps_list, container, false);

        // Get a reference of the recycler view in the fragment_master_recipe_list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // Get the reference of the recycler view for recipe steps
        mRecipeStepsRv = rootView.findViewById(R.id.rv_recipe_steps);
        // Set the layout of the recipe steps
        mRecipeStepsRv.setLayoutManager(linearLayoutManager);

        // If saved instance state is null
        // Query the appropriate recipe and parse the steps and ingredients
        if(savedInstanceState == null) {
            getRecipeSteps();
        } else {
            recipeShortDesc = savedInstanceState.getStringArrayList(DESCRIPTION_LIST);
        }

        // Create the adapter and set it to the recycler view
        mRecipeStepsAdapter = new RecipeStepsAdapter(getContext(),recipeShortDesc, this);
        mRecipeStepsRv.setAdapter(mRecipeStepsAdapter);

        return rootView;

    }

    /**
     * Save the shortDescription Arraylist for rotation changes
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(DESCRIPTION_LIST, recipeShortDesc);
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
            // System.out.println("HOW MANY ROWS " + cursor.getCount());

            // Initialize ArrayList for holding recipe short descriptions
            recipeShortDesc = new ArrayList<>();

            /*********************
             *   GRAB DATA HERE  *
             *********************/
            rSteps = cursor.getString(INDEX_STEPS);

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

    /**
     * This function will call the interface on the callback to start new intent activity in the
     * host activity.
     * @param idNumber      Item Clicked
     */
    @Override
    public void clickedRecipeSteps(int idNumber) {
        mCallback.onStepSelected(idNumber);
    }
}
