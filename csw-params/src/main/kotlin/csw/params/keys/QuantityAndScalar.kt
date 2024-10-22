package csw.params.keys

data class Scalar(val value: Array<String>) {

    override fun toString(): String = "<Scalar ${value}>"

    val length = value.size

    fun asInt(): Int = QandSHelpers.asInt(value, 0)
    fun asIntArray(): IntArray = QandSHelpers.asIntArray(value)

    fun asLong(): Long = QandSHelpers.asLong(value, 0)
    fun asLongArray(): LongArray = QandSHelpers.asLongArray(value)

    fun asDouble(): Double = QandSHelpers.asDouble(value, 0)
    fun asDoubleArray(): DoubleArray = QandSHelpers.asDoubleArray(value)

    fun asFloat(): Float = QandSHelpers.asFloat(value, 0)
    fun asFloatArray(): FloatArray = QandSHelpers.asFloatArray(value)

    fun asBoolean(): Boolean = QandSHelpers.asBoolean(value, 0)
    fun asBooleanArray(): BooleanArray = QandSHelpers.asBooleanArray(value)

    fun asString(): String = QandSHelpers.asString(value, 0)
    fun asStringArray(): Array<String> = QandSHelpers.asStringArray(value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scalar
        return other.value.contentDeepEquals(this.value as Array<*>)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    companion object {
        fun create(sstore: Sstore): Scalar {
            return Scalar(sstore.data)
        }
    }
}

data class QuantityX<T>(val value: T, val units: Units = Units.NoUnits) {
    override fun toString(): String = "<Quantity ${KeyHelpers.aashow(value)} ${units.name}>"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuantityX<*>
        if (other.value is Array<*>) {
            return this.units == other.units && other.value.contentDeepEquals(this.value as Array<*>)
        } else {
            // Simple, non-array value
            return this.value == other.value && this.units == other.units
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}


data class Quantity(val svalue: Array<String>, val units: Units = Units.NoUnits) {
    override fun toString(): String = "<Quantity ${svalue.joinToString(separator = ",", "[", "]")} ${units.name}>"

    val length = svalue.size

    fun asInt(): Int = QandSHelpers.asInt(svalue, index = 0)
    fun asIntArray(): IntArray = QandSHelpers.asIntArray(svalue)

    fun asLong(): Long = QandSHelpers.asLong(svalue, index = 0)
    fun asLongArray(): LongArray = QandSHelpers.asLongArray(svalue)

    fun asDouble(): Double = QandSHelpers.asDouble(svalue, index = 0)
    fun asDoubleArray(): DoubleArray = QandSHelpers.asDoubleArray(svalue)

    fun asFloat(): Float = QandSHelpers.asFloat(svalue, index = 0)
    fun asFloatArray(): FloatArray = QandSHelpers.asFloatArray(svalue)

    fun asString(): String = QandSHelpers.asString(svalue, index = 0)
    fun asStringArray(): Array<String> = QandSHelpers.asStringArray(svalue)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quantity
        return (units == other.units) && other.svalue.contentDeepEquals(this.svalue as Array<*>)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

private object QandSHelpers {
    fun toArrayString(a: Array<String>): String = a.joinToString(",", "[", "]")
    fun printArray(a: Array<String>) = println(toArrayString(a))

    fun asInt(a: Array<String>, index: Int): Int = a[Math.min(index, a.size)].toInt()
    fun asIntArray(a: Array<String>): IntArray = a.map { it.toDouble().toInt() }.toIntArray()

    fun asLong(a: Array<String>, index: Int): Long = a[index].toLong()
    fun asLongArray(a: Array<String>): LongArray = a.map { it.toDouble().toLong() }.toLongArray()

    fun asDouble(a: Array<String>, index: Int): Double = a[index].toDouble()
    fun asDoubleArray(a: Array<String>): DoubleArray = a.map { it.toDouble() }.toDoubleArray()

    fun asFloat(a: Array<String>, index: Int): Float = a[index].toFloat()
    fun asFloatArray(a: Array<String>): FloatArray = a.map { it.toFloat() }.toFloatArray()

    fun asString(a: Array<String>, index: Int): String = a[index]
    fun asStringArray(a: Array<String>): Array<String> = a.map { it }.toTypedArray()

    fun testBoolean(s: String): Boolean = s[0] == 't'

    fun asBoolean(a: Array<String>, index: Int): Boolean  = testBoolean(a[index])
    fun asBooleanArray(a: Array<String>): BooleanArray = a.map { testBoolean(it) }.toBooleanArray()
}