package com.gbryk.telnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_terminal.*

class TerminalActivity : AppCompatActivity(), Runnable{
    var telnet: Telnet? = null
    private var root: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal)
        root = findViewById(android.R.id.content);
        telnet = Telnet(intent.getStringExtra("host")!!, intent.getIntExtra("port", 2137))
        telnet!!.start()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d("sendmessage", "Got keyUpEvent")
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            Log.d("sendmessage", "got Enter")
            val lines = terminalText.text.split("\n")
            val command = lines.get(lines.lastIndex-1)
            Log.d("sendmessage", command)
            if (telnet!!.open) {
                Log.d("sendmessage", "telnet is open")
                telnet!!.send_text(command)
                Log.d("sendmessage", "sending message")
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        run()
    }

    override fun onPause() {
        root?.removeCallbacks(this)
        super.onPause()
    }

    override fun run() {
        if (telnet!!.open){
            val text = telnet?.get_text()
            if (text != null)
                terminalText.setText(text)
            root?.postDelayed(this, 100)
        } else {
            finish()
            return
        }
    }

    override fun finish() {
        telnet?.close()
        super.finish()
    }

}
