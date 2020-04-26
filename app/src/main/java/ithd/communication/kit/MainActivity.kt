package ithd.communication.kit

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import ithd.call.interfaces.WebRtcEventListener
import ithd.call.manager.CallManager
import kotlinx.android.synthetic.main.activity_main.*
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

class MainActivity : AppCompatActivity() {
    var localSurfaceView: SurfaceViewRenderer? = null
    private var rootEglBase: EglBase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addPermission()
    }

    private fun enstablish() {
        localSurfaceView = surface_view
        rootEglBase = EglBase.create()
        // local surface view
        localSurfaceView!!.init(rootEglBase!!.eglBaseContext, null)
        localSurfaceView!!.setEnableHardwareScaler(true)
        localSurfaceView!!.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
        localSurfaceView!!.setZOrderMediaOverlay(true)
        localSurfaceView!!.setMirror(true)

        val callManager = CallManager(this, rootEglBase!!)
        callManager.addWebrtcEventListener(object : WebRtcEventListener {
            override fun onVideoTrackCreated(videoTrack: VideoTrack?) {
                videoTrack?.addSink(localSurfaceView)
            }

            override fun onScreenCaptureChanged(enable: Boolean) {

            }

            override fun onAudioChanged(enable: Boolean) {

            }

            override fun onVideoChanged(enable: Boolean) {

            }

            override fun onMicrophoneStateChanged(isSpeaker: Boolean) {

            }

            override fun onIceCandidate(sdp: String?, sdpMid: String?, sdpMLineIndex: Int) {

            }

            override fun onRemoteTrackAdded(remoteVideoTrack: VideoTrack?) {

            }

            override fun onPeerConnectionCreated(participant: String?) {

            }
        })
        callManager.establish()
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
                    enstablish()
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
}
