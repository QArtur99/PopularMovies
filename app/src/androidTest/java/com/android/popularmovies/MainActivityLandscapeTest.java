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

import com.android.popularmovies.activities.MainActivity;
import com.android.popularmovies.adapters.MoviesAdapter;
import com.android.popularmovies.fragments.GridViewFragment;
import com.android.popularmovies.fragments.MovieDetailFragment;
import com.android.popularmovies.fragments.PosterFragment;

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
public class MainActivityLandscapeTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;
    private Boolean condition = true;
    private GridViewFragment gridViewFragment;
    private PosterFragment posterFragment;
    private MovieDetailFragment movieDetailFragment;

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

    @Before
    public void setUp() {
        gridViewFragment = new GridViewFragment();
        posterFragment = new PosterFragment();
        movieDetailFragment = new MovieDetailFragment();
    }

    @Test
    public void landscapeViewTest() {
        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        while (condition) {
            MoviesAdapter myAdapter = mActivityTestRule.getActivity().headFragment.moviesAdapter;
            if (myAdapter != null && 20 > myAdapter.getItemCount()) {

                String middleElementText = mActivityTestRule.getActivity().getResources().getString(R.string.middle);
                onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(19, setTextInTextView(middleElementText)));
                onView(withText(middleElementText)).check(matches(isEnabled()));
                onView(withId(R.id.test)).perform(ViewActions.swipeUp());
            } else {
                onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(25, click()));

                movieDetailFragmentTest();
                posterFragmentTest();
                condition = false;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void movieDetailFragmentTest() {
        mActivityTestRule.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.detailsViewFrame, movieDetailFragment).commit();

        onView(withId(R.id.test2)).perform(swipeUp());
        onView((withId(R.id.reviewsButton))).check(matches(isDisplayed()));
    }

    private void posterFragmentTest() {
        mActivityTestRule.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.posterViewFrame, posterFragment).commit();

        onView(withId(R.id.posterView)).check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }


}