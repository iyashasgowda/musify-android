package com.ash.studios.musify.Activities;

import static com.ash.studios.musify.utils.Utils.fetchAllSongs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ash.studios.musify.R;
import com.ash.studios.musify.utils.Instance;
import com.ash.studios.musify.utils.Utils;

public class Welcome extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    ImageView icon;

    Context context;
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        setIDs();

        new Handler(Looper.getMainLooper()).postDelayed(this::checkPermission, 500);
    }

    private void setIDs() {
        context = this;
        icon = findViewById(R.id.icon);
        appName = findViewById(R.id.app_name);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            fetchAllSongs(context);
            Instance.repeat = Utils.getRepStatus(context);
            Instance.shuffle = Utils.getShflStatus(context);
            appName.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
            icon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(context, Library.class));
                finish();
            }, 1800);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAllSongs(context);
                appName.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                icon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    startActivity(new Intent(context, Library.class));
                    finish();
                }, 1800);
            } else {
                checkPermission();
                Toast.makeText(context, "Storage permission required to read music files in your device..!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}