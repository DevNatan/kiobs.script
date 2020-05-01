package me.devnatan.kiobs.script

/**
 * Thrown when there is an error in a script.
 */
open class ScriptException : Exception()

/**
 * Thrown when there is an error before, during or after compiling a script.
 */
open class ScriptCompilationError : ScriptException()

/**
 * Thrown when any source tries to evaluate a script and an error occurs.
 */
open class ScriptEvaluationError : ScriptException()

/**
 * Launched when trying to evaluate a script that has not yet been compiled.
 * Scripts need to be compiled before they can be evaluated.
 */
class NotCompiledScriptException : ScriptEvaluationError()