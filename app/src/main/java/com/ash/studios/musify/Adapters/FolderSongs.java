package com.ash.studios.musify.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Model.Folder;
import com.ash.studios.musify.R;

import java.util.List;

public class FolderSongs extends RecyclerView.Adapter<FolderSongs.ViewHolder> {
    private Context context;
    private List<Folder> list;

    public FolderSongs(Context context, List<Folder> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FolderSongs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.folder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderSongs.ViewHolder holder, int position) {
        Folder folder = list.get(position);

        holder.folderTitle.setText(folder.getFile().getName());
        holder.folderPath.setText(folder.getFile().getPath());
        holder.folderSongCount.setText(String.valueOf(folder.getFile_count()).concat(" Songs"));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderTitle, folderPath, folderSongCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            folderPath = itemView.findViewById(R.id.folder_path);
            folderTitle = itemView.findViewById(R.id.folder_title);
            folderSongCount = itemView.findViewById(R.id.folder_songs_count);
        }
    }
}
