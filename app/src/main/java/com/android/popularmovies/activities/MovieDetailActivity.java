package com.android.popularmovies.activities;

import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.popularmovies.R;
import com.android.popularmovies.adapters.Movie;
import com.android.popularmovies.network.DetailsLoader;
import com.android.popularmovies.fragments.MovieDetailFragment;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener
        , LoaderManager.LoaderCallbacks {

    @BindView(R.id.collapsingToolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarImage) ImageView toolbarImage;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;
    @BindView(R.id.fab) LinearLayout fab;
    @BindView(R.id.fabBottom) LinearLayout fabBottom;

    private Movie movie;
    private MovieDetailFragment movieDetailFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
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
        appBar.addOnOffsetChangedListener(this);

        setPosterSize();
        setPoster();

        FragmentManager fragmentManager = getSupportFragmentManager();
        movieDetailFragment = new MovieDetailFragment();
        movieDetailFragment.setMovie(movie);
        fragmentManager.beginTransaction()
                .add(R.id.detailsViewFrame, movieDetailFragment)
                .commit();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        double percentage = (double) Math.abs(verticalOffset) / collapsingToolbar.getHeight();
        if (percentage > 0.8) {
            collapsingToolbar.setTitle(movie.original_title);
            movieDetailFragment.hideTitle();
            fab.setVisibility(View.GONE);
            fabBottom.setVisibility(View.VISIBLE);
        } else {
            collapsingToolbar.setTitle("");
            movieDetailFragment.showTitle();
            fab.setVisibility(View.VISIBLE);
            fabBottom.setVisibility(View.GONE);
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

    @OnClick(R.id.fab)
    public void addFavorite(View view) {
        movieDetailFragment.addFavorite();
    }
}
