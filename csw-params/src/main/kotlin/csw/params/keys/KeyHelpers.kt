package csw.params.keys

import csw.params.commands.HasParms

object KeyHelpers {

    internal fun <T> aencode(s: Array<T>): Array<String> = s.map { it.toString() }.toTypedArray()

    internal fun Array<String>.toDoubleArray() = this.map { it.toDouble() }.toDoubleArray()

    inline fun <reified T>getStored(item: IsKey, target: HasParms): T? =
        target.nget(item.name).let { if (it is T) it else null }

    fun FloatArray.toDoubleArray(): DoubleArray = map { it.toString().toDouble() }.toDoubleArray()
    fun ShortArray.toLongArray(): LongArray = map { it.toLong() }.toLongArray()
    fun IntArray.toLongArray(): LongArray = map { it.toLong() }.toLongArray()

    fun DoubleArray.toFloatArray(): FloatArray = map { it.toFloat() }.toFloatArray()

    fun LongArray.toIntArray(): IntArray = map { it.toInt() }.toIntArray()
    fun LongArray.toShortArray(): ShortArray = map { it.toShort() }.toShortArray()
    fun LongArray.toByteArray(): ByteArray = map { it.toByte() }.toByteArray()
}