package com.ash.studios.musify.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.palette.graphics.Palette;

import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jackandphantom.blurimage.BlurImage;

import java.io.IOException;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class Player extends AppCompatActivity {
    Bitmap bitmap;
    CircularSeekBar seekBar;
    FloatingActionButton playPause;
    TextView title, artist, duration;
    ImageView albumArt, background, previous, next, shuffle, repeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        Utils.setUpUI(this);
        setIDs();

        Song song = (Song) getIntent().getSerializableExtra("SONG");
        setSongAttrs(song);
    }

    private void setSongAttrs(Song song) {
        if (song != null) {
            title.setText(song.getTitle());
            title.setSelected(true);

            artist.setText(song.getArtist());
            duration.setText(Utils.getDuration(song.getDuration()));
            duration.setTypeface(ResourcesCompat.getFont(this, R.font.josefin_sans_bold));

            Uri image = Utils.getAlbumArt(song.getAlbum_id());
            Glide.with(this).load(image).placeholder(R.mipmap.icon).into(albumArt);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
            } catch (IOException e) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
            }
            BlurImage.with(this).load(bitmap).intensity(30).Async(true).into(background);

            Palette.from(bitmap).generate(palette -> {

                if (palette != null) {
                    Palette.Swatch vibrant = palette.getDarkVibrantSwatch();

                    if (vibrant != null) {
                        String accent = "#" + String.format("%06X", (0xFFFFFF & vibrant.getRgb()));
                        String accentLight = "#80" + String.format("%06X", (0xFFFFFF & vibrant.getRgb()));

                        seekBar.setPointerColor(Color.parseColor(accent));
                        seekBar.setCircleColor(Color.parseColor(accentLight));
                        seekBar.setCircleProgressColor(Color.parseColor(accent));
                    }
                }
            });
        }
    }

    private void setIDs() {
        title = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        seekBar = findViewById(R.id.seek_bar);
        duration = findViewById(R.id.duration);
        albumArt = findViewById(R.id.album_art);
        background = findViewById(R.id.background);
    }
}