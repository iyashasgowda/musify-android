package com.ash.studios.musify.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ash.studios.musify.R;

import static com.ash.studios.musify.Utils.Utils.fetchAllSongs;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

public class Welcome extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    ProgressBar loader;
    TextView holdOn;
    ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        setUpUI(this);
        setIDs();

        new Handler(Looper.getMainLooper()).postDelayed(this::checkPermission, 500);
    }

    private void setIDs() {
        icon = findViewById(R.id.icon);
        loader = findViewById(R.id.pb);
        holdOn = findViewById(R.id.hold_on);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            fetchAllSongs(this);
            icon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_out));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(Welcome.this, Library.class));
                finish();
            }, 1600);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAllSongs(this);
                holdOn.setVisibility(View.VISIBLE);
                loader.setVisibility(View.VISIBLE);
                icon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_out));
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    startActivity(new Intent(Welcome.this, Library.class));
                    finish();
                }, 1600);
            } else {
                checkPermission();
                Toast.makeText(this, "Storage permission required to read music files in your device..!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}