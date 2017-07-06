package com.android.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ART_F on 2017-06-28.
 */

public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {
    private List<Movie> list;
    private String sortBy;
    private String pageNo;

    public MoviesLoader(Context context, String sortBy, String pageNo) {
        super(context);
        this.sortBy = sortBy;
        this.pageNo = pageNo;
    }


    @Override
    public List<Movie> loadInBackground() {
        list = new ArrayList<>();

        try {
            getBooksList();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void getBooksList() throws IOException, JSONException {
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
