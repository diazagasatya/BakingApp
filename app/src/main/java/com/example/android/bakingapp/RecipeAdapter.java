package com.example.android.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.Data.RecipeContract;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    // Bind data with the cursor return from query
    private Cursor mCursor;
    private final Context mContext;

    // Create a reference to a click handler for the adapter
    private final RecipeAdapterOnClickHandler mClickHandler;

    /**
     * Initialie the onClickHandler and context from MainActivity
     * @param context           Context application
     * @param onClickHandler    Click Handler
     */
    public RecipeAdapter(Context context, RecipeAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
    }

    /**
     * Will create the views and group it into a view group then inflate it to the layout
     * @param parent            Group of items needed to be inflated in one layout
     * @param viewType          View Type
     * @return viewHolder
     */
    @NonNull
    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Use this object to inflate the view group of items
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        // Get the int reference from the layout
        int layoutForRecipeCards = R.layout.recipe_linear_item;

        // Make sure to not attach the view group to parent immediately
        boolean shouldNotAttachImmediately = false;

        // Inflate the layout
        View view = layoutInflater.inflate(layoutForRecipeCards,parent,shouldNotAttachImmediately);

        // Initialize the view holder with the items needed to be referenced to
        RecipeAdapterViewHolder viewHolder = new RecipeAdapterViewHolder(view);

        return viewHolder;
    }

    /**
     * Bind the information to the corresponding views
     * @param holder        View Holders
     * @param position      Position of the cursor
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeAdapterViewHolder holder, int position) {

        // Initiate variable if null
        String rName = "N/A";
        String rServings = "N/A";
        // If the cursor reached null return
        if(!mCursor.moveToPosition(position)) return;
        // Get the strings
        rName = mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeTable.COL_NAME));
        rServings = mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeTable.COL_SERVINGS));

        // Bind the name and the servings
        holder.bind(rName,rServings);
    }

    /**
     * Will swap the old cursor with the newly passed.
     * @param newCursor     new data
     */
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * Will return the number of items needed to be populated
     * @return int
     */
    @Override
    public int getItemCount() {
        if(mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * Interface that will receive the clicked item
     */
    public interface RecipeAdapterOnClickHandler {
        void clickedRecipe(int idNumber);
    }

    /**
     * Will return the number of items inserted to the view holder to cache
     */
    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Add a reference to the name and servings from the linear item layout
        TextView nRecipeName, nRecipeServings;

        /**
         * Will create reference to the items inside the layout to be binded
         * @param viewItem
         */
        public RecipeAdapterViewHolder(View viewItem) {
            super(viewItem);
            // Reference the image view and text view from the layout
            nRecipeName = viewItem.findViewById(R.id.tv_recipe_name);
            nRecipeServings = viewItem.findViewById(R.id.tv_servings);
            // Set the listener to this view
            viewItem.setOnClickListener(this);
        }

        /**
         * This function will bind the recipe name and servings
         * @param recipeName            recipe name
         * @param recipeServings        recipe servings
         */
        public void bind(String recipeName, String recipeServings) {
            nRecipeName.setText(recipeName);
            nRecipeServings.setText(recipeServings);
        }

        /**
         * Will return the id of the clicked item to the main activity
         * @param view      View clicked
         */
        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            mClickHandler.clickedRecipe(itemPosition);
        }
    }
}
