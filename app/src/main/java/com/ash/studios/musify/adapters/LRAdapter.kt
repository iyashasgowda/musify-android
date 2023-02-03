package com.ash.studios.musify.adapters

import android.content.Context
import android.media.MediaPlayer.OnCompletionListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ash.studios.musify.Interfaces.IService
import com.ash.studios.musify.Models.Song
import com.ash.studios.musify.R
import com.ash.studios.musify.activities.categories.LRList
import com.ash.studios.musify.utils.Instance.mp
import com.ash.studios.musify.utils.Instance.position
import com.ash.studios.musify.utils.Instance.songs
import com.ash.studios.musify.utils.Utils
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider

class LRAdapter(private val context: Context, var list: ArrayList<Song>, pb: ProgressBar?, nf: TextView?) : RecyclerView.Adapter<LRAdapter.VH>(), SectionTitleProvider {
    init {
        if (pb != null) pb.visibility = View.GONE
        if (nf != null && itemCount == 0) nf.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, p: Int) {
        val song = list[p]
        holder.songName.text = song.title
        holder.songArtist.text = song.artist
        holder.duration.text = Utils.getDuration(song.duration)
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(Utils.getAlbumArt(song.album_id))
            .placeholder(R.drawable.placeholder)
            .into(holder.albumCover)
        holder.itemView.setOnClickListener {
            songs = list
            position = p
            (context as IService).onStartService()
            mp!!.setOnCompletionListener(context as OnCompletionListener)
        }
        holder.itemView.setOnLongClickListener {
            val dialog = Utils.getDialog(context, R.layout.delete_dg)
            val title = dialog.findViewById<TextView>(R.id.del_dg_title)
            val body = dialog.findViewById<TextView>(R.id.del_dg_body)
            val cancel = dialog.findViewById<TextView>(R.id.close_del_dg_btn)
            val delete = dialog.findViewById<TextView>(R.id.del_song_btn)
            title.text = StringBuilder("Remove song?")
            body.text = StringBuilder("Selected song will be removed from the Low-Rated")
            cancel.setOnClickListener { dialog.dismiss() }
            delete.setOnClickListener {
                dialog.dismiss()
                list.removeAt(p)
                notifyItemRemoved(p)
                notifyItemRangeChanged(p, itemCount)
                Utils.deleteFromLR(context, song)
                if (itemCount == 0) {
                    (context as LRList).finish()
                    Toast.makeText(context, "No songs in the Low-Rated", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getSectionTitle(position: Int): String {
        return list[position].title.substring(0, 1)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songName: TextView
        var songArtist: TextView
        var duration: TextView
        var albumCover: ImageView

        init {
            songName = itemView.findViewById(R.id.title)
            songArtist = itemView.findViewById(R.id.artist)
            duration = itemView.findViewById(R.id.duration)
            albumCover = itemView.findViewById(R.id.album_art)
        }
    }
}