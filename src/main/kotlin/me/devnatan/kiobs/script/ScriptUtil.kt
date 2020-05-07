@file:JvmMultifileClass
@file:JvmName("ScriptUtil")
package me.devnatan.kiobs.script

import org.jetbrains.kotlin.daemon.common.toHexString
import java.io.File
import java.net.URISyntaxException
import java.net.URL
import java.net.URLClassLoader
import java.security.MessageDigest
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.SourceCode

/**
 * Creates a unique name from a specific source code and configuration.
 * This code is used to generate names for the script cache files.
 * @param script the source
 * @param configuration compiler settings
 */
fun createScriptName(
    script: SourceCode,
    configuration: ScriptCompilationConfiguration
): String {
    val digest = MessageDigest.getInstance("MD5").apply { update(script.text.toByteArray()) }
    configuration.notTransientData.entries.sortedBy { it.key.name }
        .forEach {
            digest.update(it.key.name.toByteArray())
            digest.update(it.value.toString().toByteArray())
        }
    return digest.digest().toHexString()
}

/**
 * Returns a list of diagnostics prior to the ordinal number of the designed diagnosis.
 * By default, the severity is [ScriptDiagnostic.Severity.WARNING], so only the severities that
 * come before will be displayed, in the case of [ScriptDiagnostic.Severity.ERROR] and [ScriptDiagnostic.Severity.FATAL].
 */
@JvmOverloads
fun Collection<ScriptDiagnostic>.filterIsSeverity(
    severity: ScriptDiagnostic.Severity = ScriptDiagnostic.Severity.WARNING
) = filter { it.severity.ordinal <= severity.ordinal }

/**
 * Convert this [URL] to a [File], or return null if not possible.
 */
fun URL.toFileOrNull() = try {
    File(toURI().schemeSpecificPart)
} catch (e: URISyntaxException) {
    if (protocol != "file") null
    else File(file)
}

/**
 * Returns a list of all files available in this [ClassLoader].
 * If the [ClassLoader] is not an [URLClassLoader] it returns an [EmptyList].
 */
fun ClassLoader.mapIsFile(): List<File> {
    return (this as? URLClassLoader)?.urLs?.mapNotNull { url ->
        url.toFileOrNull()
    } ?: emptyList()
}