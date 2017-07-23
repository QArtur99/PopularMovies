package com.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.popularmovies.adapter.Movie;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements GridViewFragment.OnImageClickListener {

    private MovieDetailFragment movieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fragmentManager = getSupportFragmentManager();
        GridViewFragment headFragment = new GridViewFragment();
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
}
