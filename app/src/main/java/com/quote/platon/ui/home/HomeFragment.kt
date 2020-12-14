package com.quote.platon.ui.home

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.quote.platon.R
import com.quote.platon.util.OnSwipeTouchListener

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

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


        val quoteContent: TextView = root.findViewById(R.id.textView3)

        quoteContent.text = resources.getStringArray(R.array.quotes).first()
        val philosopherName: TextView = root.findViewById(R.id.textView2)

        var swipeIndex = 0;
        var quotes = resources.getStringArray(R.array.quotes)
        var quotesTotalCount = quotes.count()
        val webView: WebView = root.findViewById(R.id.webView)

        webView.loadUrl("file:///android_asset/index.html");
        webView.setOnTouchListener(object : OnSwipeTouchListener() {
            override fun onSwipeLeft() {
                if (swipeIndex == quotesTotalCount - 1)
                    swipeIndex = -1;
                swipeIndex++
                val content = quotes[swipeIndex].split("-").map { it.trim() }[0]
                val name = quotes[swipeIndex].split("-").map { it.trim() }[1]
                quoteContent.text = '"' + content + '"'
                philosopherName.text = "-" + name
                Log.e("ViewSwipe", "onSwipeLeft")

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

            }
        })

        return root
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
    }
}
