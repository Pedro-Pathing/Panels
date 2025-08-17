package com.panels.configurables

import com.pedropathing.panels.configurables.variables.BaseTypes

class GenericTypeJson(
    var id: String,
    var className: String = "",
    var fieldName: String,
    var type: BaseTypes,
    var value: String = "",
    var possibleValues: List<String>? = null,
    var customValues: List<GenericTypeJson>? = null,
)

class ChangeJson(
    var id: String,
    var newValueString: String
)