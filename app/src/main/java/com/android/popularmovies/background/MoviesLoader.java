package com.android.popularmovies.background;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.android.popularmovies.adapter.Movie;
import com.android.popularmovies.database.DatabaseContract.Movies;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ART_F on 2017-06-28.
 */

public class MoviesLoader extends AsyncTaskLoader<Object> {
    private List<Movie> list;
    private String sortBy;
    private String pageNo;


    public MoviesLoader(Context context, String sortBy, String pageNo) {
        super(context);
        this.sortBy = sortBy;
        this.pageNo = pageNo;
    }

    @Override
    public Object loadInBackground() {
        list = new ArrayList<>();

        try {
            loadMovies();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Object object = list;
        return object;
    }

    private void loadMovies() throws IOException, JSONException {
        getMoviesList();
    }

    private void getMoviesList() throws IOException, JSONException {
        String jsonString = TheMovieDbAPI.getMoviesString(sortBy, pageNo);
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        int bookAmount = jsonArray.length();
        for (int i = 0; bookAmount > i; i++) {
            JSONObject jsonBookData = jsonArray.getJSONObject(i);
            list.add(new Movie(jsonBookData));
        }
    }
}
