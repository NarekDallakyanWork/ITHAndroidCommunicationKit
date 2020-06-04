package ithd.common.config

class Params {

    private val paramsMap = HashMap<ParamName, Any>()

    fun put(name: ParamName, value: Any): Params {
        paramsMap[name] = value
        return this
    }

    fun get(): HashMap<ParamName, Any> {
        return paramsMap
    }

    enum class ParamName {
        EGL_BASE,
        CONTEXT,
        URL,
        EVENT_NAME,
        ROOM_NAME,
        RECONNECTION
    }
}