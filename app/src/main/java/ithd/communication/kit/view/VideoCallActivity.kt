package ithd.communication.kit.view

import android.Manifest
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import ithd.common.widget.EglBaseProvider
import ithd.communication.kit.R
import ithd.communication.kit.viewmodel.CallViewModel
import kotlinx.android.synthetic.main.activity_video.*
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer

class VideoCallActivity : AppCompatActivity() {

    private var localVideoView: SurfaceViewRenderer? = null
    private var remoteVideoView: SurfaceViewRenderer? = null
    private var callViewModel: CallViewModel? = null
    private var eglBaseProvider: EglBaseProvider? = null

    private var rootEglBase: EglBase? = null

    // Variables
    private var roomName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        getIntentData()
        rootEglBase = EglBase.create()
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

    private fun initVideoView() {

        // inititialize local video view
        localVideoView = localVideo
        localVideoView?.init(rootEglBase?.eglBaseContext, null)
        localVideoView?.setEnableHardwareScaler(true)
        localVideoView?.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
        localVideoView?.setZOrderMediaOverlay(true)
        localVideoView?.setMirror(true)

        // initialize remote video view
        remoteVideoView = remoteVideo
        remoteVideoView?.init(rootEglBase?.eglBaseContext, null)
        remoteVideoView?.setEnableHardwareScaler(true)
        remoteVideoView?.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
        remoteVideoView?.setMirror(true)

        callViewModel?.enstablish(rootEglBase!!, roomName!!,this)
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
                    initVideoView()
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

            it?.addSink(localVideoView) ?: run {
                Toast.makeText(this@VideoCallActivity, "video track error", Toast.LENGTH_SHORT).show()
            }
        })

        // remote Participant Added live data
        callViewModel?.getRemoteParticipantAddedLiveData()?.observe(this, Observer {

            it?.addSink(remoteVideoView) ?: run {
                Toast.makeText(this@VideoCallActivity, "remote participant error", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}
