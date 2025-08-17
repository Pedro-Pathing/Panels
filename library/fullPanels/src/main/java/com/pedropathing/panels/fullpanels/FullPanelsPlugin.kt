package com.pedropathing.panels.fullpanels

import android.content.Context
import com.pedropathing.panels.Panels
import com.pedropathing.panels.plugins.BasePluginConfig
import com.pedropathing.panels.plugins.Plugin
import com.pedropathing.panels.server.Socket
import com.qualcomm.ftccommon.FtcEventLoop
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl

open class FullPanelsPluginConfig : BasePluginConfig() {
}

object Plugin : Plugin<FullPanelsPluginConfig>(FullPanelsPluginConfig()) {
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