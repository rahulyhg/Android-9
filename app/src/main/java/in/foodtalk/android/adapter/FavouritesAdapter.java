package in.foodtalk.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.object.FavoritesObj;

/**
 * Created by RetailAdmin on 13-05-2016.
 */
public class FavouritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    public List<FavoritesObj> favList = new ArrayList<>();
    private LayoutInflater layoutInflater;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public FavouritesAdapter (Context context, List<FavoritesObj>favList){
        layoutInflater = layoutInflater.from(context);
        this.context = context;
        this.favList = favList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FavHolder favHolder;
        ProgressViewHolder progressViewHolder;

        if(viewType == VIEW_ITEM){
            View view = layoutInflater.inflate(R.layout.card_favourites, parent,false);
            favHolder = new FavHolder(view);
            return favHolder;
        }else {
            View view = layoutInflater.inflate(R.layout.progress_load_more, parent, false);
            progressViewHolder = new ProgressViewHolder(view);
            return progressViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof FavHolder){
                FavoritesObj current = favList.get(position);
                FavHolder favHolder = (FavHolder) holder;
                favHolder.txtDishName.setText(current.dishName);
            }else if (holder instanceof ProgressViewHolder){
                ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
                progressViewHolder.progressBar.setIndeterminate(true);
            }
    }

    @Override
    public int getItemCount() {
        return favList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return favList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    class FavHolder extends RecyclerView.ViewHolder{
        TextView txtDishName;
        public FavHolder(View itemView) {
            super(itemView);
            txtDishName = (TextView) itemView.findViewById(R.id.txt_dish_name_fav);
        }
    }
    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);

        }
    }
}
