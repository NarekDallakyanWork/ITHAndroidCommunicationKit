package ithd.call.helper

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.util.Log
import ithd.call.constants.WEBRTC.AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT
import ithd.call.constants.WEBRTC.AUDIO_ECHO_CANCELLATION_CONSTRAINT
import ithd.call.constants.WEBRTC.AUDIO_HIGH_PASS_FILTER_CONSTRAINT
import ithd.call.constants.WEBRTC.AUDIO_NOISE_SUPPRESSION_CONSTRAINT
import ithd.call.constants.WEBRTC.AUDIO_TRACK_ID
import ithd.call.constants.WEBRTC.FPS
import ithd.call.constants.WEBRTC.VIDEO_RESOLUTION_HEIGHT
import ithd.call.constants.WEBRTC.VIDEO_RESOLUTION_WIDTH
import ithd.call.constants.WEBRTC.VIDEO_TRACK_ID
import ithd.call.constants.WEBRTC.VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL
import ithd.call.interfaces.WebRtcEventListener
import ithd.call.model.CallModel
import ithd.call.model.Participant
import ithd.call.sdp.MySdpObserver
import ithd.call.sdp.SdpCallBack
import ithd.call.sdp.SessionDescription
import org.webrtc.*
import org.webrtc.PeerConnection.*
import org.webrtc.PeerConnection.Observer
import java.util.*
import kotlin.collections.ArrayList

class WebrtcHelper : WebrtcHelperInterface {
    private val TAG = WebrtcHelper::class.java.name

    // context
    private lateinit var eglBase: EglBase
    private lateinit var context: Context

    // peer connection
    private lateinit var factory: PeerConnectionFactory

    // listener
    private lateinit var webRtcEventListener: WebRtcEventListener
    private var onIceCandidateListener: ((String?, String?, Int) -> Unit?)? = null

    // audio
    private var localAudioTrack: AudioTrack? = null
    private var audioConstraints: MediaConstraints? = null
    private var audioSource: AudioSource? = null

    // video
    private var videoSender: RtpSender? = null
    private var audioSender: RtpSender? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    // call model hashset
    private var callModels: HashSet<CallModel?> = HashSet()

    // Participants
    private val participants = ArrayList<Participant>()

    // call actions
    private var isAudioEnabled: Boolean = true
    private val isSpeaker = false
    private val isVideoEnabled = true
    private val isScreenCaptureEnabled = false

    private val mediaProjectionPermissionResultData: Intent? = null
    private val mediaProjectionPermissionResultCode = 0

    override fun addEglContext(eglBase: EglBase) {
        ObjectHelper.requireNonNull(eglBase, "EglBase can't be null")
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

        // check if user enable screen sharing
        val videoCapturer = createVideoCapture()
            ?: throw java.lang.NullPointerException("VideoCapturer object can't be null")
        val videoSource =
            factory.createVideoSource(videoCapturer.isScreencast)
        surfaceTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name,
            eglBase.eglBaseContext
        )
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS)
        val videoTrackFromCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource)
        videoTrackFromCamera.setEnabled(true)
        return videoTrackFromCamera
    }

    override fun getPeerConnection(
        participant: String,
        videoTrackFromCamera: VideoTrack
    ): PeerConnection {

        val peerConnection: PeerConnection?
        if (!isParticipantExists(participant)) {

            // create new peer connection
            peerConnection = createPeerConnection(factory)
            // add participant to Room object
            //webRtcEventListener.onPeerConnectionCreated(participant)// TODO: FIX

            // add participant to all participant array
            participants.add(Participant(participant))

            // create correspond call model list
            val callModel = CallModel()
            callModel.participant = participant
            callModel.peerConnection = peerConnection

            // start stream
            val callModelWithSenders: CallModel? =
                startStreamingVideo(callModel, videoTrackFromCamera)

            // push call model
            callModels.add(callModelWithSenders)

        } else { // get peer connection by existed id
            peerConnection = getCallModelById(participant)?.peerConnection
        }
        return peerConnection!!
    }

    override fun addOnIceCandidateListener(onIceCandidate: ((String?, String?, Int) -> Unit?)?) {
        this.onIceCandidateListener = onIceCandidate
    }

    private fun startStreamingVideo(
        callModel: CallModel,
        videoTrackFromCamera: VideoTrack
    ): CallModel? {
        val peerConnection: PeerConnection? = callModel.peerConnection
        val mediaStreamLabels = listOf("ARDAMS")
        videoSender = peerConnection?.addTrack(videoTrackFromCamera, mediaStreamLabels)
        audioSender = peerConnection?.addTrack(localAudioTrack, mediaStreamLabels)
        callModel.rtpSenderVideo = videoSender
        callModel.rtpSenderAudio = audioSender
        Log.i(TAG, "Add track")
        return callModel
    }

    private fun isParticipantExists(participant: String): Boolean {
        if (callModels.size == 0) return false
        for (callModel in callModels) {
            if (callModel?.participant.equals(participant)) {
                return true
            }
        }
        return false
    }

    private fun getCallModelById(participant: String): CallModel? {
        if (callModels.size == 0) throw NullPointerException("Can't find participant by $participant")
        for (callModel in callModels) {
            if (callModel?.participant.equals(participant)) return callModel
        }
        throw NullPointerException("Can't find participant by $participant")
    }

    override fun createPeerConnection(peerConnectionFactory: PeerConnectionFactory): PeerConnection? {
        val iceServers = ArrayList<IceServer>()
        val iceServer =
            IceServer.builder("stun:stun.l.google.com:19302")
        iceServers.add(iceServer.createIceServer())
        val rtcConfig = RTCConfiguration(iceServers)

        val pcObserver: Observer = object : Observer {
            override fun onSignalingChange(signalingState: SignalingState) {
                Log.d(
                    TAG,
                    "onSignalingChange: "
                )
            }

            override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {
                Log.d(
                    TAG,
                    "onIceConnectionChange: "
                )
            }

            override fun onIceConnectionReceivingChange(b: Boolean) {
                Log.d(
                    TAG,
                    "onIceConnectionReceivingChange: "
                )
            }

            override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {
                Log.d(
                    TAG,
                    "onIceGatheringChange: "
                )
            }

            override fun onIceCandidate(iceCandidate: IceCandidate) {
                Log.d(
                    TAG,
                    "onIceCandidate: "
                )

                onIceCandidateListener?.let {
                    it(iceCandidate.sdp,
                        iceCandidate.sdpMid,
                        iceCandidate.sdpMLineIndex)
                }
            }

            override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                Log.d(
                    TAG,
                    "onIceCandidatesRemoved: "
                )
            }

            override fun onAddStream(mediaStream: MediaStream) {
                Log.d(
                    TAG,
                    "onAddStream: " + mediaStream.videoTracks.size
                )
                val remoteVideoTrack = mediaStream.videoTracks[0]
                remoteVideoTrack.setEnabled(true)

                webRtcEventListener.onRemoteTrackAdded(remoteVideoTrack, participants)
            }

            override fun onRemoveStream(mediaStream: MediaStream) {
                Log.d(
                    TAG,
                    "onRemoveStream: "
                )
            }

            override fun onDataChannel(dataChannel: DataChannel) {
                Log.d(TAG, "onDataChannel: ")
            }

            override fun onRenegotiationNeeded() {
                Log.d(
                    TAG,
                    "onRenegotiationNeeded: "
                )
            }

            override fun onAddTrack(
                rtpReceiver: RtpReceiver,
                mediaStreams: Array<MediaStream>
            ) {
            }
        }

        return factory.createPeerConnection(rtcConfig, pcObserver)
    }

    override fun createVideoCapture(): VideoCapturer? {
        return if (!useCamera2()) {
            createCameraCapture(Camera2Enumerator(context))
        } else {
            createCameraCapture(Camera1Enumerator(true))
        }
    }

    private fun useCamera2(): Boolean {
        return Camera2Enumerator.isSupported(context)
    }

    private fun createCameraCapture(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    override fun getOfferSdp(
        participant: String,
        sdpCallBack: SdpCallBack,
        videoTrackFromCamera: VideoTrack
    ) {
        val peerConnection = getPeerConnection(participant, videoTrackFromCamera)
        val sdpMediaConstraints = MediaConstraints()
        peerConnection.createOffer(object : MySdpObserver() {
            override fun onCreateSuccess(p0: org.webrtc.SessionDescription?) {
                peerConnection.setLocalDescription(this, p0)
                val mSessionDescription = SessionDescription()
                mSessionDescription.sessionDesctiption = p0?.description!!
                sdpCallBack.onSdpCreated(
                    participant,
                    mSessionDescription
                )
            }
        }, sdpMediaConstraints)
    }

    override fun getAnswerSdp(
        participant: String,
        sdpCallBack: SdpCallBack,
        videoTrackFromCamera: VideoTrack
    ) {
        val peerConnection = getPeerConnection(participant, videoTrackFromCamera)
        val sdpMediaConstraints = MediaConstraints()
        peerConnection.createAnswer(object : MySdpObserver() {
            override fun onCreateSuccess(p0: org.webrtc.SessionDescription?) {
                peerConnection.setLocalDescription(this, p0)

                val mSessionDescription = SessionDescription()
                mSessionDescription.sessionDesctiption = p0?.description!!


                sdpCallBack.onSdpCreated(participant, mSessionDescription)
            }
        }, sdpMediaConstraints)
    }

    override fun addIceCandidate(
        participant: String,
        sessionDescription: SessionDescription,
        videoTrackFromCamera: VideoTrack
    ) {
        val peerConnection = getPeerConnection(participant, videoTrackFromCamera)
        val candidate = IceCandidate(
            sessionDescription.sdpMidRes,
            sessionDescription.sdpMidLineIndex,
            sessionDescription.candidateRes
        )
        peerConnection.addIceCandidate(candidate)
    }

    override fun addRemoteSdp(
        participant: String,
        sessionDescription: org.webrtc.SessionDescription,
        videoTrackFromCamera: VideoTrack
    ) {
        val peerConnection = getPeerConnection(participant, videoTrackFromCamera)

        peerConnection.setRemoteDescription(object : MySdpObserver() {}, sessionDescription)
    }

    override fun addWebRtcListener(webRtcEventListener: WebRtcEventListener) {
        this.webRtcEventListener = webRtcEventListener
    }

    override fun participants(): ArrayList<Participant> {
        return participants
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

    @TargetApi(21)
    private fun createScreenCapture(): VideoCapturer? {
        if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
            Log.i(
                TAG,
                "createScreenCapturer: User didn't give permission to capture the screen."
            )
            return null
        }
        return if (mediaProjectionPermissionResultData != null) {
            ScreenCapturerAndroid(
                mediaProjectionPermissionResultData, object : MediaProjection.Callback() {
                    override fun onStop() {
                        Log.i(
                            TAG,
                            "onStop: User revoked permission to capture the screen."
                        )
                    }
                })
        } else null
    }

    /**
     * Call model managment
     */

    private fun removeParticipant(participant: String): Unit {
        if (callModels.size > 0) {
            val it = callModels.iterator()
            while (it.hasNext()) {
                val callModel = it.next()
                if (callModel?.participant.equals(participant)) it.remove()
            }
        }
    }
}