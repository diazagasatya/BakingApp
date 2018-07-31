package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.Data.RecipeContract;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory(this.getApplicationContext());
    }
}

class ListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    // Projection of columns that will be needed to retrieve in loader
    public static final String[] RECIPE_PROJECTION = {
            RecipeContract.RecipeTable.COL_NAME,
            RecipeContract.RecipeTable.COL_INGREDIENTS,
            RecipeContract.RecipeTable.COL_STEPS,
            RecipeContract.RecipeTable.COL_SERVINGS,
    };

    public ListRemoteViewFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    /**
     * Once the remote factory is created as well as every time its notified
     * to update its data.
     */
    @Override
    public void onDataSetChanged() {
        // Get all of the recipe
        Uri recipeUri = RecipeContract.RecipeTable.CONTENT_URI;
        if(mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                recipeUri,
                RECIPE_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if(mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * Acts like onBindViewHolder method in an Adapter
     * @param position      The current position of the item in the ListView to be displayed
     * @return The RemoteViews object to display for the provided position
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if(mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);
        String recipeName = mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeTable.COL_NAME));
        String recipeServings = mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeTable.COL_SERVINGS));

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget);

        views.setTextViewText(R.id.tv_recipe_name_widget, recipeName);
        views.setTextViewText(R.id.tv_servings_widget, recipeServings);

        // fill in the onClick PendingIntent Template using specific recipe ID for each item individually
        Uri recipeWithUri = RecipeContract.RecipeTable.buildUriWithIdRecipe(position);
        Intent fillInIntent = new Intent();
        fillInIntent.setData(recipeWithUri);
        views.setOnClickFillInIntent(R.id.recipe_widget, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
