package ithd.communication.kit.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ithd.call.interfaces.WebRtcEventListener
import ithd.call.model.Participant
import ithd.common.client.Connection
import ithd.common.client.ConnectionClient
import ithd.common.config.Params
import ithd.communication.kit.constants.ServerConstants
import org.webrtc.EglBase
import org.webrtc.VideoTrack

class CallViewModel(mApplication: Application) : AndroidViewModel(mApplication) {

    private val connectionClient: Connection = ConnectionClient()
    private val params = Params()

    // Live Data
    private var localVideoLiveData =  MutableLiveData<VideoTrack?>()
    private var remoteParticipantAddedLiveData =  MutableLiveData<VideoTrack?>()


    fun enstablish(
        eglBaseProvider: EglBase,
        roomName: String,
        context: Context
    ) {

        params.put(Params.ParamName.CONTEXT, context)
        params.put(Params.ParamName.EGL_BASE, eglBaseProvider)
        params.put(Params.ParamName.URL, ServerConstants.SERVER_URL)
        params.put(Params.ParamName.RECONNECTION, true)
        params.put(Params.ParamName.EVENT_NAME, ServerConstants.SIGNAL_EVENT)
        params.put(Params.ParamName.ROOM_NAME, roomName)

        connectionClient.configure(params)
        connectionClient.addWebrtcEventListener(object : WebRtcEventListener {
            override fun onVideoTrackCreated(videoTrack: VideoTrack?) {

                // notify local video track created
                localVideoLiveData.value = videoTrack
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

            override fun onRemoteTrackAdded(
                remoteVideoTrack: VideoTrack?,
                participants: ArrayList<Participant>
            ) {

                if (remoteVideoTrack == null) {
                    remoteParticipantAddedLiveData.postValue(null)
                } else {
                    remoteParticipantAddedLiveData.postValue(remoteVideoTrack)
                }
            }

            override fun onPeerConnectionCreated(participant: String?) {
                println()
            }

            override fun onParticipantJoined(
                allParticipants: List<Participant>,
                newParticipant: Participant
            ) {

            }
        })
        connectionClient.enstablish()
    }

    fun getLocalVideoLiveData() = localVideoLiveData
    fun getRemoteParticipantAddedLiveData() = remoteParticipantAddedLiveData
}