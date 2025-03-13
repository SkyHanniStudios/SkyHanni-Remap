package com.replaymod.gradle.remap

import java.io.Serializable

data class PatternMapping(
    val newClass: String,
    val oldClass: String,
    val newMethod: String,
    val oldMethod: String,
    val neededImport: String,
) : Serializable {
    fun matches(className: String?, methodName: String): Boolean {
        return className == oldClass && methodName == oldMethod.removeSuffix("()")
    }
}