package com.ash.studios.musify.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;

public class Library extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);
        Utils.setUpUI(this);
        checkPermission();

        findViewById(R.id.all_songs).setOnClickListener(this);
        findViewById(R.id.folders).setOnClickListener(this);
        findViewById(R.id.albums).setOnClickListener(this);
        findViewById(R.id.artists).setOnClickListener(this);
        findViewById(R.id.genres).setOnClickListener(this);
        findViewById(R.id.play_lists).setOnClickListener(this);
        findViewById(R.id.top_rated).setOnClickListener(this);
        findViewById(R.id.recently_added).setOnClickListener(this);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission granted :)", Toast.LENGTH_SHORT).show();
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, SongList.class);

        switch (view.getId()) {
            case R.id.all_songs:
                intent.putExtra("list_type", "all_songs");
                startActivity(intent);
                break;
            case R.id.folders:
                intent.putExtra("list_type", "folders");
                startActivity(intent);
                break;
            case R.id.albums:
                intent.putExtra("list_type", "albums");
                startActivity(intent);
                break;
            case R.id.artists:
                intent.putExtra("list_type", "artists");
                startActivity(intent);
                break;
            case R.id.genres:
                intent.putExtra("list_type", "genres");
                startActivity(intent);
                break;
            case R.id.play_lists:
                intent.putExtra("list_type", "play_lists");
                startActivity(intent);
                break;
            case R.id.top_rated:
                intent.putExtra("list_type", "top_rated");
                startActivity(intent);
                break;
            case R.id.recently_added:
                intent.putExtra("list_type", "recently_added");
                startActivity(intent);
                break;
        }
    }

    private void setUpUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}