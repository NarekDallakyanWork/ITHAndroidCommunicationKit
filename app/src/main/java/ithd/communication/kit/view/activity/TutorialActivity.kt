package ithd.communication.kit.view.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ithd.communication.kit.R
import ithd.communication.kit.view.tutorial.*
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        initTutorialAdapter()
    }

    private fun initTutorialAdapter() {

        val tutorialAdapter = TutorialAdapter(supportFragmentManager, fragments.size, fragments)
        tutorialViewPager.setPageTransformer(true, ZoomOutPageTransformer())
        tutorialViewPager.adapter = tutorialAdapter
        tutorialIndicatorLayout.setupWithViewPager(tutorialViewPager, false)
        tutorialIndicatorLayout.tabRippleColor = null
    }

    private val fragments: ArrayList<Fragment> by lazy {
        val fragments = ArrayList<Fragment>()
        fragments.add(WelcomeFragment())
        fragments.add(VideoCallTutorialFragment())
        fragments.add(MessagingTutorialFragment())
        fragments
    }
}