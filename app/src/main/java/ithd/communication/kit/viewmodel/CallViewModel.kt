package ithd.communication.kit.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import ithd.call.interfaces.WebRtcEventListener
import ithd.call.manager.CallManager
import ithd.common.client.Connection
import ithd.common.client.ConnectionClient
import ithd.common.config.Params
import ithd.common.widget.EglBaseProvider
import ithd.common.widget.VideoView
import org.webrtc.VideoTrack

class CallViewModel(mApplication: Application) : AndroidViewModel(mApplication) {

    private val connectionClient: Connection = ConnectionClient()
    private val params = Params()

    fun enstablish(
        eglBaseProvider: EglBaseProvider,
        context: Context,
        localSurfaceView: VideoView
    ) {

        params.put(Params.ParamName.CONTEXT, context)
        params.put(Params.ParamName.EGL_BASE, eglBaseProvider.localEglBase)

        connectionClient.configure(params)
        connectionClient.addWebrtcEventListener(object : WebRtcEventListener {
            override fun onVideoTrackCreated(videoTrack: VideoTrack?) {
                videoTrack?.addSink(localSurfaceView)
            }

            override fun onScreenCaptureChanged(enable: Boolean) {
                println()
            }

            override fun onAudioChanged(enable: Boolean) {
                println()
            }

            override fun onVideoChanged(enable: Boolean) {
                println()
            }

            override fun onMicrophoneStateChanged(isSpeaker: Boolean) {
                println()
            }

            override fun onIceCandidate(sdp: String?, sdpMid: String?, sdpMLineIndex: Int) {
                println()
            }

            override fun onRemoteTrackAdded(remoteVideoTrack: VideoTrack?) {
                println()
            }

            override fun onPeerConnectionCreated(participant: String?) {
                println()
            }
        })
        connectionClient.enstablish()
    }
}