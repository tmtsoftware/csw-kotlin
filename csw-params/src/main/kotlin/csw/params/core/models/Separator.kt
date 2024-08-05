package csw.params.core.models

object Separator {
    val Hyphen: String = "-"

    fun hyphenate(vararg ins: String): String = ins.joinToString(Hyphen)
}
