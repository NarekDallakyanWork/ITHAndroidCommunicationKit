package ithd.common.client

import ithd.call.interfaces.WebRtcEventListener
import ithd.common.config.Params

interface Connection {

    fun configure(params: Params): Connection

    fun addWebrtcEventListener(webRtcEventListener: WebRtcEventListener): Connection

    fun enstablish()

}