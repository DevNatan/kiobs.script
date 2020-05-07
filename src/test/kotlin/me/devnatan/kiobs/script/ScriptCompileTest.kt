package me.devnatan.kiobs.script

import kotlinx.coroutines.runBlocking
import me.devnatan.kiobs.script.host.createJvmScriptHost
import me.devnatan.kiobs.script.loader.DefaultScriptLoader
import org.junit.Test
import java.io.File
import kotlin.reflect.jvm.jvmName
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ScriptCompileTest {

    private val resources   = File("src/test/resources")
    private val loader      = DefaultScriptLoader(createJvmScriptHost(File(resources, "cache")))

    @Test
    fun `should compile script from file`() = test {
        val name = "My file script"
        val script = loader.compileScript(File(resources, "scripts/script.kts"))

        assertTrue(script.info.isValid())
        assertEquals(name, script.info.name)
    }

    @Test
    fun `shouldn't compile script without Script header`() {
        assertFailsWith(ScriptCompilationError::class) {
            runBlocking {
                loader.compileScript(File(resources, "scripts/no-script.kts"))
            }
        }
    }

    @Test
    fun `should compile script from string`() = test {
        val name = "My testing script"
        val script = loader.compileScript(
            """
                @file:Script("$name")
                
                import me.devnatan.kiobs.script.annotations.Script
            """.trimIndent()
        )

        assertTrue(script.info.isValid())
        assertEquals(name, script.info.name)
    }


    private inline fun test(
        bypass: Boolean = false,
        crossinline block: suspend () -> Unit
    ) {
        runBlocking {
            if (bypass) {
                block()
                return@runBlocking
            }

            try {
                block()
            } catch (e: ScriptException) {
                e.diagnostics.filterIsSeverity(ScriptDiagnostic.Severity.WARNING).forEach {
                    System.err.println("[${e::class.simpleName ?: e::class.jvmName}] $it")
                }
            }
        }
    }
}