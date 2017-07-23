package com.android.popularmovies.background;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ART_F on 2017-07-12.
 */

public class DetailsLoader extends AsyncTaskLoader<List<JSONObject>> {
    private int loaderId;
    private List<JSONObject> jsonObjectList;
    private String movieId;

    public DetailsLoader(Context context, int loaderId, String movieId) {
        super(context);
        this.loaderId = loaderId;
        this.movieId = movieId;
    }

    @Override
    public List<JSONObject> loadInBackground() {

        jsonObjectList = new ArrayList<>();

        try {
            getData();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return jsonObjectList;
    }

    private void getData() throws JSONException, IOException {
        String jsonString = "";

        switch (loaderId) {
            case 1:
                jsonString = TheMovieDbAPI.getMovieReviews(movieId);
                break;
            case 2:
                jsonString = TheMovieDbAPI.getMovieTrailers(movieId);
                break;
        }

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        int bookAmount = jsonArray.length();
        for (int i = 0; bookAmount > i; i++) {
            JSONObject jsonMovieData = jsonArray.getJSONObject(i);
            if (loaderId == 1) {
                jsonObjectList.add(jsonMovieData);
            } else if (loaderId == 2) {
                if (jsonMovieData.getString("type").equals("Trailer")) {
                    jsonObjectList.add(jsonMovieData);
                }
            }
        }
    }
}
