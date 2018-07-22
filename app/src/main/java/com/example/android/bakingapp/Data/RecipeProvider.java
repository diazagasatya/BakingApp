package com.example.android.bakingapp.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RecipeProvider extends ContentProvider {

    // Create the instance of the database
    private RecipeDBHelper mRecipeDatabase;

    // Create an integer reference to the recipe table
    private static final int RECIPE_PATH = 100;
    private static final int RECIPE_PATH_WITH_ID = 101;

    // Create a glboal variable of uriMatcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * This helper method will create an instance of UriMatcher with the
     * registered paths added to it.
     */
    public static UriMatcher buildUriMatcher() {

        // Instantiate a blank Uri Matcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add the URI's that matches the tables in the recipe database
        uriMatcher.addURI(RecipeContract.CONTENT_AUTHORITY,
                RecipeContract.PATH_RECIPE, RECIPE_PATH);
        uriMatcher.addURI(RecipeContract.CONTENT_AUTHORITY,
                RecipeContract.PATH_RECIPE + "/#", RECIPE_PATH_WITH_ID);

        return uriMatcher;
    }

    /**
     * Will initialize the content provider on the initial startup. This will create the database
     * on the main thread on the start of the application.
     * @return boolean      Return true if cp is instantiated successfully.
     */
    @Override
    public boolean onCreate() {
        // Instantiate the Database that will be used in this provider
        Context context = getContext();
        mRecipeDatabase = new RecipeDBHelper(context);

        return true;
    }

    /**
     * Will build a SQL Query to the database and retrieve the appropriate data as a cursor.
     * @param uri               Uri of the query
     * @param projections       Columns to return
     * @param selection         Selection to return
     * @param selectionArgs     Where clause
     * @param sortOrder         Sort the query by
     * @return Cursor
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projections, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get the reference to the Recipe database
        final SQLiteDatabase db = mRecipeDatabase.getReadableDatabase();

        // Reference to the cursor
        Cursor resultData;

        // Use a switch statement to return the cursor from the SQL Query
        switch(sUriMatcher.match(uri)) {
            case RECIPE_PATH:
                // Retrieve the appropriate query
                resultData = db.query(RecipeContract.RecipeTable.TABLE_NAME,
                        projections,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case RECIPE_PATH_WITH_ID:
                // Get the id from the Uri
                String id = uri.getPathSegments().get(1);
                // Selection is the _ID column = ?, and the selection args = the Row ID
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                // Retrieve the appropriate query
                resultData = db.query(RecipeContract.RecipeTable.TABLE_NAME,
                        projections,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unkown Uri " + uri);
        }

        // Set a notification URI on the cursor
        resultData.setNotificationUri(getContext().getContentResolver(),uri);

        return resultData;
    }

    /**
     * Not implementing getType for this application
     * @param uri       Uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Not implementing insert - but implementing bulkInsert for this application
     * @param uri               Uri
     * @param contentValues     Values need to be inserted to the database
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    /**
     * Not implementing delete for this application
     * @param uri           Uri
     * @param selection     Selection to Delete
     * @param selectionArgs Selection Argument
     * @return int
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    /**
     * Not implementing update on this application
     * @param uri               Uri
     * @param contentValues     Values need to be updated in db
     * @param s                 Selection
     * @param strings           SelectionArgs
     * @return int
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    /**
     * This function will bulk insert the data to the appropriate table.
     * @param uri           Uri Table
     * @param values        Content Values
     * @return int
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        // Create the reference to the writable database
        final SQLiteDatabase db = mRecipeDatabase.getWritableDatabase();

        // Use switch statement to insert to appropriate table
        switch(sUriMatcher.match(uri)) {
            case RECIPE_PATH:
                // Begin the transaction here
                db.beginTransaction();
                int numberOfRowsInserted = 0;

                // Insert the content values to the database inside try and catch block
                try {
                    // Iterate for all of the values inside the cv array
                    for(ContentValues value : values) {
                        // Insert the cv to the database
                        long _id = db.insert(RecipeContract.RecipeTable.TABLE_NAME,
                                null,value);
                        if(_id != -1) {
                            numberOfRowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    // End the transaction here
                    db.endTransaction();
                }

                // Make sure to notify change if bulk insert was successful
                if(numberOfRowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return numberOfRowsInserted;
            default:
                return super.bulkInsert(uri, values);

        }
    }
}
