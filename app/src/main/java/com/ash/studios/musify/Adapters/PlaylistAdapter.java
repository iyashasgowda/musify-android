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

import com.ash.studios.musify.Activities.Categories.BunchList;
import com.ash.studios.musify.Model.Playlist;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.VH> {
    private Context context;
    private ArrayList<Playlist> list;

    public PlaylistAdapter(Context context, ArrayList<Playlist> list, ProgressBar pb, TextView nf) {
        this.context = context;
        this.list = list;
        if (pb != null) pb.setVisibility(View.GONE);
        if (nf != null && getItemCount() == 0) nf.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public PlaylistAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.VH holder, int position) {
        Playlist playlist = list.get(position);
        ArrayList<Song> songs = playlist.getSongs();

        holder.playlistArtist.setVisibility(View.GONE);
        holder.playlistName.setText(playlist.getName());
        holder.playlistCount.setText(new StringBuilder("\u266B ").append(songs.size()));
        if (playlist.getSongs().size() > 0)
            Glide.with(context.getApplicationContext())
                    .asBitmap().load(Utils.getAlbumArt(playlist.getSongs().get(0).getAlbum_id()))
                    .placeholder(R.mipmap.icon)
                    .into(holder.playlistCover);

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, BunchList.class)
                .putExtra("list_from", "Playlists")
                .putExtra("list_name", playlist.getName())
                .putExtra("list", songs)));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView playlistName, playlistArtist, playlistCount;
        ImageView playlistCover;

        public VH(@NonNull View itemView) {
            super(itemView);

            playlistName = itemView.findViewById(R.id.title);
            playlistArtist = itemView.findViewById(R.id.artist);
            playlistCount = itemView.findViewById(R.id.duration);
            playlistCover = itemView.findViewById(R.id.album_art);
        }
    }
}
