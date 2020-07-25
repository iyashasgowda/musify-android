package com.ash.studios.musify.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Activities.Bunch;
import com.ash.studios.musify.Model.Playlist;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class Playlists extends RecyclerView.Adapter<Playlists.ViewHolder> {
    private Context context;
    private ArrayList<Playlist> list;

    public Playlists(Context context, ArrayList<Playlist> list, ProgressBar pb, TextView nf) {
        this.context = context;
        this.list = list;
        pb.setVisibility(View.GONE);

        if (getItemCount() == 0) nf.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Playlists.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Playlists.ViewHolder holder, int position) {
        Playlist playlist = list.get(position);
        ArrayList<Song> songs = playlist.getSongs();

        holder.playlistArtist.setVisibility(View.GONE);
        holder.playlistName.setText(playlist.getName());
        holder.playlistCount.setText("\u266B " + songs.size());
        if (playlist.getSongs().size() > 0)
            Glide.with(context.getApplicationContext())
                    .asBitmap().load(Utils.getAlbumArt(playlist.getSongs().get(0).getAlbum_id()))
                    .placeholder(R.mipmap.icon)
                    .into(holder.playlistCover);

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, Bunch.class)
                .putExtra("TYPE", "PLAYLISTS")
                .putExtra("CONTENT", playlist)));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName, playlistArtist, playlistCount;
        ImageView playlistCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            playlistName = itemView.findViewById(R.id.title);
            playlistArtist = itemView.findViewById(R.id.artist);
            playlistCount = itemView.findViewById(R.id.duration);
            playlistCover = itemView.findViewById(R.id.album_art);
        }
    }
}
