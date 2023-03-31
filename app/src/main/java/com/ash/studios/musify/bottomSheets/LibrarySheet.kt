package com.ash.studios.musify.bottomSheets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ash.studios.musify.R
import com.ash.studios.musify.activities.Library
import com.ash.studios.musify.utils.Constants.LIBRARY_OPTIONS
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LibrarySheet(var context: Context) : BottomSheetDialogFragment() {
    private lateinit var list0: ConstraintLayout
    private lateinit var list1: ConstraintLayout
    private lateinit var list2: ConstraintLayout
    private lateinit var list3: ConstraintLayout
    private lateinit var list4: ConstraintLayout
    private lateinit var list5: ConstraintLayout
    private lateinit var list6: ConstraintLayout
    private lateinit var list7: ConstraintLayout
    private lateinit var list8: ConstraintLayout
    private lateinit var check0: CheckBox
    private lateinit var check1: CheckBox
    private lateinit var check2: CheckBox
    private lateinit var check3: CheckBox
    private lateinit var check4: CheckBox
    private lateinit var check5: CheckBox
    private lateinit var check6: CheckBox
    private lateinit var check7: CheckBox
    private lateinit var check8: CheckBox
    private lateinit var close: ImageView
    private var prefs: SharedPreferences = context.getSharedPreferences(LIBRARY_OPTIONS, Context.MODE_PRIVATE)
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.lib_sheet, container, false)
        setIDs(v)
        checkStates
        return v
    }

    private fun setIDs(v: View) {
        close = v.findViewById(R.id.close)
        check0 = v.findViewById(R.id.all_songs_check)
        check1 = v.findViewById(R.id.folders_check)
        check2 = v.findViewById(R.id.albums_check)
        check3 = v.findViewById(R.id.artist_check)
        check4 = v.findViewById(R.id.genres_check)
        check5 = v.findViewById(R.id.years_check)
        check6 = v.findViewById(R.id.playlist_check)
        check7 = v.findViewById(R.id.top_rated_check)
        check8 = v.findViewById(R.id.low_rated_check)
        list0 = (context as Library).findViewById(R.id.all_songs)
        list1 = (context as Library).findViewById(R.id.folders)
        list2 = (context as Library).findViewById(R.id.albums)
        list3 = (context as Library).findViewById(R.id.artists)
        list4 = (context as Library).findViewById(R.id.genres)
        list5 = (context as Library).findViewById(R.id.years)
        list6 = (context as Library).findViewById(R.id.play_lists)
        list7 = (context as Library).findViewById(R.id.top_rated)
        list8 = (context as Library).findViewById(R.id.low_rated)
        close.setOnClickListener { dismiss() }
        check0.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            editor = prefs.edit()
            if (checked) {
                editor.putBoolean("state0", true)
                list0.visibility = View.VISIBLE
            } else {
                editor.putBoolean("state0", false)
                list0.visibility = View.GONE
            }
            editor.apply()
        }
        check1.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            editor = prefs.edit()
            if (checked) {
                editor.putBoolean("state1", true)
                list1.visibility = View.VISIBLE
            } else {
                editor.putBoolean("state1", false)
                list1.visibility = View.GONE
            }
            editor.apply()
        }
        check2.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            editor = prefs.edit()
            if (checked) {
                editor.putBoolean("state2", true)
                list2.visibility = View.VISIBLE
            } else {
                editor.putBoolean("state2", false)
                list2.visibility = View.GONE
            }
            editor.apply()
        }
        check3.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            editor = prefs.edit()
            if (checked) {
                editor.putBoolean("state3", true)
                list3.visibility = View.VISIBLE
            } else {
                editor.putBoolean("state3", false)
                list3.visibility = View.GONE
            }
            editor.apply()
        }
        check4.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            editor = prefs.edit()
            if (checked) {
                editor.putBoolean("state4", true)
                list4.visibility = View.VISIBLE
            } else {
                editor.putBoolean("state4", false)
                list4.visibility = View.GONE
            }
            editor.apply()
        }
        check5.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            editor = prefs.edit()
            if (checked) {
                editor.putBoolean("state5", true)
                list5.visibility = View.VISIBLE
            } else {
                editor.putBoolean("state5", false)
                list5.visibility = View.GONE
            }
            editor.apply()
        }
        check6.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            editor = prefs.edit()
            if (checked) {
                editor.putBoolean("state6", true)
                list6.visibility = View.VISIBLE
            } else {
                editor.putBoolean("state6", false)
                list6.visibility = View.GONE
            }
            editor.apply()
        }
        check7.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            editor = prefs.edit()
            if (checked) {
                editor.putBoolean("state7", true)
                list7.visibility = View.VISIBLE
            } else {
                editor.putBoolean("state7", false)
                list7.visibility = View.GONE
            }
            editor.apply()
        }
        check8.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            editor = prefs.edit()
            if (checked) {
                editor.putBoolean("state8", true)
                list8.visibility = View.VISIBLE
            } else {
                editor.putBoolean("state8", false)
                list8.visibility = View.GONE
            }
            editor.apply()
        }
    }

    private val checkStates: Unit
        get() {
            check0.isChecked = prefs.getBoolean("state0", true)
            check1.isChecked = prefs.getBoolean("state1", true)
            check2.isChecked = prefs.getBoolean("state2", true)
            check3.isChecked = prefs.getBoolean("state3", true)
            check4.isChecked = prefs.getBoolean("state4", true)
            check5.isChecked = prefs.getBoolean("state5", true)
            check6.isChecked = prefs.getBoolean("state6", true)
            check7.isChecked = prefs.getBoolean("state7", true)
            check8.isChecked = prefs.getBoolean("state8", true)
        }
}