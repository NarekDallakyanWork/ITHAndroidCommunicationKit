package dappy.signal.kit.model

class SignalState {

    var message: String = ""
    var status: Status? = null

    enum class Status {
        CONNECTED, DISCONNECTED, ERROR
    }
}