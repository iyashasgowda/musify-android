package com.ash.studios.musify.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Model.Artist;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Artists extends RecyclerView.Adapter<Artists.ViewHolder> {
    public static ArrayList<Artist> artists;
    private Context context;

    public Artists(Context context, ArrayList<Artist> artists) {
        this.context = context;
        Artists.artists = artists;
    }

    @NonNull
    @Override
    public Artists.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Artists.ViewHolder holder, int position) {
        Artist artist = artists.get(position);

        holder.artistName.setText(artist.getArtist());
        holder.songCount.setText(artist.getSong_count() == 1 ? "1 Song" : artist.getSong_count() + " Songs");
        holder.albumCount.setText(artist.getAlbum_count() == 1 ? "1 Album" : artist.getAlbum_count() + " Albums");
        holder.albumCount.setTypeface(ResourcesCompat.getFont(context, R.font.josefin_sans_bold));
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(Utils.getAlbumArt(artist.getArtist_id()))
                .placeholder(R.mipmap.icon)
                .into(holder.albumCover);

        holder.itemView.setOnClickListener(v -> {
            //TODO
        });
    }

    @Override
    public int getItemCount() {
        return artists == null ? 0 : artists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView artistName, albumCount, songCount;
        ImageView albumCover;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            artistName = itemView.findViewById(R.id.title);
            songCount = itemView.findViewById(R.id.artist);
            albumCount = itemView.findViewById(R.id.duration);
            albumCover = itemView.findViewById(R.id.album_art);
        }
    }
}
