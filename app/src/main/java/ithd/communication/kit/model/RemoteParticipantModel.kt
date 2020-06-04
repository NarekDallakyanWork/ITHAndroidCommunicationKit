package ithd.communication.kit.model

import ithd.call.model.Participant
import org.webrtc.VideoTrack

class RemoteParticipantModel(
    var remoteVideoTrack: VideoTrack,
    var participants: ArrayList<Participant>
)