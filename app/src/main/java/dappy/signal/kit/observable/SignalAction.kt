package dappy.signal.kit.observable

import dappy.signal.kit.model.Signal
import dappy.signal.kit.socket.SignalOption

interface SignalAction {

    fun connect(signalOption: SignalOption): SignalAction

    fun sendSignal(signal: Signal)

    fun disconnect()

    fun unsubscribe(observer: SignalActionObserver)

    fun observe(observer: SignalActionObserver): SignalAction
}