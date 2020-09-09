package com.ash.studios.musify.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Engine;
import com.ash.studios.musify.Utils.Utils;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;
import static com.ash.studios.musify.Utils.Utils.getDialog;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

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
        setUpUI(this);

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
        ImageView github, twitter, instagram, facebook;
        TextView version;

        //Visiting Github
        github = dialog.findViewById(R.id.github);
        github.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://github.com/yashas003");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            dialog.dismiss();
        });

        //Visiting Twitter
        twitter = dialog.findViewById(R.id.twitter);
        twitter.setOnClickListener(v -> {
            Intent intent;
            try {
                // get the Twitter app if possible
                context.getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=1281605687001243648"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (Exception e) {
                // no Twitter app! revert to browser
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/ash_studio_apps"));
            }
            context.startActivity(intent);
            dialog.dismiss();
        });

        //Visiting Instagram
        instagram = dialog.findViewById(R.id.instagram);
        instagram.setOnClickListener(v -> {
            Uri uri = Uri.parse("http://instagram.com/_u/ash.studio.apps");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.instagram.android");

            try {
                context.startActivity(likeIng);
            } catch (Exception e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/ash.studio.apps")));
            }
            dialog.dismiss();
        });

        //Visiting LinkedIn
        facebook = dialog.findViewById(R.id.facebook);
        facebook.setOnClickListener(v -> {
            String facebookUrl = "https://www.facebook.com/ash.studio.apps/";
            try {
                int versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                if (versionCode >= 3002850) {
                    Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } else {
                    Uri uri = Uri.parse("fb://page/ash.studio.apps");
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            } catch (PackageManager.NameNotFoundException e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
            }
            dialog.dismiss();
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
        Uri uri = Uri.parse("https://github.com/yashas003/Privacy-Policies/blob/master/Musify.md");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void openDevAccount() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://play.google.com/store/apps/dev?id=6467928347053951775"));
        startActivity(intent);
    }

    private void rateApplication() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        try {
            startActivity(goToMarket);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    private void shareApplication() {
        String appPackageName = this.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share using"));
    }

    private void reportApplicationBug() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "ash.studio.apps@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Color Palette : Report Bug");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}