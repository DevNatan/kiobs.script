package me.devnatan.kiobs.script.loader

import me.devnatan.kiobs.script.*
import me.devnatan.kiobs.script.host.ScriptHost
import java.io.File
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.valueOr
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.host.StringScriptSource
import kotlin.script.experimental.host.toScriptSource

interface ScriptLoader {

    /**
     * Loads a script from a .kts file.
     * This method depends entirely on the implementation.
     */
    suspend fun loadScript(file: File): CompiledScript

    /**
     * Loads a script from a plain string.
     * This method depends entirely on the implementation.
     */
    suspend fun loadScript(text: String): CompiledScript

    /**
     * Compiles a script from a .kts file.
     * @param file the script
     * @throws ScriptCompilationError if an error occurs during the compilation
     */
    @Throws(ScriptCompilationError::class)
    suspend fun compileScript(file: File): CompiledScript

    /**
     * Compiles a script from a plain string.
     * @param text the script
     * @throws ScriptCompilationError if an error occurs during the compilation
     */
    @Throws(ScriptCompilationError::class)
    suspend fun compileScript(text: String): CompiledScript

    /**
     * Evaluates a precompiled script.
     * @see compileScript
     * @throws ScriptEvaluationError if an error occurs during the evaluation
     */
    @Throws(ScriptEvaluationError::class)
    suspend fun evalScript(script: CompiledScript): CompiledScript

}

class DefaultScriptLoader(val host: ScriptHost) : ScriptLoader {

    override suspend fun loadScript(file: File): CompiledScript {
        return evalScript(compileScript(file))
    }

    override suspend fun loadScript(text: String): CompiledScript {
        return evalScript(compileScript(text))
    }

    private suspend fun compileScript(source: SourceCode): KotlinCompiledScript {
        return host.compile(source).valueOr {
            throw ScriptCompilationError(it.reports)
        }
    }

    override suspend fun compileScript(file: File): CompiledScript {
        val source = file.toScriptSource() as FileScriptSource
        val compile = compileScript(source)
        return FileBasedCompiledScript(file, source, compile, compile.compilationConfiguration[ScriptCompilationConfiguration.scriptInfo]!!)
    }

    override suspend fun compileScript(text: String): CompiledScript {
        val source = text.toScriptSource() as StringScriptSource
        val compile = compileScript(source)
        return StringBasedCompiledScript(text, source, compile, compile.compilationConfiguration[ScriptCompilationConfiguration.scriptInfo]!!)
    }

    override suspend fun evalScript(script: CompiledScript) = script.apply {
        evaluation = host.eval(script.script).valueOr {
            throw ScriptEvaluationError(it.reports)
        }
    }

}