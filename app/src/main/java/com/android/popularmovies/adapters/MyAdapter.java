package com.android.popularmovies.adapters;

import android.view.View;

import com.android.popularmovies.R;

import java.util.List;

public class MyAdapter extends MyBaseAdapter {
    final private ListItemClickListener mOnClickListener;
    private List<Movie> data;
    private int columns;

    public MyAdapter(List<Movie> myDataset, ListItemClickListener listener, int columns) {
        data = myDataset;
        mOnClickListener = listener;
        this.columns = columns;
    }

    @Override
    public ListItemClickListener getListItemClickListener() {
        return mOnClickListener;
    }

    @Override
    public int getNumberOfColumns() {
        return columns;
    }

    @Override
    public Movie getDataAtPosition(int position) {
        return data.get(position);
    }

    @Override
    public int getLayoutIdForType(int viewType) {
        return R.layout.row_movie_item;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clearMovies() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public List<Movie> getData() {
        return data;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, View view);
    }

}