package ithd.common.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer

class VideoView(context: Context, @Nullable attrs: AttributeSet?) :
    SurfaceViewRenderer(context, attrs) {

    private var eglBaseProvider: EglBaseProvider? = null
    private var listener: VideoRenderer.Listener? = null
    private var mirror = false
    private var overlaySurface = false

    private val uiThreadHandler = Handler(Looper.getMainLooper())

    private val internalEventListener: RendererCommon.RendererEvents =
        object : RendererCommon.RendererEvents {
            override fun onFirstFrameRendered() {
                refreshRenderer()
                listener?.onFirstFrame()
            }

            override fun onFrameResolutionChanged(
                videoWidth: Int, videoHeight: Int, rotation: Int
            ) {
                refreshRenderer()
                listener?.onFrameDimensionsChanged(videoWidth, videoHeight, rotation)
            }
        }

    private fun setupRenderer() {
        init(eglBaseProvider?.rootEglBase?.eglBaseContext, internalEventListener)
        setMirror(mirror)
        setZOrderMediaOverlay(overlaySurface)
    }

    private fun getEglBaseProvider(): EglBaseProvider? {
        return eglBaseProvider
    }

    /** Sets listener of rendering events.  */
    fun setListener(@Nullable listener: VideoRenderer.Listener?) {
        this.listener = listener
    }

    private fun refreshRenderer() {
        uiThreadHandler.post(this::requestLayout)
    }

    /** Returns whether or not this view is mirroring video.  */
    fun getMirror(): Boolean {
        return mirror
    }

    /** Sets whether or not the rendered video should be mirrored.  */
    override fun setMirror(mirror: Boolean) {
        this.mirror = mirror
        super.setMirror(mirror)
        refreshRenderer()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Do not setup the renderer when using developer tools to avoid EGL14 runtime exceptions
        // Do not setup the renderer when using developer tools to avoid EGL14 runtime exceptions
        if (!isInEditMode) {
            eglBaseProvider = EglBaseProvider.instance(this)
            setupRenderer()
            listener?.onEglBaseReady(eglBaseProvider)
        }
    }

    /**
     * Controls placement of the video render relative to other surface.
     *
     * @param overlaySurface if true, video renderer is placed on top of another video renderer in
     * the window (but still behind window itself).
     */
    fun applyZOrder(overlaySurface: Boolean) {
        this.overlaySurface = overlaySurface
        setZOrderMediaOverlay(overlaySurface)
    }

    override fun onDetachedFromWindow() {
        super.release()
        eglBaseProvider!!.release(this)
        super.onDetachedFromWindow()
    }
}