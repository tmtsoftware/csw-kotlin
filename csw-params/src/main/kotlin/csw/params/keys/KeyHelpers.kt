package csw.params.keys

import arrow.core.None
import arrow.core.Option
import csw.params.commands.HasParms
import csw.params.core.models.ArrayData

object KeyHelpers {
    private const val DELIM: String = ","
    private const val AR_DELIM: String = ":"

    internal fun <T> aencode(s: Array<T>): String = s.joinToString(DELIM)

    internal fun <T> lencode(s: List<T>): String = s.joinToString(",")

    internal fun sencode(s: Number): String = aencode(arrayOf(s))

    internal fun decodeValue(s: String): Array<Double> = asStrings(s).map{ it.toDouble() }.toTypedArray()

    internal fun asStrings(s: String): Array<String> = s.split(DELIM).toTypedArray()

    //internal fun testBoolean(s: String): Boolean = if (s == "f") false else true

    // internal fun asBoolean(a: Array<String>): Boolean  = testBoolean(a[0])
    //internal fun asBooleanArray(a: Array<String>): BooleanArray = a.map { testBoolean(it) }.toBooleanArray()

    internal fun arrayencode(arrays: List<ArrayData>): String {
        var str = arrays.map { it.data.joinToString(",") }.joinToString(":")
        println("STR: $str")
        return str
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