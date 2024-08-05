package csw.params.keys

import arrow.core.*
import csw.params.commands.HasParms
import csw.params.core.models.ArrayData
import kotlinx.serialization.Serializable

@Serializable
data class ArStore(override val name: Key, val value: String, val units: Units = Units.NoUnits) : HasKey {
    val values: Array<ArrayData>
        get() = KeyHelpers.decodeArrayValue(value)
    val svalues: Array<ArrayData>
        get() = KeyHelpers.decodeArrayValue(value)

    companion object {
        internal fun getStored(name: Key, target: HasParms): Option<ArStore> {
            val s: HasKey? = target.nget(name)
            return if (s is ArStore) Option(s) else None
        }
    }
}


data class NumberArrayKey(override val name: Key, val units: Units = Units.NoUnits): IsKey {

    fun set(value: ArrayData, vararg values: ArrayData): ArStore {
        val valueList = (arrayOf(value) + values).toList()
        val result = ArStore(name, KeyHelpers.arrayencode(valueList), units)

        return result
    }

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Array<ArrayData> { //Option<Quantity> =
        val x = ArStore.getStored(name, target)
        val y = x.getOrElse { throw NoSuchElementException("The command does not have the key: \"$name\"") }
        return y.values
    }
/*
    fun scalar(target: HasParms): Scalar {
        val q = get(target).getOrElse { throw NoSuchElementException("The command does not have the key: \"$name\"") }
        return Scalar(q.svalues)
    }
*/
  //  operator fun invoke(target: HasParms): Quantity = get(target).getOrElse { throw NoSuchElementException("Setup doesn't have it") }
//    fun value(s: HasParms): DoubleArray = invoke(s).asDoubleArray()
//    fun head(s: HasParms): Double = invoke(s).asDoubleArray().elementAt(0)

    // For compatibility
    companion object {
  //      fun make(name: Key, units: Units = Units.NoUnits): NumberKey = NumberKey(name, units)
    }
}

@Serializable
data class DoubleArrayKey(override val name: Key, val units: Units = Units.NoUnits): IsKey {

    @Serializable
    data class DAStore(override val name: Key, val units: Units, val value: DoubleArray): HasKey {
        companion object {
            internal fun getStored(name: Key, target: HasParms): Option<DAStore> {
                val s: HasKey? = target.nget(name)
                return if (s is DAStore) Option(s) else None
            }
        }
    }

    fun set(value: DoubleArray): DAStore = DAStore(name, units, value)

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<DoubleArray> =
        DAStore.getStored(name, target).map { it.value }


    operator fun invoke(target: HasParms): DoubleArray = get(target).getOrElse { throw NoSuchElementException("Setup doesn't have it") }
    fun value(s: HasParms): DoubleArray = invoke(s)
    fun head(s: HasParms): Double = invoke(s).elementAt(0)

    // For compatibility
    companion object {
        //      fun make(name: Key, units: Units = Units.NoUnits): NumberKey = NumberKey(name, units)
    }
}