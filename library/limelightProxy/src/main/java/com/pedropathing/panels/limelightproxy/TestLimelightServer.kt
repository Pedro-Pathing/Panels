package com.pedropathing.panels.limelightproxy

import fi.iki.elonen.NanoHTTPD

class TestLimelightServer() : NanoHTTPD(3331) {
    override fun serve(session: IHTTPSession): Response {

        if (session.uri == "/test") return newFixedLengthResponse("test")

        return newFixedLengthResponse("Hello, World!")
    }

    fun startServer() {
        if(isAlive) return
        start()
        println("PANELS: PLUGINS/com.pedropathing.panels.LIMELIGHTPROXY: LimelightServer started on port 3331")
    }

    fun stopServer() {
        if(!isAlive) return
        stop()
        println("PANELS: PLUGINS/com.pedropathing.panels.LIMELIGHTPROXY: LimelightServer stopped")
    }
}