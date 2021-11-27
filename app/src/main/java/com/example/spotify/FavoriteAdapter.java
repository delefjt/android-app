package com.example.spotify;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.List;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {

    private Context mContext;
    private List<ArtistModel> artistData;
    private final ClickListener listener;


    public FavoriteAdapter(Context mContext, List<ArtistModel> artistData, ClickListener clickListener) {
        this.mContext = mContext;
        this.artistData = artistData;
        this.listener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.artist_item, parent, false);

        return new MyViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mbid.setText(artistData.get(position).getMbid());
        holder.name.setText(artistData.get(position).getName());
        if (artistData.get(position).getFavorite()) {
            holder.favBtn.setText("Unfollow");
        }

        // display image through glide library
        Glide.with(mContext)
                .load(artistData.get(position).getImg())
                .into(holder.img);


    }

    @Override
    public int getItemCount() {
        return artistData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mbid;
        TextView name;
        ImageView img;
        Button favBtn;
        private WeakReference<ClickListener> listenerRef;


        public MyViewHolder(@NonNull View itemView, ClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            mbid = itemView.findViewById(R.id.id_name);
            name = itemView.findViewById(R.id.artist_name);
            img = itemView.findViewById(R.id.imageView);
            favBtn = (Button) itemView.findViewById(R.id.like_btn);

            favBtn.setOnClickListener(this);
        }
        // onClick Listener for view
        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }
}
