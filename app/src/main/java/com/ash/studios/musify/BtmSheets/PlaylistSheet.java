package com.ash.studios.musify.BtmSheets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Adapters.SheetListAdapter;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.ash.studios.musify.Utils.Utils.getPlaylists;

public class PlaylistSheet extends BottomSheetDialogFragment {
    TextView NF;
    RecyclerView rv;
    Context context;
    ProgressBar loader;
    ConstraintLayout addNewBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pl_sheet, container, false);
        setIDs(v);

        addNewBtn.setOnClickListener(btn -> {
            Dialog nameDialog = Utils.getDialog(context, R.layout.name_dg);

            TextView okBtn = nameDialog.findViewById(R.id.ok_btn);
            TextView cancel = nameDialog.findViewById(R.id.cancel_btn);
            EditText plEditText = nameDialog.findViewById(R.id.playlist_edit_text);
            plEditText.requestFocus();

            cancel.setOnClickListener(c -> nameDialog.dismiss());
            okBtn.setOnClickListener(ok -> {
                String playlistName = plEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(playlistName)) {
                    Utils.createNewPlaylist(context, playlistName);
                    SheetListAdapter playlists = new SheetListAdapter(context, getPlaylists(context), loader, this, NF);
                    rv.setAdapter(playlists);
                    playlists.notifyDataSetChanged();
                    nameDialog.dismiss();
                }
                if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)
                    NF.setVisibility(View.GONE);
            });
        });

        rv.setAdapter(new SheetListAdapter(context, getPlaylists(context), loader, this, NF));
        return v;
    }

    private void setIDs(View v) {
        context = v.getContext();
        rv = v.findViewById(R.id.sheet_rv);
        loader = v.findViewById(R.id.sheet_pb);
        NF = v.findViewById(R.id.nothing_found);
        addNewBtn = v.findViewById(R.id.add_new_btn);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        rv.setLayoutManager(manager);
    }
}
