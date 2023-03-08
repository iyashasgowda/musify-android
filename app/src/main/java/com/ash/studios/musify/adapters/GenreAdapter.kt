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
import com.ash.studios.musify.models.Genre
import com.ash.studios.musify.R
import com.ash.studios.musify.activities.categories.BunchList
import com.ash.studios.musify.utils.Utils
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider

class GenreAdapter(private val context: Context, private val genres: ArrayList<Genre>?, pb: ProgressBar?, nf: TextView?) : RecyclerView.Adapter<GenreAdapter.ViewHolder>(), SectionTitleProvider {
    init {
        if (pb != null) pb.visibility = View.GONE
        if (nf != null && itemCount == 0) nf.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = genres!![position]
        holder.dummyText.visibility = View.GONE
        holder.genreName.text = genre.genre
        holder.songCount.text = StringBuilder("\u266B ").append(genre.song_count)
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(Utils.getAlbumArt(genre.album_id))
            .placeholder(R.drawable.placeholder)
            .into(holder.genreCover)
        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, BunchList::class.java)
                    .putExtra("list_from", "Genres")
                    .putExtra("list_name", genre.genre)
                    .putExtra("list", Utils.getGenreSongs(context, genre.genre_id))
            )
        }
    }

    override fun getItemCount(): Int {
        return genres?.size ?: 0
    }

    override fun getSectionTitle(position: Int): String {
        return genres!![position].genre.substring(0, 1)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var genreName: TextView
        var dummyText: TextView
        var songCount: TextView
        var genreCover: ImageView

        init {
            genreName = itemView.findViewById(R.id.title)
            dummyText = itemView.findViewById(R.id.artist)
            songCount = itemView.findViewById(R.id.duration)
            genreCover = itemView.findViewById(R.id.album_art)
        }
    }
}