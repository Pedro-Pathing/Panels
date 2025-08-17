package com.panels.configurables

object PanelsConfigurables {
    fun refreshClass(cls: Any) {
        val name = cls::class.qualifiedName ?: return
        Plugin.refreshClass(name)
    }
}