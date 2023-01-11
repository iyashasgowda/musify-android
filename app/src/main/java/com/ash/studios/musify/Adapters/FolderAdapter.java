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

import com.ash.studios.musify.activities.categories.BunchList;
import com.ash.studios.musify.Models.Folder;
import com.ash.studios.musify.R;
import com.ash.studios.musify.utils.Utils;
import com.bumptech.glide.Glide;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> implements SectionTitleProvider {
    private final ArrayList<Folder> folders;
    private final Context context;

    public FolderAdapter(Context context, ArrayList<Folder> folders, ProgressBar pb, TextView nf) {
        this.folders = folders;
        this.context = context;

        if (pb != null) pb.setVisibility(View.GONE);
        if (nf != null && getItemCount() == 0) nf.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.ViewHolder holder, int position) {
        Folder folder = folders.get(position);

        holder.folderName.setText(folder.getName());
        holder.dummyText.setVisibility(View.GONE);
        holder.songsCount.setText(new StringBuilder("\u266B ").append(folder.getSongs().size()));
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(Utils.getAlbumArt(folder.getSongs().get(0).getAlbum_id()))
                .placeholder(R.drawable.placeholder)
                .into(holder.albumCover);

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, BunchList.class)
                .putExtra("list_from", "Folders")
                .putExtra("list_name", folder.getName())
                .putExtra("list", folder.getSongs())));
    }

    @Override
    public int getItemCount() {
        return folders == null ? 0 : folders.size();
    }

    @Override
    public String getSectionTitle(int position) {
        return folders.get(position).getName().substring(0, 1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderName, dummyText, songsCount;
        ImageView albumCover;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            folderName = itemView.findViewById(R.id.title);
            dummyText = itemView.findViewById(R.id.artist);
            songsCount = itemView.findViewById(R.id.duration);
            albumCover = itemView.findViewById(R.id.album_art);
        }
    }
}