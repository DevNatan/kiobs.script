@file:JvmMultifileClass
@file:JvmName("ScriptHosts")
package me.devnatan.kiobs.script.host

import me.devnatan.kiobs.script.CompiledScript
import me.devnatan.kiobs.script.KiobsScript
import me.devnatan.kiobs.script.KotlinCompiledScript
import me.devnatan.kiobs.script.compiler.DefaultScriptCompilationConfiguration
import me.devnatan.kiobs.script.createScriptName
import me.devnatan.kiobs.script.evaluator.DefaultScriptEvaluationConfiguration
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.jvm.BasicJvmScriptEvaluator
import kotlin.script.experimental.jvm.compilationCache
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.CompiledScriptJarsCache
import kotlin.script.experimental.jvmhost.JvmScriptCompiler
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvmhost.createJvmEvaluationConfigurationFromTemplate

interface ScriptHost {

    /**
     * The script compiler that will be used.
     */
    val compiler: ScriptCompiler

    /**
     * The script evaluator that will be used.
     */
    val evaluator: ScriptEvaluator

    /**
     * Settings that will be used when compiling.
     * @see compiler
     */
    var compilerConfiguration: ScriptCompilationConfiguration

    /**
     * The settings that will be used when evaluating.
     * @see evaluator
     */
    var evaluatorConfiguration: ScriptEvaluationConfiguration

    /**
     * Compiles a script returning the diagnostics with the result of the compilation.
     * This method does not automatically evaluate the script,
     * it just leaves it precompiled and takes it from the cache if necessary.
     *
     * Leaving pre-compiled scripts for later evaluation is a
     * good practice so that you don't have to recompile it every time.
     * As this is a suspend method it does not block the current thread,
     * it will depend on how it is inserted in the current coroutine context.
     */
    suspend fun compile(script: SourceCode): ResultWithDiagnostics<KotlinCompiledScript>

    /**
     * Evaluates a script already precompiled by the [compile] method.
     * This method returns the result of the evaluation but it can also be
     * accessed later ([CompiledScript.evaluation]) on the object of the script in question.
     */
    suspend fun eval(script: KotlinCompiledScript): ResultWithDiagnostics<EvaluationResult>

}

open class JvmScriptHost(
    scriptsCacheDir: File,
    override val compiler: ScriptCompiler,
    override val evaluator: ScriptEvaluator,
    override var compilerConfiguration: ScriptCompilationConfiguration,
    override var evaluatorConfiguration: ScriptEvaluationConfiguration
) : ScriptHost {

    init {
        withUpdatedConfiguration {
            hostConfiguration(ScriptingHostConfiguration {
                jvm {
                    compilationCache(CompiledScriptJarsCache { script, configuration ->
                        File(scriptsCacheDir.apply { if (!exists()) mkdir() }, createScriptName(script, configuration) + ".jar")
                    })
                }
            })
        }
    }

    override suspend fun compile(script: SourceCode): ResultWithDiagnostics<KotlinCompiledScript> {
        return compiler(script, ScriptCompilationConfiguration(createJvmCompilationConfigurationFromTemplate<KiobsScript>())) as ResultWithDiagnostics<KotlinCompiledScript>
    }

    override suspend fun eval(script: KotlinCompiledScript): ResultWithDiagnostics<EvaluationResult> {
        return evaluator(script, ScriptEvaluationConfiguration(createJvmEvaluationConfigurationFromTemplate<KiobsScript>()))
    }

}

/**
 * Creates a [ScriptHost] for the JVM with the default compilation and evaluation settings.
 * @param cache script cache directory
 * @return [JvmScriptHost]
 */
fun createJvmScriptHost(cache: File): ScriptHost = JvmScriptHost(
    cache,
    JvmScriptCompiler(),
    BasicJvmScriptEvaluator(),
    DefaultScriptCompilationConfiguration,
    DefaultScriptEvaluationConfiguration
)

/**
 * Apply new settings to settings already defined for the current host.
 * The new settings will be merged with the existing ones, some of which can be overwritten.
 * @param configuration the new configuration
 */
fun ScriptHost.withUpdatedConfiguration(
    configuration: ScriptCompilationConfiguration.Builder.() -> Unit
) = apply { compilerConfiguration = compilerConfiguration.with(configuration) }