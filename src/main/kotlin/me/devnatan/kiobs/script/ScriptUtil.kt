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

fun createScriptName(script: SourceCode, configuration: ScriptCompilationConfiguration): String {
    val digest = MessageDigest.getInstance("MD5").apply { update(script.text.toByteArray()) }
    configuration.notTransientData.entries.sortedBy { it.key.name }
        .forEach {
            digest.update(it.key.name.toByteArray())
            digest.update(it.value.toString().toByteArray())
        }
    return digest.digest().toHexString()
}

fun resolveScriptClasspath(classLoader: ClassLoader): List<File> {
    fun URL.toFileOrNull() = try {
        File(toURI().schemeSpecificPart)
    } catch (e: URISyntaxException) {
        if (protocol != "file") null
        else File(file)
    }

    return (classLoader as? URLClassLoader)?.urLs?.mapNotNull { url ->
        url.toFileOrNull()
    } ?: emptyList()
}

fun mapScriptErrorDiagnostics(diagnostics: List<ScriptDiagnostic>) = diagnostics.filter {
    it.severity.ordinal <= 2
}