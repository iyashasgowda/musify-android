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
import com.ash.studios.musify.Model.Artist;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private ArrayList<Artist> artists;
    private Context context;

    public ArtistAdapter(Context context, ArrayList<Artist> artists, ProgressBar pb, TextView nf) {
        this.artists = artists;
        this.context = context;

        if (pb != null) pb.setVisibility(View.GONE);
        if (nf != null && getItemCount() == 0) nf.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistAdapter.ViewHolder holder, int position) {
        Artist artist = artists.get(position);

        holder.dummyText.setVisibility(View.GONE);
        holder.artistName.setText(artist.getArtist());
        holder.songCount.setText(new StringBuilder("\u266B ").append(artist.getSong_count()));
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(Utils.getAlbumArt(artist.getAlbum_id()))
                .placeholder(R.mipmap.ic_abstract)
                .into(holder.artistCover);

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, BunchList.class)
                .putExtra("list_from", "Artists")
                .putExtra("list_name", artist.getArtist())
                .putExtra("list", Utils.getArtistSongs(context, artist.getArtist_id()))));
    }

    @Override
    public int getItemCount() {
        return artists == null ? 0 : artists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView artistName, dummyText, songCount;
        ImageView artistCover;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            artistName = itemView.findViewById(R.id.title);
            dummyText = itemView.findViewById(R.id.artist);
            songCount = itemView.findViewById(R.id.duration);
            artistCover = itemView.findViewById(R.id.album_art);
        }
    }
}
