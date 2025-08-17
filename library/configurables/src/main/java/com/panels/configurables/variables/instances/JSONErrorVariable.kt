package com.panels.configurables.variables.instances

import com.pedropathing.panels.configurables.GenericTypeJson
import com.pedropathing.panels.configurables.variables.BaseTypes
import com.pedropathing.panels.configurables.variables.generics.GenericVariable

class JSONErrorVariable(
    override val className: String,
    val name: String
) : GenericVariable(className) {
    override val toJsonType: GenericTypeJson
        get() = GenericTypeJson(
            id = "",
            className = className,
            fieldName = name,
            type = BaseTypes.JSON_ERROR,
            value = "",
        )
}