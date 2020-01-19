package com.gbryk.telnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_terminal.*

class TerminalActivity : AppCompatActivity(), Runnable {
    var telnet: Telnet? = null
    private var root: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal)
        root = findViewById(android.R.id.content)
        sendCommandButton.setOnClickListener { sendButton() }
        telnet = Telnet(intent.getStringExtra("host")!!, intent.getIntExtra("port", 2137))
        telnet!!.start()
    }

    override fun onBackPressed() {
        telnet?.close()
        super.onBackPressed()
    }

    fun sendButton() {
        telnet!!.send_text(commandText.text.toString())
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
        if (telnet!!.open) {
            val text = telnet?.get_text()
            if (text != null)
                terminalText.setText(text)
        } else if (telnet!!.closed){
            finish()
            return
        }
        root?.postDelayed(this, 100)
    }

    override fun finish() {
        telnet?.close()
        super.finish()
    }

}
