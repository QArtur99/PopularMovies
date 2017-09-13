package com.qartf.popularmovies.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DetailsLoader extends AsyncTaskLoader<Object> {
    private int loaderId;
    private List<JSONObject> jsonObjectList;
    private String movieId;

    public DetailsLoader(Context context, int loaderId, String movieId) {
        super(context);
        this.loaderId = loaderId;
        this.movieId = movieId;
    }

    @Override
    public Object loadInBackground() {

        jsonObjectList = new ArrayList<>();

        try {
            getData();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        Object object = jsonObjectList;
        return object;
    }

    private void getData() throws JSONException, IOException {
        String jsonString = "";

        switch (loaderId) {
            case 2:
                jsonString = TheMovieDbAPI.getMovieReviews(movieId);
                break;
            case 3:
                jsonString = TheMovieDbAPI.getMovieTrailers(movieId);
                break;
        }

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        int bookAmount = jsonArray.length();
        for (int i = 0; bookAmount > i; i++) {
            JSONObject jsonMovieData = jsonArray.getJSONObject(i);
            if (loaderId == 2) {
                jsonObjectList.add(jsonMovieData);
            } else if (loaderId == 3) {
                if (jsonMovieData.getString("type").equals("Trailer")) {
                    jsonObjectList.add(jsonMovieData);
                }
            }
        }
    }
}
