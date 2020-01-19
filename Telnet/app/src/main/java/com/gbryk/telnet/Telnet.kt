package com.gbryk.telnet

import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.text.Charsets.US_ASCII

class Telnet(private val host: String, private val port: Int) : Thread() {
    private var connection: Socket? = null
    private var received: String? = null
    private val queue: Queue<String> = LinkedBlockingQueue<String>()
    var open: Boolean = false

    fun send_text(text: String) {
        queue.offer(text)
    }

    fun get_text(): String? {
        val cp = received
        received = null
        return cp
    }

    override fun run() {
        connection = Socket(host, port)
        connection!!.soTimeout = 1000
        val dataOutputStream = DataOutputStream(connection!!.getOutputStream())
        val inputStream = connection!!.getInputStream()
        val bufferedReader = inputStream.bufferedReader()
        open = true
        while (!connection!!.isClosed) {
            if (inputStream.available() > 0) {
                Log.d("telnet", "received")
                var res = ""
                while(true){
                    try{
                        val line = bufferedReader.readLine()
                        res = res + line + "\n"
                    } catch (e: Exception){
                        break
                    }
                }

                received = res
                Log.d("telnet", received!!)
            } else {
                sleep(100)
            }
            if (!queue.isEmpty()) {
                Log.d("telnet", "sending message to net")
                val message = queue.poll()!!
                dataOutputStream.write(message.toByteArray(US_ASCII))
                Log.d("telnet", "sent")
                Log.d("telnet", message)
            }
        }
        open = false
        dataOutputStream.close()
        inputStream.close()
    }

    fun close() {
        connection?.close()
    }
}