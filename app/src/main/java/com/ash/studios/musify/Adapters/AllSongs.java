package com.ash.studios.musify.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Activities.Player;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AllSongs extends RecyclerView.Adapter<AllSongs.ViewHolder> {
    private Context context;
    public ArrayList<Song> allSongs;

    public AllSongs(Context context, ArrayList<Song> allSongs, ProgressBar pb) {
        this.context = context;
        this.allSongs = allSongs;
        pb.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public AllSongs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AllSongs.ViewHolder holder, int position) {
        Song song = allSongs.get(position);

        holder.songName.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());
        holder.duration.setText(Utils.getDuration(song.getDuration()));
        holder.duration.setTypeface(ResourcesCompat.getFont(context, R.font.josefin_sans_bold));
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(Utils.getAlbumArt(song.getAlbum_id()))
                .placeholder(R.mipmap.icon)
                .into(holder.albumCover);

        holder.itemView.setOnClickListener(v -> {
            Instance.songs = allSongs;
            context.startActivity(new Intent(context, Player.class).putExtra("position", position));
        });
    }

    @Override
    public int getItemCount() {
        return allSongs == null ? 0 : allSongs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songName, songArtist, duration;
        ImageView albumCover;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.title);
            songArtist = itemView.findViewById(R.id.artist);
            duration = itemView.findViewById(R.id.duration);
            albumCover = itemView.findViewById(R.id.album_art);
        }
    }
}
