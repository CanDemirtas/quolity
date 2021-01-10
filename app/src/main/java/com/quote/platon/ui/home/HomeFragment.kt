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
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.quote.platon.R
import com.quote.platon.util.OnSwipeTouchListener
import com.quote.platon.util.Screenshot


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mediaPlayer: MediaPlayer
    private val main: View? = null
    private var isSwipeEffectEnable: Boolean = false;

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        //val textView: TextView = root.findViewById(R.id.text_home)
        //homeViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        //})
        val preference =
            context?.getSharedPreferences(
                resources.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )

        isSwipeEffectEnable = preference?.getBoolean("swipeSoundEffect", true)!!


        val quoteContent: TextView = root.findViewById(R.id.textView3)






        val philosopherName: TextView = root.findViewById(R.id.textView2)

        var swipeIndex = 0;
        var quotes = resources.getStringArray(R.array.quotes)
        var quotesTotalCount = quotes.count()

        val content = quotes[0].split("-").map { it.trim() }[0]
        val name = quotes[0].split("-").map { it.trim() }[1]
        quoteContent.text = '"' + content + '"'
        philosopherName.text = "-" + name

        var homeLayout = root.findViewById<ConstraintLayout>(R.id.homeLayout)
        mediaPlayer = MediaPlayer.create(
            context, R.raw.transition
        )
        mediaPlayer.setVolume(0.5F,0.5F)
        homeLayout.setOnTouchListener(object : OnSwipeTouchListener() {

            override fun onSwipeLeft() {
                if (swipeIndex == quotesTotalCount - 1)
                    swipeIndex = -1;
                swipeIndex++
                val content = quotes[swipeIndex].split("-").map { it.trim() }[0]
                val name = quotes[swipeIndex].split("-").map { it.trim() }[1]
                quoteContent.text = '"' + content + '"'
                philosopherName.text = "-" + name
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

        return root
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
    }
}
