package me.devnatan.kiobs.script.compiler

import me.devnatan.kiobs.script.createScriptName
import me.devnatan.kiobs.script.resolver.resolveScriptInfo
import me.devnatan.kiobs.script.scriptInfo
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.jvm.compilationCache
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.CompiledScriptJarsCache
import me.devnatan.kiobs.script.annotations.Script as ScriptAnnotation

class ScriptCompilerConfiguration @JvmOverloads constructor(
    val scriptsCacheFolder: File,
    val forceScriptAnnotation: Boolean = true
) : ScriptCompilationConfiguration({
    defaultImports(ScriptAnnotation::class)
    jvm {
        dependenciesFromClassContext(ScriptCompilerConfiguration::class, wholeClasspath = true)
    }
    refineConfiguration {
        onAnnotations(ScriptAnnotation::class, handler = ::resolveScriptInfo)
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
    hostConfiguration(ScriptingHostConfiguration {
        jvm {
            compilationCache(CompiledScriptJarsCache { script, configuration ->
                File(scriptsCacheFolder.apply { if (!exists()) mkdir() }, createScriptName(script, configuration) + ".jar")
            })
        }
    })
    refineConfiguration {
        beforeCompiling { ctx ->
            if (forceScriptAnnotation) {
                /**
                 * all scripts must contain this annotation that will be used to identify it.
                 * you can override this configuration from the compiler and remove this option but it is not recommended.
                 */
                val content = ctx.compilationConfiguration.get(ScriptCompilationConfiguration.scriptInfo)!!
                if (!content.isValid()) {
                    return@beforeCompiling ResultWithDiagnostics.Failure(
                        ScriptDiagnostic(
                            "A script must have a @file:Script annotation",
                            ScriptDiagnostic.Severity.FATAL
                        )
                    )
                }
            }

            ResultWithDiagnostics.Success(ctx.compilationConfiguration)
        }
    }
})