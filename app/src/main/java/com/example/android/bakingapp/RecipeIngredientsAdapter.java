package com.example.android.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.RecipeIngredientsAdapterViewHolder>{

    // Bind data with the JSONArray from query
    private JSONArray mJsonArray;
    private final Context mContext;

    public static final String INGREDIENT = "ingredient";
    public static final String MEASURE = "measure";
    public static final String QUANTITY = "quantity";

    /**
     * Constructor initializing context and recipe ingredients
     * @param context               Context Application
     * @param recipeIngredients     Recipe Ingredients
     */
    public RecipeIngredientsAdapter(Context context, JSONArray recipeIngredients) {
        mContext = context;
        mJsonArray = recipeIngredients;
    }

    /**
     * Will create the views and group it into a view group then inflate it to the layout
     * @param parent            Group of items needed to be inflated in one layout
     * @param viewType          View Type
     * @return viewHolder
     */
    @NonNull
    @Override
    public RecipeIngredientsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the view group of items
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        // Get the int reference from the layout
        int layoutForRecipeIngredients = R.layout.recipe_ingredient_item;
        // No attach to parent immediately
        boolean shouldNotAttachImmediately = false;
        // Inflate the layout
        View view = layoutInflater.inflate(layoutForRecipeIngredients,parent,shouldNotAttachImmediately);
        // Initialize view holder to inflate to UI
        RecipeIngredientsAdapterViewHolder viewHolder = new RecipeIngredientsAdapterViewHolder(view);

        return viewHolder;
    }

    /**
     * Bind the information to the corresponding views
     * @param holder        View Holders
     * @param position      Position of the cursor
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeIngredientsAdapterViewHolder holder, int position) {

        String rIngredientName = "N/A";
        String rQuantity = "N/A";
        String rUnit = "N/A";

        if(mJsonArray.length() == 0) {
            return;
        }

        // Surround with try catch block
        try {
            rIngredientName = mJsonArray.getJSONObject(position).getString(INGREDIENT);
            rQuantity = mJsonArray.getJSONObject(position).getString(QUANTITY);
            rUnit = mJsonArray.getJSONObject(position).getString(MEASURE);
            // Bind the string to the view
            holder.bind(rIngredientName,rQuantity,rUnit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will return the number of items needed to be populated
     * @return int
     */
    @Override
    public int getItemCount() {
        if(mJsonArray.length() == 0) return 0;
        return mJsonArray.length();
    }


    /**
     * Will return the bind view to cache
     */
    public class RecipeIngredientsAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView rIngredientName, rQuantity, rUnit;

        /**
         * Constructor to initialize reference to each items
         * @param viewItem          View Item
         */
        public RecipeIngredientsAdapterViewHolder(View viewItem) {
            super(viewItem);
            rIngredientName = viewItem.findViewById(R.id.tv_recipe_item_name);
            rQuantity = viewItem.findViewById(R.id.tv_recipe_quantity);
            rUnit = viewItem.findViewById(R.id.tv_recipe_unit);
        }

        /**
         * Bind the string values to the Text Views
         * @param ingredientName            Ingredient name
         * @param quantity                  Quantity of ingredient
         * @param unit                      Ingredient unit
         */
        public void bind(String ingredientName, String quantity, String unit) {
            rIngredientName.setText(ingredientName);
            rQuantity.setText(quantity);
            rUnit.setText(unit);
        }

    }
}
