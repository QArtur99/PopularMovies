package com.qartf.popularmovies.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    private DatabaseContract() {
    }

    public static class Movies implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;


        public static final String TABLE_NAME = "Movies";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE = "movie";

        public static final String[] PROJECTION_LIST = {
                _ID,
                MOVIE_ID,
                MOVIE
        };

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + MOVIE_ID + " TEXT,"
                        + MOVIE + " TEXT"
                        + ");";

        static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
