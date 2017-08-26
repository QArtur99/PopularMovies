package com.android.popularmovies.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.popularmovies.R;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {


    final private MoviesAdapter.ListItemClickListener mOnClickListener;
    private List<Movie> data;
    private int columns;

    public MoviesAdapter(List<Movie> myDataset, MoviesAdapter.ListItemClickListener listener, int columns) {
        data = myDataset;
        mOnClickListener = listener;
        this.columns = columns;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_movie_item, parent, false);
        RelativeLayout relativeLayout = view.findViewById(R.id.gridView);
        setViewSize(parent.getContext(), relativeLayout);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(position);
    }

    public int getNumberOfColumns() {
        return columns;
    }


    public Object getDataAtPosition(int position) {
        return data.get(position);
    }


    public void setViewSize(Context context, RelativeLayout relativeLayout) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int width = size.x;
        int widthDp = (int) (width / Resources.getSystem().getDisplayMetrics().density);

        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) relativeLayout.getLayoutParams();

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && widthDp >= 600) {
            int spaceWidth = width / 3;
            int posterWidth = spaceWidth / getNumberOfColumns();
            layoutParams.width = posterWidth;
            layoutParams.height = (int) (posterWidth * 1.5);
        } else {
            int posterWidth = width / getNumberOfColumns();
            layoutParams.height = (int) (posterWidth * 1.5);
        }
        relativeLayout.setLayoutParams(layoutParams);
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.itemImage) ImageView poster;
        @BindView(R.id.movieTitle) TextView movieTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Movie movie = (Movie) getDataAtPosition(position);
            String posterURL = "http://image.tmdb.org/t/p/w185/" + movie.poster_path;
            Glide.with(poster.getContext())
                    .load(posterURL)
                    .thumbnail(Glide.with(poster.getContext()).load(R.drawable.ic_ondemand_video))
                    .into(poster);
            movieTitle.setText(movie.original_title);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition, v);
        }

    }

}
