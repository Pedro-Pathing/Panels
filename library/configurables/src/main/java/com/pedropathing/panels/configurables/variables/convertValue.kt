package com.pedropathing.panels.configurables.variables

import com.pedropathing.panels.configurables.ConfigurablesLogger


fun convertValue(value: String, type: com.pedropathing.panels.configurables.variables.BaseTypes, enumConstants: Array<out Any>?): Any? {
    return when (type) {
        com.pedropathing.panels.configurables.variables.BaseTypes.INT -> {
            when {
                value.toIntOrNull() != null -> value.toInt()
                value.toFloatOrNull() != null -> value.toFloat()
                    .toInt()

                value.toDoubleOrNull() != null -> value.toDouble()
                    .toInt()

                else -> value.toInt()
            }
        }

        com.pedropathing.panels.configurables.variables.BaseTypes.DOUBLE -> {
            when {
                value.toDoubleOrNull() != null -> value.toDouble()
                value.toFloatOrNull() != null -> value.toFloat()
                    .toDouble()

                else -> value.toDouble()
            }
        }

        com.pedropathing.panels.configurables.variables.BaseTypes.STRING -> {
            value
        }

        com.pedropathing.panels.configurables.variables.BaseTypes.BOOLEAN -> {
            value.toBoolean()
        }

        com.pedropathing.panels.configurables.variables.BaseTypes.FLOAT -> {
            when {
                value.toFloatOrNull() != null -> value.toFloat()
                value.toDoubleOrNull() != null -> value.toDouble()
                    .toFloat()

                else -> value.toFloat()
            }
        }

        com.pedropathing.panels.configurables.variables.BaseTypes.LONG -> {
            when {
                value.toLongOrNull() != null -> value.toLong()
                value.toDoubleOrNull() != null -> value.toDouble()
                    .toLong()

                else -> value.toLong()
            }
        }

        com.pedropathing.panels.configurables.variables.BaseTypes.ENUM -> {
            if (enumConstants == null) return null
            try {
                val enumType = enumConstants.first()::class.java
                return enumType.enumConstants.find { it.toString() == value }
            } catch (e: Exception) {
                ConfigurablesLogger.error("Error converting to enum: ${e.message}")
                return null
            }
        }

        else -> null
    }
}