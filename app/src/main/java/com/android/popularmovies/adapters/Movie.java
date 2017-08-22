package com.android.popularmovies.adapters;

import org.json.JSONException;
import org.json.JSONObject;


public class Movie {
    public String vote_count;
    public String id;
    public String video;
    public String vote_average;
    public String popularity;
    public String poster_path;
    public String original_language;
    public String original_title;
    public String genre_ids;
    public String backdrop_path;
    public String adult;
    public String overview;
    public String release_date;


    public Movie(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("vote_count")) {
            vote_count = jsonObject.getString("vote_count");
        }

        if (jsonObject.has("id")) {
            id = jsonObject.getString("id");
        }


        if (jsonObject.has("video")) {
            video = jsonObject.getString("video");
        }

        if (jsonObject.has("vote_average")) {
            vote_average = jsonObject.getString("vote_average");
        }

        if (jsonObject.has("popularity")) {
            popularity = jsonObject.getString("popularity");
        }

        if (jsonObject.has("poster_path")) {
            poster_path = jsonObject.getString("poster_path");
        }

        if (jsonObject.has("original_language")) {
            original_language = jsonObject.getString("original_language");
        }

        if (jsonObject.has("original_title")) {
            original_title = jsonObject.getString("original_title");
        }

        if (jsonObject.has("genre_ids")) {
            genre_ids = jsonObject.getString("genre_ids");
        }

        if (jsonObject.has("backdrop_path")) {
            backdrop_path = jsonObject.getString("backdrop_path");
        }

        if (jsonObject.has("adult")) {
            adult = jsonObject.getString("adult");
        }

        if (jsonObject.has("overview")) {
            overview = jsonObject.getString("overview");
        }

        if (jsonObject.has("release_date")) {
            release_date = jsonObject.getString("release_date");
        }
    }
}
