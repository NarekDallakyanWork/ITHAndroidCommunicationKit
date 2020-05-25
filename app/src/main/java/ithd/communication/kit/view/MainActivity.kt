package ithd.communication.kit.view

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import ithd.common.widget.EglBaseProvider
import ithd.common.widget.VideoRenderer
import ithd.common.widget.VideoView
import ithd.communication.kit.R
import ithd.communication.kit.view.dialog.RoundedBottomSheetDialogFragment
import ithd.communication.kit.viewmodel.CallViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.webrtc.RendererCommon

class MainActivity : AppCompatActivity() {

    private var localSurfaceView: VideoView? = null
    private var callViewModel: CallViewModel? = null
    private val menuFragment = RoundedBottomSheetDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
        addPermission()
        onClicked()
    }

    private fun onClicked() {
        connectBtn.setOnClickListener {

            menuFragment.show(supportFragmentManager, menuFragment.tag)
            menuFragment.addListener(onClick = {

            })
        }
    }

    private fun initViewModel() {

        callViewModel = ViewModelProvider(this).get(CallViewModel::class.java)
    }

    private fun initVideoView() {

        localSurfaceView = surface_view
        localSurfaceView?.setEnableHardwareScaler(true)
        localSurfaceView?.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
        localSurfaceView?.setZOrderMediaOverlay(true)
        localSurfaceView?.setMirror(true)

        localSurfaceView?.setListener(object : VideoRenderer.Listener {
            override fun onFirstFrame() {
                println()
            }

            override fun onFrameDimensionsChanged(width: Int, height: Int, rotation: Int) {
                println()
            }

            override fun onEglBaseReady(eglBaseProvider: EglBaseProvider?) {
                eglBaseProvider?.let {
                    callViewModel?.enstablish(it, this@MainActivity, localSurfaceView!!)
                }
            }
        })
    }

    private fun addPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    initVideoView()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }
}
