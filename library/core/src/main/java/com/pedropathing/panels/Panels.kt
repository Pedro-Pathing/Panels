package com.pedropathing.panels

import android.content.Context
import com.pedropathing.panels.core.TextHandler
import com.pedropathing.panels.core.OpModeHandler
import com.pedropathing.panels.core.PreferencesHandler
import com.pedropathing.panels.plugins.PluginsManager
import com.pedropathing.panels.reflection.ClassFinder
import com.pedropathing.panels.server.Socket
import com.pedropathing.panels.server.StaticServer
import com.qualcomm.ftccommon.FtcEventLoop
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier.Notifications
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar
import com.qualcomm.robotcore.util.WebHandlerManager
import org.firstinspires.ftc.ftccommon.external.OnCreate
import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop
import org.firstinspires.ftc.ftccommon.external.OnDestroy
import org.firstinspires.ftc.ftccommon.external.WebHandlerRegistrar
import org.firstinspires.ftc.ftccommon.internal.FtcRobotControllerWatchdogService
import org.firstinspires.ftc.robotcore.internal.system.AppUtil


object Panels : Notifications {
    lateinit var server: StaticServer
    lateinit var socket: Socket
    var config = PanelsConfig()

    var wasStarted = false

    @JvmStatic
    @OpModeRegistrar
    fun registerOpMode(manager: OpModeManager) {
        OpModeHandler.init(manager)
        OpModeHandler.registerOpMode()
    }

    @JvmStatic
    @OnCreate
    fun start(context: Context) {
        wasStarted = false

        TaskTimer.measure("Init all reflection classes") {
            ClassFinder.init(context.packageCodePath)
        }

        var configs = listOf<ClassFinder.ClassEntry>()

        TaskTimer.measure("Find configs") {
            configs = ClassFinder.findClasses { clazz ->
                PanelsConfig::class.java.isAssignableFrom(clazz) && clazz != PanelsConfig::class.java
            }
        }

        Logger.coreLog("Found ${configs.size} configs.")

        if (configs.isNotEmpty()) {
            config = Class.forName(configs[0].className)
                .getDeclaredConstructor()
                .newInstance() as PanelsConfig
        }
        Logger.coreLog("Config is $config")

        PreferencesHandler.init(context)

        if (config.isDisabled) {
            PreferencesHandler.isEnabled = false
            disable()
        } else {
            enable()
        }

        try {
            server = StaticServer(context, 8001, "web")
            socket = Socket(8002)
        } catch (e: Exception) {
            Logger.coreLog("Failed to start webserver: " + e.message)
        }

        if (PreferencesHandler.isEnabled) {
            server.startServer()
            socket.startServer()
        }

        TextHandler.injectText()

        PluginsManager.init(context)

        TaskTimer.measure("Server compress data") {
            server.precompressData()
        }

        wasStarted = true
    }

    @JvmStatic
    @WebHandlerRegistrar
    fun attachWebServer(context: Context, manager: WebHandlerManager) {
    }

    @JvmStatic
    @OnCreateEventLoop
    fun attachEventLoop(context: Context, eventLoop: FtcEventLoop) {
        PluginsManager.plugins.values.forEach { it.onAttachEventLoop(eventLoop) }
        PluginsManager.plugins.values.forEach { it.onOpModeManager(eventLoop.opModeManager) }

        eventLoop.opModeManager.unregisterListener(this)
        eventLoop.opModeManager.registerListener(this)
    }

    @JvmStatic
    @OnDestroy
    fun stop(context: Context) {
        if (!FtcRobotControllerWatchdogService.isLaunchActivity(
                AppUtil.getInstance().rootActivity
            )
        ) {
            return
        }

        TextHandler.removeText()
        server.stopServer()
        socket.stopServer()
    }

    override fun onOpModePreInit(opMode: OpMode) {
        Logger.coreLog("Init of opMode")
        PluginsManager.plugins.values.forEach { it.preInitInternal(opMode) }
    }

    override fun onOpModePreStart(opMode: OpMode) {
        Logger.coreLog("Start of opMode")
        PluginsManager.plugins.values.forEach { it.preStartInternal(opMode) }
    }

    override fun onOpModePostStop(opMode: OpMode) {
        Logger.coreLog("Stop of opMode")
        PluginsManager.plugins.values.forEach { it.postStopInternal(opMode) }
    }


    fun toggle() {
        when (PreferencesHandler.isEnabled) {
            true -> disable()
            false -> enable()
        }
    }

    fun enable() {
        if (PreferencesHandler.isEnabled) return
        PreferencesHandler.isEnabled = true
        server.startServer()
        socket.startServer()
    }

    fun disable() {
        if (!PreferencesHandler.isEnabled) return
        PreferencesHandler.isEnabled = false
        server.stopServer()
        socket.stopServer()
    }
}