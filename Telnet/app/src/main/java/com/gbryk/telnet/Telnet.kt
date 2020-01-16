package com.gbryk.telnet

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class Telnet(private val host: String, private val port: Int) : Thread() {
    private var connection: Socket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var received: String = ""

    fun send_text(text: String) {
        outputStream!!.write(text.toByteArray())
    }

    fun get_text(): String? {
        if (received.isEmpty()) return null
        val cp = received
        received = ""
        return cp
    }

    override fun run() {
        connection = Socket(host, port)
        outputStream = connection!!.getOutputStream()
        inputStream = connection!!.getInputStream()
        while (!connection!!.isClosed) {
            if (received.isEmpty() && inputStream!!.available() > 0) {
                val tmp = ByteArray(1024)
                inputStream!!.read(tmp)
                received = String(tmp)
            } else {
                sleep(10)
            }
        }
    }
}