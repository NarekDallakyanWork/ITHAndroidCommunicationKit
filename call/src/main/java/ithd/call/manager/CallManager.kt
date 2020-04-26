package ithd.call.manager

import android.content.Context
import android.media.AudioManager
import ithd.call.helper.WebrtcHelper
import ithd.call.helper.WebrtcHelperInterface
import ithd.call.interfaces.WebRtcEventListener
import org.webrtc.*

class CallManager(
    private var context: Context,
    private var rootEglBase: EglBase
) {
    private val webrtcHelper: WebrtcHelperInterface = WebrtcHelper()
    private var webRtcEventListener: WebRtcEventListener? = null

    /**
     * Audio
     */
    private var audioManager: AudioManager? = null
    private var localAudioTrack: AudioTrack? = null
    private var audioSource: AudioSource? = null
    private var audioConstraints: MediaConstraints? = null
    private var videoSender: RtpSender? = null
    private var audioSender: RtpSender? = null

    /**
     * Video
     */
    private var videoTrackFromCamera: VideoTrack? = null

    private var factory: PeerConnectionFactory? = null


    fun addWebrtcEventListener(webRtcEventListener: WebRtcEventListener) {
        this.webRtcEventListener = webRtcEventListener
    }

    fun establish() {
        webrtcHelper.addContext(context)
        webrtcHelper.addEglContext(rootEglBase)
        // get peerConnection factory
        factory = webrtcHelper.getConnectionFactory()
        // get local audio track
        localAudioTrack = webrtcHelper.getAudioTrack()
        // get video trach from camera
        videoTrackFromCamera = webrtcHelper.getVideoTrack()
        webRtcEventListener?.onVideoTrackCreated(videoTrackFromCamera)
    }
}