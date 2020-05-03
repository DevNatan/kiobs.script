@file:JvmMultifileClass
@file:JvmName("ScriptResolver")
package me.devnatan.kiobs.script.resolver

import me.devnatan.kiobs.script.ScriptInfo
import me.devnatan.kiobs.script.annotations.Script
import me.devnatan.kiobs.script.scriptInfo
import kotlin.script.experimental.api.*

fun resolveScriptInfo(ctx: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
    val annotations = ctx.collectedData?.get(ScriptCollectedData.foundAnnotations)
        ?.takeIf { it.isNotEmpty() } ?: return ctx.compilationConfiguration.asSuccess()

    return ScriptCompilationConfiguration(ctx.compilationConfiguration) {
        for (annotation in annotations) {
            when (annotation) {
                is Script -> {
                    scriptInfo(ScriptInfo(annotation.name))
                }
            }
        }
    }.asSuccess()
}