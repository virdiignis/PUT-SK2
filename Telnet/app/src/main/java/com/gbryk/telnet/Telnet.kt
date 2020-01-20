package com.gbryk.telnet

import android.util.Log
import java.io.DataOutputStream
import java.lang.Exception
import java.net.Socket
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.text.Charsets.US_ASCII

// class representing threat responsible for network communication with server
class Telnet(private val host: String, private val port: Int) : Thread() {
    private var connection: Socket? = null
    private var received: String? = null
    private val queue: Queue<String> = LinkedBlockingQueue<String>()
    var open: Boolean = false //open can be false before the communication has started, and after it
    var closed: Boolean = false //finished, closed is set true only after communication is finished

    fun send_text(text: String) {
        queue.offer(text)
    }

    fun get_text(): String? {
        val cp = received
        received = null
        return cp
    }

    //main function run by thread
    override fun run() {
        connection = Socket(host, port) //crating socket connection
        connection!!.soTimeout = 200 //setting timeout to 200ms
        //wrapping socket streams with buffers
        val dataOutputStream = DataOutputStream(connection!!.getOutputStream())
        val inputStream = connection!!.getInputStream()
        val bufferedReader = inputStream.bufferedReader()
        open = true
        //loop active for whole connection life
        while (connection!!.isConnected) {
            if (inputStream.available() > 0) {
                Log.d("telnet", "received")
                var res = ""
                // reading lines of input until timeout is thrown
                // the hack is made because usual .readText() and similar methods are throwing
                // timeout without returning contents, and no other wrappers were able to fix it.
                // On the other hand, inputStream.available() returns 0 just after first line is
                // read, so there is no other way to know if next line can be read that to try
                // and read it.
                while (true) {
                    try {
                        val line = bufferedReader.readLine()
                        res = res + line + "\n"
                    } catch (e: Exception) {
                        break
                    }
                }

                received = res
                Log.d("telnet", received!!)
            } else {
                sleep(100)
            }
            // sending queued commands from user to the server
            if (!queue.isEmpty()) {
                Log.d("telnet", "sending message to net")
                val message = queue.poll()!!
                dataOutputStream.write(message.toByteArray(US_ASCII))
                Log.d("telnet", "sent")
                Log.d("telnet", message)
            }
        }
        open = false
        closed = true
        dataOutputStream.close()
        inputStream.close()
    }

    //safely close the communication
    fun close() {
        if (!connection?.isClosed!!) {
            connection?.getOutputStream()?.write("exit".toByteArray(US_ASCII))
            connection?.close()
        }
    }
}