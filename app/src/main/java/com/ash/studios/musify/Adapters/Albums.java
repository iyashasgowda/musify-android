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
import com.ash.studios.musify.Model.Album;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class Albums extends RecyclerView.Adapter<Albums.ViewHolder> {
    public ArrayList<Album> albums;
    private Context context;

    public Albums(Context context, ArrayList<Album> albums, ProgressBar pb) {
        this.context = context;
        this.albums = albums;
        pb.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public Albums.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Albums.ViewHolder holder, int position) {
        Album album = albums.get(position);

        holder.albumName.setText(album.getAlbum());
        holder.albumArtist.setText(album.getArtist());
        holder.songsCount.setText("\u266B " + album.getSong_count());
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(Utils.getAlbumArt(album.getAlbum_id()))
                .placeholder(R.mipmap.icon)
                .into(holder.albumCover);

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, Bunch.class)
                .putExtra("TYPE", "ALBUMS")
                .putExtra("CONTENT", album)));
    }

    @Override
    public int getItemCount() {
        return albums == null ? 0 : albums.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView albumName, albumArtist, songsCount;
        ImageView albumCover;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            albumName = itemView.findViewById(R.id.title);
            albumArtist = itemView.findViewById(R.id.artist);
            songsCount = itemView.findViewById(R.id.duration);
            albumCover = itemView.findViewById(R.id.album_art);
        }
    }
}
