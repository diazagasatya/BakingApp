package com.example.android.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.RecipeStepsAdapterViewHolder> {

    // Bind the data with the cursor return from query
    private final Context mContext;
    private ArrayList<String> recipeSteps;

    // Create reference to a click handler for the adapter
    private final RecipeStepsAdapterOnClickHandler mClickHandler;

    /**
     * Default constructor that will initialize the context and clickhandler
     * @param context       Context
     * @param clickHandler  Click Handler
     */
    public RecipeStepsAdapter(Context context, ArrayList<String> recipeStepsArray, RecipeStepsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        recipeSteps = recipeStepsArray;
    }

    /**
     * Will create views for each items in a view holder and return each item view
     * @param parent            View Group
     * @param viewType          View Type
     * @return viewholder
     */
    @NonNull
    @Override
    public RecipeStepsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Initialize the layout for each item and create a view holder to return
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        int layoutForRecipeDesc = R.layout.recipe_steps_item;
        boolean shouldAttachImmediately = false;
        View view = layoutInflater.inflate(layoutForRecipeDesc,parent,shouldAttachImmediately);
        RecipeStepsAdapterViewHolder viewHolder = new RecipeStepsAdapterViewHolder(view);
        return viewHolder;
    }

    /**
     * Bind the recipe short description from the array
     * @param holder        View Holder
     * @param position      Position
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeStepsAdapterViewHolder holder, int position) {
        holder.bind(recipeSteps.get(position));
    }


    /**
     * This will count the number of the item needed to be created
     * @return int
     */
    @Override
    public int getItemCount() {
        if(recipeSteps.size() == 0) return 0;
        return recipeSteps.size();
    }


    /**
     * Interface that will receive the clicked item
     */
    public interface RecipeStepsAdapterOnClickHandler {
        void clickedRecipeSteps(int idNumber);
    }

    public class RecipeStepsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Add a reference to the short description
        TextView nRecipeShortDesc;

        /**
         * Will create reference to the items inside the layout to be binded
         * @param viewItem
         */
        public RecipeStepsAdapterViewHolder(View viewItem) {
            super(viewItem);
            // Reference the text view from the layout
            nRecipeShortDesc = viewItem.findViewById(R.id.tv_recipe_short_desc);
            // Set the listener to this view
            viewItem.setOnClickListener(this);
        }

        /**
         * Bind the respected string to the recipe short description text view
         * @param recipeShortDesc       Recipe Short Description
         */
        public void bind(String recipeShortDesc) {
            nRecipeShortDesc.setText(recipeShortDesc);
        }

        /**
         * Will return the id of the clicked item to the main activity
         * @param view      View Clicked
         */
        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            mClickHandler.clickedRecipeSteps(itemPosition);
        }
    }
}

