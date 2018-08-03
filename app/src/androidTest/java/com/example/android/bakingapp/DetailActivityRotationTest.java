package com.example.android.bakingapp;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(JUnit4.class)
public class DetailActivityRotationTest {

    @Rule public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);


    /**
     * This test will first clicked the Nutella Pie recipe, checking if the
     * ingredients and steps are visible after scrolling. Play and Pause the video
     * and click the next navigation bar to check if its working by checking if the
     * prev nav is visible. (The first step will not show prev nav)
     */
    @Test
    public void recipeTestClickedPlayPauseVideo_prevNavigationDisplayed() {

        // Click the Nutella Pie recipe on the recycler view
        onView(withId(R.id.rv_recipe_cards))
                .perform(actionOnItemAtPosition(0, click()));
        // Check if ingredient is displayed
        onView(withId(R.id.rv_recipe_ingredients)).check(matches(isDisplayed()));

        // Scroll the View to find the recipe steps
        onView(withId(R.id.rv_recipe_steps)).perform(ViewActions.scrollTo());

        // Click the recipe steps
        onView(withId(R.id.rv_recipe_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        // Check if the video is displayed
        onView(withId(R.id.media_player_container)).check(matches(isDisplayed()));

        // Play the video
        onView(withId(R.id.exo_play)).perform(click());

        // Pause the video
        onView(withId(R.id.exo_pause)).perform(click());

        // Click the next navigation button
        onView(withId(R.id.ib_next_nav)).perform(click());

        // Check if the prev navigation button is visible
        onView(withId(R.id.ib_prev_nav)).check(matches(isDisplayed()));
    }

}
