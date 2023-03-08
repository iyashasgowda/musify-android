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
import com.ash.studios.musify.models.Folder
import com.ash.studios.musify.R
import com.ash.studios.musify.activities.categories.BunchList
import com.ash.studios.musify.utils.Utils
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider

class FolderAdapter(private val context: Context, private val folders: ArrayList<Folder>?, pb: ProgressBar?, nf: TextView?) : RecyclerView.Adapter<FolderAdapter.ViewHolder>(), SectionTitleProvider {
    init {
        if (pb != null) pb.visibility = View.GONE
        if (nf != null && itemCount == 0) nf.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders!![position]
        holder.folderName.text = folder.name
        holder.dummyText.visibility = View.GONE
        holder.songsCount.text = StringBuilder("\u266B ").append(folder.songs.size)
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(Utils.getAlbumArt(folder.songs[0].album_id))
            .placeholder(R.drawable.placeholder)
            .into(holder.albumCover)
        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, BunchList::class.java)
                    .putExtra("list_from", "Folders")
                    .putExtra("list_name", folder.name)
                    .putExtra("list", folder.songs)
            )
        }
    }

    override fun getItemCount(): Int {
        return folders?.size ?: 0
    }

    override fun getSectionTitle(position: Int): String {
        return folders!![position].name.substring(0, 1)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var folderName: TextView
        var dummyText: TextView
        var songsCount: TextView
        var albumCover: ImageView

        init {
            folderName = itemView.findViewById(R.id.title)
            dummyText = itemView.findViewById(R.id.artist)
            songsCount = itemView.findViewById(R.id.duration)
            albumCover = itemView.findViewById(R.id.album_art)
        }
    }
}