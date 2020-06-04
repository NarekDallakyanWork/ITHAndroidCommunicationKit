package ithd.common.callback

import ithd.call.sdp.SessionDescription

interface OfferCallBack {

    fun onSdpCreated(
        participant: String?,
        sessionDescription: SessionDescription?
    )
}