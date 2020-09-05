package com.ash.studios.musify.Adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Interfaces.IService;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AllSongAdapter extends RecyclerView.Adapter<AllSongAdapter.ViewHolder> {
    public ArrayList<Song> list;
    private Context context;

    public AllSongAdapter(Context context, ArrayList<Song> list, ProgressBar pb, TextView nf) {
        this.list = list;
        this.context = context;

        if (pb != null) pb.setVisibility(View.GONE);
        if (nf != null && getItemCount() == 0) nf.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public AllSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AllSongAdapter.ViewHolder holder, int position) {
        Song song = list.get(position);

        holder.songName.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());
        holder.duration.setText(Utils.getDuration(song.getDuration()));
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(Utils.getAlbumArt(song.getAlbum_id()))
                .placeholder(R.mipmap.ic_abstract)
                .into(holder.albumCover);

        holder.itemView.setOnClickListener(v -> {
            Instance.songs = list;
            Instance.position = position;
            ((IService) context).onStartService();
            Instance.mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
        });

        holder.itemView.setOnLongClickListener(v -> {
            /*Dialog dialog = Utils.getDialog(context, R.layout.delete_dg);
            TextView cancel = dialog.findViewById(R.id.close_del_dg_btn);
            TextView delete = dialog.findViewById(R.id.del_song_btn);

            cancel.setOnClickListener(c -> dialog.dismiss());
            delete.setOnClickListener(d -> {
                dialog.dismiss();

                int temp = 0;
                if (Instance.songs.get(Instance.position).getId() == song.getId()) {

                    if (Instance.position != Instance.songs.size() - 1) temp = Instance.position;

                    if (Instance.songs.size() > 1) new Engine(context).playNextSong();
                    else if (Instance.mp != null) {
                        Instance.mp.stop();
                        Instance.mp.reset();
                    }
                    ((IControl) context).onSongDeleted();
                }

                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, allSongs.get(position).getId());

                try {
                    //searchAndDelete(context, song);
                    context.getContentResolver().delete(uri, null, null);
                    allSongs.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, allSongs.size());
                    Toast.makeText(context, "Song deleted :)", Toast.LENGTH_SHORT).show();
                    Instance.position = temp;
                    Instance.songs.remove(Instance.position);
                } catch (Exception e) {
                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Couldn't delete the song :(", Toast.LENGTH_SHORT).show();
                }
            });

            if (allSongs.size() == 0) nf.setVisibility(View.VISIBLE);*/
            return true;
        });
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
