package ithd.communication.kit.view.activity

import android.Manifest
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import ithd.communication.kit.R
import ithd.communication.kit.helper.ParticipantsViewHelper
import ithd.communication.kit.viewmodel.CallViewModel
import kotlinx.android.synthetic.main.activity_video.*
import org.webrtc.EglBase

class VideoCallActivity : AppCompatActivity() {

    private var callViewModel: CallViewModel? = null

    private var rootEglBase = EglBase.create()
    private var participantsViewHelper: ParticipantsViewHelper? = null

    // Variables
    private var roomName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        participantsViewHelper =
            ParticipantsViewHelper(rootView)
        getIntentData()
        initViewModel()
        observeLiveData()
        addPermission()
    }

    private fun getIntentData() {

        intent?.let {

            roomName = it.getStringExtra("roomName")
        }

    }

    private fun initViewModel() {

        callViewModel = ViewModelProvider(this).get(CallViewModel::class.java)
    }

    private fun enstablishCall() {

        participantsViewHelper?.eglBase(rootEglBase)
        callViewModel?.enstablish(rootEglBase!!, roomName!!, this)
    }

    private fun addPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    enstablishCall()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun observeLiveData() {

        // local Video track created live data
        callViewModel?.getLocalVideoLiveData()?.observe(this, Observer {

            if (it == null) return@Observer

            participantsViewHelper?.submitParticipant(it, "You")
        })

        // remote Participant Added live data
        callViewModel?.getRemoteParticipantAddedLiveData()?.observe(this, Observer {

            if (it == null) return@Observer

            participantsViewHelper?.submitParticipant(
                it,
                "Video ${participantsViewHelper?.getParticipantCount()}"
            )
        })
    }
}
