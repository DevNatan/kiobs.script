# kiobs.script
Kotlin has some internal support for scripts even though it is still in the experimental phase it is already used in large projects like [kotlinx.html](https://github.com/Kotlin/kotlinx.html) for example.\
<br>
Classes, functions and interiors of methods are slightly documented, this will make it easier if you want to use it in your project and something goes wrong and you will explore the code knowing what happens in each place.\
<br>
To start loading your scripts it is very simple just follow the summary step by step.

## Summary
* [Script host](#script-host)
* [Loading scripts](#loading-scripts)
* [Compilation](#compilation)
* [Evaluation](#evaluation)
* Composing a script
  * Annotations
  * Default imports
  * Classloader injection
  * Dependencies
  * Instance and internal attributes
* [Errors](#errors)
* [Contributing](#contributing)
* [License](#license)

## Script Host
Host script is the code base for compiling and evaluating scripts, it contains the method of [*`compile`*](https://github.com/DevNatan/kiobs.script/blob/master/src/main/kotlin/me/devnatan/kiobs/script/host/ScriptHost.kt#L23) and [*`eval`*](https://github.com/DevNatan/kiobs.script/blob/master/src/main/kotlin/me/devnatan/kiobs/script/host/ScriptHost.kt#L23).\
The project was built thinking about other projects that could use it soon you can extend this [ScriptHost](https://github.com/DevNatan/kiobs.script/blob/master/src/main/kotlin/me/devnatan/kiobs/script/host/ScriptHost.kt) and make your own system to compile and evaluate scripts.
```kotlin
val scriptsHost = ScriptHost(cache, compiler, evaluator, compilerConfig, evaluatorConfig)
```
To create a ScriptHost it is necessary that you pass a compiler and an evaluator to perform your tasks.\
If you don't have your own ScriptHost, you can use the one provided by us: [JvmScriptHost](https://github.com/DevNatan/kiobs.script/blob/master/src/main/kotlin/me/devnatan/kiobs/script/host/ScriptHost.kt#L34).
An example of a pre-defined configuration:
```kotlin
import kotlin.script.experimental.jvmhost.JvmScriptCompiler
import kotlin.script.experimental.jvm.BasicJvmScriptEvaluator
import me.devnatan.kiobs.script.compiler.DefaultScriptCompilationConfiguration
import me.devnatan.kiobs.script.evaluator.DefaultScriptEvaluationConfiguration

val scriptsHost = ScriptHost(
    File("cache"),
    JvmScriptCompiler(),
    BasicJvmScriptEvaluator(),
    DefaultScriptCompilationConfiguration,
    DefaultScriptEvaluationConfiguration
)
```
We give you standard compiler and evaluator configurations, they already do what you need, but you can create your own if you want.

## Loading Scripts
Our ScriptHost already gives us the methods of compiling and evaluating, but they can well be misinterpreted by those who do not have full knowledge of the code.\
For this we created a class much easier and easier to understand, come on, define ScriptLoader:
```kotlin
val scriptsLoader = Scriptloader(scriptsHost)
```
ScriptLoader gives you several useful methods to compile, evaluate, or both directly:
* [`loadScript(File)`](https://github.com/DevNatan/kiobs.script/blob/develop/src/main/kotlin/me/devnatan/kiobs/script/ScriptLoader.kt#L17) - compiles and evaluates a script provided by a File
* [`loadScript(text)`](https://github.com/DevNatan/kiobs.script/blob/develop/src/main/kotlin/me/devnatan/kiobs/script/ScriptLoader.kt#L20) - compiles and evaluates a script provided by a string
* [`compileScript(file)`](https://github.com/DevNatan/kiobs.script/blob/develop/src/main/kotlin/me/devnatan/kiobs/script/ScriptLoader.kt#L30) - compiles a script provided by a File
* [`compileScript(text)`](https://github.com/DevNatan/kiobs.script/blob/develop/src/main/kotlin/me/devnatan/kiobs/script/ScriptLoader.kt#L30) - compiles a script provided by a string
* [`evalScript(script)`](https://github.com/DevNatan/kiobs.script/blob/develop/src/main/kotlin/me/devnatan/kiobs/script/ScriptLoader.kt#L30) - evaluates **compiled** a script

## Compilation
The *compilation* stage is where the script code is interpreted (this stage is preceded by *parsing* which is where we check if the script is really valid and recognizable).
All build configurations are changeable, can be extended by changing them along with the defaults later.\
If you are thinking of **adding default imports** or **providing a classpath** for the script that does not yet exist in it, now is the time.
<br><br>
Let's go back to our ScriptHost and modify it.
```kotlin
import me.devnatan.kiobs.script.host.withUpdatedConfiguration
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.dependenciesFromClassLoader

withUpdatedConfiguration {
    jvm {
        dependenciesFromClassLoader(myClassLoader)
    }
}
```
See this method *`dependenciesFromClassLoader`* it will be responsible for automatically injecting your ClassLoader in all your scripts, loaded from the modified configuration.\
This means that from now on the scripts will have access to imports from that class loader.
<br><br>
You can explore other configuration extension options such as `refineConfiguration` which will refine the configuration and you can even access things before the *compilation* or *parsing* stage starts.

## Evaluation
The evaluation stage in summary is what runs the script.\
For it to start it is necessary that the **script has to be compiled to be executed before**, obviously.\
<br>
The evaluation of the script is the fastest stage of all that happens, so you can run it as many times as necessary.\
Unlike the build stage, if it is not cached it may take a while to complete.\
<br>
After evaluating a script you can obtain the evaluation results or the [ScriptEvaluationError](https://github.com/DevNatan/kiobs.script/blob/develop/src/main/kotlin/me/devnatan/kiobs/script/ScriptException.kt#L22) of the same.
```kotlin
import me.devnatan.kiobs.script.ScriptException

try {
    scriptLoader.loadScript(File("some-script.kts"))
} catch (e: ScriptException) {
    e.diagnostics.forEach { report ->
        report.exception?.printStackTrace() ?: println(report.toString())
    }
}
```

## Errors
Each singular execution of parsing, compiling or evaluating the script can have errors (failures) or returns.\
To make it easier to identify errors and what stage they happen, we created a [ScriptException](https://github.com/DevNatan/kiobs.script/blob/develop/src/main/kotlin/me/devnatan/kiobs/script/ScriptException.kt).
* For *parsing* or *compilation* errors: [ScriptCompilationError](https://github.com/DevNatan/kiobs.script/blob/develop/src/main/kotlin/me/devnatan/kiobs/script/ScriptException.kt#L15).
* For *evaluation* errors: [ScriptEvaluationError](https://github.com/DevNatan/kiobs.script/blob/develop/src/main/kotlin/me/devnatan/kiobs/script/ScriptException.kt#L15).

In all of them there are *`ScriptException#diagnostics`* that can be used to define where the error is up to which line of code, where it occurs.

## Contributing
You can contribute to the project, but first you should know a few things that we have for you. See [how to contribute](https://github.com/DevNatan/kiobs.script/blob/develop/CONTRIBUTING.md).

## License
Kiobs Script is distributed under the [MIT License](https://github.com/DevNatan/kiobs.script/blob/develop/LICENSE).