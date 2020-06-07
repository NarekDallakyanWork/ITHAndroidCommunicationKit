package ithd.communication.kit.view.tutorial

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ithd.communication.kit.R
import kotlinx.android.synthetic.main.fragment_welcome.*


class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {

        val welcomeBodyString = welcomeBody.text.toString()
        val str = SpannableStringBuilder(welcomeBodyString)
        str.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            "Unlimited".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        welcomeBody.text = str
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}