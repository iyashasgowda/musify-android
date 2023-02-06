package com.ash.studios.musify.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ash.studios.musify.Models.Playlist
import com.ash.studios.musify.R
import com.ash.studios.musify.activities.categories.PLBunchList
import com.ash.studios.musify.activities.categories.PlayList
import com.ash.studios.musify.utils.Utils
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider

class PlaylistAdapter(private val context: Context, private val list: ArrayList<Playlist>?, pb: ProgressBar?, nf: TextView?) : RecyclerView.Adapter<PlaylistAdapter.VH>(), SectionTitleProvider {
    init {
        if (pb != null) pb.visibility = View.GONE
        if (nf != null && itemCount == 0) nf.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val playlist = list!![position]
        val songs = playlist.songs
        holder.playlistArtist.visibility = View.GONE
        holder.playlistName.text = playlist.name
        holder.playlistCount.text = StringBuilder("\u266B ").append(songs.size)
        if (playlist.songs.size > 0) Glide.with(context.applicationContext)
            .asBitmap().load(Utils.getAlbumArt(playlist.songs[0].album_id))
            .placeholder(R.drawable.placeholder)
            .into(holder.playlistCover)
        holder.itemView.setOnClickListener {
            if (playlist.songs.size > 0) context.startActivity(
                Intent(context, PLBunchList::class.java)
                    .putExtra("position", position)
                    .putExtra("playlist", playlist)
            ) else Toast.makeText(context, "No songs in that playlist :(", Toast.LENGTH_SHORT).show()
        }
        holder.itemView.setOnLongClickListener {
            val dialog = Utils.getDialog(context, R.layout.delete_dg)
            val title = dialog.findViewById<TextView>(R.id.del_dg_title)
            val body = dialog.findViewById<TextView>(R.id.del_dg_body)
            val cancel = dialog.findViewById<TextView>(R.id.close_del_dg_btn)
            val delete = dialog.findViewById<TextView>(R.id.del_song_btn)
            title.text = StringBuilder("Remove playlist?")
            body.text = StringBuilder("Selected playlist will be removed from this playlists")
            cancel.setOnClickListener { dialog.dismiss() }
            delete.setOnClickListener {
                dialog.dismiss()
                list.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
                Utils.deletePlaylist(context, playlist)
                if (itemCount == 0) {
                    (context as PlayList).finish()
                    Toast.makeText(context, "No playlist found.", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun getSectionTitle(position: Int): String {
        return list!![position].name.substring(0, 1)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var playlistName: TextView
        var playlistArtist: TextView
        var playlistCount: TextView
        var playlistCover: ImageView

        init {
            playlistName = itemView.findViewById(R.id.title)
            playlistArtist = itemView.findViewById(R.id.artist)
            playlistCount = itemView.findViewById(R.id.duration)
            playlistCover = itemView.findViewById(R.id.album_art)
        }
    }
}