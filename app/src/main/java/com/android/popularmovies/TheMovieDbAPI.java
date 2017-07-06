package com.android.popularmovies;

import android.support.annotation.NonNull;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ART_F on 2017-06-29.
 */

public class TheMovieDbAPI {
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    private static final String API_KEY_V3_AUTH = "555893d8d1ad611b2b0e2e9d7abf3f3a";
    private static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "api_key";
    private static final String PAGE = "page";

    public static String getMoviesString(String sortBy, String pageNo) throws JSONException, IOException {
        HashMap<String, String> args = new HashMap<>();
        args.put(API_KEY, API_KEY_V3_AUTH);
        args.put(PAGE, pageNo);
        String url = MOVIE_DB_BASE_URL + sortBy + "?" + getUri(args);
        String jsonString = getQueryJSONObject(url);
        return jsonString;
    }

    private static String getQueryJSONObject(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

        StringBuilder jsonString = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            jsonString.append(line).append("\n");
        }
        br.close();

        return jsonString.toString();
    }

//    @NonNull
//    private static String getUri(HashMap<String, String> args) throws JSONException {
//        JSONObject uriBuilder = new JSONObject(args);
//        Iterator<?> keys = uriBuilder.keys();
//        String postData = "";
//        while (keys.hasNext()) {
//            String key = (String) keys.next();
//            if (postData.length() > 0) {
//                postData += "&";
//            }
//            postData += key + "=" + uriBuilder.getString(key);
//        }
//        return postData;
//    }

    @NonNull
    private static String getUri(HashMap<String, String> args) throws JSONException {
        Set<String> keys = args.keySet();
        String postData = "";
        for (String key : keys) {
            if (postData.length() > 0) {
                postData += "&";
            }
            postData += key + "=" + args.get(key);
        }
        return postData;
    }
}
