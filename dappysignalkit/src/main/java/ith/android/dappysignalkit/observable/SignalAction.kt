package ith.android.dappysignalkit.observable

import ith.android.dappysignalkit.model.Signal
import ith.android.dappysignalkit.socket.SignalOption

interface SignalAction {

    fun connect(signalOption: SignalOption): SignalAction

    fun sendSignal(signal: Signal)

    fun disconnect()

    fun unsubscribe(observer: SignalActionObserver)

    fun observe(observer: SignalActionObserver): SignalAction
}