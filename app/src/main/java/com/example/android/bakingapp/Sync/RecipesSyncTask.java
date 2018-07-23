package com.example.android.bakingapp.Sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bakingapp.Data.RecipeContract;
import com.example.android.bakingapp.Utilities.NetworkUtilities;
import com.example.android.bakingapp.Utilities.RecipeJsonUtilities;

import java.net.URL;

public class RecipesSyncTask {

    private static final String TAG = "Recipe_Sync_Task";

    /**
     * This wrapper function will check internet connection first,
     * instantiate the async task for context reference, and build URL
     * from the NetworkUtils class and pass in the url to the async task
     * to run HTTP request from the api
     * @param context       Context
     */
    public static void syncRecipes(@NonNull Context context) {
        // Check internet connection
        if(!checkInternetConnection(context)) {
            Toast.makeText(context,"No Internet Conenction",Toast.LENGTH_LONG).show();
            return;
        }
        // Instantiate the AsyncTask to reference the context
        RecipeAsyncTask asyncTask = new RecipeAsyncTask(context);

        // Build the URL for API request recipe JSON
        try {
            // Get the URL
            URL recipeUrl = NetworkUtilities.getRecipeUrl();
            // Run the HTTP request using asynctask
            asyncTask.execute(recipeUrl);

        } catch (NullPointerException e) {
            Log.v(TAG, "URL is null, please check the URL in Network Utilities");
            return;
        }
    }

    /**
     * Sync the database with the current recipe JSON from the URL given
     */
    public static class RecipeAsyncTask extends AsyncTask<URL,Void,Void> {

        // Reference to context
        private Context mContext;

        public RecipeAsyncTask(@NonNull Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(URL... urls) {

            // Grab the URL
            URL url = urls[0];

            // Try getting response from HTTP and insert data to appropriate table
            try {
                // Sync the recipe with the recipe URL
                String httpRecipeResults = NetworkUtilities.getResponseFromHttpUrl(url);
                Log.v(TAG, httpRecipeResults); // Debugging

                // Grab the content values after JSON Parsing
                ContentValues[] recipeContentValues =
                        RecipeJsonUtilities.getRecipesContentValuesFromJson(httpRecipeResults);

                // Bulk insert the content values to the database
                if(recipeContentValues != null && recipeContentValues.length != 0) {
                    // Get a handle to the content resolver
                    ContentResolver contentResolver = mContext.getContentResolver();
                    // Delete old values
                    contentResolver.delete(RecipeContract.RecipeTable.CONTENT_URI,
                            null,null);
                    // Update to new values
                    contentResolver.bulkInsert(RecipeContract.RecipeTable.CONTENT_URI,
                            recipeContentValues);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * This helper function will check Internet Connection
     * @param context           Context
     * @return boolean
     */
    public static boolean checkInternetConnection(Context context) {
        boolean internetConnection = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            internetConnection = true;
        }
        return internetConnection;
    }

}
