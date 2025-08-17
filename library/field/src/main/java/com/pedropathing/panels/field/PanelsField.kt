package com.pedropathing.panels.field

class FieldPresets{
    val PANELS = CanvasPreset { /* no-op */ }

    val PEDRO_PATHING = CanvasPreset {
        it.offsetX = 24.0 * -3
        it.offsetY = 24.0 * 3
        it.rotation = CanvasRotation.DEG_270
    }
    val ROAD_RUNNER = CanvasPreset { /* no-op */ }
}

object PanelsField {
    val field: FieldManager
        get() = Plugin.manager

    val presets = FieldPresets()

    val TRANSPARENT = "transparent"
    val WHITE = "white"
    val BLACK = "black"
    val RED = "red"
    val BLUE = "blue"

    val images = FieldImages()
}