package com.ash.studios.musify.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Activities.Player;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AllSongs extends RecyclerView.Adapter<AllSongs.ViewHolder> {
    private Context context;
    public static ArrayList<Song> list;

    public AllSongs(Context context, ArrayList<Song> list) {
        this.context = context;
        AllSongs.list = list;
    }

    @NonNull
    @Override
    public AllSongs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AllSongs.ViewHolder holder, int position) {
        Song song = list.get(position);

        //Setting title
        holder.songName.setText(song.getTitle());

        //Setting artist
        holder.songArtist.setText(song.getArtist());

        //Setting song duration
        holder.duration.setText(Utils.getDuration(song.getDuration()));
        holder.duration.setTypeface(ResourcesCompat.getFont(context, R.font.josefin_sans_bold));

        //Setting album art
        Glide.with(context).asBitmap().load(Utils.getAlbumArt(song.getAlbum_id())).placeholder(R.mipmap.icon).into(holder.albumCover);

        //Setting onClick listener
        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, Player.class).putExtra("position", position)));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
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
