package com.replaymod.gradle.remap

import org.jetbrains.kotlin.cli.common.config.KotlinSourceRoot
import java.nio.file.Path

fun createSourceRoot190(
    tempDir: Path,
    isCommon: Boolean
): KotlinSourceRoot {
    return KotlinSourceRoot(tempDir.toString(), isCommon, null)
}
