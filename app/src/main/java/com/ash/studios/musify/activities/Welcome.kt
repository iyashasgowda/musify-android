package com.ash.studios.musify.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ash.studios.musify.R
import com.ash.studios.musify.utils.Instance.shuffle
import com.ash.studios.musify.utils.Instance.repeat
import com.ash.studios.musify.utils.Utils

class Welcome : AppCompatActivity() {
    private lateinit var icon: ImageView
    private lateinit var context: Context
    private lateinit var appName: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setIDs()
        Handler(Looper.getMainLooper()).postDelayed({ this.checkPermission() }, 500)
    }

    private fun setIDs() {
        context = this
        icon = findViewById(R.id.icon)
        appName = findViewById(R.id.app_name)
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
        } else {
            Utils.fetchAllSongs(context)
            repeat = Utils.getRepStatus(context)
            shuffle = Utils.getShflStatus(context)
            appName.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
            icon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(context, Library::class.java))
                finish()
            }, 1800)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.fetchAllSongs(context)
                appName.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
                icon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(context, Library::class.java))
                    finish()
                }, 1800)
            } else {
                checkPermission()
                Toast.makeText(context, "Storage permission required to read music files in your device..!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 1
    }
}