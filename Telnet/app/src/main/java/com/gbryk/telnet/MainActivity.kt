package com.gbryk.telnet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        connection_button.setOnClickListener { startTelnet() }
    }

    private fun startTelnet() {
        val i = Intent(applicationContext, TerminalActivity::class.java)
        i.putExtra("host", host_field.text.toString())
        i.putExtra("port", port_field.text.toString().toInt())
        startActivity(i)
    }
}
