package csw.params.keys

import arrow.core.*
import csw.params.commands.HasParms
import csw.params.keys.StoredType.NUMBER
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object TestKeys {
    @Serializable
    data class Qstore(override val name: Key, val stype: StoredType,
                      val values: Array<String>, val units: Units = Units.NoUnits) : HasKey {

        companion object {
            internal fun getStored(name: Key, target: HasParms): Option<Qstore> {
                val s: HasKey? = target.nget(name)
                return if (s is Qstore) Option(s) else None
            }
        }
    }

    data class NumberKey(override val name: Key, val units: Units = Units.NoUnits) : IsKey {
        private val notFound = "Parameter set does not contain key: $name"

        fun set(value: Double, vararg values: Double): Qstore =
            Qstore(name, NUMBER, doubleArrayOf(value, *values).toStrings())

        fun set(value: Float, vararg values: Float): Qstore =
            Qstore(name, NUMBER, floatArrayOf(value, *values).toStrings())

        fun set(value: DoubleArray): Qstore = Qstore(name, NUMBER, value.toStrings(), units)

        fun set(value: FloatArray): Qstore = Qstore(name, NUMBER, value.toStrings(), units)

        private fun DoubleArray.toStrings(): Array<String> =
            this.map { it.toString() }.toTypedArray()

        private fun FloatArray.toStrings(): Array<String> =
            this.map { it.toString() }.toTypedArray()

        /*
        fun set(value: ShortArray): Qstore =
            Qstore(name, NUMBER, KeyHelpers.aencode(value.toTypedArray()), units)
*/
        fun contains(target: HasParms): Boolean = target.exists(this)

        fun get(target: HasParms): Option<Quantity> {
            val qs = KeyHelpers.getStored<Qstore>(this, target)
            return qs?.let { qs -> Some(Quantity(qs.values, qs.units)) } ?: None
        }

        /*
        fun scalar(target: HasParms): Scalar {
            val q = get(target).getOrElse { throw NoSuchElementException(notFound) }
            return Scalar(q.svalue)
        }
*/
        operator fun invoke(target: HasParms): Quantity =
            get(target).map { Quantity(it.svalue, it.units)}.getOrElse { throw NoSuchElementException(notFound)  }

        fun value(s: HasParms): DoubleArray = invoke(s).svalue.map { it.toDouble() }.toDoubleArray()

        fun head(s: HasParms): Double = invoke(s).asDouble()

        companion object {
            fun make(name: Key, units: Units = Units.NoUnits): NumberKey = NumberKey(name, units)
        }
    }

}

data class Ssetup(val name: String, val parms: List<HasKey>) {

    fun add(item: HasKey): Ssetup {
        return copy(parms = parms + item)
    }

    inline fun <reified T> get(name: IsKey): T {
        val x: HasKey? = parms.find { it.name == name.name }
        return if (x != null && x is T) {
            return x as T
        } else
            throw NoSuchElementException()
    }
}

