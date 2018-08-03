package com.example.android.bakingapp;

import android.support.test.espresso.ViewInteraction;
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
import static org.hamcrest.core.AllOf.allOf;


@RunWith(JUnit4.class)
public class MainActivityBasicTest {

    // Create the rule here that the main test activity will be in MainActivity
    @Rule public ActivityTestRule<MainActivity> mActivityBasicTestRule =
            new ActivityTestRule<>(MainActivity.class);

    /**
     * This test will check if one of the recipe is clicked in MainAcitivity
     * it will go to the DetailActivity.
     */
    @Test
    public void recipeOnclickTest_showDetailInformation() {
        ViewInteraction recyclerView = onView(allOf(withId(R.id.rv_recipe_cards), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(1, click()));

        onView(withId(R.id.rv_recipe_ingredients)).check(matches(isDisplayed()));
    }
}
