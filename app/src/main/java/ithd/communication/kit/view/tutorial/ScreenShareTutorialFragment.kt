package ithd.communication.kit.view.tutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ithd.communication.kit.R
import ithd.communication.kit.view.activity.ConnectRoomActivity
import kotlinx.android.synthetic.main.fragment_screen_share_tutorial.*

class ScreenShareTutorialFragment : Fragment(R.layout.fragment_screen_share_tutorial) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextLayout.setOnClickListener {

            val intent = Intent(activity, ConnectRoomActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            activity?.finish()
        }
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