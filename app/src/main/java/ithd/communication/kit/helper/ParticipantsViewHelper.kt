package ithd.communication.kit.helper

import android.view.LayoutInflater
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import ithd.communication.kit.R
import kotlinx.android.synthetic.main.participant_item.view.*
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.VideoTrack

class ParticipantsViewHelper(private var rootView: LinearLayout) {

    init {
        (rootView.parent as HorizontalScrollView).isHorizontalScrollBarEnabled = false
    }
    private var rootEglBase: EglBase? = null
    private var participants = ArrayList<VideoTrack>()

    fun submitParticipant(videoTrack: VideoTrack?, participantName: String) {
        videoTrack?.let {
            updateView(it, participantName)
        }
    }

    fun getParticipantCount() : Int {
        return participants.size
    }

    private fun updateView(videoTrack: VideoTrack, participantName: String) {

        val videoViewLayout = createVideoView(rootEglBase, participantName)
        videoTrack.addSink(videoViewLayout?.localVideo)
        rootView.addView(videoViewLayout)
        participants.add(videoTrack)
    }

    fun eglBase(rootEglBase: EglBase?) {
        this.rootEglBase = rootEglBase
    }

    private fun createVideoView(
        rootEglBase: EglBase?,
        participantName: String
    ): RelativeLayout? {

        val videoLayout = LayoutInflater.from(rootView.context).inflate(R.layout.participant_item, null, false)
        val videoView = videoLayout.localVideo
        videoLayout.nameText.text = participantName
        videoView.init(rootEglBase?.eglBaseContext, null)
        videoView.setEnableHardwareScaler(true)
        videoView.setZOrderMediaOverlay(true)
        videoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
        videoView.setMirror(true)
        return videoLayout as RelativeLayout
    }
}