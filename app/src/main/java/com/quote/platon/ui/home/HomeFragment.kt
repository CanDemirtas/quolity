package com.quote.platon.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.quote.platon.MainActivity
import com.quote.platon.R
import com.quote.platon.R.*
import com.quote.platon.util.OnSwipeTouchListener
import com.quote.platon.util.Screenshot
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mediaPlayer: MediaPlayer
    private val main: View? = null
    private var isSwipeEffectEnable: Boolean = false;
    private var quotes: Array<String> = emptyArray()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(layout.fragment_home, container, false)
        //val textView: TextView = root.findViewById(R.id.text_home)
        //homeViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        //})
        val preference =
            context?.getSharedPreferences(
                resources.getString(string.app_name),
                Context.MODE_PRIVATE
            )

        isSwipeEffectEnable = preference?.getBoolean("swipeSoundEffect", true)!!

        val slideIn = AnimationUtils.loadAnimation(context, anim.slide_in)

        val quoteContent: TextView = root.findViewById(R.id.textView3)

        val heartView: ImageView = root.findViewById(R.id.heart_animation_view)

        // Kalp animasyonu: Scale ve fade out
        heartView.visibility = View.INVISIBLE
        heartView.scaleX = 1f
        heartView.scaleY = 1f
        heartView.alpha = 1f

        val scaleAnim = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnim.duration = 300 // 300ms scale up

        val fadeAnim = AlphaAnimation(1f, 0f)
        fadeAnim.duration = 500 // 500ms fade out
        fadeAnim.startOffset = 300 // Scale'den sonra başla

        scaleAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                heartView.startAnimation(fadeAnim)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        fadeAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                heartView.visibility = View.INVISIBLE
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })


        val philosopherName: TextView = root.findViewById(R.id.textView2)
        val authorKey = arguments?.getString("author") ?: "quotes"
        val webView = (activity as? MainActivity)?.findViewById<WebView>(R.id.webView)

        var swipeIndex = 0;
        webView?.loadUrl("file:///android_asset/index.html");

         quotes = when (authorKey) {
            "quotes" -> {
                resources.getStringArray(array.quotes)}
            "favorites" -> {
                val favSet = preference?.getStringSet("favorites", emptySet<String>()) ?: emptySet<String>()
                favSet.toTypedArray()
                //resources.getStringArray(array.favorites)
            }
            else -> resources.getStringArray(array.quotes)
        }

        var quotesTotalCount = quotes.count()

        val content = quotes[0].split("-").map { it.trim() }[0]
        val name = quotes[0].split("-").map { it.trim() }[1]
        quoteContent.text = '"' + content + '"'
        philosopherName.text = "-" + name

        var homeLayout = root.findViewById<ConstraintLayout>(R.id.homeLayout)
        mediaPlayer = MediaPlayer.create(
            context, raw.transition
        )
        mediaPlayer.setVolume(0.5F,0.5F)


        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
                if (quotesTotalCount == 0) return true

                val currentQuote = quotes[swipeIndex]
                val favorites = preference?.getStringSet("favorites", mutableSetOf<String>())?.toMutableSet() ?: mutableSetOf()

                if (authorKey == "quotes") {
                    if (!favorites.contains(currentQuote)) {
                        favorites.add(currentQuote)
                        preference?.edit()?.putStringSet("favorites", favorites)?.apply()
                        heartView.visibility = View.VISIBLE
                        heartView.startAnimation(scaleAnim)
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        heartView.startAnimation(scaleAnim)

                        Toast.makeText(context, "Already in favorites", Toast.LENGTH_SHORT).show()
                    }
                } else if (authorKey == "favorites") {
                    if (favorites.remove(currentQuote)) {
                        preference?.edit()?.putStringSet("favorites", favorites)?.apply()
                        // Update quotes
                        quotes = favorites.toTypedArray()
                        quotesTotalCount = quotes.size

                        if (quotesTotalCount > 0) {
                            if (swipeIndex >= quotesTotalCount) {
                                swipeIndex = quotesTotalCount - 1
                            }
                            val content = quotes[swipeIndex].split("-").map { it.trim() }[0]
                            val name = quotes[swipeIndex].split("-").map { it.trim() }[1]
                            quoteContent.text = '"' + content + '"'
                            philosopherName.text = "-" + name
                        } else {
                            swipeIndex = 0
                            quoteContent.text = "No quotes available"
                            philosopherName.text = ""
                        }

                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null || quotesTotalCount == 0) return false
                val deltaX = e2.x - e1.x
                val deltaY = e2.y - e1.y
                if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 100 && Math.abs(velocityX) > 100) {
                    if (deltaX < 0) {
                        // Swipe left
                        if (swipeIndex == quotesTotalCount - 1) swipeIndex = -1
                        swipeIndex++
                    } else {
                        // Swipe right
                        if (swipeIndex == 0) swipeIndex = quotesTotalCount
                        swipeIndex--
                    }
                    val content = quotes[swipeIndex].split("-").map { it.trim() }[0]
                    val name = quotes[swipeIndex].split("-").map { it.trim() }[1]
                    quoteContent.text = '"' + content + '"'
                    philosopherName.text = "-" + name

                    quoteContent.startAnimation(slideIn)
                    philosopherName.startAnimation(slideIn)

                    if (isSwipeEffectEnable) {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.stop()
                            mediaPlayer.prepare()
                            mediaPlayer.start()
                        } else {
                            mediaPlayer.start()
                        }
                    }
                    return true
                }
                return false
            }
        })




        homeLayout.setOnTouchListener(object : OnSwipeTouchListener() {

            override fun onSwipeLeft() {
                if (swipeIndex == quotesTotalCount - 1)
                    swipeIndex = -1;
                swipeIndex++
                val content = quotes[swipeIndex].split("-").map { it.trim() }[0]
                val name = quotes[swipeIndex].split("-").map { it.trim() }[1]
                quoteContent.text = '"' + content + '"'
                philosopherName.text = "-" + name

                quoteContent.startAnimation(slideIn)
                philosopherName.startAnimation(slideIn)

                Log.e("ViewSwipe", "onSwipeLeft")

                if (isSwipeEffectEnable) {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    } else
                        mediaPlayer.start()
                }


            }

            override fun onSwipeRight() {
                if (swipeIndex == 0)
                    swipeIndex = quotesTotalCount;
                swipeIndex--
                val content = quotes[swipeIndex].split("-").map { it.trim() }[0]
                val name = quotes[swipeIndex].split("-").map { it.trim() }[1]
                quoteContent.text = '"' + content + '"'
                philosopherName.text = "-" + name
                Log.e("ViewSwipe", "Right")
                if (isSwipeEffectEnable) {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    } else
                        mediaPlayer.start()
                }


            }
        })

        homeLayout.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true // Consume the event
        }

        return root
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
    }
}
