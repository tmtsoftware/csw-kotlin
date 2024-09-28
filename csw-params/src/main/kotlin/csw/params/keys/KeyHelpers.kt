package csw.params.keys

import arrow.core.None
import arrow.core.Option
import csw.params.commands.HasParms
import csw.params.core.models.ArrayData

object KeyHelpers {
    private const val DELIM: String = ","
    private const val AR_DELIM: String = ":"

    internal fun <T> aencode(s: Array<T>): Array<String> = s.map { it.toString() }.toTypedArray()

    //internal fun <T> lencode(s: List<T>): Array<String> = s.map { it.toString() }.toTypedArray()

    //internal fun sencode(s: Number): Array<String> = aencode(arrayOf(s))

    //internal fun decodeValue(s: Array<String>): DoubleArray = s.map{ it.toDouble() }.toDoubleArray()

    internal fun Array<String>.toDoubleArray() = this.map { it.toDouble() }.toDoubleArray()

    internal fun asStrings(s: String): Array<String> = s.split(DELIM).toTypedArray()

    internal fun arrayencode(arrays: List<ArrayData>): String {
        var str = arrays.map { it.data.joinToString(",") }.joinToString(":")
        println("STR: $str")
        return str
    }

    inline fun <reified T>getStored(item: IsKey, target: HasParms): T? {
        val s: HasKey? = target.nget(item.name)
        return if (s is T) s as T else null
    }

    fun <T> aashow(a: T): String {
        return when (a) {
            is DoubleArray -> a.joinToString(DELIM, "[", "[")
            is Array<*> -> a.joinToString(DELIM, "[", "]")
            else -> throw IllegalArgumentException("Fuck")
        }
    }


    fun <T> ashow(a: Array<T>) = a.joinToString(DELIM, "[", "[")

    internal fun decodeOneArray(s: List<String>): ArrayData {
        val y = s.map { println(it); it.toDouble() }.toDoubleArray()
        return ArrayData(y)
    }

    internal fun decodeArrayValue(s: String): Array<ArrayData> { //Array<ArrayData> {
        var arrs = s.split(AR_DELIM)
        println("arrs size: ${arrs.size}")

        val x = arrs.map { it.split(DELIM) }
        println("x: ${x}")
        val yyy = x.map { decodeOneArray(it) }

        return yyy.toTypedArray()
    }



    //internal fun exists(name: Key, target: HasParms): Boolean = target.nget(name) != null

}