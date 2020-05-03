package me.devnatan.kiobs.script

open class ScriptInfo(
    val name: String
) : java.io.Serializable {

    object Empty : ScriptInfo("<empty>") {
        override fun isValid(): Boolean = false
    }

    open fun isValid(): Boolean {
        return true
    }

    override fun toString(): String {
        if (!isValid())
            throw IllegalStateException("Not valid")

        return "ScriptInfo (name=$name)"
    }

}