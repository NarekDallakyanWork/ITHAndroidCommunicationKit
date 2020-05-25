package ith.signal.manager

import ith.signal.model.Signal
import ith.signal.model.SignalState
import ith.signal.observable.SignalActionObserver

class SignalSubscribersManager {

    private var subscribers = ArrayList<SignalActionObserver>()

    @Synchronized
    fun addSubscriber(subscriber: SignalActionObserver) {
        subscribers.add(subscriber)
    }

    @Synchronized
    fun removeSubscriber(subscriber: SignalActionObserver) {
        subscribers.apply {
            remove(subscriber)
        }
    }

    @Synchronized
    fun removeAll() {
        subscribers.clear()
    }

    @Synchronized
    fun onSignal(signal: Signal) {
        subscribers.forEach {
            it.onSignal(signal)
        }
    }

    fun connectionChanged(signalState: SignalState) {
        when (signalState.status) {
            SignalState.Status.CONNECTED -> {
                subscribers.forEach {
                    it.onConnected()
                }
            }

            SignalState.Status.DISCONNECTED -> {
                subscribers.forEach {
                    it.onDisconnected()
                }
            }

            SignalState.Status.ERROR -> {
                subscribers.forEach {
                    it.onError(signalState.message)
                }
            }
        }
    }
}