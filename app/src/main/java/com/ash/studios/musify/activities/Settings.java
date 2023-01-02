package com.ash.studios.musify.activities;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;
import static com.ash.studios.musify.utils.Utils.getDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ash.studios.musify.R;
import com.ash.studios.musify.utils.Engine;
import com.ash.studios.musify.utils.Utils;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class Settings extends AppCompatActivity {
    ConstraintLayout rateApp, moreApps, reportBug, shareApp, aboutApp, termsConditions, privacyPolicy, libraryOption;
    ScrollView scrollView;
    Toolbar toolbar;

    Engine engine;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        setIDs();
    }

    private void setIDs() {
        context = this;
        engine = new Engine(context);
        shareApp = findViewById(R.id.share);
        aboutApp = findViewById(R.id.about);
        toolbar = findViewById(R.id.toolbar);
        rateApp = findViewById(R.id.rate_us);
        moreApps = findViewById(R.id.more_apps);
        reportBug = findViewById(R.id.report_bug);
        termsConditions = findViewById(R.id.terms);
        privacyPolicy = findViewById(R.id.privacy);
        scrollView = findViewById(R.id.settings_scroll_view);
        libraryOption = findViewById(R.id.library_options);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(view -> finish());
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);

        privacyPolicy.setOnClickListener(v -> viewPolicy());
        moreApps.setOnClickListener(v -> openDevAccount());
        rateApp.setOnClickListener(v -> rateApplication());
        shareApp.setOnClickListener(v -> shareApplication());
        termsConditions.setOnClickListener(v -> showTerms());
        reportBug.setOnClickListener(v -> reportApplicationBug());
        aboutApp.setOnClickListener(v -> showAbout());
        libraryOption.setOnClickListener(v -> Toast.makeText(context, "In development", Toast.LENGTH_SHORT).show());
    }

    private void showAbout() {
        Dialog dialog = getDialog(context, R.layout.about);
        ImageView linkedin, twitter, instagram, facebook;
        TextView version;

        //LinkedIn
        linkedin = dialog.findViewById(R.id.linkedin);
        linkedin.setOnClickListener(v -> {
            Uri uri = Uri.parse(context.getString(R.string.dev_linkedin));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            dialog.dismiss();
        });

        //Instagram
        instagram = dialog.findViewById(R.id.instagram);
        instagram.setOnClickListener(v -> {
            Uri uri = Uri.parse(context.getString(R.string.dev_instagram));
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.instagram.android");

            try {
                context.startActivity(likeIng);
            } catch (Exception e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/iyashasgowda")));
            }
            dialog.dismiss();
        });

        //Twitter
        twitter = dialog.findViewById(R.id.twitter);
        twitter.setOnClickListener(v -> {
            Uri uri = Uri.parse(context.getString(R.string.dev_twitter));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            dialog.dismiss();
        });

        //Visiting LinkedIn
        facebook = dialog.findViewById(R.id.facebook);
        facebook.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100069452733970"));
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.dev_facebook))));
            }
        });

        //Setting Version
        version = dialog.findViewById(R.id.version);
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        String packageName = context.getApplicationContext().getPackageName();
        String myVersionName = "Version";

        try {
            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setText(new StringBuilder("v").append(myVersionName));
    }

    @SuppressLint("WrongConstant")
    private void showTerms() {
        Dialog dialog = Utils.getDialog(context, R.layout.terms);

        TextView agree = dialog.findViewById(R.id.agree);
        agree.setOnClickListener(v -> dialog.dismiss());
        TextView termsText = dialog.findViewById(R.id.terms_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            termsText.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
    }

    private void viewPolicy() {
        Uri uri = Uri.parse("https://github.com/iyashasgowda/Privacy-Policies/blob/master/Musify.md");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void openDevAccount() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(getString(R.string.dev_google_play)));
        startActivity(intent);
    }

    private void rateApplication() {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        try {
            startActivity(goToMarket);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    private void shareApplication() {
        String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share using"));
    }

    private void reportApplicationBug() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + getString(R.string.dev_email)));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Color Palette : Report Bug");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}