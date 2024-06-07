package com.replaymod.gradle.remap

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.config.KotlinSourceRoot
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

fun analyze(
    environment: KotlinCoreEnvironment,
    ktFiles: List<KtFile>
): AnalysisResult {
    return try {
        analyze1521(environment, ktFiles)
    } catch (e: Throwable) {
        try {
            analyze1620(environment, ktFiles)
        } catch (e: Throwable) {
            analyze200(environment, ktFiles)
        }
    }
}

fun createSourceRoot(
    tempDir: Path,
    isCommon: Boolean
): KotlinSourceRoot {
    return try {
        createSourceRoot1521(tempDir.toAbsolutePath().toString(), isCommon)
    } catch (e: NoSuchMethodError) {
        createSourceRoot190(tempDir.toAbsolutePath().toString(), isCommon)
    }
}
