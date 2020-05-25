package ith.signal.observable

import ith.signal.model.Signal
import ith.signal.socket.SignalOption

interface SignalAction {

    /**
     *  Connecte to Signaling server that sdp transfer
     */
    fun connect(signalOption: SignalOption): SignalAction

    /**
     *  Send any message from participant to participant
     */
    fun sendSignal(signal: Signal)

    /**
     *  Disconnect from signaling server
     */
    fun disconnect()

    /**
     *  unSubscribe
     */
    fun unsubscribe(observer: SignalActionObserver)

    /**
     *  subscribe
     */
    fun observe(observer: SignalActionObserver): SignalAction
}