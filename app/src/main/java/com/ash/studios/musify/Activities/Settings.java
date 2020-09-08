package com.ash.studios.musify.Activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Engine;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Utils.setUpUI;

public class Settings extends AppCompatActivity {
    TextView supportTitle, legalTitle;
    ScrollView scrollView;
    Toolbar toolbar;

    Engine engine;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setUpUI(this);

        setIDs();
    }

    private void setIDs() {
        context = this;
        engine = new Engine(context);
        toolbar = findViewById(R.id.toolbar);
        legalTitle = findViewById(R.id.legal_title);
        supportTitle = findViewById(R.id.support_title);
        scrollView = findViewById(R.id.settings_scroll_view);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(view -> finish());
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }
}