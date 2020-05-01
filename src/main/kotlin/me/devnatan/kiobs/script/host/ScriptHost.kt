package me.devnatan.kiobs.script.host

import me.devnatan.kiobs.script.CompiledScript
import me.devnatan.kiobs.script.KotlinCompiledScript
import kotlin.script.experimental.api.*

interface ScriptHost {

    val compiler: ScriptCompiler

    val evaluator: ScriptEvaluator

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
    override val compiler: ScriptCompiler,
    override val evaluator: ScriptEvaluator,
    protected val compilerConfiguration: ScriptCompilationConfiguration,
    protected val evaluatorConfiguration: ScriptEvaluationConfiguration
) : ScriptHost {

    override suspend fun compile(script: SourceCode): ResultWithDiagnostics<KotlinCompiledScript> {
        return compiler(script, compilerConfiguration) as ResultWithDiagnostics<KotlinCompiledScript>
    }

    override suspend fun eval(script: KotlinCompiledScript): ResultWithDiagnostics<EvaluationResult> {
        return evaluator(script, evaluatorConfiguration)
    }

}