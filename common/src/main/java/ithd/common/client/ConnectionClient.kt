package ithd.common.client

import android.content.Context
import com.google.gson.Gson
import ith.signal.model.Signal
import ith.signal.observable.SignalAction
import ith.signal.observable.SignalActionObserver
import ith.signal.socket.SignalOption
import ith.signal.socket.SocketIO
import ithd.call.interfaces.WebRtcEventListener
import ithd.call.manager.CallManager
import ithd.call.model.Participant
import ithd.call.sdp.SessionDescription
import ithd.common.config.Params
import ithd.common.model.Message
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.EglBase
import java.util.*

class ConnectionClient : Connection {

    private val myId: String = UUID.randomUUID().toString()

    /**
     * WEBRTC PARAMETERS
     */
    private lateinit var params: Params
    private var webRtcEventListener: WebRtcEventListener? = null
    private var callManager: CallManager? = null

    /**
     * SIGNALING PARAMETERS
     */
    private var signaling: SignalAction = SocketIO()
    private lateinit var signalOption: SignalOption

    override fun configure(params: Params): ConnectionClient {

        this.params = params
        return this
    }

    override fun addWebrtcEventListener(webRtcEventListener: WebRtcEventListener): Connection {

        this.webRtcEventListener = webRtcEventListener
        return this
    }

    override fun enstablish() {

        initializeCall()
        callManager?.establish()
        connectSignaling()
        callManager?.addOnIceCandidateListener { sdp, sdpMid, sdpMLineIndex ->
            onIceCreated(sdp, sdpMid, sdpMLineIndex)
        }
    }

    private fun onIceCreated(sdp: String?, sdpMid: String?, sdpMLineIndex: Int) {

        val participants = callManager?.participants()

        if (participants != null) {
            for (participant in participants) {
                val message = JSONObject()
                try {
                    message.put("s", "ICE")
                    message.put("fr", myId)
                    message.put("to", participant.participant)
                    message.put("n", signalOption.roomName)
                    // create Ice obj
                    val iceObj = JSONObject()
                    iceObj.put("candidate", sdp)
                    iceObj.put("sdpMid", sdpMid)
                    iceObj.put("sdpMLineIndex", sdpMLineIndex)
                    message.put("ice", iceObj)
                    signaling.sendSignal(Signal(message.toString()))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun connectSignaling() {
        signaling.observe(signalObserver)
        signaling.connect(signalOption)
    }

    private fun initializeCall() {

        val context: Context = params.get()[Params.ParamName.CONTEXT] as Context
        val eglBase: EglBase = params.get()[Params.ParamName.EGL_BASE] as EglBase
        val url: String = params.get()[Params.ParamName.URL] as String
        val reconnection: Boolean = params.get()[Params.ParamName.RECONNECTION] as Boolean
        val eventName: String = params.get()[Params.ParamName.EVENT_NAME] as String
        val roomName: String = params.get()[Params.ParamName.ROOM_NAME] as String
        signalOption = SignalOption(url, eventName, roomName, reconnection)

        callManager = CallManager(context, eglBase)
        callManager?.addWebrtcEventListener(webRtcEventListener!!)
    }

    private var signalObserver = object : SignalActionObserver {
        override fun onConnected() {
            join()
        }

        override fun onDisconnected() {
            println()
        }

        override fun onError(errorMessage: String) {
            println()
        }

        override fun onSignal(signal: Signal) {

            // convert signal to message model
            val message = Gson().fromJson(signal.signal.toString(), Message::class.java)
            handleReceiveSignal(message)
        }
    }

    private fun handleReceiveSignal(message: Message) {

        if (myId == message.fr) return

        when (message.s) {

            "JOIN" -> {
                // send offer
                sendOffer(message)
            }
            "OFFER" -> {
                sendAnswer(message)
            }
            "ANSWER" -> {
                addRemoteSdp(message)
            }
            "ICE" -> {
                addIceCandidate(message)
            }
            "UPDATE" -> {

            }
            "CHAT" -> {

            }
            "LEAVE" -> {

            }
        }
    }

    private fun addIceCandidate(message: Message) {

        val ice = message.ice
        val remoteParticipant = message.fr
        val candidateRes = ice?.candidate
        val sdpMidRes = ice?.sdpMid
        val sdpMidLineIndex = ice?.sdpMLineIndex
        callManager?.addIceCandidate(remoteParticipant, candidateRes, sdpMidRes, sdpMidLineIndex)
    }

    private fun addRemoteSdp(message: Message) {

        val remoteParticipant = message.fr
        val sessionDescription = message.sdp
        val sdpType = "answer"
        callManager?.addRemoteSdp(remoteParticipant, sdpType, sessionDescription)
    }

    private fun sendAnswer(message: Message) {

        val remoteParticipantId = message.fr
        val sessionDescription = message.sdp
        val sdpType = "offer"
        callManager?.addRemoteSdp(remoteParticipantId, sdpType, sessionDescription)

        callManager?.getAnswerSdp(
            remoteParticipantId!!,
            { s: String?, sessionDescription: SessionDescription? ->

                val message = JSONObject()
                try {
                    message.put("s", "ANSWER")
                    message.put("fr", myId)
                    message.put("to", remoteParticipantId)
                    message.put("n", signalOption.roomName)
                    message.put("sdp", sessionDescription?.sessionDesctiption)
                    message.put("ve", true)
                    signaling.sendSignal(Signal(message.toString()))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            })
    }

    private fun sendOffer(message: Message) {

        val remoteParticipantId = message.fr
        callManager
            ?.getOfferSdp(remoteParticipantId,
                { participant: String?, sessionDescription: SessionDescription? ->
                    val message = JSONObject()
                    try {
                        message.put("s", "OFFER")
                        message.put("fr", myId)
                        message.put("to", participant)
                        message.put("n", signalOption.roomName)
                        message.put("sdp", sessionDescription?.sessionDesctiption)
                        message.put("ve", true)
                        signaling.sendSignal(Signal(message.toString()))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                })
        webRtcEventListener?.onParticipantJoined(
            callManager?.participants()!!,
            Participant(remoteParticipantId)
        )
    }

    private fun join() {

        val join = JSONObject()
        try {
            join.put("s", "JOIN")
            join.put("fr", myId)
            join.put("n", signalOption.roomName)
            signaling.sendSignal(Signal(join.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}