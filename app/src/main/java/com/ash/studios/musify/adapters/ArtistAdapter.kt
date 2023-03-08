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
import com.ash.studios.musify.models.Artist
import com.ash.studios.musify.R
import com.ash.studios.musify.activities.categories.BunchList
import com.ash.studios.musify.utils.Utils
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider

class ArtistAdapter(private val context: Context, private val artists: ArrayList<Artist>?, pb: ProgressBar?, nf: TextView?) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>(), SectionTitleProvider {
    init {
        if (pb != null) pb.visibility = View.GONE
        if (nf != null && itemCount == 0) nf.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = artists!![position]
        holder.dummyText.visibility = View.GONE
        holder.artistName.text = artist.artist
        holder.songCount.text = StringBuilder("\u266B ").append(artist.song_count)
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(Utils.getAlbumArt(artist.album_id))
            .placeholder(R.drawable.placeholder)
            .into(holder.artistCover)
        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, BunchList::class.java)
                    .putExtra("list_from", "Artists")
                    .putExtra("list_name", artist.artist)
                    .putExtra("list", Utils.getArtistSongs(context, artist.artist_id))
            )
        }
    }

    override fun getItemCount(): Int {
        return artists?.size ?: 0
    }

    override fun getSectionTitle(position: Int): String {
        return artists!![position].artist.substring(0, 1)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var artistName: TextView
        var dummyText: TextView
        var songCount: TextView
        var artistCover: ImageView

        init {
            artistName = itemView.findViewById(R.id.title)
            dummyText = itemView.findViewById(R.id.artist)
            songCount = itemView.findViewById(R.id.duration)
            artistCover = itemView.findViewById(R.id.album_art)
        }
    }
}