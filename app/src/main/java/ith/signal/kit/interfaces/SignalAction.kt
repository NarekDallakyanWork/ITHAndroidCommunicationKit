package ith.signal.kit.interfaces

import ith.signal.kit.model.SignalOption

interface SignalAction {

    fun connect(signalOption: SignalOption)

    fun disconnect()

    fun observe()


}