package ith.signal.kit.observable

import ith.signal.kit.model.Signal

interface SignalActionObserver {
    /**
     * Called when the signaling is connected.
     */
    fun onConnected()

    /**
     * Called when the signaling is disconnected.
     */
    fun onDisconnected()

    /**
     * Called when the have new signal.
     */
    fun onSignal(signal: Signal)
}