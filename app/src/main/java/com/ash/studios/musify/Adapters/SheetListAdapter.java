package com.ash.studios.musify.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.BtmSheets.PlaylistSheet;
import com.ash.studios.musify.Model.Playlist;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SheetListAdapter extends RecyclerView.Adapter<SheetListAdapter.VH> {
    private Context context;
    private PlaylistSheet sheet;
    private ArrayList<Playlist> list;

    public SheetListAdapter(Context context, ArrayList<Playlist> list, ProgressBar pb, PlaylistSheet sheet, TextView nf) {
        this.list = list;
        this.sheet = sheet;
        this.context = context;
        if (pb != null) pb.setVisibility(View.GONE);
        if (nf != null && getItemCount() == 0) nf.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public SheetListAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.item_small, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SheetListAdapter.VH holder, int position) {
        Playlist playlist = list.get(position);
        ArrayList<Song> songs = playlist.getSongs();

        holder.sheetPlName.setText(playlist.getName());
        holder.sheetPlCount.setText(new StringBuilder("\u266B ").append(songs.size()));
        if (playlist.getSongs().size() > 0)
            Glide.with(context.getApplicationContext())
                    .asBitmap().load(Utils.getAlbumArt(playlist.getSongs().get(0).getAlbum_id()))
                    .placeholder(R.mipmap.ic_abstract)
                    .into(holder.sheetPlCover);

        holder.itemView.setOnClickListener(v -> {
            Utils.addToPlaylist(context, position, Instance.songs.get(Instance.position));
            sheet.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView sheetPlName, sheetPlCount;
        ImageView sheetPlCover;

        public VH(@NonNull View itemView) {
            super(itemView);

            sheetPlCover = itemView.findViewById(R.id.sheet_pl_item_art);
            sheetPlName = itemView.findViewById(R.id.sheet_pl_item_name);
            sheetPlCount = itemView.findViewById(R.id.sheet_pl_item_songs_count);
        }
    }
}
