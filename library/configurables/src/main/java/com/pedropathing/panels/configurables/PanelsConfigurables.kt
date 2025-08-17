package com.pedropathing.panels.configurables

object PanelsConfigurables {
    fun refreshClass(cls: Any) {
        val name = cls::class.qualifiedName ?: return
        _root_ide_package_.com.pedropathing.panels.configurables.Plugin.refreshClass(name)
    }
}