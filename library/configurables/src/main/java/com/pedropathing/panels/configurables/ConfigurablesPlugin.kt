package com.pedropathing.panels.configurables

import android.R.attr.type
import android.content.Context
import com.pedropathing.panels.configurables.GlobalConfigurables
import com.pedropathing.panels.configurables.GlobalConfigurables.jvmFields
import com.pedropathing.panels.configurables.annotations.Configurable
import com.pedropathing.panels.configurables.annotations.IgnoreConfigurable
import com.pedropathing.panels.configurables.variables.MyField
import com.pedropathing.panels.configurables.variables.generics.GenericField
import com.pedropathing.panels.Panels
import com.pedropathing.panels.json.SocketMessage
import com.pedropathing.panels.plugins.BasePluginConfig
import com.pedropathing.panels.plugins.Plugin
import com.pedropathing.panels.reflection.ClassFinder
import com.pedropathing.panels.server.Socket
import com.qualcomm.ftccommon.FtcEventLoop
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl
import java.lang.reflect.Modifier
import kotlin.jvm.java

open class ConfigurablesPluginConfig : BasePluginConfig() {
}

object Plugin : com.pedropathing.panels.plugins.Plugin<ConfigurablesPluginConfig>(
    ConfigurablesPluginConfig()
) {
    var fieldsMap = mutableMapOf<String, MyField>()
    var configurableClasses: List<ClassFinder.ClassEntry> = listOf()
    var allFields: MutableList<_root_ide_package_.com.pedropathing.panels.configurables.GenericTypeJson> = mutableListOf()

    val allFieldsMap: Map<String, List<_root_ide_package_.com.pedropathing.panels.configurables.GenericTypeJson>>
        get() = _root_ide_package_.com.pedropathing.panels.configurables.Plugin.allFields.groupBy { it.className }.toSortedMap()

    var initialAFieldsMap: Map<String, List<_root_ide_package_.com.pedropathing.panels.configurables.GenericTypeJson>> = mapOf()

    override fun onNewClient(client: Socket.ClientSocket) {
        sendClient(client, "initialConfigurables",
            _root_ide_package_.com.pedropathing.panels.configurables.Plugin.initialAFieldsMap
        )
        sendClient(client, "configurables",
            _root_ide_package_.com.pedropathing.panels.configurables.Plugin.allFieldsMap
        )
    }

    override fun onMessage(client: Socket.ClientSocket, type: String, data: Any?) {
        log("Got message of type $type with data $data")
        if (type == "updatedConfigurable") {
            val changes = try {
                SocketMessage.convertData<List<_root_ide_package_.com.pedropathing.panels.configurables.ChangeJson>>(data)
            } catch (e: Exception) {
                log("Failed to convert data: ${e.message}")
                emptyList()
            }

            if(changes == null) return

            changes.forEach {
                val generalRef = _root_ide_package_.com.pedropathing.panels.configurables.GlobalConfigurables.fieldsMap[it.id] ?: return
                log("Field id: ${it.id}, New value: ${it.newValueString}")
                generalRef.setValue(it.newValueString)
                it.newValueString = generalRef.getValue().toString()

                _root_ide_package_.com.pedropathing.panels.configurables.Plugin.allFields = jvmFields.map { it.toJsonType }.toMutableList()
            }

            send("newConfigurables", changes)
        }
    }

    fun refreshClass(className: String) {
        _root_ide_package_.com.pedropathing.panels.configurables.Plugin.allFields.removeAll { it.className == className }

        val fields: MutableList<GenericField> = mutableListOf()

        try {
            val clazz = Class.forName(className)
            log("Inspecting class $className")
            fields.addFieldsFromClass(clazz, className)
            try {
                val companionClazz = Class.forName("${className}\$Companion")
                fields.addFieldsFromClass(companionClazz, className)
            } catch (e: ClassNotFoundException) {
                // no companion found
            }
        } catch (e: Exception) {
            error("Error inspecting class ${className}: ${e.message}")
        }

        val newFields = fields.map { it.toJsonType }.toMutableList()

        _root_ide_package_.com.pedropathing.panels.configurables.Plugin.allFields.addAll(newFields)

        send("configurables",
            _root_ide_package_.com.pedropathing.panels.configurables.Plugin.allFieldsMap
        )
    }

    override fun onRegister(
        panelsInstance: Panels,
        context: Context
    ) {
        log("Initializing configurables")
        _root_ide_package_.com.pedropathing.panels.configurables.Plugin.fieldsMap = mutableMapOf()
        _root_ide_package_.com.pedropathing.panels.configurables.Plugin.configurableClasses = listOf()

        _root_ide_package_.com.pedropathing.panels.configurables.GlobalConfigurables.jvmFields = mutableListOf()
        _root_ide_package_.com.pedropathing.panels.configurables.GlobalConfigurables.fieldsMap = mutableMapOf()

        _root_ide_package_.com.pedropathing.panels.configurables.Plugin.configurableClasses = ClassFinder.findClasses(
            { clazz ->
                val hasConfigurable =
                    clazz.isAnnotationPresent(Configurable::class.java)
                val hasIgnoreConfigurable =
                    clazz.isAnnotationPresent(IgnoreConfigurable::class.java)
                val shouldKeep =
                    hasConfigurable && !hasIgnoreConfigurable

                shouldKeep
            }
        )

        log("Configurable classes: ${_root_ide_package_.com.pedropathing.panels.configurables.Plugin.configurableClasses.map { it.className }}")

        val fields: MutableList<GenericField> = mutableListOf()

        _root_ide_package_.com.pedropathing.panels.configurables.Plugin.configurableClasses.forEach {
            val className = it.className

            try {
                val clazz = Class.forName(className)
                log("Inspecting class $className")
                fields.addFieldsFromClass(clazz, className)
                try {
                    val companionClazz = Class.forName("${className}\$Companion")
                    fields.addFieldsFromClass(companionClazz, className)
                } catch (e: ClassNotFoundException) {
                    // no companion found
                }
            } catch (e: Exception) {
                error("Error inspecting class ${className}: ${e.message}")
            }
        }

        log("Configurable fields: ${fields.map { it.name }}")

        _root_ide_package_.com.pedropathing.panels.configurables.Plugin.allFields = jvmFields.map { it.toJsonType }.toMutableList()

        _root_ide_package_.com.pedropathing.panels.configurables.Plugin.initialAFieldsMap =
            _root_ide_package_.com.pedropathing.panels.configurables.Plugin.allFieldsMap

        send("initialConfigurables",
            _root_ide_package_.com.pedropathing.panels.configurables.Plugin.initialAFieldsMap
        )
        send("configurables",
            _root_ide_package_.com.pedropathing.panels.configurables.Plugin.allFieldsMap
        )
    }

    private fun MutableList<GenericField>.addFieldsFromClass(
        clazz: Class<*>,
        originalClassName: String
    ) {
        val fields = clazz.declaredFields
        fields.forEach { field ->
            try {
                val isFinal = Modifier.isFinal(field.modifiers)
                val isStatic = Modifier.isStatic(field.modifiers)
                val isIgnored = field.isAnnotationPresent(IgnoreConfigurable::class.java)

                val isPrivate = Modifier.isPrivate(field.modifiers)
                val isNull = try {
                    if (field.get(null) == null) {
                        println("PANELS: Field ${field.name} in $clazz is null")
                        true
                    } else false
                } catch (t: Throwable) {
                    false
                }

                val isJvmField = !isFinal && isStatic && !isIgnored

                val fieldTypeName = field.type.canonicalName ?: ""

                log("Found field of $fieldTypeName / $isJvmField")

                if (isJvmField) {
                    val displayClassName =
                        if (clazz.name.endsWith("\$Companion")) originalClassName else clazz.name
                    val genericField = GenericField(className = displayClassName, reference = field)
                    log("Adding field $genericField / ${genericField.type} / ${genericField.value} / ${genericField.isNull}")
                    add(genericField)
                    _root_ide_package_.com.pedropathing.panels.configurables.GlobalConfigurables.jvmFields.add(genericField)
                }
            } catch (t: Throwable) {
                error("Error inspecting field ${field.name} in $clazz: ${t.message}")
            }
        }
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