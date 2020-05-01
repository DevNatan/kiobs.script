package me.devnatan.kiobs.script.resolver

import me.devnatan.kiobs.script.ScriptInfo
import me.devnatan.kiobs.script.annotations.Script
import me.devnatan.kiobs.script.scriptInfo
import kotlin.script.experimental.api.*

internal fun resolveScriptInfo(ctx: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
    val annotations = ctx.collectedData?.get(ScriptCollectedData.foundAnnotations)
        ?.takeIf { it.isNotEmpty() } ?: return ctx.compilationConfiguration.asSuccess()

    return ScriptCompilationConfiguration(ctx.compilationConfiguration) {
        val info = ScriptInfo.Empty()
        for (annotation in annotations) {
            when (annotation) {
                is Script -> {
                    info.name = annotation.name
                }
            }
        }

        scriptInfo(info)
    }.asSuccess()
}