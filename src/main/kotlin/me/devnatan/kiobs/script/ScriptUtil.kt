package me.devnatan.kiobs.script

import org.jetbrains.kotlin.daemon.common.toHexString
import java.io.File
import java.net.URLClassLoader
import java.security.MessageDigest
import kotlin.script.experimental.api.ScriptCompilationConfiguration
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

fun resolveClasspath(classLoader: ClassLoader): List<File> {
    return (classLoader as URLClassLoader).urLs.mapNotNull { url ->
        runCatching { File(url.toURI().schemeSpecificPart) }.getOrNull()
    }
}