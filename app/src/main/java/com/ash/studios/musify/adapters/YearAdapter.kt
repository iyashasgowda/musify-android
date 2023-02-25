package com.ash.studios.musify.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ash.studios.musify.Models.Year
import com.ash.studios.musify.R
import com.ash.studios.musify.activities.categories.BunchList
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider

class YearAdapter(private val context: Context, private val years: ArrayList<Year>?, pb: ProgressBar?, nf: TextView?) : RecyclerView.Adapter<YearAdapter.VH>(), SectionTitleProvider {
    init {
        if (pb != null) pb.visibility = View.GONE
        if (nf != null && itemCount == 0) nf.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val year = years!![position]
        holder.dummyText.visibility = View.GONE
        holder.yearName.text = year.year
        holder.songCount.text = StringBuilder("\u266B ").append(year.songs.size)
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(year.albumArt)
            .placeholder(R.drawable.placeholder)
            .into(holder.yearCover)
        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, BunchList::class.java)
                    .putExtra("list_from", "Years")
                    .putExtra("list_name", year.year)
                    .putExtra("list", year.songs)
            )
        }
    }

    override fun getItemCount(): Int {
        return years?.size ?: 0
    }

    override fun getSectionTitle(position: Int): String {
        return years!![position].year
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var yearName: TextView
        var dummyText: TextView
        var songCount: TextView
        var yearCover: ImageView

        init {
            yearName = itemView.findViewById(R.id.title)
            dummyText = itemView.findViewById(R.id.artist)
            songCount = itemView.findViewById(R.id.duration)
            yearCover = itemView.findViewById(R.id.album_art)
        }
    }
}