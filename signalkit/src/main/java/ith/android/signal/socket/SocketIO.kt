package ith.android.signal.socket

import ith.android.signal.helper.ObjectHelper
import ith.android.signal.manager.SignalSubscribersManager
import ith.android.signal.model.Signal
import ith.android.signal.model.SignalState
import ith.android.signal.observable.SignalAction
import ith.android.signal.observable.SignalActionObserver
import io.socket.client.IO
import io.socket.client.Socket

class SocketIO : SignalAction {

    private lateinit var socket: Socket
    private lateinit var signalOption: SignalOption
    private val subscriberManager =
        SignalSubscribersManager()
    private val signalState: SignalState by lazy { return@lazy SignalState() }

    /**
     * create socket instance
     */
    override fun connect(signalOption: SignalOption): SignalAction {
        ObjectHelper.requireNonNull(signalOption, "SignalOption can't be null")
        this.signalOption = signalOption
        val opts = IO.Options()
        opts.reconnection = signalOption.isReconnecting
        socket = IO.socket(signalOption.serverUrl, opts)
        listenSocket()
        socket.connect()
        return this
    }

    /**
     * send data to signaling server
     */
    override fun sendSignal(signal: Signal) {
        ObjectHelper.requireNonNull(signal, "Signal can't be null")
        socket.emit(signalOption.eventName, signal.get().toString())
    }

    /**
     * notify all active participant, when signal received
     */
    private fun listenSocket() {
        socket.on(signalOption.eventName) {
            subscriberManager.onSignal(Signal(it))
        }

        // listen server response
        // listen server response
        socket.on(
            Socket.EVENT_CONNECT
        ) {
            changeConnection(SignalState.Status.CONNECTED)
        }.on(
            Socket.EVENT_DISCONNECT
        ) {
            changeConnection(SignalState.Status.DISCONNECTED)
        }.on(
            Socket.EVENT_ERROR
        ) {
            changeConnection(SignalState.Status.ERROR, "Disconnect from Dappy system")
        }
    }

    private fun changeConnection(status: SignalState.Status, message: String = "") {
        signalState.status = status
        signalState.message = message
        subscriberManager.connectionChanged(signalState)
    }

    /**
     * disconnect signaling and unsubscribe all participants
     */
    override fun disconnect() {
        socket.disconnect()
        subscriberManager.removeAll()
    }

    /**
     * unsubscribe Signaling
     */
    override fun unsubscribe(observer: SignalActionObserver) {
        ObjectHelper.requireNonNull(observer, "SignalActionObserver can't be null")
        subscriberManager.removeSubscriber(observer)
    }

    /**
     * subscribe Signaling
     */
    override fun observe(observer: SignalActionObserver): SignalAction {
        ObjectHelper.requireNonNull(observer, "SignalActionObserver can't be null")
        subscriberManager.addSubscriber(observer)
        return this
    }
}