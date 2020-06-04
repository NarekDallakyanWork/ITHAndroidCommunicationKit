package ithd.communication.kit.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ithd.communication.kit.R
import kotlinx.android.synthetic.main.activity_connect_room.*

class ConnectRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_room)
        joinRoom()
    }

    private fun joinRoom() {
        joinRoomId.setOnClickListener {

            if (TextUtils.isEmpty(roomNameId.text.toString())) {
                Toast.makeText(this, "Please fill room name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("roomName", roomNameId.text.toString())
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}