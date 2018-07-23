package com.example.android.bakingapp.Utilities;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtilities {

    // Base URL for the Recipe JSON file
    private static final String recipeUrlString = "https://d17h27t6h515a5.cloudfront" +
            ".net/topher/2017/May/59121517_baking/baking.json";
    private static final String TAG = "Network_Utilities";

    /**
     * This function will return the recipe URL
     * @return URL
     */
    public static URL getRecipeUrl() {
        try {
            URL recipeUrl = new URL(recipeUrlString);
            return recipeUrl;
        } catch (MalformedURLException e) {
            Log.v(TAG, "Malformed URL string, please check again!");
            return null;
        }
    }

    /**
     * This funciton will get HTTP URL connection with the passed in url argument,
     * make sure to use delimiter to \\A to force all input data. and return the
     * string value inside scanner if any.
     * @param url       Recipe URL
     * @return String   JSON
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        // Open HTTP connection using the url passed
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        // Grab all of the string values from the http request
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            //Force the scanner to read the entire input stream
            scanner.useDelimiter("\\A");
            boolean hasInputs = scanner.hasNext();
            if(hasInputs) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            // Disconnect from the connection
            urlConnection.disconnect();
        }
    }

}
