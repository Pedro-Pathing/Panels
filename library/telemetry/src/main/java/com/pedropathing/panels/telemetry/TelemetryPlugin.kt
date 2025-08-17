package com.pedropathing.panels.telemetry

import android.content.Context
import com.pedropathing.panels.Panels
import com.pedropathing.panels.plugins.BasePluginConfig
import com.pedropathing.panels.plugins.Plugin
import com.pedropathing.panels.server.Socket
import com.qualcomm.ftccommon.FtcEventLoop
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl

open class TelemetryPluginConfig : BasePluginConfig() {
    open var telemetryUpdateInterval = 75L
}

object Plugin : Plugin<TelemetryPluginConfig>(TelemetryPluginConfig()) {
    val manager = TelemetryManager(config) { lines -> send("telemetryPacket", lines) }

    override fun onNewClient(client: Socket.ClientSocket) {
    }

    override fun onMessage(client: Socket.ClientSocket, type: String, data: Any?) {
        log("Got message of type $type with data $data")
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
        manager.update()
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