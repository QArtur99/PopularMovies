package com.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.popularmovies.adapter.DetailsAdapter;
import com.android.popularmovies.adapter.Movie;
import com.android.popularmovies.background.DetailsLoader;
import com.android.popularmovies.database.DatabaseContract.Movies;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ART_F on 2017-07-06.
 */

public class MovieDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener
        , DetailsAdapter.ListItemClickListener
        , LoaderManager.LoaderCallbacks {

    @BindView(R.id.collapsingToolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarImage) ImageView toolbarImage;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.movieTitleDetail) TextView movieTitle;
    @BindView(R.id.overview) TextView overview;
    @BindView(R.id.voteAverage) TextView voteAverage;
    @BindView(R.id.releaseDate) TextView releaseDate;
    @BindView(R.id.detail) LinearLayout detailLayout;
    @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;
    @BindView(R.id.fab) LinearLayout fab;
    @BindView(R.id.fabBottom) LinearLayout fabBottom;
    private List<JSONObject> jsonObjectTrailers, jsonObjectReviews;
    private Movie movie;
    private int dialogInfo;
    private DetailsAdapter detailsAdapter;

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
        setTextBackground();
        setDetailData();

        getSupportLoaderManager().initLoader(2, null, this).forceLoad();
        getSupportLoaderManager().initLoader(3, null, this).forceLoad();
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
            fab.setVisibility(View.GONE);
            fabBottom.setVisibility(View.VISIBLE);


        } else {
            collapsingToolbar.setTitle("");
            movieTitle.setText(movie.original_title);
            movieTitle.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            fabBottom.setVisibility(View.GONE);
        }
    }

    private void setPoster() {
        String posterURL = "http://image.tmdb.org/t/p/w500/" + movie.poster_path;
        Picasso.with(this).load(posterURL).into(toolbarImage);
    }

    private void setTextBackground() {
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

    public void showReviews(View view) {
        if (jsonObjectReviews.isEmpty()) {
            Toast.makeText(this, "This movie doesn't have any review", Toast.LENGTH_SHORT).show();
            return;
        }
        dialogInfo = 2;
        setDetailDialog();
    }

    public void showTrailers(View view) {
        if (jsonObjectTrailers.isEmpty()) {
            Toast.makeText(this, "This movie doesn't have any trailer", Toast.LENGTH_SHORT).show();
            return;
        }
        dialogInfo = 3;
        setDetailDialog();
    }

    private void setDetailDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_details)
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialogTitlte);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        switch (dialogInfo) {
            case 2:
                dialogTitle.setText("Reviews");
                detailsAdapter = new DetailsAdapter(jsonObjectReviews, this, dialogInfo);
                break;
            case 3:
                dialogTitle.setText("Trailers");
                detailsAdapter = new DetailsAdapter(jsonObjectTrailers, this, dialogInfo);
                break;
        }
        recyclerView.setAdapter(detailsAdapter);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new DetailsLoader(MovieDetailActivity.this, id, movie.id);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case 1:
                jsonObjectReviews = (List<JSONObject>) data;
                break;
            case 2:
                jsonObjectTrailers = (List<JSONObject>) data;
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if(detailsAdapter != null) {
            detailsAdapter.setData(new ArrayList<JSONObject>());
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        JSONObject jsonObject;
        switch (dialogInfo) {
            case 1:
                jsonObject = jsonObjectReviews.get(clickedItemIndex);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("url"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                jsonObject = jsonObjectTrailers.get(clickedItemIndex);
                try {
                    String youTubeBase = "https://www.youtube.com/watch?v=";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youTubeBase + jsonObject.getString("key"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    public void addFavorite(View view) {
        String selection = Movies.MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{movie.id};
        Cursor cursor = getContentResolver().query(Movies.CONTENT_URI, Movies.PROJECTION_LIST,
                selection, selectionArgs, null);

        if (cursor != null && cursor.getCount() == 0) {
            insertMovie();
        } else {
            deleteMovie(cursor);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void deleteMovie(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndex(Movies._ID));
            Uri currentMovieUri = ContentUris.withAppendedId(Movies.CONTENT_URI, id);
            int rowsDeleted = getContentResolver().delete(currentMovieUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void insertMovie() {
        ContentValues values = new ContentValues();
        values.put(Movies.MOVIE_ID, movie.id);
        values.put(Movies.MOVIE, getIntent().getStringExtra("movie"));
        Uri newUri = getContentResolver().insert(Movies.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, getString(R.string.editor_insert_movie_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_insert_movie_successful), Toast.LENGTH_SHORT).show();
        }
    }
}
