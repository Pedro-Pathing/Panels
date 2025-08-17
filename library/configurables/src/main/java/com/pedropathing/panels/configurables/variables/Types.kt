package com.pedropathing.panels.configurables.variables

import com.pedropathing.panels.configurables.annotations.GenericValue
import java.lang.reflect.ParameterizedType
import java.lang.reflect.TypeVariable
import kotlin.collections.get
import kotlin.jvm.java

private val typeMapping = mapOf(
    Int::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.INT,
    Integer::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.INT,
    Double::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.DOUBLE,
    java.lang.Double::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.DOUBLE,
    String::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.STRING,
    Boolean::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.BOOLEAN,
    java.lang.Boolean::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.BOOLEAN,
    Float::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.FLOAT,
    java.lang.Float::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.FLOAT,
    Long::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.LONG,
    java.lang.Long::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.LONG,
    IntArray::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.ARRAY,
    DoubleArray::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.ARRAY,
    BooleanArray::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.ARRAY,
    FloatArray::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.ARRAY,
    LongArray::class.java to com.pedropathing.panels.configurables.variables.BaseTypes.ARRAY,
)

fun getType(
    classType: Class<*>?,
    reference: MyField? = null,
    parentReference: MyField? = null
): com.pedropathing.panels.configurables.variables.BaseTypes {
    return when {
        classType == null -> com.pedropathing.panels.configurables.variables.BaseTypes.UNKNOWN
        typeMapping.containsKey(classType) -> typeMapping[classType] ?: com.pedropathing.panels.configurables.variables.BaseTypes.UNKNOWN
        classType.isEnum -> com.pedropathing.panels.configurables.variables.BaseTypes.ENUM
        classType.isArray -> com.pedropathing.panels.configurables.variables.BaseTypes.ARRAY
        Map::class.java.isAssignableFrom(classType) -> com.pedropathing.panels.configurables.variables.BaseTypes.MAP
        List::class.java.isAssignableFrom(classType) -> com.pedropathing.panels.configurables.variables.BaseTypes.LIST
        MutableList::class.java.isAssignableFrom(classType) -> com.pedropathing.panels.configurables.variables.BaseTypes.LIST
        isCustomClass(classType) -> com.pedropathing.panels.configurables.variables.BaseTypes.CUSTOM

        else -> resolveGenericType(reference, parentReference)
    }
}

private fun isCustomClass(clazz: Class<*>): Boolean {
    return !(clazz.isPrimitive
            || clazz.`package`?.name?.startsWith("java.") == true
            || clazz.`package`?.name?.startsWith("kotlin.") == true
            || clazz.isInterface
            || clazz.isAnnotation
            || clazz.isEnum
            || clazz.isArray)
}

private fun resolveGenericType(reference: MyField?, parentReference: MyField?): com.pedropathing.panels.configurables.variables.BaseTypes {
    if (reference == null || parentReference == null) return com.pedropathing.panels.configurables.variables.BaseTypes.UNKNOWN

    val genericType = reference.ref?.genericType

    val isGeneric =
        genericType is ParameterizedType || genericType is TypeVariable<*>

    if (!isGeneric) return com.pedropathing.panels.configurables.variables.BaseTypes.CUSTOM

    val genericAnnotation = parentReference.ref?.getAnnotation(GenericValue::class.java)

    if (genericAnnotation == null) return com.pedropathing.panels.configurables.variables.BaseTypes.GENERIC_NO_ANNOTATION

    if (genericType is ParameterizedType) return com.pedropathing.panels.configurables.variables.BaseTypes.GENERIC

    if (genericType is TypeVariable<*>) {
        val resolvedType = when (genericType.name) {
            "T" -> genericAnnotation.tParam
            "V" -> genericAnnotation.vParam
            else -> null
        }
        if (resolvedType != null) {
            return getType(resolvedType.java)
        }
    }

    return com.pedropathing.panels.configurables.variables.BaseTypes.GENERIC
}