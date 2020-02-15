package dappy.signal.kit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dappy.signal.kit.constants.ServerInformation
import dappy.signal.kit.model.Signal
import dappy.signal.kit.observable.SignalAction
import dappy.signal.kit.observable.SignalActionObserver
import dappy.signal.kit.socket.SignalOption
import dappy.signal.kit.socket.SocketIO

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startSignaling()
    }

    private fun startSignaling() {
       val aaa =  SocketIO()
            .observe(object : SignalActionObserver {
                override fun onConnected() {
                    println()
                    Thread.sleep(1000)

                }

                override fun onDisconnected() {
                    println()
                }

                override fun onError(errorMessage: String) {
                    println()
                }

                override fun onSignal(signal: Signal) {
                    println()
                }

            })
            .connect(
                SignalOption(
                    ServerInformation.SERVER_URL,
                    "signal",
                    true
                )
            )

    }
}
