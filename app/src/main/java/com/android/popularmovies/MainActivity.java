package com.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.popularmovies.adapter.Movie;
import com.android.popularmovies.background.DetailsLoader;
import com.android.popularmovies.background.MoviesLoader;
import com.android.popularmovies.database.DatabaseContract;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GridViewFragment.OnImageClickListener, LoaderManager.LoaderCallbacks {

    private MovieDetailFragment movieDetailFragment;
    private GridViewFragment headFragment;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        headFragment = new GridViewFragment();
        fragmentManager.beginTransaction()
                .add(R.id.gridViewMain, headFragment)
                .commit();

        if (findViewById(R.id.detailsViewFrame) == null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

    }

    @Override
    public void onImageSelected(Movie movie) {
        if (findViewById(R.id.detailsViewFrame) == null) {
            String jsonString = new Gson().toJson(movie);
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie", jsonString);
            startActivity(intent);
        } else {
            loadDetails(movie);
        }
    }

    private void loadDetails(Movie movie) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PosterFragment posterFragment = new PosterFragment();
        posterFragment.setPoster(movie.poster_path);
        fragmentManager.beginTransaction()
                .add(R.id.posterViewFrame, posterFragment)
                .commit();

        movieDetailFragment = new MovieDetailFragment();
        movieDetailFragment.setMovie(movie);
        fragmentManager.beginTransaction()
                .add(R.id.detailsViewFrame, movieDetailFragment)
                .commit();
    }

    public void showReviews(View view) {
        movieDetailFragment.showReviews(view);
    }

    public void showTrailers(View view) {
        movieDetailFragment.showTrailers(view);
    }

    public void addFavorite(View view) {
        movieDetailFragment.addFavorite();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new CursorLoader(this, DatabaseContract.Movies.CONTENT_URI, DatabaseContract.Movies.PROJECTION_LIST, null, null, null);
            case 1:
                return new MoviesLoader(this, headFragment.getSortBy(), String.valueOf(headFragment.getPageNo()));
            case 2:
                return new DetailsLoader(this, id, movieDetailFragment.movie.id);
            case 3:
                return new DetailsLoader(this, id, movieDetailFragment.movie.id);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, final Object object) {
        List<Movie> temp = new ArrayList<>();
        switch (loader.getId()) {
            case 0:
                cursor = (Cursor) object;
                if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
                    do {
                        Movie movie = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(DatabaseContract.Movies.MOVIE)), Movie.class);
                        temp.add(movie);
                    } while (cursor.moveToNext());
                }
                headFragment.onLoadFinished(temp);
                break;
            case 1:
                temp = (List<Movie>) object;
                headFragment.onLoadFinished(temp);
                break;
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
        if (2 > loader.getId()) {
            headFragment.onRestartLoader();
        } else {
            movieDetailFragment.onLoaderReset();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
    }

}
