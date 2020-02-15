package dappy.call.helper

import android.content.Context
import org.webrtc.AudioTrack
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.VideoTrack

interface ConnectionHelperInterface {

    fun addEglContext(eglBase: EglBase)

    fun addContext(context: Context)

    fun getConnectionFactory(): PeerConnectionFactory?

    fun getAudioTrack(): AudioTrack

    fun getVideoTrack(): VideoTrack
}