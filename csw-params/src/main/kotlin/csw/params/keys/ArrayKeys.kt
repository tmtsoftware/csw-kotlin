package csw.params.keys

import csw.params.commands.HasParms
import csw.params.keys.KeyHelpers.toDoubleArray
import csw.params.keys.KeyHelpers.toLongArray
import kotlinx.serialization.Serializable

interface ArrayStorage

data class NumberArrayKey(override val name: Key, val units: Units = Units.NoUnits): IsKey {
    enum class StoreType { DOUBLE, FLOAT }

    @Serializable
    data class NAStore(override val name: Key, val storeType: StoreType, val units: Units, val data: Array<DoubleArray>): HasKey, ArrayStorage {

        override fun toString(): String =
            "NAStore($name, $storeType, $units, ${data.contentDeepToString()})"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as NAStore
            if (name != other.name) return false
            if (units != other.units) return false
            if (!data.contentDeepEquals(other.data)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + units.hashCode()
            result = 31 * result + data.contentHashCode()
            return result
        }
    }

    fun set(value: DoubleArray, vararg values: DoubleArray): NAStore =
        NAStore(name, StoreType.DOUBLE, units, arrayOf(value, *values))

    fun set(value: FloatArray, vararg values: FloatArray): NAStore =
        NAStore(name, StoreType.FLOAT, units, listOf(value, *values).map { it.toDoubleArray() }.toTypedArray())

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Array<DoubleArray>? = KeyHelpers.getStored<NAStore>(this, target)?.data
    fun get(target: HasParms, row: Int): DoubleArray? =
        get(target)?.let {
            if (row < 0 || row >= it.size) null else it[row]
        }

    fun get(target: HasParms, row: Int, col: Int): Double? =
        get(target, row)?.let {
            if (col < 0 || col >= it.size) return null else it[col]
        }

    operator fun invoke(target: HasParms): Array<DoubleArray> = get(target) ?: throw NoSuchElementException("The value is not present")
    fun value(s: HasParms): Array<DoubleArray> = invoke(s)
    fun valueAt(s: HasParms, index: Int): DoubleArray = invoke(s)[index]
    fun head(s: HasParms): DoubleArray = valueAt(s, 0)
    fun item(s: HasParms, i: Int, j: Int): Double = valueAt(s, i)[j]

    // For compatibility
    companion object {
        fun make(name: Key, units: Units = Units.NoUnits): NumberArrayKey = NumberArrayKey(name, units)
    }
}



data class IntegerArrayKey(override val name: Key, val units: Units = Units.NoUnits): IsKey {
    enum class StoreType { SHORT, INT, LONG }

    @Serializable
    data class IAStore(override val name: Key,
                       val storeType: StoreType, val units: Units, val data: Array<LongArray>): HasKey, ArrayStorage {

        override fun toString(): String =
            "IAStore($name, $storeType, $units, ${data.contentDeepToString()})"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as IAStore
            if (name != other.name) return false
            if (units != other.units) return false
            if (!data.contentDeepEquals(other.data)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + units.hashCode()
            result = 31 * result + data.contentHashCode()
            return result
        }
    }

    fun set(value: ShortArray, vararg values: ShortArray): IAStore =
        IAStore(name, StoreType.SHORT, units, listOf(value, *values).map { it.toLongArray() }.toTypedArray())

    fun set(value: IntArray, vararg values: IntArray): IAStore =
        IAStore(name, StoreType.INT, units, listOf(value, *values).map { it.toLongArray() }.toTypedArray())

    fun set(value: LongArray, vararg values: LongArray): IAStore =
        IAStore(name, StoreType.LONG, units, arrayOf(value, *values))

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Array<LongArray>? = KeyHelpers.getStored<IAStore>(this, target)?.data
    fun get(target: HasParms, row: Int): LongArray? =
        get(target)?.let {
            if (row < 0 || row >= it.size) null else it[row]
        }

    fun get(target: HasParms, row: Int, col: Int): Long? =
        get(target, row)?.let {
            if (col < 0 || col >= it.size) return null else it[col]
        }

    operator fun invoke(target: HasParms): Array<LongArray> = get(target) ?: throw NoSuchElementException("Setup doesn't have it")
    fun value(s: HasParms): Array<LongArray> = invoke(s)
    fun valueAt(s: HasParms, index: Int): LongArray = invoke(s)[index]
    fun head(s: HasParms): LongArray = valueAt(s, 0)
    fun item(s: HasParms, i: Int, j: Int): Long = valueAt(s, i)[j]

    // For compatibility
    companion object {
        fun make(name: Key, units: Units = Units.NoUnits): IntegerArrayKey = IntegerArrayKey(name, units)
    }
}


@Serializable
data class StoredArrays(override val name: Key, val data: List<ArrayStorage>) : HasKey {
    /**
     * Returns the number of coords in the Coordinates
     */
    val size: Int
        get() = data.size
}

data class ArrayKey(override val name: Key): IsKey {

    fun set(value: ArrayStorage, vararg values: ArrayStorage): StoredArrays =
        StoredArrays(name, arrayOf(value, *values).toList())

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): StoredArrays? = KeyHelpers.getStored<StoredArrays>(this, target)

    operator fun invoke(target: HasParms): StoredArrays = get(target) ?: throw NoSuchElementException("Container doesn't have it")
    fun value(s: HasParms): StoredArrays = invoke(s)
    fun head(s: HasParms): ArrayStorage = invoke(s).data[0]

    fun get(target: HasParms, i: Int): ArrayStorage = value(target).data[i]

    // For compatibility
    companion object {
        fun make(name: Key, units: Units = Units.NoUnits): IntegerArrayKey = IntegerArrayKey(name, units)
    }
}