package com.quote.platon

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManagerFactory
import com.quote.platon.util.Screenshot


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        );

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_setting, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mediaPlayer = MediaPlayer.create(
            this, R.raw.nome
        )

        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }

        val webView: WebView = findViewById(R.id.webView)

        webView.loadUrl("file:///android_asset/index.html");

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.count() > 0
                    && grantResults[0] === PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("Permission Allowed", "Permission allowed from user.")
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(
                        this,
                        "Permission denied to read your External storage.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)

        if (item.itemId == R.id.action_settings) {
            Log.d("", "action_settings")
            val manager = ReviewManagerFactory.create(this)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = request.result

                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    }
                } else {
                    // There was some problem, continue regardless of the result.
                }
            }


            navController.navigate(R.id.nav_setting)
        }

        if (item.itemId == R.id.share_quote) {
            Screenshot.takeScreenshot(findViewById<View>(R.id.include), this)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
