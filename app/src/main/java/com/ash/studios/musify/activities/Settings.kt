package com.ash.studios.musify.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.ash.studios.musify.R
import com.ash.studios.musify.utils.Engine
import com.ash.studios.musify.utils.Utils
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class Settings : AppCompatActivity() {
    private lateinit var rateApp: ConstraintLayout
    private lateinit var moreApps: ConstraintLayout
    private lateinit var reportBug: ConstraintLayout
    private lateinit var shareApp: ConstraintLayout
    private lateinit var aboutApp: ConstraintLayout
    private lateinit var termsConditions: ConstraintLayout
    private lateinit var privacyPolicy: ConstraintLayout
    private lateinit var libraryOption: ConstraintLayout
    private lateinit var scrollView: ScrollView
    private lateinit var toolbar: Toolbar
    private lateinit var engine: Engine
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setIDs()
    }

    private fun setIDs() {
        context = this
        engine = Engine(context)
        shareApp = findViewById(R.id.share)
        aboutApp = findViewById(R.id.about)
        toolbar = findViewById(R.id.toolbar)
        rateApp = findViewById(R.id.rate_us)
        moreApps = findViewById(R.id.more_apps)
        reportBug = findViewById(R.id.report_bug)
        termsConditions = findViewById(R.id.terms)
        privacyPolicy = findViewById(R.id.privacy)
        scrollView = findViewById(R.id.settings_scroll_view)
        libraryOption = findViewById(R.id.library_options)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back_white)
        toolbar.setNavigationOnClickListener { finish() }
        OverScrollDecoratorHelper.setUpOverScroll(scrollView)
        privacyPolicy.setOnClickListener { viewPolicy() }
        moreApps.setOnClickListener { openDevAccount() }
        rateApp.setOnClickListener { rateApplication() }
        shareApp.setOnClickListener { shareApplication() }
        termsConditions.setOnClickListener { showTerms() }
        reportBug.setOnClickListener { reportApplicationBug() }
        aboutApp.setOnClickListener { showAbout() }
        libraryOption.setOnClickListener { Toast.makeText(context, "In development", Toast.LENGTH_SHORT).show() }
    }

    private fun showAbout() {
        val dialog = Utils.getDialog(context, R.layout.about)

        //LinkedIn
        val linkedin: ImageView = dialog.findViewById(R.id.linkedin)
        linkedin.setOnClickListener {
            val uri = Uri.parse(context.getString(R.string.dev_linkedin))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
            dialog.dismiss()
        }

        //Instagram
        val instagram: ImageView = dialog.findViewById(R.id.instagram)
        instagram.setOnClickListener {
            val uri = Uri.parse(context.getString(R.string.dev_instagram))
            val likeIng = Intent(Intent.ACTION_VIEW, uri)
            likeIng.setPackage("com.instagram.android")
            try {
                context.startActivity(likeIng)
            } catch (e: Exception) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/iyashasgowda")
                    )
                )
            }
            dialog.dismiss()
        }

        //Twitter
        val twitter: ImageView = dialog.findViewById(R.id.twitter)
        twitter.setOnClickListener {
            val uri = Uri.parse(context.getString(R.string.dev_twitter))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
            dialog.dismiss()
        }

        //Visiting LinkedIn
        val facebook: ImageView = dialog.findViewById(R.id.facebook)
        facebook.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100069452733970"))
                startActivity(intent)
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.dev_facebook))))
            }
        }

        //Setting Version
        val version: TextView = dialog.findViewById(R.id.version)
        val packageManager = context.applicationContext.packageManager
        val packageName = context.applicationContext.packageName
        var myVersionName: String? = "Version"
        try {
            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        version.text = StringBuilder("v").append(myVersionName)
    }

    @SuppressLint("WrongConstant")
    private fun showTerms() {
        val dialog = Utils.getDialog(context, R.layout.terms)
        val agree = dialog.findViewById<TextView>(R.id.agree)
        agree.setOnClickListener { dialog.dismiss() }
        val termsText = dialog.findViewById<TextView>(R.id.terms_text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) termsText.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
    }

    private fun viewPolicy() {
        val uri = Uri.parse("https://github.com/iyashasgowda/Privacy-Policies/blob/master/Musify.md")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun openDevAccount() {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data = Uri.parse(getString(R.string.dev_google_play))
        startActivity(intent)
    }

    private fun rateApplication() {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                )
            )
        }
    }

    private fun shareApplication() {
        val appPackageName = context.packageName
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=$appPackageName")
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Share using"))
    }

    private fun reportApplicationBug() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + getString(R.string.dev_email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Color Palette : Report Bug")
            intent.putExtra(Intent.EXTRA_TEXT, "")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}