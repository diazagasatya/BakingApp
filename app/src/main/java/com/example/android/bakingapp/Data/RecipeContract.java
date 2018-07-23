package com.example.android.bakingapp.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This Contract class will hold the skeleton of the data organized in the database
 */
public class RecipeContract {

    // Create an empty constructor for safety reasons
    public RecipeContract() {}

    // Initialize the CONTENT_AUTHORITY for accessing database through content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.BakingApp";

    // Create base content uri for developers to access database
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Create paths for accessing specific table in the database
    public static final String PATH_RECIPE = "recipe";

    /**
     * This Table will hold all of the recipe from a given JSON file
     */
    public static final class RecipeTable implements BaseColumns {

        // Create the content uri for accessing this table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE).build();

        // Table name
        public static final String TABLE_NAME = "recipes";
        // Column names
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
        public static final String COL_INGREDIENTS = "ingredients";
        public static final String COL_STEPS = "steps";
        public static final String COL_SERVINGS = "servings";

        /**
         * This method will create a URI with appended ID
         * @param recipeId          ID of the recipe in the table
         * @return Uri with ID
         */
        public static Uri buildUriWithIdRecipe(int recipeId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(recipeId)).build();
        }
    }
}
