package com.example.android.bakingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the appropriate screen layout
        if(findViewById(R.id.rv_recipe_cards) != null) {
            // This means we are running in phone screen size
            phoneScreenLayoutAdapter();
        }
    }

    /**
     * This method will initialize the layout for phone screen size
     * and set the adapter to the recycler view to populate the recipe cards.
     */
    private void phoneScreenLayoutAdapter() {
        // Reference the correct recycler view layout
        mRecyclerView = findViewById(R.id.rv_recipe_cards);

        // Initialize LinearLayout for the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        // Set the layout of the Recycler View
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Will not change the child layout of the Reycler View
        mRecyclerView.setHasFixedSize(true);

        // TODO(1) Create a class adapter of the Recycler View
        // TODO(2) Set the adapter of the Recycler View
        // TODO(3) Initialize the loader for populating the items in Recycler View
        // TODO(4) Sync the database with JSON here

    }
}
