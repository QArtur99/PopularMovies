package com.android.popularmovies.fragments;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.popularmovies.R;
import com.android.popularmovies.activities.MainActivity;
import com.android.popularmovies.activities.MovieDetailActivity;
import com.android.popularmovies.adapters.DetailsAdapter;
import com.android.popularmovies.adapters.Movie;
import com.android.popularmovies.database.DatabaseContract;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MovieDetailFragment extends Fragment implements DetailsAdapter.ListItemClickListener {

    public Movie movie;
    @BindView(R.id.movieTitleDetail) TextView movieTitle;
    @BindView(R.id.overview) TextView overview;
    @BindView(R.id.voteAverage) TextView voteAverage;
    @BindView(R.id.releaseDate) TextView releaseDate;
    @BindView(R.id.detail) LinearLayout detailLayout;
    private List<JSONObject> jsonObjectTrailers, jsonObjectReviews;
    private int dialogInfo;
    private DetailsAdapter detailsAdapter;
    private boolean firstStart = true;

    public MovieDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);
        setData();
        return rootView;
    }

    private void setData() {
        if (movie != null) {

            setTextBackground();
            setDetailData();

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                MainActivity mainActivity = ((MainActivity) getActivity());
                getActivity().getSupportLoaderManager().restartLoader(2, null, mainActivity).forceLoad();
                getActivity().getSupportLoaderManager().restartLoader(3, null, mainActivity).forceLoad();
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                MovieDetailActivity mainActivity = ((MovieDetailActivity) getActivity());
                getActivity().getSupportLoaderManager().restartLoader(2, null, mainActivity).forceLoad();
                getActivity().getSupportLoaderManager().restartLoader(3, null, mainActivity).forceLoad();
            }
        }
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void loadNewMovie(Movie movie) {
        this.movie = movie;
        setData();
    }

    private void setDetailData() {
        if (movie != null) {
            movieTitle.setText(movie.original_title);
            overview.setText(movie.overview);
            voteAverage.setText(movie.vote_average);
            releaseDate.setText(movie.release_date);
        }
    }

    private void setTextBackground() {
        if (movie != null) {
            String posterURL = "http://image.tmdb.org/t/p/w500/" + movie.poster_path;
            final ImageView imageView = new ImageView(getActivity());
            Picasso.with(getContext()).load(posterURL).into(imageView);

            if (firstStart) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createLayerDrawable(imageView);
                    }
                }, 700);
                firstStart = false;
            } else {
                createLayerDrawable(imageView);
            }
        }
    }

    private void createLayerDrawable(ImageView imageView) {
        Drawable poster = imageView.getDrawable();
        if (poster != null) {
            Drawable[] layers = new Drawable[2];
            layers[0] = poster;
            layers[1] = ContextCompat.getDrawable(getActivity(), R.drawable.background_transparent);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            detailLayout.setBackground(layerDrawable);
        }
    }

    @OnClick(R.id.reviewsButton)
    public void showReviews() {
        if (jsonObjectReviews.isEmpty()) {
            Toast.makeText(getContext(), "This movie doesn't have any review", Toast.LENGTH_SHORT).show();
            return;
        }
        dialogInfo = 2;
        setDetailDialog();
    }

    @OnClick(R.id.trailersButton)
    public void showTrailers() {
        if (jsonObjectTrailers.isEmpty()) {
            Toast.makeText(getContext(), "This movie doesn't have any trailer", Toast.LENGTH_SHORT).show();
            return;
        }
        dialogInfo = 3;
        setDetailDialog();
    }

    private void setDetailDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(R.layout.dialog_details)
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialogTitlte);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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

    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case 2:
                jsonObjectReviews = (List<JSONObject>) data;
                break;
            case 3:
                jsonObjectTrailers = (List<JSONObject>) data;
                break;
        }
    }

    public void onLoaderReset() {
        if (detailsAdapter != null) {
            detailsAdapter.setData(new ArrayList<JSONObject>());
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        JSONObject jsonObject;
        switch (dialogInfo) {
            case 2:
                jsonObject = jsonObjectReviews.get(clickedItemIndex);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("url"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
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

    public void addFavorite() {
        String selection = DatabaseContract.Movies.MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{movie.id};
        Cursor cursor = getActivity().getContentResolver().query(DatabaseContract.Movies.CONTENT_URI, DatabaseContract.Movies.PROJECTION_LIST,
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
            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.Movies._ID));
            Uri currentMovieUri = ContentUris.withAppendedId(DatabaseContract.Movies.CONTENT_URI, id);
            int rowsDeleted = getActivity().getContentResolver().delete(currentMovieUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(getContext(), getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void insertMovie() {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Movies.MOVIE_ID, movie.id);
        String jsonString = new Gson().toJson(movie);
        values.put(DatabaseContract.Movies.MOVIE, jsonString);
        Uri newUri = getActivity().getContentResolver().insert(DatabaseContract.Movies.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(getContext(), getString(R.string.editor_insert_movie_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.editor_insert_movie_successful), Toast.LENGTH_SHORT).show();
        }
    }

    public void hideTitle() {
        movieTitle.setVisibility(View.GONE);
    }

    public void showTitle() {
        movieTitle.setVisibility(View.VISIBLE);
    }
}
