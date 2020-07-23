package com.ash.studios.musify.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Activities.Bunch;
import com.ash.studios.musify.Model.Genre;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Genres extends RecyclerView.Adapter<Genres.ViewHolder> {
    public ArrayList<Genre> genres;
    private Context context;

    public Genres(Context context, ArrayList<Genre> genres) {
        this.context = context;
        this.genres = genres;
    }

    @NonNull
    @Override
    public Genres.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Genres.ViewHolder holder, int position) {
        Genre genre = genres.get(position);

        holder.genreName.setText(genre.getGenre());
        holder.songCount.setText(genre.getSong_count() == 1 ? "\u266B 1" : "\u266B " + genre.getSong_count());

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(Utils.getAlbumArt(genre.getAlbum_id()))
                .placeholder(R.mipmap.icon)
                .into(holder.genreCover);

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, Bunch.class)
                .putExtra("TYPE", "GENRES")
                .putExtra("CONTENT", genre)));
    }

    @Override
    public int getItemCount() {
        return genres == null ? 0 : genres.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView genreName, songCount;
        ImageView genreCover;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            genreName = itemView.findViewById(R.id.title);
            songCount = itemView.findViewById(R.id.artist);
            genreCover = itemView.findViewById(R.id.album_art);
        }
    }
}
