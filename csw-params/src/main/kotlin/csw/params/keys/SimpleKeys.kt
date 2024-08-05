package csw.params.keys

import arrow.core.*
import csw.params.commands.HasParms
import csw.params.keys.StoredType.*
import kotlinx.serialization.Serializable


enum class StoredType { NUMBER, INTEGER, BOOLEAN, STRING }

@Serializable
data class Qstore(override val name: Key, val stype: StoredType,
                  val value: String, val units: Units = Units.NoUnits) : HasKey {
    val values: Array<Double>
        get() = KeyHelpers.decodeValue(value)
    val svalues: Array<String>
        get() = KeyHelpers.asStrings(value)

    companion object {
        internal fun getStored(name: Key, target: HasParms): Option<Qstore> {
            val s: HasKey? = target.nget(name)
            return if (s is Qstore) Option(s) else None
        }
    }
}

data class NumberKey(override val name: Key, val units: Units = Units.NoUnits): IsKey {
    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: Number, vararg values: Number): Qstore {
        val valueList = (arrayOf(value) + values).toList()

        val result = when (value) {
            is Double-> Qstore(name, NUMBER, KeyHelpers.lencode(valueList), units)
            is Float -> Qstore(name, NUMBER, KeyHelpers.lencode(valueList), units)
            //is Int -> Stored(name, StoredType.INTEGER, sencode(value), units)
            //is Long -> Stored(name, StoredType.INTEGER, sencode(value), units)
            is Short -> Qstore(name, NUMBER, KeyHelpers.lencode(valueList), units)
            is Byte -> Qstore(name, StoredType.INTEGER, KeyHelpers.lencode(valueList), units)
            else -> throw IllegalArgumentException("Improper type used to set a NumberKey")
        }
        return result
    }

    fun set(value: DoubleArray): Qstore =
        Qstore(name, NUMBER, KeyHelpers.aencode(value.toTypedArray()), units)

    fun set(value: FloatArray): Qstore =
        Qstore(name, NUMBER, KeyHelpers.aencode(value.toTypedArray()), units)

    fun set(value: ShortArray): Qstore =
        Qstore(name, NUMBER, KeyHelpers.aencode(value.toTypedArray()), units)

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<Quantity> =
        Qstore.getStored(name, target).map { Quantity(it.svalues, it.units) }

    fun scalar(target: HasParms): Scalar {
        val q = get(target).getOrElse { throw NoSuchElementException(notFound) }
        return Scalar(q.svalue)
    }

    operator fun invoke(target: HasParms): Quantity = get(target).getOrElse { throw NoSuchElementException(notFound) }
    fun value(s: HasParms): DoubleArray = invoke(s).asDoubleArray()
    fun head(s: HasParms): Double = invoke(s).asDoubleArray()[0]

    companion object {
        fun make(name: Key, units: Units = Units.NoUnits): NumberKey = NumberKey(name, units)
    }
}

data class IntegerKey(override val name: Key, val units: Units = Units.NoUnits): IsKey {
    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: Number, vararg values: Number): Qstore {
        val valueList = (arrayOf(value) + values).toList()
        val result = when (value) {
            is Int -> Qstore(name, INTEGER, KeyHelpers.lencode(valueList), units)
            is Long -> Qstore(name, INTEGER, KeyHelpers.lencode(valueList), units)
            is Short -> Qstore(name, INTEGER, KeyHelpers.lencode(valueList), units)
            is Byte -> Qstore(name, INTEGER, KeyHelpers.lencode(valueList), units)
            else -> throw IllegalArgumentException("Improper type used to set an IntegerKey")
        }
        return result
    }

    fun set(value: IntArray): Qstore =
        Qstore(name, INTEGER, KeyHelpers.aencode(value.toTypedArray()), units)

    fun set(value: LongArray): Qstore =
        Qstore(name, INTEGER, KeyHelpers.aencode(value.toTypedArray()), units)

    fun set(value: ShortArray): Qstore =
        Qstore(name, INTEGER, KeyHelpers.aencode(value.toTypedArray()), units)

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<Quantity> =
        Qstore.getStored(name, target).map { Quantity(it.svalues, it.units) }

    fun scalar(target: HasParms): Scalar {
        val q = get(target).getOrElse { throw NoSuchElementException(notFound) }
        return Scalar(q.svalue)
    }

    operator fun invoke(target: HasParms): Quantity =
        get(target).getOrElse { throw NoSuchElementException(notFound) }
    fun value(s: HasParms): LongArray = invoke(s).asLongArray()
    fun head(s: HasParms): Long = invoke(s).asLongArray()[0]

    // For compatibility
    companion object {
        fun make(name: Key, units: Units = Units.NoUnits): IntegerKey = IntegerKey(name, units)
    }
}

@Serializable
data class Sstore(override val name: Key, val stype: StoredType, val value: String) : HasKey {
    val svalues: Array<String>
        get() = KeyHelpers.asStrings(value)

    companion object {
        internal fun getStored(name: Key, target: HasParms): Option<Sstore> {
            val s: HasKey? = target.nget(name)
            return if (s is Sstore) Option(s) else None
        }
    }
}

data class BooleanKey(override val name: Key): IsKey {
    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: Boolean, vararg values: Boolean): Sstore =
        Sstore(name, BOOLEAN, booleanArrayWrite(booleanArrayOf(value) + values))

    fun set(value: BooleanArray): Sstore = Sstore(name, BOOLEAN, booleanArrayWrite(value))

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<Scalar> = Sstore.getStored(name, target).map { Scalar(it.svalues) }

    fun scalar(target: HasParms): Scalar =
        get(target).getOrElse { throw NoSuchElementException(notFound) }

    operator fun invoke(target: HasParms): Scalar = scalar(target)
    fun value(s: HasParms): BooleanArray = invoke(s).asBooleanArray()
    fun value(s: HasParms, index: Int): Boolean = value(s)[index]
    fun head(s: HasParms): Boolean = value(s, 0)

    private fun booleanArrayWrite(value: BooleanArray): String =
        KeyHelpers.aencode(value.map { (it).toString()  }.toTypedArray())

    // For compatibility
    companion object {
        fun make(name: Key): BooleanKey = BooleanKey(name)
    }
}

data class StringKey(override val name: Key): IsKey {
    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: String, vararg values: String): Sstore =
        Sstore(name, STRING, KeyHelpers.aencode(arrayOf(value) + values))

    fun set(value: Char, vararg values: Char): Sstore =
        set(value.toString(), *(values.map { it.toString()}).toTypedArray() )

    fun set(value: Array<String>): Sstore = Sstore(name, STRING, KeyHelpers.aencode(value))

    fun exists(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<Scalar> = Sstore.getStored(name, target).map { Scalar(it.svalues) }

    fun scalar(target: HasParms): Scalar =
        get(target).getOrElse { throw NoSuchElementException(notFound) }

    operator fun invoke(target: HasParms): Scalar =
        get(target).getOrElse { throw NoSuchElementException(notFound) }

    fun value(target: HasParms): Array<String> = invoke(target).asStringArray()
    fun head(target: HasParms): String = invoke(target).asStringArray()[0]

    // For compatibility
    companion object {
        fun make(name: Key): StringKey = StringKey(name)
    }
}