package com.quote.platon

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManagerFactory
import com.quote.platon.ui.home.HomeFragment
import com.quote.platon.ui.setting.SettingViewModel
import com.quote.platon.util.Screenshot


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var settingViewModel: SettingViewModel
    private var isMusicEnable: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)


        settingViewModel =
            ViewModelProviders.of(this).get(SettingViewModel::class.java)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        );

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_favorites
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mediaPlayer = MediaPlayer.create(
            this, R.raw.nome
        )
        val preference =
            this.getSharedPreferences(
                resources.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
        val landscapeOrientation = preference?.getBoolean("landscapeOrientation", false)!!
        if (landscapeOrientation)
            requestedOrientation =
                if (Build.VERSION.SDK_INT < 9) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        isMusicEnable = preference?.getBoolean("music", true)!!


        mediaPlayer.stop()

        if (isMusicEnable) {
            mediaPlayer.prepare()
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }


        val adView = AdView(this)
        adView.adUnitId = "ca-app-pub-5856186651471440/6875909231"

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        findViewById<AdView>(R.id.adView).loadAd(adRequest)

        val webView: WebView = findViewById(R.id.webView)

        webView.loadUrl("file:///android_asset/index.html");

        settingViewModel.landscapeOrientation.observe(this, Observer {
            requestedOrientation = if (it)
                if (Build.VERSION.SDK_INT < 9) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else
                if (Build.VERSION.SDK_INT < 9) ActivityInfo.SCREEN_ORIENTATION_USER else ActivityInfo.SCREEN_ORIENTATION_USER

        })

        settingViewModel.musicState.observe(this, Observer {
            isMusicEnable = it
            if (it) {
                mediaPlayer.prepare()
                mediaPlayer.start()
            } else
                mediaPlayer.stop()
        })

    }


    override fun onPause() {
        if (isMusicEnable)
            mediaPlayer.pause()
        super.onPause()
    }

    override fun onResume() {
        if (isMusicEnable)
            mediaPlayer.start()
        super.onResume()
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


        if (item.itemId == R.id.nav_favorites) {

            val manager = ReviewManagerFactory.create(this)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    val reviewInfo = request.result
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener {
                        // Değerlendirme süreci tamamlandıktan sonra fragment geç
                        openHomeFragment("favorites")
                    }
                } else {
                    // Değerlendirme başarısız olsa da fragment geç
                    openHomeFragment("favorites")
                }
            }
        }

        if (item.itemId == R.id.share_quote) {
            Screenshot.takeScreenshot(findViewById<View>(R.id.include), this)
        }

        return super.onOptionsItemSelected(item)
    }
    private fun openHomeFragment(author: String) {
        val fragment = HomeFragment()
        val bundle = Bundle()
        bundle.putString("author", author)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_home, fragment) // container ID'yi kendi layout'undakiyle değiştir
            //.addToBackStack(null)
            .commit()
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
