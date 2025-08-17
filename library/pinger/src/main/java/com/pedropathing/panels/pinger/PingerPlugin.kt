package com.pedropathing.panels.pinger

import android.content.Context
import com.pedropathing.panels.Panels
import com.pedropathing.panels.json.SocketMessage
import com.pedropathing.panels.plugins.BasePluginConfig
import com.pedropathing.panels.plugins.Plugin
import com.pedropathing.panels.server.Socket
import com.qualcomm.ftccommon.FtcEventLoop
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl
import com.sun.tools.doclint.Entity.delta

open class ExamplePluginConfig : BasePluginConfig() {
}

object Plugin : Plugin<ExamplePluginConfig>(ExamplePluginConfig()) {
    override fun onNewClient(client: Socket.ClientSocket) {
    }

    override fun onMessage(client: Socket.ClientSocket, type: String, data: Any?) {
        log("Got message of type $type with data $data")
        if(type == "request"){
            val data = try {
                SocketMessage.convertData<Data>(data)
            } catch (e: Exception) {
                log("Failed to convert data: ${e.message}")
                null
            }

            if(data == null) return

            sendClient(client, "answer", data)
        }
    }

    override fun onRegister(
        panelsInstance: Panels,
        context: Context
    ) {

    }

    override fun onAttachEventLoop(eventLoop: FtcEventLoop) {
    }

    override fun onOpModeManager(o: OpModeManagerImpl) {
    }

    override fun onOpModePreInit(opMode: OpMode) {
    }

    override fun onOpModePreStart(opMode: OpMode) {
    }

    override fun onOpModePostStop(opMode: OpMode) {
    }


    override fun onEnablePanels() {
    }

    override fun onDisablePanels() {
    }
}