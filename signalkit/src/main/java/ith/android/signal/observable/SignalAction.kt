package ith.android.signal.observable

import ith.android.signal.model.Signal
import ith.android.signal.socket.SignalOption

interface SignalAction {

    fun connect(signalOption: SignalOption): SignalAction

    fun sendSignal(signal: Signal)

    fun disconnect()

    fun unsubscribe(observer: SignalActionObserver)

    fun observe(observer: SignalActionObserver): SignalAction
}