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
import com.ash.studios.musify.Models.Year;
import com.ash.studios.musify.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.VH> {
    private ArrayList<Year> years;
    private Context context;

    public YearAdapter(Context context, ArrayList<Year> years, ProgressBar pb, TextView nf) {
        this.years = years;
        this.context = context;

        if (pb != null) pb.setVisibility(View.GONE);
        if (nf != null && getItemCount() == 0) nf.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new YearAdapter.VH(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Year year = years.get(position);

        holder.dummyText.setVisibility(View.GONE);
        holder.yearName.setText(year.getYear());
        holder.songCount.setText(new StringBuilder("\u266B ").append(year.getSongs().size()));
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(year.getAlbumArt())
                .placeholder(R.drawable.placeholder)
                .into(holder.yearCover);

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, BunchList.class)
                .putExtra("list_from", "Years")
                .putExtra("list_name", year.getYear())
                .putExtra("list", year.getSongs())));
    }

    @Override
    public int getItemCount() {
        return years == null ? 0 : years.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView yearName, dummyText, songCount;
        ImageView yearCover;

        VH(@NonNull View itemView) {
            super(itemView);

            yearName = itemView.findViewById(R.id.title);
            dummyText = itemView.findViewById(R.id.artist);
            songCount = itemView.findViewById(R.id.duration);
            yearCover = itemView.findViewById(R.id.album_art);
        }
    }
}