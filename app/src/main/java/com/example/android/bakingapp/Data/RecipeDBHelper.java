package com.example.android.bakingapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This DB Helper will create the database and initialize the table needed
 */
public class RecipeDBHelper extends SQLiteOpenHelper {

    // Name of the database
    public static final String DATABASE_NAME = "baking_app.db";

    // Initiate the version of the database from 1 and increment if changes made
    public static final int DATABASE_VERSION = 1;

    /**
     * Create the database with the name and its version
     * @param context       Context of the application
     */
    public RecipeDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    /**
     * This method will initialize the table with its corresponding data types
     * @param sqLiteDatabase            SQLite Database
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the SQL create table query
        final String CREATE_RECIPE_TABLE = "CREATE TABLE "
                + RecipeContract.RecipeTable.TABLE_NAME + " ("
                + RecipeContract.RecipeTable.COL_ID + " TEXT NOT NULL, "
                + RecipeContract.RecipeTable.COL_NAME + " TEXT NOT NULL, "
                + RecipeContract.RecipeTable.COL_INGREDIENTS + " TEXT NOT NULL, "
                + RecipeContract.RecipeTable.COL_STEPS + " TEXT NOT NULL, "
                + RecipeContract.RecipeTable.COL_SERVINGS + " TEXT NOT NULL )";

        // Execute database query here to create the table
        sqLiteDatabase.execSQL(CREATE_RECIPE_TABLE);
    }

    /**
     * Upgrade the current database with the newest version edited.
     * @param sqLiteDatabase            SQLite Database
     * @param oldVersion                Old Version number
     * @param newVersion                New Version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if(newVersion > oldVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeTable.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
