package com.pedropathing.panels.field

import java.io.FileNotFoundException
import android.util.Base64

data class FieldImage(
    val LIGHT: ImagePreset,
    val DARK: ImagePreset
)

fun interface ImagePreset {
    fun get(): String
}


class FieldImages {
    fun loadResourceAsBytes(resourceName: String): ByteArray {
        return this::class.java.classLoader?.getResource(resourceName)
            ?.openStream()
            ?.readBytes()
            ?: throw FileNotFoundException("Resource not found: $resourceName")
    }

    fun loadResourceAsBase64(resourceName: String): String {
        val bytes = loadResourceAsBytes(resourceName)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    val INTO_THE_DEEP = FieldImage(
        LIGHT = ImagePreset { loadResourceAsBase64("into-the-deep-dark.png") },
        DARK = ImagePreset { loadResourceAsBase64("into-the-deep-light.png") }
    )
}