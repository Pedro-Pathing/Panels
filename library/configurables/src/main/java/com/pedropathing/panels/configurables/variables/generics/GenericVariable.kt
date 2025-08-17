package com.pedropathing.panels.configurables.variables.generics

import com.pedropathing.panels.configurables.GenericTypeJson

abstract class GenericVariable(
    open val className: String,
) {
    abstract val toJsonType: GenericTypeJson
}