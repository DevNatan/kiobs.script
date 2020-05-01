package me.devnatan.kiobs.script

import me.devnatan.kiobs.script.compiler.ScriptCompilerConfiguration
import me.devnatan.kiobs.script.evaluator.ScriptEvaluatorConfiguration
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ScriptCompilationConfigurationKeys
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.util.PropertiesCollection

typealias KotlinCompiledScript = kotlin.script.experimental.api.CompiledScript<Script>

@KotlinScript(
    displayName = "Kiobs Script",
    fileExtension = Script.FILE_EXTENSION,
    compilationConfiguration = ScriptCompilerConfiguration::class,
    evaluationConfiguration = ScriptEvaluatorConfiguration::class
)
abstract class Script {

    companion object {
        /**
         * The extension of the scripts, all scripts must have this extension and it is not changeable.
         */
        const val FILE_EXTENSION = "kts"
    }

}

/**
 * Represents a script already compiled containing the result of the compilation.
 * The result of the evaluation is also saved in it but started late.
 * Attempting to obtain the evaluation result without first evaluating
 * it will result in [UninitializedPropertyAccessException] by Kotlin, because the result of the evaluation is a `lateinit` property.
 * You can check for an evaluation result first using the [isEvaluated] method.
 */
open class CompiledScript(
    val file: File,
    val source: FileScriptSource,
    val script: KotlinCompiledScript
) : Script() {

    lateinit var evaluation: EvaluationResult

    fun isEvaluated(): Boolean {
        return ::evaluation.isInitialized
    }
}

/**
 * It represents a compiled script in use, an enabled script.
 * @see CompiledScript
 */
open class LoadedScript(
    delegate: CompiledScript
) : CompiledScript(delegate.file, delegate.source, delegate.script)

val ScriptCompilationConfigurationKeys.scriptInfo by PropertiesCollection.key<ScriptInfo>(ScriptInfo.Empty())