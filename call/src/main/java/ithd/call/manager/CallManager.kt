package ithd.call.manager

import android.content.Context
import android.media.AudioManager
import ithd.call.helper.WebrtcHelper
import ithd.call.helper.WebrtcHelperInterface
import ithd.call.interfaces.WebRtcEventListener
import ithd.call.model.Participant
import ithd.call.sdp.SdpCallBack
import ithd.call.sdp.SessionDescription
import org.webrtc.*
import java.util.*
import kotlin.collections.ArrayList

class CallManager(
    private var context: Context,
    private var rootEglBase: EglBase
) {
    private val webrtcHelper: WebrtcHelperInterface = WebrtcHelper()
    private var webRtcEventListener: WebRtcEventListener? = null
    private var onIceCandidate: ((String?, String?, Int) -> Unit?)? = null

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

    /**
     *  Peer connection
     */
    private var factory: PeerConnectionFactory? = null

    /**
     *  All Participants
     */
    private val participants = ArrayList<Participant>()


    fun addWebrtcEventListener(webRtcEventListener: WebRtcEventListener) {

        this.webRtcEventListener = webRtcEventListener
        webrtcHelper.addWebRtcListener(webRtcEventListener)
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

    fun getOfferSdp(
        participant: String?,
        callbck: (participant: String?, sessionDescription: SessionDescription?) -> Unit
    ) {

        webrtcHelper.getOfferSdp(participant!!, object : SdpCallBack {
            override fun onSdpCreated(
                participant: String?,
                sessionDescription: SessionDescription?
            ) {
                callbck(participant, sessionDescription)
            }
        }, videoTrackFromCamera!!)
    }

    fun getAnswerSdp(
        participant: String,
        callbck: (participant: String?, sessionDescription: SessionDescription?) -> Unit
    ) {

        webrtcHelper.getAnswerSdp(participant, object : SdpCallBack {
            override fun onSdpCreated(
                participant: String?,
                sessionDescription: SessionDescription?
            ) {
                callbck(participant, sessionDescription)
            }
        }, videoTrackFromCamera!!)
    }

    fun addRemoteSdp(participant: String?, sdpType: String, sessionDescription: String?) {

        val sd = if (sdpType == "offer") SessionDescription(
            org.webrtc.SessionDescription.Type.OFFER,
            sessionDescription
        ) else SessionDescription(
            org.webrtc.SessionDescription.Type.ANSWER,
            sessionDescription
        )
        webrtcHelper.addRemoteSdp(participant!!, sd, videoTrackFromCamera!!)
    }

    fun addIceCandidate(
        participant: String?,
        candidateRes: String?,
        sdpMidRes: String?,
        sdpMidLineIndex: Int?
    ) {

        val sessionDescription = SessionDescription()

        if (candidateRes != null) {
            sessionDescription.candidateRes = candidateRes
        }
        if (sdpMidLineIndex != null) {
            sessionDescription.sdpMidLineIndex = sdpMidLineIndex
        }
        if (sdpMidRes != null) {
            sessionDescription.sdpMidRes = sdpMidRes
        }
        webrtcHelper.addIceCandidate(participant!!, sessionDescription, videoTrackFromCamera!!)
    }

    fun addOnIceCandidateListener(onIceCandidate: ((String?, String?, Int) -> Unit?)?) {
        this.onIceCandidate = onIceCandidate
    }

    fun participants(): List<Participant>? {
        return webrtcHelper.participants()
    }
}