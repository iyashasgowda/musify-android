package com.ash.studios.musify.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Activities.Categories.LRList;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class LRAdapter extends RecyclerView.Adapter<LRAdapter.VH> {
    public ArrayList<Song> list;
    private Context context;

    public LRAdapter(Context context, ArrayList<Song> list, ProgressBar pb, TextView nf) {
        this.list = list;
        this.context = context;

        if (pb != null) pb.setVisibility(View.GONE);
        if (nf != null && getItemCount() == 0) nf.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public LRAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LRAdapter.VH(LayoutInflater.from(context).inflate(R.layout.item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull LRAdapter.VH holder, int position) {
        Song song = list.get(position);

        holder.songName.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());
        holder.duration.setText(Utils.getDuration(song.getDuration()));
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(Utils.getAlbumArt(song.getAlbum_id()))
                .placeholder(R.mipmap.icon)
                .into(holder.albumCover);

        holder.itemView.setOnClickListener(v -> {
            Instance.songs = list;
            Instance.position = position;
            ((IControl) context).onStartPlayer();
            Instance.mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
        });

        holder.itemView.setOnLongClickListener(v -> {
            Dialog dialog = Utils.getDialog(context, R.layout.delete_dg);
            TextView title = dialog.findViewById(R.id.del_dg_title);
            TextView body = dialog.findViewById(R.id.del_dg_body);
            TextView cancel = dialog.findViewById(R.id.close_del_dg_btn);
            TextView delete = dialog.findViewById(R.id.del_song_btn);

            title.setText(new StringBuilder("Remove song?"));
            body.setText(new StringBuilder("Selected song will be removed from the Low-Rated."));
            cancel.setOnClickListener(c -> dialog.dismiss());
            delete.setOnClickListener(d -> {
                dialog.dismiss();

                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                Utils.deleteFromLR(context, song);
                if (getItemCount() == 0) {
                    ((LRList) context).finish();
                    Toast.makeText(context, "No songs in the Low-Rated", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView songName, songArtist, duration;
        ImageView albumCover;

        public VH(@NonNull View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.title);
            songArtist = itemView.findViewById(R.id.artist);
            duration = itemView.findViewById(R.id.duration);
            albumCover = itemView.findViewById(R.id.album_art);
        }
    }
}
