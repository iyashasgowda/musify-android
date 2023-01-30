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
import com.ash.studios.musify.Models.Album
import com.ash.studios.musify.R
import com.ash.studios.musify.activities.categories.BunchList
import com.ash.studios.musify.utils.Utils
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider

class AlbumAdapter(private val context: Context, private val albums: ArrayList<Album>?, pb: ProgressBar?, nf: TextView?) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>(), SectionTitleProvider {
    init {
        if (pb != null) pb.visibility = View.GONE
        if (nf != null && itemCount == 0) nf.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albums!![position]
        holder.albumName.text = album.album
        holder.albumArtist.text = album.artist
        holder.songsCount.text = StringBuilder("\u266B ").append(album.song_count)
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(Utils.getAlbumArt(album.album_id))
            .placeholder(R.drawable.placeholder)
            .into(holder.albumCover)
        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, BunchList::class.java)
                    .putExtra("list_from", "Albums")
                    .putExtra("list_name", album.album)
                    .putExtra("list", Utils.getAlbumSongs(context, album.album_id))
            )
        }
    }

    override fun getItemCount(): Int {
        return albums?.size ?: 0
    }

    override fun getSectionTitle(position: Int): String {
        return albums!![position].album.substring(0, 1)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var albumName: TextView
        var albumArtist: TextView
        var songsCount: TextView
        var albumCover: ImageView

        init {
            albumName = itemView.findViewById(R.id.title)
            albumArtist = itemView.findViewById(R.id.artist)
            songsCount = itemView.findViewById(R.id.duration)
            albumCover = itemView.findViewById(R.id.album_art)
        }
    }
}