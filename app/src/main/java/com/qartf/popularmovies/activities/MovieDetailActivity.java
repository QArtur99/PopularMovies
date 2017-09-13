package com.qartf.popularmovies.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qartf.popularmovies.adapters.Movie;
import com.qartf.popularmovies.database.DatabaseContract;
import com.qartf.popularmovies.fragments.MovieDetailFragment;
import com.qartf.popularmovies.network.DetailsLoader;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener
        , LoaderManager.LoaderCallbacks {

    @Nullable
    @BindView(com.qartf.popularmovies.R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Nullable
    @BindView(com.qartf.popularmovies.R.id.appBar)
    AppBarLayout appBar;
    @Nullable
    @BindView(com.qartf.popularmovies.R.id.toolbarImage)
    ImageView toolbarImage;
    @BindView(com.qartf.popularmovies.R.id.toolbar) Toolbar toolbar;
    @Nullable
    @BindView(com.qartf.popularmovies.R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    @Nullable
    @BindView(com.qartf.popularmovies.R.id.fabTopBackground)
    LinearLayout fabTopBackground;
    @Nullable
    @BindView(com.qartf.popularmovies.R.id.fabTop)
    FloatingActionButton fabTop;

    @BindView(com.qartf.popularmovies.R.id.fabBottomBackground) LinearLayout fabBottomBackground;
    @BindView(com.qartf.popularmovies.R.id.fabBottom) FloatingActionButton fabBottom;
    private Movie movie;
    private MovieDetailFragment movieDetailFragment;
    private boolean isFavorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.qartf.popularmovies.R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.finish();
            return;
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        movie = new Gson().fromJson(getIntent().getStringExtra("movie"), Movie.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            appBar.addOnOffsetChangedListener(this);
        }

        if(toolbarImage != null) {
            setPosterSize();
            setPoster();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        movieDetailFragment = new MovieDetailFragment();
        movieDetailFragment.setMovie(movie);
        fragmentManager.beginTransaction()
                .add(com.qartf.popularmovies.R.id.detailsViewFrame, movieDetailFragment)
                .commit();

        isFavorite();

    }



    private void isFavorite() {
        String selection = DatabaseContract.Movies.MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{movie.id};
        Cursor cursor = getContentResolver().query(DatabaseContract.Movies.CONTENT_URI, DatabaseContract.Movies.PROJECTION_LIST,
                selection, selectionArgs, null);

        if (cursor != null && cursor.getCount() == 0) {
            isFavorite = false;
        } else {
            isFavorite = true;
        }

        setFabIcon();
        if (cursor != null) {
            cursor.close();
        }
    }

    private void setFabIcon() {
        if (isFavorite) {
            if(fabTop != null) {
                fabTop.setImageResource(com.qartf.popularmovies.R.drawable.ic_favorite);
            }
            fabBottom.setImageResource(com.qartf.popularmovies.R.drawable.ic_favorite);
        } else {
            if(fabTop != null) {
                fabTop.setImageResource(com.qartf.popularmovies.R.drawable.ic_favorite_border);
            }
            fabBottom.setImageResource(com.qartf.popularmovies.R.drawable.ic_favorite_border);
        }
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        final int startScrollPos = getResources().getDimensionPixelSize(com.qartf.popularmovies.R.dimen.init_scroll_distance);
        Animator animator = ObjectAnimator.ofInt(
                nestedScrollView,
                "scrollY",
                startScrollPos).setDuration(300);
        animator.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        double percentage = (double) Math.abs(verticalOffset) / collapsingToolbar.getHeight();
        if (percentage > 0.8) {
            collapsingToolbar.setTitle(movie.original_title);
            movieDetailFragment.hideTitle();
            fabTopBackground.setVisibility(View.GONE);
            fabBottomBackground.setVisibility(View.VISIBLE);
        } else {
            collapsingToolbar.setTitle("");
            movieDetailFragment.showTitle();
            fabTopBackground.setVisibility(View.VISIBLE);
            fabBottomBackground.setVisibility(View.GONE);
        }
    }

    private void setPoster() {
        String posterURL = "http://image.tmdb.org/t/p/w500/" + movie.poster_path;
        Picasso.with(this).load(posterURL).into(toolbarImage);
    }

    public void setPosterSize() {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            ViewGroup.LayoutParams params = toolbarImage.getLayoutParams();
            params.height = (int) (size.x * 1.5);
            toolbarImage.setLayoutParams(params);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new DetailsLoader(MovieDetailActivity.this, id, movie.id);
    }

    @Override
    public void onLoadFinished(Loader loader, Object object) {
        switch (loader.getId()) {
            case 2:
                movieDetailFragment.onLoadFinished(loader, object);
                break;
            case 3:
                movieDetailFragment.onLoadFinished(loader, object);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        movieDetailFragment.onLoaderReset();
    }

    public void addFavorite(View view) {
        isFavorite = !isFavorite;
        setFabIcon();
        movieDetailFragment.addFavorite();
    }
}
