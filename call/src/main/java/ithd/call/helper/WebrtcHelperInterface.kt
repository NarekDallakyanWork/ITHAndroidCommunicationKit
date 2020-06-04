package ithd.call.helper

import android.content.Context
import ithd.call.interfaces.WebRtcEventListener
import ithd.call.model.Participant
import ithd.call.sdp.SdpCallBack
import ithd.call.sdp.SessionDescription
import org.webrtc.*

interface WebrtcHelperInterface {

    fun addEglContext(eglBase: EglBase)

    fun addContext(context: Context)

    fun getConnectionFactory(): PeerConnectionFactory?

    fun getAudioTrack(): AudioTrack

    fun getVideoTrack(): VideoTrack

    fun getPeerConnection(participant: String, videoTrackFromCamera: VideoTrack): PeerConnection

    fun createPeerConnection(peerConnectionFactory: PeerConnectionFactory): PeerConnection?

    fun createVideoCapture(): VideoCapturer?

    fun getOfferSdp(participant: String, sdpCallBack: SdpCallBack, videoTrackFromCamera: VideoTrack)

    fun getAnswerSdp(
        participant: String,
        sdpCallBack: SdpCallBack,
        videoTrackFromCamera: VideoTrack
    )

    fun addIceCandidate(
        participant: String,
        sessionDescription: SessionDescription,
        videoTrackFromCamera: VideoTrack
    )

    fun addRemoteSdp(
        participant: String,
        sessionDescription: org.webrtc.SessionDescription,
        videoTrackFromCamera: VideoTrack
    )

    fun addOnIceCandidateListener(onIceCandidate: ((String?, String?, Int) -> Unit?)?)

    /**
     *  Webrtc event listener
     */
    fun addWebRtcListener(webRtcEventListener: WebRtcEventListener)

    /**
     * get all participants array
     */
    fun participants(): ArrayList<Participant>
}