package com.example.android.bakingapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.android.bakingapp.R;

public class NavigationStepsFragment extends Fragment {

    // Reference to buttons
    private ImageButton previousB, nextB;
    private NavigationOnClickListener mCallback;

    // Reference for host activity
    private static final int PREVIOUS_BUTTON = -1;
    private static final int NEXT_BUTTON = 1;

    // Curent step position
    private int stepsPosition;

    // Get position from argument passed in host activity
    private static final String STEPS_POSITION = "steps_position";

    /**
     * This interface will interact with the host activity to change the other fragments
     */
    public interface NavigationOnClickListener {
        void navigationClicked(int navigationButton);
    }

    /**
     * Make sure the container activity has the callback
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Make sure the host activity implement the callback interface
        try {
            mCallback = (NavigationOnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " implement OnStepsClickListener");
        }
    }

    /**
     * Default Constructor
     */
    public NavigationStepsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate root view of this fragment to UI
        View rootView = inflater.inflate(R.layout.fragment_navigation,container,false);

        // Reference to the image buttons
        previousB = rootView.findViewById(R.id.ib_prev_nav);
        nextB = rootView.findViewById(R.id.ib_next_nav);

        // Grab the position of the steps clicked
        if(getArguments() != null) {
            stepsPosition = getArguments().getInt(STEPS_POSITION);
        }

        // If at step 0 set previous to invisible
        if(stepsPosition == 0) {
            previousB.setVisibility(View.INVISIBLE);
            nextB.setVisibility(View.VISIBLE);
        } else {
            previousB.setVisibility(View.VISIBLE);
            nextB.setVisibility(View.VISIBLE);
        }

        previousB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.navigationClicked(PREVIOUS_BUTTON);
            }
        });

        nextB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.navigationClicked(NEXT_BUTTON);
            }
        });

        return rootView;
    }

}
