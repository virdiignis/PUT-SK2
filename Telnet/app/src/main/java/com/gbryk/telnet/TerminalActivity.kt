package com.gbryk.telnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.content_main.*

class TerminalActivity : AppCompatActivity() {
    var telnet: Telnet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal)
        telnet = Telnet(intent.getStringExtra("host")!!, intent.getIntExtra("port", 2137))
    }
}
