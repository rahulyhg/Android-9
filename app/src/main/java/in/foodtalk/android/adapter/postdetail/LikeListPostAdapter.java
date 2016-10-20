package in.foodtalk.android.adapter.postdetail;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.foodtalk.android.R;
import in.foodtalk.android.fragment.postdetails.LikeListFragment;
import in.foodtalk.android.object.LikeListObj;

/**
 * Created by RetailAdmin on 19-10-2016.
 */

public class LikeListPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LayoutInflater layoutInflater;
    List<LikeListObj> likeList;
    Context context;

    public LikeListPostAdapter (Context context, List<LikeListObj> likeList){
        this.context = context;
        this.likeList = likeList;
        layoutInflater = layoutInflater.from(context);
        Log.d("likelistpost adapter","size "+likeList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_like,parent, false);
        LikeHolder likeHolder = new LikeHolder(view);
        return likeHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LikeListObj current = likeList.get(position);
        LikeHolder likeHolder = (LikeHolder) holder;
        likeHolder.txtUsername.setText(current.userName);
        likeHolder.txtFullname.setText(current.fullName);
        likeHolder.iFollowIt = current.iFollowIt;
        if (current.iFollowIt.equals("0")){
            likeHolder.txtFollow.setText("Follow");
            likeHolder.txtFollow.setTextColor(ContextCompat.getColor(context, R.color.active));
        }else {
            likeHolder.txtFollow.setText("Following");
            likeHolder.txtFollow.setTextColor(ContextCompat.getColor(context, R.color.positive));
        }
        Picasso.with(context)
                .load(current.image)
                .fit()
                .placeholder(R.drawable.user_placeholder)
                .into(likeHolder.userThumb);
        Log.d("likelist post adapter","position: "+position+" userName: "+current.userName);

    }

    @Override
    public int getItemCount() {
        Log.d("likelistpost adapter1","size "+likeList.size());

        return likeList.size();
    }

    class LikeHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        TextView txtUsername, txtFullname, txtFollow;
        ImageView userThumb;
        String iFollowIt;
        public LikeHolder(View itemView) {
            super(itemView);
            txtUsername = (TextView) itemView.findViewById(R.id.txt_username);
            txtFullname = (TextView) itemView.findViewById(R.id.txt_fullname);
            txtFollow = (TextView) itemView.findViewById(R.id.txt_follow);
            userThumb = (ImageView) itemView.findViewById(R.id.user_thumb);

            txtFollow.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.txt_follow:
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            if (iFollowIt.equals("0")){
                                Log.d("Likelist adapter", "follow");
                            }else if (iFollowIt.equals("1")){
                                Log.d("Likelist adapter", "unfollow");
                            }
                            break;
                    }
                    break;
            }
            return true;
        }
    }
}
