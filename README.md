# kiobs.script
Kotlin has some internal support for scripts even though it is still in the experimental phase it is already used in large projects like [kotlinx.html](https://github.com/Kotlin/kotlinx.html) for example.\
<br>
Classes, functions and interiors of methods are slightly documented, this will make it easier if you want to use it in your project and something goes wrong and you will explore the code knowing what happens in each place.\
<br>
To start loading your scripts it is very simple just follow the summary step by step.

## Summary
* [Script host](#script-host)
* Loading scripts
* Compilation
* Evaluation
* Composing a script
  * Annotations
  * Default imports
  * Classloader injection
  * Dependencies
  * Instance and internal attributes
* Results and errors
* How to contribute
* License

## Script Host
Host script is the code base for compiling and evaluating scripts, it contains the method of [*`compile`*](https://github.com/DevNatan/kiobs.script/blob/master/src/main/kotlin/me/devnatan/kiobs/script/host/ScriptHost.kt#L23) and [*`eval`*](https://github.com/DevNatan/kiobs.script/blob/master/src/main/kotlin/me/devnatan/kiobs/script/host/ScriptHost.kt#L23).\
The project was built thinking about other projects that could use it soon you can extend this [ScriptHost](https://github.com/DevNatan/kiobs.script/blob/master/src/main/kotlin/me/devnatan/kiobs/script/host/ScriptHost.kt) and make your own system to compile and evaluate scripts.
```kotlin
val scriptHost = ScriptHost(compiler, evaluator)
```
To create a ScriptHost it is necessary that you pass a compiler and an evaluator to perform your tasks.\
If you don't have your own ScriptHost, you can use the one provided by us: [JvmScriptHost](https://github.com/DevNatan/kiobs.script/blob/master/src/main/kotlin/me/devnatan/kiobs/script/host/ScriptHost.kt#L34).
