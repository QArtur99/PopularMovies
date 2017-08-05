package com.android.popularmovies;

import android.content.pm.ActivityInfo;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import com.android.popularmovies.adapter.MyAdapter;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;
    private Boolean condition = true;


    public static ViewAction setTextInTextView(final String value) {
        return new ViewAction() {
            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TextView.class));
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView title = view.findViewById(R.id.movieTitle);
                title.setText(value);
            }

            @Override
            public String getDescription() {
                return "replace text";
            }
        };
    }

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void hasSpecialText() {
        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        while (condition) {
            MyAdapter myAdapter = mActivityTestRule.getActivity().headFragment.moviesAdapter;
            if (myAdapter != null && 20 > myAdapter.getItemCount()) {
                String middleElementText = mActivityTestRule.getActivity().getResources().getString(R.string.middle);
                onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(19, setTextInTextView(middleElementText)));
                onView(withText(middleElementText)).check(matches(isEnabled()));
                onView(withId(android.R.id.content)).perform(ViewActions.swipeUp());
            } else {
                onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(25, click()));
                onView(withId(R.id.toolbarImage)).perform(swipeUp());
                onView((withId(R.id.reviewsButton))).perform(click());
                onView(withId(R.id.dialogTitlte)).check(matches(isDisplayed()));
                condition = false;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }


}
