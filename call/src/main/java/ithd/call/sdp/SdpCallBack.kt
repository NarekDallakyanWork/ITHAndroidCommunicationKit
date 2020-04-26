package ithd.call.sdp

interface SdpCallBack {
    fun onSdpCreated(
        participant: String?,
        sessionDescription: SessionDescription?
    )
}