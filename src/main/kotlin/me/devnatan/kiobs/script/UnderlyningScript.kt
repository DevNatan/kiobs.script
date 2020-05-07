@file:JvmMultifileClass
@file:JvmName("ScriptKeys")
package me.devnatan.kiobs.script

import me.devnatan.kiobs.script.compiler.DefaultScriptCompilationConfiguration
import me.devnatan.kiobs.script.evaluator.DefaultScriptEvaluationConfiguration
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ScriptCompilationConfigurationKeys
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.host.StringScriptSource
import kotlin.script.experimental.util.PropertiesCollection
import me.devnatan.kiobs.script.annotations.Script as ScriptAnnotation

/**
 * The current instance of the script,
 * which can be accessed directly within the script.
 */
interface UnderlyningScript {

    /**
     * The information contained in the initial annotation [ScriptAnnotation] script.
     * This information is not changeable and is defined during the compilation stage.
     */
    val info: ScriptInfo

}

@KotlinScript("Kiobs Script",
    compilationConfiguration = DefaultScriptCompilationConfiguration::class,
    evaluationConfiguration = DefaultScriptEvaluationConfiguration::class
)
abstract class KiobsScript(override val info: ScriptInfo) : UnderlyningScript {

    override fun toString(): String {
        return "Script (info=$info)"
    }

}

typealias KotlinCompiledScript = kotlin.script.experimental.api.CompiledScript<KiobsScript>

/**
 * Represents a script already compiled containing the result of the compilation.
 * The result of the evaluation is also saved in it but started late.
 * Attempting to obtain the evaluation result without first evaluating
 * it will result in [UninitializedPropertyAccessException] by Kotlin, because the result of the evaluation is a `lateinit` property.
 * You can check for an evaluation result first using the [isEvaluated] method.
 */
interface CompiledScript : UnderlyningScript {

    /**
     * The source code of the script.
     * @see FileBasedCompiledScript
     * @see StringBasedCompiledScript
     */
    val source: SourceCode

    /**
     * The final result of the compilation given by the compiler.
     */
    val script: KotlinCompiledScript

    /**
     * The final result of the evaluation given by the evaluator.
     */
    var evaluation: EvaluationResult

    /**
     * If the script has already been evaluated,
     * it may have been compiled but not evaluated, this may occur.
     */
    fun isEvaluated(): Boolean

}

open class BaseCompiledScript(
    override val source: SourceCode,
    override val script: KotlinCompiledScript,
    override val info: ScriptInfo
) : CompiledScript {

    final override lateinit var evaluation: EvaluationResult

    final override fun isEvaluated(): Boolean {
        return ::evaluation.isInitialized
    }

}

/**
 * Represents a script compiled using a file.
 * @property file the script file
 */
open class FileBasedCompiledScript(
    val file: File,
    source: FileScriptSource,
    script: KotlinCompiledScript,
    info: ScriptInfo
) : BaseCompiledScript(source, script, info)

/**
 * Represents a script compiled using text directly.
 * @property text the script itself
 */
open class StringBasedCompiledScript(
    val text: String,
    source: StringScriptSource,
    script: KotlinCompiledScript,
    info: ScriptInfo
) : BaseCompiledScript(source, script, info)

val ScriptCompilationConfigurationKeys.scriptInfo by PropertiesCollection.key<ScriptInfo>(ScriptInfo.Empty)