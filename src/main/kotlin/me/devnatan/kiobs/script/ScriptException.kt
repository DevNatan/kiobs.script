package me.devnatan.kiobs.script

import kotlin.script.experimental.api.ScriptDiagnostic

/**
 * Thrown when there is an error in a script.
 */
open class ScriptException(
    val diagnostics: List<ScriptDiagnostic> = emptyList()
) : Exception()

/**
 * Thrown when there is an error before, during or after compiling a script.
 */
open class ScriptCompilationError(
    diagnostics: List<ScriptDiagnostic>
) : ScriptException(diagnostics)

/**
 * Thrown when any source tries to evaluate a script and an error occurs.
 */
open class ScriptEvaluationError(
    diagnostics: List<ScriptDiagnostic>
) : ScriptException(diagnostics)

/**
 * Launched when trying to evaluate a script that has not yet been compiled.
 * Scripts need to be compiled before they can be evaluated.
 */
class NotCompiledScriptException : ScriptEvaluationError(emptyList())