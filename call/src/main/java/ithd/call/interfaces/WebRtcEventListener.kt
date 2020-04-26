package ithd.call.interfaces

import org.webrtc.VideoTrack

interface WebRtcEventListener {
    fun onVideoTrackCreated(videoTrack: VideoTrack?)

    fun onScreenCaptureChanged(enable: Boolean)

    fun onAudioChanged(enable: Boolean)

    fun onVideoChanged(enable: Boolean)

    fun onMicrophoneStateChanged(isSpeaker: Boolean)

    fun onIceCandidate(
        sdp: String?,
        sdpMid: String?,
        sdpMLineIndex: Int
    )

    fun onRemoteTrackAdded(remoteVideoTrack: VideoTrack?)

    fun onPeerConnectionCreated(participant: String?)
}