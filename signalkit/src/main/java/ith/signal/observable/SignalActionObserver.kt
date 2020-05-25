package ith.signal.observable

import ith.signal.model.Signal

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
     * Called when the signaling have error.
     */
    fun onError(errorMessage: String)

    /**
     * Called when the have new signal.
     */
    fun onSignal(signal: Signal)
}