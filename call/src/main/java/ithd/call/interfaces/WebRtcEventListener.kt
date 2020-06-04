package ithd.call.interfaces

import ithd.call.model.Participant
import org.webrtc.VideoTrack

interface WebRtcEventListener {
    fun onVideoTrackCreated(videoTrack: VideoTrack?)

    fun onScreenCaptureChanged(enable: Boolean)

    fun onAudioChanged(enable: Boolean)

    fun onVideoChanged(enable: Boolean)

    fun onMicrophoneStateChanged(isSpeaker: Boolean)

    fun onRemoteTrackAdded(
        remoteVideoTrack: VideoTrack?,
        participants: ArrayList<Participant>
    )

    fun onPeerConnectionCreated(participant: String?)

    /**
     * Called when a participant has connected to a room.
     */
    fun onParticipantJoined(
        allParticipants: List<Participant>, newParticipant: Participant
    )
}