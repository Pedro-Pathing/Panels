package com.panels.configurables

import com.pedropathing.panels.configurables.variables.MyField
import com.pedropathing.panels.configurables.variables.generics.GenericField

object GlobalConfigurables {
    var jvmFields = mutableListOf<GenericField>()

    var fieldsMap = mutableMapOf<String, MyField>()
}