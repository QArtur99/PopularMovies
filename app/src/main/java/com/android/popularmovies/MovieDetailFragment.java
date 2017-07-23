package com.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
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

import com.android.popularmovies.adapter.DetailsAdapter;
import com.android.popularmovies.adapter.Movie;
import com.android.popularmovies.background.DetailsLoader;
import com.android.popularmovies.database.DatabaseContract;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ART_F on 2017-07-20.
 */

public class MovieDetailFragment extends Fragment implements DetailsAdapter.ListItemClickListener
        , LoaderManager.LoaderCallbacks<List<JSONObject>> {

    @BindView(R.id.fabBottom) LinearLayout fabBottom;
    private TextView releaseDate, voteAverage, overview, movieTitle;
    private LinearLayout detailLayout;
    private Movie movie;
    private List<JSONObject> jsonObjectTrailers, jsonObjectReviews;
    private int dialogInfo;
    private DetailsAdapter detailsAdapter;

    public MovieDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        if (movie != null) {
            ButterKnife.bind(getActivity(), rootView);
            movieTitle = rootView.findViewById(R.id.movieTitleDetail);
            overview = rootView.findViewById(R.id.overview);
            voteAverage = rootView.findViewById(R.id.voteAverage);
            releaseDate = rootView.findViewById(R.id.releaseDate);
            detailLayout = rootView.findViewById(R.id.detail);

            setTextBackground();
            setDetailData();

            getActivity().getSupportLoaderManager().restartLoader(1, null, this).forceLoad();
            getActivity().getSupportLoaderManager().restartLoader(2, null, this).forceLoad();
        }

        return rootView;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
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
            ImageView imageView = new ImageView(getContext());
            Picasso.with(getContext()).load(posterURL).into(imageView);

            LayerDrawable cellLayerDrawable = (LayerDrawable) detailLayout.getBackground();
            Drawable drawable = imageView.getDrawable();
            cellLayerDrawable.setDrawableByLayerId(R.id.backgroundBitmap, drawable);
        }
    }

    public void showReviews(View view) {
        if (jsonObjectReviews.isEmpty()) {
            Toast.makeText(getContext(), "This movie doesn't have any review", Toast.LENGTH_SHORT).show();
            return;
        }
        dialogInfo = 1;
        setDetailDialog();
    }

    public void showTrailers(View view) {
        if (jsonObjectTrailers.isEmpty()) {
            Toast.makeText(getContext(), "This movie doesn't have any trailer", Toast.LENGTH_SHORT).show();
            return;
        }
        dialogInfo = 2;
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
            case 1:
                dialogTitle.setText("Reviews");
                detailsAdapter = new DetailsAdapter(jsonObjectReviews, this, dialogInfo);
                break;
            case 2:
                dialogTitle.setText("Trailers");
                detailsAdapter = new DetailsAdapter(jsonObjectTrailers, this, dialogInfo);
                break;
        }
        recyclerView.setAdapter(detailsAdapter);
    }

    @Override
    public Loader<List<JSONObject>> onCreateLoader(int id, Bundle args) {
        return new DetailsLoader(getContext(), id, movie.id);
    }

    @Override
    public void onLoadFinished(Loader<List<JSONObject>> loader, List<JSONObject> data) {
        switch (loader.getId()) {
            case 1:
                jsonObjectReviews = data;
                break;
            case 2:
                jsonObjectTrailers = data;
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<JSONObject>> loader) {
        detailsAdapter.setData(new ArrayList<JSONObject>());
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

}
