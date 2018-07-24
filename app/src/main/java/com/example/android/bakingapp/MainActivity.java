package com.example.android.bakingapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.android.bakingapp.Data.RecipeContract;
import com.example.android.bakingapp.Sync.RecipesSyncTask;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
            RecipeAdapter.RecipeAdapterOnClickHandler {

    private static final String TAG = "main_activity";
    private static final String ORIENTATION_CHANGE = "orientation_change";

    // Initialize references
    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;

    // Use integer id for loading recipe data
    private static final int ID_RECIPE_LOADER = 100;
    // Set initial position to -1
    private int mPosition = RecyclerView.NO_POSITION;

    // Projection of columns that will be needed to retrieve in loader
    public static final String[] RECIPE_PROJECTION = {
            RecipeContract.RecipeTable.COL_NAME,
            RecipeContract.RecipeTable.COL_INGREDIENTS,
            RecipeContract.RecipeTable.COL_STEPS,
            RecipeContract.RecipeTable.COL_SERVINGS,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the appropriate screen layout
        if(findViewById(R.id.rv_recipe_cards) != null) {
            // This means we are running in phone screen size
            phoneScreenLayoutAdapter(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIENTATION_CHANGE, "true");
    }

    /**
     * This method will initialize the layout for phone screen size
     * and set the adapter to the recycler view to populate the recipe cards.
     */
    private void phoneScreenLayoutAdapter(Bundle savedInstancedState) {
        // Reference the correct recycler view layout
        mRecyclerView = findViewById(R.id.rv_recipe_cards);

        // Initialize LinearLayout for the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        // Set the layout of the Recycler View
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Will not change the child layout of the Recycler View
        mRecyclerView.setHasFixedSize(true);

        // Instantiate Adapter
        mAdapter = new RecipeAdapter(this,this);

        // Connect the recycler view with the adapter
        mRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(ID_RECIPE_LOADER, null, this);

        // Sync with the JSON if null
        if(savedInstancedState == null) {
            RecipesSyncTask.syncRecipes(this);
        }
    }

    /**
     * Where loader is initiated, it will load the appropriate cursor from content resolver
     * @param loaderId          Loader Table ID
     * @param bundle            Bundle that is saved in the lifecycle
     * @return CursorLoader
     */
    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {
        if(loaderId == ID_RECIPE_LOADER) {
            Uri recipeTableUri = RecipeContract.RecipeTable.CONTENT_URI;
            return new CursorLoader(this,
                    recipeTableUri,
                    RECIPE_PROJECTION,
                    null,
                    null,
                    null);
        } else {
            throw new RuntimeException("Loader not implemented " + loaderId);
        }
    }

    /**
     * After onCreateLoader, swap the old cursor with the new one from content resolver.
     * This way the new updated data will be inflated to the UI
     * @param loader        Loader Object
     * @param cursor        New Cursor
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        // Pass in the new cursor after loading query
        mAdapter.swapCursor(cursor);
        // Initialize the position to 0
        if(mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
        // Set the recycler view to scroll smoothly
        mRecyclerView.smoothScrollToPosition(mPosition);
        // It will take some time to run http request in the background
        if(cursor.getCount() == 0) {
            getSupportLoaderManager().restartLoader(ID_RECIPE_LOADER, null,this);
        }
    }

    /**
     * If loader is reset, erase the data in cursor.
     * @param loader            Loader Object
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void clickedRecipe(int idNumber) {
        // Debugging
        Log.v(TAG,"clicked item " + idNumber);

        // Instantiate intent to detail activity
        Intent recipeDetailActivity = new Intent(MainActivity.this, DetailActivity.class);
        Uri recipeDetailWithId = RecipeContract.RecipeTable.buildUriWithIdRecipe(idNumber);

        // Adding the URI to the intent
        // Pass in the idNumber which correspond to the row id in the table
        recipeDetailActivity.setData(recipeDetailWithId);
        startActivity(recipeDetailActivity);
    }
}
