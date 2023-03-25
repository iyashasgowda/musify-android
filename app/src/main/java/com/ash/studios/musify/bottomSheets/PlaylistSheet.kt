package com.ash.studios.musify.bottomSheets

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ash.studios.musify.R
import com.ash.studios.musify.adapters.SheetListAdapter
import com.ash.studios.musify.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaylistSheet : BottomSheetDialogFragment() {
    private lateinit var notFound: TextView
    private lateinit var rv: RecyclerView
    private lateinit var loader: ProgressBar
    private lateinit var addNewBtn: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetTheme)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.pl_sheet, container, false)
        setIDs(v)
        addNewBtn.setOnClickListener {
            val nameDialog = Utils.getDialog(requireContext(), R.layout.name_dg)
            val okBtn = nameDialog.findViewById<TextView>(R.id.ok_btn)
            val cancel = nameDialog.findViewById<TextView>(R.id.cancel_btn)
            val plEditText = nameDialog.findViewById<EditText>(R.id.playlist_edit_text)
            plEditText.requestFocus()
            cancel.setOnClickListener { nameDialog.dismiss() }
            okBtn.setOnClickListener {
                val playlistName = plEditText.text.toString().trim { it <= ' ' }
                if (!TextUtils.isEmpty(playlistName)) {
                    Utils.createNewPlaylist(requireContext(), playlistName)
                    val playlists = SheetListAdapter(requireContext(), Utils.getPlaylists(requireContext()), loader, this, notFound)
                    rv.adapter = playlists
                    playlists.notifyDataSetChanged()
                    nameDialog.dismiss()
                }
                if (rv.adapter != null && rv.adapter!!.itemCount > 0) notFound.visibility = View.GONE
            }
        }
        rv.adapter = SheetListAdapter(requireContext(), Utils.getPlaylists(requireContext()), loader, this, notFound)
        return v
    }

    private fun setIDs(v: View) {
        rv = v.findViewById(R.id.sheet_rv)
        loader = v.findViewById(R.id.sheet_pb)
        notFound = v.findViewById(R.id.nothing_found)
        addNewBtn = v.findViewById(R.id.add_new_btn)
        val manager = LinearLayoutManager(requireContext())
        manager.reverseLayout = true
        manager.stackFromEnd = true
        rv.layoutManager = manager
    }
}