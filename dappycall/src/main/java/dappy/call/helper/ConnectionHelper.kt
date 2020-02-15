package dappy.call.helper

import android.content.Context
import dappy.call.constants.WEBRTC.AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT
import dappy.call.constants.WEBRTC.AUDIO_ECHO_CANCELLATION_CONSTRAINT
import dappy.call.constants.WEBRTC.AUDIO_HIGH_PASS_FILTER_CONSTRAINT
import dappy.call.constants.WEBRTC.AUDIO_NOISE_SUPPRESSION_CONSTRAINT
import dappy.call.constants.WEBRTC.AUDIO_TRACK_ID
import dappy.call.constants.WEBRTC.FPS
import dappy.call.constants.WEBRTC.VIDEO_RESOLUTION_HEIGHT
import dappy.call.constants.WEBRTC.VIDEO_RESOLUTION_WIDTH
import dappy.call.constants.WEBRTC.VIDEO_TRACK_ID
import dappy.call.constants.WEBRTC.VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL
import org.webrtc.*

class ConnectionHelper : ConnectionHelperInterface {

    private lateinit var eglBase: EglBase
    private lateinit var context: Context
    private lateinit var factory: PeerConnectionFactory

    // audio
    private var localAudioTrack: AudioTrack? = null
    private var audioConstraints: MediaConstraints? = null
    private var audioSource: AudioSource? = null

    // video
    private var videoTrack: VideoTrack? = null
    private val videoCapturer: VideoCapturer? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    override fun addEglContext(eglBase: EglBase) {
        ObjectHelper.requireNonNull(context, "EglBase can't be null")
        this.eglBase = eglBase
    }

    override fun addContext(context: Context) {
        ObjectHelper.requireNonNull(context, "Context can't be null")
        this.context = context
    }

    override fun getConnectionFactory(): PeerConnectionFactory? {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setFieldTrials(VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )

        val encoderFactory: VideoEncoderFactory
        val decoderFactory: VideoDecoderFactory

        encoderFactory = DefaultVideoEncoderFactory(
            eglBase.eglBaseContext, true /* enableIntelVp8Encoder */, true
        )
        decoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

        val options = PeerConnectionFactory.Options()
        factory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
        return factory
    }

    override fun getAudioTrack(): AudioTrack {
        createAudioConstraints(false)
        audioSource = factory.createAudioSource(audioConstraints)
        localAudioTrack = factory.createAudioTrack(AUDIO_TRACK_ID, audioSource)
        return localAudioTrack!!
    }

    override fun getVideoTrack(): VideoTrack {
        val videoSource =
            factory.createVideoSource(videoCapturer!!.isScreencast)
        surfaceTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name,
            eglBase.eglBaseContext
        )
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS)
        videoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource)
        return videoTrack!!
    }

    private fun createAudioConstraints(speaker: Boolean) {
        audioConstraints = MediaConstraints()
        // added for audio performance measurements
        if (speaker) {
            audioConstraints!!.mandatory.add(
                MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false")
            )
            audioConstraints!!.mandatory.add(
                MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false")
            )
            audioConstraints!!.mandatory.add(
                MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false")
            )
            audioConstraints!!.mandatory.add(
                MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false")
            )
        }
    }
}