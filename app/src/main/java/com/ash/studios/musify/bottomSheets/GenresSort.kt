package com.ash.studios.musify.bottomSheets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ash.studios.musify.R
import com.ash.studios.musify.adapters.GenreAdapter
import com.ash.studios.musify.utils.Constants.GENRES_SORT
import com.ash.studios.musify.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GenresSort(private var context: Context, var rv: RecyclerView, var pb: ProgressBar, var nf: TextView) : BottomSheetDialogFragment() {
    private lateinit var button0: RadioButton
    private lateinit var button1: RadioButton
    private lateinit var button2: RadioButton
    private lateinit var button3: RadioButton
    private lateinit var button4: RadioButton
    private lateinit var button5: RadioButton
    private lateinit var sortGroup: RadioGroup
    private lateinit var reverse: CheckBox
    private lateinit var close: ImageView
    var prefs: SharedPreferences = context.getSharedPreferences(GENRES_SORT, Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.sort_sheet, container, false)
        setIDs(v)
        return v
    }

    private fun setIDs(v: View) {
        close = v.findViewById(R.id.close)
        sortGroup = v.findViewById(R.id.sort_group)
        button0 = v.findViewById(R.id.by_track)
        button1 = v.findViewById(R.id.by_name)
        button2 = v.findViewById(R.id.by_title)
        button3 = v.findViewById(R.id.by_album)
        button4 = v.findViewById(R.id.by_artist)
        button5 = v.findViewById(R.id.by_year)
        reverse = v.findViewById(R.id.order_by_check)
        checkState
        close.setOnClickListener { dismiss() }
        button0.visibility = View.GONE
        button1.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            if (checked) {
                prefs.edit()
                    .putString("sort_by", "name")
                    .putBoolean("order_by", reverse.isChecked)
                    .apply()
                rv.adapter = GenreAdapter(context, Utils.genres, pb, nf)
            }
        }
        button2.visibility = View.GONE
        button3.visibility = View.GONE
        button4.visibility = View.GONE
        button5.visibility = View.GONE
        reverse.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            val list = Utils.genres
            list.reverse()
            if (checked) prefs.edit().putBoolean("order_by", true).apply() else prefs.edit().putBoolean("order_by", false).apply()
            rv.adapter = GenreAdapter(context, list, pb, nf)
        }
    }

    private val checkState: Unit
        get() {
            reverse.isChecked = prefs.getBoolean("order_by", false)
            if (prefs.getString("sort_by", "name") == "name") button1.isChecked = true
        }
}