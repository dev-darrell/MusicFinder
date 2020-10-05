package com.dev.darrell.musicfinder.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.darrell.musicfinder.R;
import com.dev.darrell.musicfinder.activity.TrackPlayer;
import com.dev.darrell.musicfinder.model.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private ArrayList<Track> mTracks;
    private static final String TAG = "TrackAdapter";

    public TrackAdapter(ArrayList<Track> tracks) {
        this.mTracks = tracks;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track currentTrack = mTracks.get(position);

        holder.trackTitle.setText(currentTrack.getTitle());
        holder.artistName.setText(currentTrack.getArtist());
        holder.duration.setText(currentTrack.getDuration() + " seconds");
        String image_url = currentTrack.getAlbumCover();

        Picasso.get().load(image_url)
                .placeholder(R.drawable.ic_stat_name)
                .into(holder.trackCover);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_track_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView trackTitle;
        public TextView artistName;
        public TextView duration;
        public ImageView trackCover;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            trackTitle = itemView.findViewById(R.id.tv_track_title);
            artistName = itemView.findViewById(R.id.tv_artist_name);
            duration = itemView.findViewById(R.id.tv_duration);
            trackCover = itemView.findViewById(R.id.img_track_cover);
            Log.d(TAG, "ViewHolder: Loaded image into Picasso");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int trackId = mTracks.get(getAdapterPosition()).getId();
                    Intent intent = new Intent(itemView.getContext(), TrackPlayer.class);
                    intent.putExtra(TrackPlayer.TRACK_EXTRA, trackId);
                    itemView.getContext().startActivity(intent);

                }
            });
        }
    }

}
