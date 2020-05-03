@file:JvmMultifileClass
@file:JvmName("JvmScriptLoader")
package me.devnatan.kiobs.script

import me.devnatan.kiobs.script.host.ScriptHost
import java.io.File
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.valueOr
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.host.StringScriptSource
import kotlin.script.experimental.host.toScriptSource

open class ScriptLoader(val host: ScriptHost) {

    @Throws(ScriptException::class)
    open suspend fun loadScript(file: File) = evalScript(compileScript(file))

    @Throws(ScriptException::class)
    open suspend fun loadScript(text: String) = evalScript(compileScript(text))

    @Throws(ScriptCompilationError::class)
    open suspend fun compileScript(source: SourceCode): KotlinCompiledScript {
        return host.compile(source).valueOr {
            throw ScriptCompilationError(mapScriptErrorDiagnostics(it.reports))
        }
    }

    @Throws(ScriptCompilationError::class)
    open suspend fun compileScript(file: File): CompiledScript {
        val source = file.toScriptSource() as FileScriptSource
        val compile = compileScript(source)
        return FileBasedCompiledScript(file, source, compile, compile.compilationConfiguration[ScriptCompilationConfiguration.scriptInfo]!!)
    }

    @Throws(ScriptCompilationError::class)
    open suspend fun compileScript(text: String): CompiledScript {
        val source = text.toScriptSource() as StringScriptSource
        val compile = compileScript(source)
        return StringBasedCompiledScript(text, source, compile, compile.compilationConfiguration[ScriptCompilationConfiguration.scriptInfo]!!)
    }

    @Throws(ScriptEvaluationError::class)
    open suspend fun evalScript(script: CompiledScript) = script.apply {
        evaluation = host.eval(script.script).valueOr {
            throw ScriptEvaluationError(mapScriptErrorDiagnostics(it.reports))
        }
    }

}