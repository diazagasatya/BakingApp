package com.example.android.bakingapp.Utilities;

import android.content.ContentValues;
import android.util.Log;

import com.example.android.bakingapp.Data.RecipeContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class RecipeJsonUtilities {

    // String reference of JSON Keys
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String INGREDIENTS = "ingredients";
    private static final String STEPS = "steps";
    private static final String SERVINGS = "servings";

    // Use this static string as a confirmation of http request
    private static final String TAG = "JSON_Debugging";

    /**
     * This method will return an array of Content Values parsed from the JSON String
     * @param jsonStringResponse        HTTP String JSON response
     * @return ContentValues[]
     */
    public static ContentValues[] getRecipesContentValuesFromJson(String jsonStringResponse)
    throws JSONException{

        // Instantiate the jsonStringResponse into a JSON Array
        JSONArray recipeArray = new JSONArray(jsonStringResponse);

        // Get the array of recipe list from the JSON Object
        int numberOfRecipes = recipeArray.length();

        // Instantiate the Content Values array
        ContentValues[] contentValuesArray = new ContentValues[numberOfRecipes];

        // Iterate to one by one inside the array
        for(int i = 0; i < recipeArray.length(); i++) {

            String rId, rName, rIngredients, rSteps, rServings;
            JSONObject recipe = recipeArray.getJSONObject(i);
            ContentValues contentValues = new ContentValues();

            // Grab the JSON values in the JSONObject
            // Make ID starts from 0
            rId = Integer.toString((Integer.parseInt(recipe.getString(ID)) - 1));
            rName = recipe.getString(NAME);
            rIngredients = recipe.getString(INGREDIENTS);
            rSteps = recipe.getString(STEPS);
            rServings = recipe.getString(SERVINGS);

            // Log the strings for debugging
            Log.v(TAG, "JSON strings : " + rId + rName + " " + rIngredients + " "
                    + rSteps + " " + rServings);

            // Put the string values to the content value
            contentValues.put(RecipeContract.RecipeTable.COL_ID, rId);
            contentValues.put(RecipeContract.RecipeTable.COL_NAME, rName);
            contentValues.put(RecipeContract.RecipeTable.COL_INGREDIENTS, rIngredients);
            contentValues.put(RecipeContract.RecipeTable.COL_STEPS, rSteps);
            contentValues.put(RecipeContract.RecipeTable.COL_SERVINGS, rServings);

            // Insert the content values inside the CVArray
            contentValuesArray[i] = contentValues;

        }
        return contentValuesArray;
    }
}
