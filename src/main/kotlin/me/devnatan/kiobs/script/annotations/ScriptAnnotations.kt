package me.devnatan.kiobs.script.annotations

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Script(val name: String)