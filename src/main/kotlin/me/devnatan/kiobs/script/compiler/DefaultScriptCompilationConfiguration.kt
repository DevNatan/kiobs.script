package me.devnatan.kiobs.script.compiler

import me.devnatan.kiobs.script.KiobsScript
import me.devnatan.kiobs.script.resolver.resolveScriptInfo
import me.devnatan.kiobs.script.scriptInfo
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import me.devnatan.kiobs.script.annotations.Script

object DefaultScriptCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports(Script::class, KiobsScript::class)
    jvm {
        dependenciesFromClassContext(DefaultScriptCompilationConfiguration::class, wholeClasspath = true)
    }
    refineConfiguration {
        onAnnotations(Script::class, handler = ::resolveScriptInfo)
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }

    refineConfiguration {
        beforeCompiling { ctx ->
            // all scripts must contain this annotation that will be used to identify it.
            // you can override this configuration from the compiler and remove this option but it is not recommended.
            val content = ctx.compilationConfiguration[ScriptCompilationConfiguration.scriptInfo]
            if ((content == null) || !content.isValid()) {
                return@beforeCompiling ResultWithDiagnostics.Failure(
                    ScriptDiagnostic(
                        "A script must have a @file:Script annotation",
                        ScriptDiagnostic.Severity.FATAL
                    )
                )
            }

            ctx.compilationConfiguration.asSuccess()
        }
    }
})