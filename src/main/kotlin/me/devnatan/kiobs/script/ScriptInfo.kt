package me.devnatan.kiobs.script

open class ScriptInfo : java.io.Serializable {

    class Empty : ScriptInfo()

    lateinit var name: String

    fun isValid(): Boolean {
        return ::name.isInitialized
    }

    override fun toString(): String {
        if (!isValid())
            throw IllegalStateException("Not valid")

        return "ScriptInfo (name=$name)"
    }

}