package ithd.common.widget

/**
 * A VideoRenderer is used to receive frames from a [VideoTrack].
 */
interface VideoRenderer {
    /**
     * Interface that provides events related to a [VideoRenderer].
     */
    interface Listener {
        /**
         * This method notifies the observer when the first frame has arrived.
         */
        fun onFirstFrame()

        /**
         * This method notifies the observer when the frame dimensions have changed.
         *
         * @param width    frame width
         * @param height   frame height
         * @param rotation frame rotation
         */
        fun onFrameDimensionsChanged(width: Int, height: Int, rotation: Int)

        /**
         * Egl base provider ready
         */
        fun onEglBaseReady(eglBaseProvider: EglBaseProvider?)
    }

    /**
     * Provides the YUV frame in I420 format.
     *
     * @param frame I420 YUV frame
     */
    fun renderFrame(frame: Any)
}