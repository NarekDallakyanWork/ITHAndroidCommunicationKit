package ithd.call.model

import org.webrtc.PeerConnection
import org.webrtc.RtpSender
import java.util.*

class CallModel {
    var participant: String? = null
    var peerConnection: PeerConnection? = null
    var rtpSenderVideo: RtpSender? = null
    var rtpSenderAudio: RtpSender? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val callModel = other as CallModel
        return participant == callModel.participant
    }

    override fun hashCode(): Int {
        return Objects.hash(participant)
    }

    override fun toString(): String {
        return "CallModel{" +
                "participant='" + participant + '\'' +
                ", peerConnection=" + peerConnection +
                '}'
    }
}