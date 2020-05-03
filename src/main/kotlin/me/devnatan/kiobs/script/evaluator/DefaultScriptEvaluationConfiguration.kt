package me.devnatan.kiobs.script.evaluator

import me.devnatan.kiobs.script.KiobsScript
import me.devnatan.kiobs.script.scriptInfo
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm

/**
 * @property scriptsInstancesSharing property must be false since we do not know how
 * the scripts will be used sharing instances could result in several conflicts,
 * only scripts that depend on one another can import it.
 */
object DefaultScriptEvaluationConfiguration : ScriptEvaluationConfiguration({
    refineConfigurationBeforeEvaluate { ctx ->
        // we can define the script constructor args since
        // if we got here this is why the compilation went well, so we get the script
        // information provided by the compilation and resolution stage.
        val scriptInfo = ctx.compiledScript.compilationConfiguration[ScriptCompilationConfiguration.scriptInfo]!!

        ctx.evaluationConfiguration.with {
            constructorArgs(scriptInfo)
        }.asSuccess()
    }

    scriptsInstancesSharing(false)
    jvm {
        baseClassLoader(KiobsScript::class.java.classLoader)
    }
})