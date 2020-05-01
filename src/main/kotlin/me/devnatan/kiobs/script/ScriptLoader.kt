package me.devnatan.kiobs.script

import me.devnatan.kiobs.script.host.ScriptHost
import java.io.File
import kotlin.script.experimental.api.valueOr
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.host.toScriptSource

open class ScriptLoader(
    val scriptsFolder: File,
    val host: ScriptHost
) {

    val scripts: MutableList<LoadedScript> = arrayListOf()

    suspend fun loadScripts() {
        if (!scriptsFolder.exists())
            scriptsFolder.mkdirs()

        for(script in scriptsFolder.listFiles { file ->
            file.isFile && file.extension == Script.FILE_EXTENSION
        }) loadScript(script)
    }

    @Throws(ScriptException::class)
    suspend fun loadScript(file: File): Boolean {
        return scripts.add(LoadedScript(compileScript(file)).apply { evalScript(this) })
    }

    @Throws(ScriptCompilationError::class)
    suspend fun compileScript(file: File): CompiledScript {
        val source = file.toScriptSource()
        val compile = host.compile(source).valueOr {
            throw ScriptCompilationError(mapScriptErrorDiagnostics(it.reports))
        }
        return CompiledScript(file, source as FileScriptSource, compile)
    }

    @Throws(ScriptEvaluationError::class)
    suspend fun evalScript(file: File): CompiledScript {
        return scripts.find { it.file == file }?.also {
            evalScript(it)
        } ?: throw NotCompiledScriptException()
    }

    @Throws(ScriptEvaluationError::class)
    suspend fun evalScript(script: CompiledScript) = script.apply {
        evaluation = host.eval(script.script).valueOr {
            throw ScriptEvaluationError(mapScriptErrorDiagnostics(it.reports))
        }
    }

}