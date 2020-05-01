package me.devnatan.kiobs.script.evaluator

import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.scriptsInstancesSharing

/**
 * script instances sharing must be false since we do not know how the scripts will be used
 * sharing instances could result in several conflicts,
 * only scripts that depend on one another can import it
 */
object ScriptEvaluatorConfiguration : ScriptEvaluationConfiguration({
    scriptsInstancesSharing(false)
})