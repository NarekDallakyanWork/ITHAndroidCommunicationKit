package ithd.common.client

import android.content.Context
import ith.signal.model.Signal
import ith.signal.observable.SignalAction
import ith.signal.observable.SignalActionObserver
import ith.signal.socket.SocketIO
import ithd.call.interfaces.WebRtcEventListener
import ithd.call.manager.CallManager
import ithd.common.config.Params
import org.webrtc.EglBase

class ConnectionClient : Connection {

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
    }

    private fun connectSignaling() {
        signaling.observe(object :SignalActionObserver{
            override fun onConnected() {

            }

            override fun onDisconnected() {

            }

            override fun onError(errorMessage: String) {

            }

            override fun onSignal(signal: Signal) {

            }

        })
    }

    private fun initializeCall() {

        val context: Context = params.get()[Params.ParamName.CONTEXT] as Context
        val eglBase: EglBase = params.get()[Params.ParamName.EGL_BASE] as EglBase
        callManager = CallManager(context, eglBase)
        callManager?.addWebrtcEventListener(webRtcEventListener!!)
    }
}