package com.panels.configurables.variables.generics

import com.pedropathing.panels.configurables.ConfigurablesLogger
import com.pedropathing.panels.configurables.GenericTypeJson
import com.pedropathing.panels.configurables.variables.BaseTypes
import com.pedropathing.panels.configurables.variables.convertToMyField
import com.pedropathing.panels.configurables.variables.getType
import com.pedropathing.panels.configurables.variables.instances.JSONErrorVariable
import com.pedropathing.panels.configurables.variables.processValue
import java.lang.reflect.Field

class GenericField(
    var className: String,
    var reference: Field,
) {

    val type = getType(reference.type, convertToMyField(reference), null)
    val value: GenericVariable = processValue(0, className, type, convertToMyField(reference), null)

    val isNull: Boolean
        get() = toJsonType.type == BaseTypes.JSON_ERROR

    val name: String
        get() = reference.name

    fun debug() {
        ConfigurablesLogger.log("Debug for $className / ${reference.name}")
        ConfigurablesLogger.log("Of type $type")
    }

    val toJsonType: GenericTypeJson
        get() {
            try {
                val json = value.toJsonType
                return json
            } catch (t: Throwable) {
                ConfigurablesLogger.error("Error getting JSON for $className / ${reference.name}: ${t.message}")
                return JSONErrorVariable(className, reference.name).toJsonType
            }
        }
}