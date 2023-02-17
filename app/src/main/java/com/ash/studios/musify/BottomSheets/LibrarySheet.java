package com.ash.studios.musify.BottomSheets;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ash.studios.musify.Activities.Library;
import com.ash.studios.musify.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.ash.studios.musify.Utils.Constants.LIBRARY_OPTIONS;

public class LibrarySheet extends BottomSheetDialogFragment {
    ConstraintLayout list0, list1, list2, list3, list4, list5, list6, list7, list8;
    CheckBox check0, check1, check2, check3, check4, check5, check6, check7, check8;
    ImageView close;

    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public LibrarySheet(Context context) {
        this.context = context;

        prefs = context.getSharedPreferences(LIBRARY_OPTIONS, Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lib_sheet, container, false);
        setIDs(v);
        getCheckStates();
        return v;
    }

    private void setIDs(View v) {
        close = v.findViewById(R.id.close);
        check0 = v.findViewById(R.id.all_songs_check);
        check1 = v.findViewById(R.id.folders_check);
        check2 = v.findViewById(R.id.albums_check);
        check3 = v.findViewById(R.id.artist_check);
        check4 = v.findViewById(R.id.genres_check);
        check5 = v.findViewById(R.id.years_check);
        check6 = v.findViewById(R.id.playlist_check);
        check7 = v.findViewById(R.id.top_rated_check);
        check8 = v.findViewById(R.id.low_rated_check);

        list0 = ((Library) context).findViewById(R.id.all_songs);
        list1 = ((Library) context).findViewById(R.id.folders);
        list2 = ((Library) context).findViewById(R.id.albums);
        list3 = ((Library) context).findViewById(R.id.artists);
        list4 = ((Library) context).findViewById(R.id.genres);
        list5 = ((Library) context).findViewById(R.id.years);
        list6 = ((Library) context).findViewById(R.id.play_lists);
        list7 = ((Library) context).findViewById(R.id.top_rated);
        list8 = ((Library) context).findViewById(R.id.low_rated);

        close.setOnClickListener(c -> dismiss());
        check0.setOnCheckedChangeListener((compoundButton, checked) -> {
            editor = prefs.edit();
            if (checked) {
                editor.putBoolean("state0", true);
                list0.setVisibility(View.VISIBLE);
            } else {
                editor.putBoolean("state0", false);
                list0.setVisibility(View.GONE);
            }
            editor.apply();
        });
        check1.setOnCheckedChangeListener((compoundButton, checked) -> {
            editor = prefs.edit();
            if (checked) {
                editor.putBoolean("state1", true);
                list1.setVisibility(View.VISIBLE);
            } else {
                editor.putBoolean("state1", false);
                list1.setVisibility(View.GONE);
            }
            editor.apply();
        });
        check2.setOnCheckedChangeListener((compoundButton, checked) -> {
            editor = prefs.edit();
            if (checked) {
                editor.putBoolean("state2", true);
                list2.setVisibility(View.VISIBLE);
            } else {
                editor.putBoolean("state2", false);
                list2.setVisibility(View.GONE);
            }
            editor.apply();
        });
        check3.setOnCheckedChangeListener((compoundButton, checked) -> {
            editor = prefs.edit();
            if (checked) {
                editor.putBoolean("state3", true);
                list3.setVisibility(View.VISIBLE);
            } else {
                editor.putBoolean("state3", false);
                list3.setVisibility(View.GONE);
            }
            editor.apply();
        });
        check4.setOnCheckedChangeListener((compoundButton, checked) -> {
            editor = prefs.edit();
            if (checked) {
                editor.putBoolean("state4", true);
                list4.setVisibility(View.VISIBLE);
            } else {
                editor.putBoolean("state4", false);
                list4.setVisibility(View.GONE);
            }
            editor.apply();
        });
        check5.setOnCheckedChangeListener((compoundButton, checked) -> {
            editor = prefs.edit();
            if (checked) {
                editor.putBoolean("state5", true);
                list5.setVisibility(View.VISIBLE);
            } else {
                editor.putBoolean("state5", false);
                list5.setVisibility(View.GONE);
            }
            editor.apply();
        });
        check6.setOnCheckedChangeListener((compoundButton, checked) -> {
            editor = prefs.edit();
            if (checked) {
                editor.putBoolean("state6", true);
                list6.setVisibility(View.VISIBLE);
            } else {
                editor.putBoolean("state6", false);
                list6.setVisibility(View.GONE);
            }
            editor.apply();
        });
        check7.setOnCheckedChangeListener((compoundButton, checked) -> {
            editor = prefs.edit();
            if (checked) {
                editor.putBoolean("state7", true);
                list7.setVisibility(View.VISIBLE);
            } else {
                editor.putBoolean("state7", false);
                list7.setVisibility(View.GONE);
            }
            editor.apply();
        });
        check8.setOnCheckedChangeListener((compoundButton, checked) -> {
            editor = prefs.edit();
            if (checked) {
                editor.putBoolean("state8", true);
                list8.setVisibility(View.VISIBLE);
            } else {
                editor.putBoolean("state8", false);
                list8.setVisibility(View.GONE);
            }
            editor.apply();
        });

    }

    private void getCheckStates() {
        check0.setChecked(prefs.getBoolean("state0", true));
        check1.setChecked(prefs.getBoolean("state1", true));
        check2.setChecked(prefs.getBoolean("state2", true));
        check3.setChecked(prefs.getBoolean("state3", true));
        check4.setChecked(prefs.getBoolean("state4", true));
        check5.setChecked(prefs.getBoolean("state5", true));
        check6.setChecked(prefs.getBoolean("state6", true));
        check7.setChecked(prefs.getBoolean("state7", true));
        check8.setChecked(prefs.getBoolean("state8", true));
    }
}
