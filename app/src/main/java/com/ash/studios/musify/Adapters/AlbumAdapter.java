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
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.activities.Categories.BunchList;
import com.ash.studios.musify.Models.Album;
import com.ash.studios.musify.R;
import com.ash.studios.musify.utils.Utils;
import com.bumptech.glide.Glide;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> implements SectionTitleProvider {
    private final ArrayList<Album> albums;
    private final Context context;

    public AlbumAdapter(Context context, ArrayList<Album> albums, ProgressBar pb, TextView nf) {
        this.albums = albums;
        this.context = context;

        if (pb != null) pb.setVisibility(View.GONE);
        if (nf != null && getItemCount() == 0) nf.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.ViewHolder holder, int position) {
        Album album = albums.get(position);

        holder.albumName.setText(album.getAlbum());
        holder.albumArtist.setText(album.getArtist());
        holder.songsCount.setText(new StringBuilder("\u266B ").append(album.getSong_count()));
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(Utils.getAlbumArt(album.getAlbum_id()))
                .placeholder(R.drawable.placeholder)
                .into(holder.albumCover);

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, BunchList.class)
                .putExtra("list_from", "Albums")
                .putExtra("list_name", album.getAlbum())
                .putExtra("list", Utils.getAlbumSongs(context, album.getAlbum_id()))));
    }

    @Override
    public int getItemCount() {
        return albums == null ? 0 : albums.size();
    }

    @Override
    public String getSectionTitle(int position) {
        return albums.get(position).getAlbum().substring(0, 1);
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