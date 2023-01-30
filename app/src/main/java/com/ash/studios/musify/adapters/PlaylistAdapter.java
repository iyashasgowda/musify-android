package com.ash.studios.musify.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.activities.categories.PLBunchList;
import com.ash.studios.musify.activities.categories.PlayList;
import com.ash.studios.musify.Models.Playlist;
import com.ash.studios.musify.Models.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.utils.Utils;
import com.bumptech.glide.Glide;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.VH> implements SectionTitleProvider {
    private final Context context;
    private final ArrayList<Playlist> list;

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
                    .placeholder(R.drawable.placeholder)
                    .into(holder.playlistCover);

        holder.itemView.setOnClickListener(v -> {
            if (playlist.getSongs().size() > 0)
                context.startActivity(new Intent(context, PLBunchList.class)
                        .putExtra("position", position)
                        .putExtra("playlist", playlist));
            else
                Toast.makeText(context, "No songs in that playlist :(", Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnLongClickListener(v -> {
            Dialog dialog = Utils.getDialog(context, R.layout.delete_dg);
            TextView title = dialog.findViewById(R.id.del_dg_title);
            TextView body = dialog.findViewById(R.id.del_dg_body);
            TextView cancel = dialog.findViewById(R.id.close_del_dg_btn);
            TextView delete = dialog.findViewById(R.id.del_song_btn);

            title.setText(new StringBuilder("Remove playlist?"));
            body.setText(new StringBuilder("Selected playlist will be removed from this playlists"));
            cancel.setOnClickListener(c -> dialog.dismiss());
            delete.setOnClickListener(d -> {
                dialog.dismiss();

                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                Utils.deletePlaylist(context, playlist);
                if (getItemCount() == 0) {
                    ((PlayList) context).finish();
                    Toast.makeText(context, "No playlist found.", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public String getSectionTitle(int position) {
        return list.get(position).getName().substring(0, 1);
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
