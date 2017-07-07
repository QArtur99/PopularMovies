package com.android.popularmovies;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.popularmovies.adapter.Movie;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ART_F on 2017-07-06.
 */

public class MovieDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.collapsingToolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarImage) ImageView toolbarImage;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.movieTitle) TextView movieTitle;
    @BindView(R.id.overview) TextView overview;
    @BindView(R.id.voteAverage) TextView voteAverage;
    @BindView(R.id.releaseDate) TextView releaseDate;
    @BindView(R.id.detail) RelativeLayout detailLayout;
    @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;

    private Movie movie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        movie = new Gson().fromJson(getIntent().getStringExtra("movie"), Movie.class);
        appBar.addOnOffsetChangedListener(this);

        setPosterSize();
        setPoster();
        setDetailData();
    }

    private void setDetailData() {
        movieTitle.setText(movie.original_title);
        overview.setText(movie.overview);
        voteAverage.setText(movie.vote_average);
        releaseDate.setText(movie.release_date);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        double percentage = (double) Math.abs(verticalOffset) / collapsingToolbar.getHeight();
        if (percentage > 0.8) {
            collapsingToolbar.setTitle(movie.original_title);
            movieTitle.setVisibility(View.GONE);
        } else {
            collapsingToolbar.setTitle("");
            movieTitle.setText(movie.original_title);
            movieTitle.setVisibility(View.VISIBLE);
        }
    }

    private void setPoster() {
        String posterURL = "http://image.tmdb.org/t/p/w500/" + movie.poster_path;
        Picasso.with(this).load(posterURL).into(toolbarImage);

        LayerDrawable cellLayerDrawable = (LayerDrawable) detailLayout.getBackground();
        Drawable drawable = toolbarImage.getDrawable();
        cellLayerDrawable.setDrawableByLayerId(R.id.backgroundBitmap, drawable);
    }

    public void setPosterSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        ViewGroup.LayoutParams params = toolbarImage.getLayoutParams();
        params.height = (int) (size.x * 1.5);
        toolbarImage.setLayoutParams(params);
    }
}
