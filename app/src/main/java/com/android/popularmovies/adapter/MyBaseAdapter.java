package com.android.popularmovies.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Point;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.android.popularmovies.BR;
import com.android.popularmovies.databinding.RowMovieItemBinding;


public abstract class MyBaseAdapter extends RecyclerView.Adapter<MyBaseAdapter.MyViewHolder> {

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RowMovieItemBinding binding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForType(viewType), parent, false);
        setViewSize(parent.getContext(), binding.gridView);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(getDataAtPosition(position));
    }

    public abstract MyAdapter.ListItemClickListener getListItemClickListener();

    public abstract Object getDataAtPosition(int position);

    public abstract int getLayoutIdForType(int viewType);

    public abstract int getNumberOfColumns();

    public void setViewSize(Context context, RelativeLayout relativeLayout) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int width = size.x;
        int widthDp = (int) (width / Resources.getSystem().getDisplayMetrics().density);
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) relativeLayout.getLayoutParams();

        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && widthDp >= 600){
            int spaceWidth = (int) (width / 3);
            int posterWidth =  spaceWidth / getNumberOfColumns();
            layoutParams.width = (int)  posterWidth;
            layoutParams.height = (int) (posterWidth * 1.5);
        }else{
            int posterWidth =  width / getNumberOfColumns();
            layoutParams.height = (int) (posterWidth * 1.5);
        }
        relativeLayout.setLayoutParams(layoutParams);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ViewDataBinding binding;

        public MyViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Object obj) {
            binding.setVariable(BR.obj, obj);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            getListItemClickListener().onListItemClick(clickedPosition);
        }

    }

}