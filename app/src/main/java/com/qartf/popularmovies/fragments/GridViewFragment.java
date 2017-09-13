package com.qartf.popularmovies.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qartf.popularmovies.SettingsBottomSheetDialog;
import com.qartf.popularmovies.activities.MainActivity;
import com.qartf.popularmovies.adapters.Movie;
import com.qartf.popularmovies.adapters.MoviesAdapter;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GridViewFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        MoviesAdapter.ListItemClickListener, SwipyRefreshLayout.OnRefreshListener, View.OnTouchListener {

    public MoviesAdapter moviesAdapter;
    @BindView(com.qartf.popularmovies.R.id.emptyView) RelativeLayout emptyView;
    @BindView(com.qartf.popularmovies.R.id.recyclerView) RecyclerView recyclerView;
    @BindView(com.qartf.popularmovies.R.id.swipeRefreshLayout) SwipyRefreshLayout swipyRefreshLayout;
    @BindView(com.qartf.popularmovies.R.id.loading_indicator) ProgressBar loadingIndicator;
    @BindView(com.qartf.popularmovies.R.id.empty_title_text) TextView emptyTitleText;
    @BindView(com.qartf.popularmovies.R.id.empty_subtitle_text) TextView emptySubtitleText;
    private MainActivity mainActivity;
    private int pageNoInteger = 1;
    private View rootView;
    private OnImageClickListener mCallback;
    private SharedPreferences sharedPreferences;
    private String sortBy;
    private int loaderId;
    private int recyclerViewPosition;
    private int firstView = 0;
    private GridLayoutManager layoutManager;
    private Bundle bundle;
    private boolean loadMore = false;

    public GridViewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mainActivity = ((MainActivity) getActivity());
        rootView = inflater.inflate(com.qartf.popularmovies.R.layout.fragment_grid_view, container, false);
        ButterKnife.bind(this, rootView);

        emptyView.setVisibility(View.GONE);
        recyclerViewPosition = 0;
        recyclerView.setOnTouchListener(this);
        swipyRefreshLayout.setOnRefreshListener(this);

        int columns = setupSharedPreferences();
        setAdapter(columns, new ArrayList<Movie>());
        bundle = getArguments();
        getBundleData();

        if (sortBy.equals(getString(com.qartf.popularmovies.R.string.pref_sort_by_favorite))) {
            loaderId = 0;
            getActivity().getSupportLoaderManager().initLoader(0, null, mainActivity).forceLoad();
        } else if (checkConnection()) {
            loaderId = 1;
            getActivity().getSupportLoaderManager().initLoader(1, null, mainActivity).forceLoad();
        } else {
            setInfoNoConnection();
        }

        return rootView;
    }

    private void getBundleData() {

        if (bundle != null) {
            loadMore = false;
            recyclerViewPosition = sharedPreferences.getInt(getString(com.qartf.popularmovies.R.string.pref_lastClicked), 0);
            sortBy = sharedPreferences.getString(getString(com.qartf.popularmovies.R.string.pref_sort_by_key), getString(com.qartf.popularmovies.R.string.pref_sort_by_most_popular_default));
            if (sortBy.equals(getString(com.qartf.popularmovies.R.string.pref_sort_by_favorite))) {
                pageNoInteger = 1;
                loaderId = 0;
            } else {
                loaderId = 1;
                pageNoInteger = sharedPreferences.getInt(getString(com.qartf.popularmovies.R.string.pref_pageNo), 1);
                firstView = sharedPreferences.getInt(getString(com.qartf.popularmovies.R.string.pref_firstView), 0);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnImageClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(com.qartf.popularmovies.R.menu.menu, menu);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            menu.findItem(com.qartf.popularmovies.R.id.action_favorite).setVisible(true);
        } else {
            menu.findItem(com.qartf.popularmovies.R.id.action_favorite).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case com.qartf.popularmovies.R.id.action_favorite:
                ((MainActivity) getActivity()).addFavorite(null);
                break;
            case com.qartf.popularmovies.R.id.action_refresh:
                restartLoader(loaderId);
                break;
            case com.qartf.popularmovies.R.id.action_sortBy:
                openBottomDialog();
                break;
            case com.qartf.popularmovies.R.id.one_column:
                sharedPreferences.edit().putInt(getString(com.qartf.popularmovies.R.string.pref_number_of_columns_key), 1).apply();
                break;
            case com.qartf.popularmovies.R.id.two_columns:
                sharedPreferences.edit().putInt(getString(com.qartf.popularmovies.R.string.pref_number_of_columns_key), 2).apply();
                break;
            case com.qartf.popularmovies.R.id.three_columns:
                sharedPreferences.edit().putInt(getString(com.qartf.popularmovies.R.string.pref_number_of_columns_key), 3).apply();
                break;
            case com.qartf.popularmovies.R.id.four_columns:
                sharedPreferences.edit().putInt(getString(com.qartf.popularmovies.R.string.pref_number_of_columns_key), 4).apply();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onListItemClick(int clickedItemIndex, View view) {
        Movie movie = (Movie) moviesAdapter.getDataAtPosition(clickedItemIndex);
        sharedPreferences.edit().putInt(getString(com.qartf.popularmovies.R.string.pref_pageNo), pageNoInteger).apply();
        sharedPreferences.edit().putInt(getString(com.qartf.popularmovies.R.string.pref_lastClicked), clickedItemIndex).apply();
        mCallback.onImageSelected(movie, view);
    }

    public void onLoadFinished(final List<Movie> data) {
        loadingIndicator.setVisibility(View.GONE);
        if (pageNoInteger == 1) {
            if (moviesAdapter != null) {
                moviesAdapter.clearMovies();
            }
        }
        if (data != null && !data.isEmpty()) {
            if (recyclerViewPosition == 0 && rootView.getRootView().findViewById(com.qartf.popularmovies.R.id.detailsViewFrame) != null && moviesAdapter.getData().isEmpty()) {
                rootView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getContext() != null) {
                            mCallback.onImageSelected(data.get(0), rootView);
                        }
                    }
                });
            }
            emptyView.setVisibility(View.GONE);

            if (loadMore) {
                recyclerView.scrollToPosition(moviesAdapter.getItemCount() - 1);
                loadMore = false;
            }

            moviesAdapter.setMovies(data);
            if (sortBy.equals(getResources().getString(com.qartf.popularmovies.R.string.pref_sort_by_favorite))) {
                if (recyclerViewPosition > moviesAdapter.getItemCount()) {
                    recyclerViewPosition = 0;
                }
            }

            if (!loadMore && bundle != null) {
                int adapterSize = moviesAdapter.getItemCount();
                if (adapterSize > recyclerViewPosition) {
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        final Movie movie = (Movie) moviesAdapter.getDataAtPosition(recyclerViewPosition);
                        rootView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (getContext() != null) {
                                    mCallback.onImageSelected(movie, layoutManager.findViewByPosition(recyclerViewPosition));
                                }
                            }
                        });
                    }
                    recyclerView.scrollToPosition(firstView);
                } else if (adapterSize > 0) {
                    recyclerView.scrollToPosition(adapterSize - 1);
                    swipyRefreshLayout.setRefreshing(true);
                    onRefresh(SwipyRefreshLayoutDirection.BOTTOM);
                }
            }

        } else {
            emptyView.setVisibility(View.VISIBLE);
            if (sortBy.equals(getResources().getString(com.qartf.popularmovies.R.string.pref_sort_by_favorite))) {
                emptyTitleText.setText(getString(com.qartf.popularmovies.R.string.no_favorite));
                emptySubtitleText.setText(getString(com.qartf.popularmovies.R.string.no_favorite_sub_text));
            } else {
                emptyTitleText.setText(getString(com.qartf.popularmovies.R.string.server_problem));
                emptySubtitleText.setText(getString(com.qartf.popularmovies.R.string.server_problem_sub_text));
            }
        }
    }

    public void onRestartLoader() {
        moviesAdapter.setMovies(new ArrayList<Movie>());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(com.qartf.popularmovies.R.string.pref_number_of_columns_key))) {
            int columns = sharedPreferences.getInt(key, getResources().getInteger(com.qartf.popularmovies.R.integer.number_of_columns));
            setAdapter(columns, moviesAdapter.getData());
        } else if (key.equals(getString(com.qartf.popularmovies.R.string.pref_sort_by_key))) {
            pageNoInteger = 1;
            sortBy = sharedPreferences.getString(key, getString(com.qartf.popularmovies.R.string.pref_sort_by_most_popular_default));
            if (sortBy.equals(getString(com.qartf.popularmovies.R.string.pref_sort_by_favorite))) {
                swipyRefreshLayout.setOnRefreshListener(null);
                restartLoader(0);
            } else {
                swipyRefreshLayout.setOnRefreshListener(this);
                restartLoader(1);
            }
        }
    }

    private void restartLoader(int id) {
        if (checkConnection()) {
            loaderId = id;
            moviesAdapter.clearMovies();
            getActivity().getSupportLoaderManager().restartLoader(id, null, mainActivity).forceLoad();
        } else {
            setInfoNoConnection();
        }
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void setInfoNoConnection() {
        recyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyTitleText.setText(getString(com.qartf.popularmovies.R.string.no_connection));
        emptySubtitleText.setText(getString(com.qartf.popularmovies.R.string.no_connection_sub_text));
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.edit().putInt(getString(com.qartf.popularmovies.R.string.pref_pageNo), pageNoInteger).apply();
        sharedPreferences.edit().putInt(getString(com.qartf.popularmovies.R.string.pref_firstView), getFirstVisibleItemPosition()).apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        pageNoInteger = 1;
        restartLoader(loaderId);
    }

    public int setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        int columns = sharedPreferences.getInt(getString(com.qartf.popularmovies.R.string.pref_number_of_columns_key), getResources().getInteger(com.qartf.popularmovies.R.integer.number_of_columns));
        sortBy = sharedPreferences.getString(getString(com.qartf.popularmovies.R.string.pref_sort_by_key), getString(com.qartf.popularmovies.R.string.pref_sort_by_most_popular_default));
        return columns;
    }

    public void setAdapter(int columns, List<Movie> movieList) {
        layoutManager = new GridLayoutManager(getContext(), columns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        moviesAdapter = new MoviesAdapter(movieList, this, columns);
        recyclerView.setAdapter(moviesAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    public int getPageNo() {
        return pageNoInteger;
    }

    public String getSortBy() {
        return sortBy;
    }

    public List<Movie> getData() {
        return moviesAdapter.getData();
    }

    public int getFirstVisibleItemPosition() {
        return layoutManager.findFirstVisibleItemPosition();
    }

    private void openBottomDialog() {
        View view = getLayoutInflater(new Bundle()).inflate(com.qartf.popularmovies.R.layout.menu_bottom, null);
        SettingsBottomSheetDialog dialog = new SettingsBottomSheetDialog(getContext());
        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.BOTTOM && !sortBy.equals(getResources().getString(com.qartf.popularmovies.R.string.pref_sort_by_favorite))) {
            pageNoInteger++;
            if (checkConnection()) {
                getActivity().getSupportLoaderManager().restartLoader(1, null, mainActivity).forceLoad();
                loadMore = true;
            } else {
                setInfoNoConnection();
            }
        } else if (direction == SwipyRefreshLayoutDirection.TOP) {
            restartLoader(loaderId);
        }

        swipyRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            firstView = getFirstVisibleItemPosition();
            sharedPreferences.edit().putInt(getString(com.qartf.popularmovies.R.string.pref_firstView), getFirstVisibleItemPosition()).apply();
        }
        return false;
    }


    public interface OnImageClickListener {
        void onImageSelected(Movie movie, View view);
    }

}
