package com.ash.studios.musify.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ash.studios.musify.bottomSheets.PlaylistSheet
import com.ash.studios.musify.models.Playlist
import com.ash.studios.musify.R
import com.ash.studios.musify.utils.Instance.position
import com.ash.studios.musify.utils.Utils
import com.bumptech.glide.Glide

class SheetListAdapter(private val context: Context, private val list: ArrayList<Playlist>?, pb: ProgressBar?, private val sheet: PlaylistSheet, nf: TextView?) :
    RecyclerView.Adapter<SheetListAdapter.VH>() {
    init {
        if (pb != null) pb.visibility = View.GONE
        if (nf != null && itemCount == 0) nf.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item_small, parent, false))
    }

    override fun onBindViewHolder(holder: VH, p: Int) {
        val playlist = list!![p]
        val songs = playlist.songs
        holder.sheetPlName.text = playlist.name
        holder.sheetPlCount.text = StringBuilder("\u266B ").append(songs.size)
        if (playlist.songs.size > 0) Glide.with(context.applicationContext)
            .asBitmap().load(Utils.getAlbumArt(playlist.songs[0].album_id))
            .placeholder(R.drawable.placeholder)
            .into(holder.sheetPlCover)
        holder.itemView.setOnClickListener {
            Utils.addToPlaylist(context, p, songs[position])
            sheet.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sheetPlName: TextView
        var sheetPlCount: TextView
        var sheetPlCover: ImageView

        init {
            sheetPlCover = itemView.findViewById(R.id.sheet_pl_item_art)
            sheetPlName = itemView.findViewById(R.id.sheet_pl_item_name)
            sheetPlCount = itemView.findViewById(R.id.sheet_pl_item_songs_count)
        }
    }
}