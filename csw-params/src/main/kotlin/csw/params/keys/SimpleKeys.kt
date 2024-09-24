package csw.params.keys

import arrow.core.*
import csw.params.commands.HasParms
import csw.params.keys.KeyHelpers.toDoubleArray
import csw.params.keys.StoredType.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*


enum class StoredType { NUMBER, INTEGER, BOOLEAN, STRING }

object QstoreSerializer : JsonTransformingSerializer<Qstore>(Qstore.generatedSerializer()) {
    @OptIn(ExperimentalSerializationApi::class)
    override fun transformSerialize(element: JsonElement): JsonElement {
        require(element is JsonObject)
        val stype = StoredType.valueOf(element.getValue("stype").jsonPrimitive.content)
        val keyType: String = when (stype) {
            StoredType.NUMBER -> "DoubleKey"
            StoredType.INTEGER -> "LongKey"
            StoredType.STRING -> "StringKey"
            StoredType.BOOLEAN -> "BooleanKey"
        }
        val values = when (stype) {
            StoredType.NUMBER -> element.getValue("values").jsonArray.map { JsonPrimitive(it.jsonPrimitive.content.toDouble()) }
            StoredType.INTEGER -> element.getValue("values").jsonArray.map { JsonPrimitive(it.jsonPrimitive.content.toInt()) }
            StoredType.STRING -> element.getValue("values").jsonArray.map { JsonPrimitive(it.jsonPrimitive.content) }
            StoredType.BOOLEAN -> element.getValue("values").jsonArray.map { JsonPrimitive(it.jsonPrimitive.content.toBoolean()) }
            else -> throw IllegalArgumentException("Bummer")
        }
        return buildJsonObject {
            putJsonObject(keyType) {
                put("keyName", element.getValue("name").jsonPrimitive.content)
                putJsonArray("values") {
                    addAll(values)
                }
                if (element.keys.contains("units"))
                    put("units", element.getValue("units"))
            }
        }
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        // XXX TODO
        require(element is JsonObject)
        val keyName = element.jsonObject.keys.first()
        val data = element.getValue(keyName).jsonObject
        return JsonObject(
            mapOf(
                "keyName" to JsonPrimitive(keyName),
                "data" to data
            )
        )
    }
}

@OptIn(InternalSerializationApi::class)
@Serializable(with = QstoreSerializer::class)
@KeepGeneratedSerializer
data class Qstore(
    override val name: Key, val stype: StoredType,
    val values: Array<String>, val units: Units = Units.NoUnits
) : HasKey {
    val asDoubles: DoubleArray
        get() = values.toDoubleArray()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Qstore
        if (name != other.name) return false
        if (!values.contentEquals(other.values)) return false
        if (units != other.units) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + values.hashCode()
        result = 31 * result + units.hashCode()
        return result
    }
}

data class NumberKey(override val name: Key, val units: Units = Units.NoUnits) : IsKey {
    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: Double, vararg values: Double): Qstore =
        Qstore(name, NUMBER, doubleArrayOf(value, *values).toStrings(), units)

    fun set(value: Float, vararg values: Float): Qstore =
        Qstore(name, NUMBER, floatArrayOf(value, *values).toStrings(), units)

    fun set(value: DoubleArray): Qstore = Qstore(name, NUMBER, value.toStrings(), units)

    fun set(value: FloatArray): Qstore = Qstore(name, NUMBER, value.toStrings(), units)

    private fun DoubleArray.toStrings(): Array<String> =
        this.map { it.toString() }.toTypedArray()

    private fun FloatArray.toStrings(): Array<String> =
        this.map { it.toString() }.toTypedArray()

    fun contains(target: HasParms): Boolean = target.exists(this)
    fun isIn(target: HasParms): Boolean = contains(target)

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
    operator fun invoke(target: HasParms): Quantity = get(target).getOrElse { throw NoSuchElementException(notFound) }
    fun value(s: HasParms): DoubleArray = invoke(s).asDoubleArray()
    fun value(s: HasParms, index: Int): Double = value(s)[index]
    fun head(s: HasParms): Double = invoke(s).asDoubleArray()[0]

    companion object {
        fun make(name: Key, units: Units = Units.NoUnits): NumberKey = NumberKey(name, units)
    }
}

data class IntegerKey(override val name: Key, val units: Units = Units.NoUnits) : IsKey {
    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: Int, vararg values: Int): Qstore =
        Qstore(name, INTEGER, intArrayOf(value, *values).toStrings(), units)

    fun set(value: Long, vararg values: Long): Qstore =
        Qstore(name, INTEGER, longArrayOf(value, *values).toStrings(), units)

    fun set(value: Short, vararg values: Short): Qstore =
        Qstore(name, INTEGER, shortArrayOf(value, *values).toStrings(), units)

    fun set(value: Byte, vararg values: Byte): Qstore =
        Qstore(name, INTEGER, byteArrayOf(value, *values).toStrings(), units)

    fun set(value: ShortArray): Qstore = Qstore(name, INTEGER, value.toStrings(), units)

    fun set(value: IntArray): Qstore =
        Qstore(name, INTEGER, KeyHelpers.aencode(value.toTypedArray()), units)

    fun set(value: LongArray): Qstore =
        Qstore(name, INTEGER, KeyHelpers.aencode(value.toTypedArray()), units)

    private fun IntArray.toStrings(): Array<String> = this.map { it.toString() }.toTypedArray()

    private fun LongArray.toStrings(): Array<String> = this.map { it.toString() }.toTypedArray()

    private fun ShortArray.toStrings(): Array<String> = this.map { it.toString() }.toTypedArray()

    private fun ByteArray.toStrings(): Array<String> = this.map { it.toString() }.toTypedArray()

    fun contains(target: HasParms): Boolean = target.exists(this)
    fun isIn(target: HasParms): Boolean = contains(target)

    fun get(target: HasParms): Option<Quantity> {
        val qs = KeyHelpers.getStored<Qstore>(this, target)
        return qs?.let { qs -> Some(Quantity(qs.values, qs.units)) } ?: None
    }

    fun get2(target: HasParms, sname: StructKey): Option<Quantity> {
        val x = Struct.getStored(sname.name, target)
        println("X: $x")
        val result = when (x) {
            is Some ->
                return get(x.value)

            is None -> None
        }
        return result
    }

    /*
        fun scalar(target: HasParms): Scalar {
            val q = get(target).getOrElse { throw NoSuchElementException(notFound) }
            return Scalar(q.svalue)
        }
    */
    operator fun invoke(target: HasParms): Quantity = get(target).getOrElse { throw NoSuchElementException(notFound) }
    fun value(s: HasParms): LongArray = invoke(s).asLongArray()
    fun value(s: HasParms, index: Int): Long = value(s)[index]
    fun head(s: HasParms): Long = invoke(s).asLong()

    // For compatibility
    companion object {
        fun make(name: Key, units: Units = Units.NoUnits): IntegerKey = IntegerKey(name, units)
    }
}


object SstoreSerializer : JsonTransformingSerializer<Sstore>(Sstore.generatedSerializer()) {
    @OptIn(ExperimentalSerializationApi::class)
    override fun transformSerialize(element: JsonElement): JsonElement {
        require(element is JsonObject)
        val stype = StoredType.valueOf(element.getValue("stype").jsonPrimitive.content)
        val values = when (stype) {
            StoredType.STRING -> element.getValue("value").jsonArray.map { JsonPrimitive(it.jsonPrimitive.content) }
            else -> throw IllegalArgumentException("Bummer")
        }
        return buildJsonObject {
            putJsonObject("StringKey") {
                put("keyName", element.getValue("name").jsonPrimitive.content)
                putJsonArray("values") {
                    addAll(values)
                }
            }
        }
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        // XXX TODO
        require(element is JsonObject)
        val keyName = element.jsonObject.keys.first()
        val data = element.getValue(keyName).jsonObject
        return JsonObject(
            mapOf(
                "keyName" to JsonPrimitive(keyName),
                "data" to data
            )
        )
    }
}

/* SStore has no units */
@OptIn(InternalSerializationApi::class)
@Serializable(with = SstoreSerializer::class)
@KeepGeneratedSerializer
data class Sstore(override val name: Key, val stype: StoredType, val value: Array<String>) : HasKey {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Sstore
        if (name != other.name) return false
        if (stype != other.stype) return false
        if (!value.contentEquals(other.value)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + stype.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }

    companion object {
        internal fun getStored(name: Key, target: HasParms): Option<Sstore> {
            val s: HasKey? = target.nget(name)
            return if (s is Sstore) Option(s) else None
        }
    }
}

data class BooleanKey(override val name: Key) : IsKey {
    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: Boolean, vararg values: Boolean): Sstore =
        Sstore(name, BOOLEAN, booleanArrayOf(value, *values).toStrings())

    fun set(value: BooleanArray): Sstore = Sstore(name, BOOLEAN, value.toStrings())

    fun contains(target: HasParms): Boolean = target.exists(this)
    fun isIn(target: HasParms): Boolean = contains(target)

    fun get(target: HasParms): Option<Scalar> {
        val ss = KeyHelpers.getStored<Sstore>(this, target)
        return ss?.let { ss -> Some(Scalar(ss.value)) } ?: None
    }

    private fun BooleanArray.toStrings(): Array<String> = this.map { it.toString() }.toTypedArray()

    fun scalar(target: HasParms): Scalar =
        get(target).getOrElse { throw NoSuchElementException(notFound) }

    operator fun invoke(target: HasParms): Scalar = scalar(target)
    fun value(s: HasParms): BooleanArray = invoke(s).asBooleanArray()
    fun value(s: HasParms, index: Int): Boolean = value(s)[index]
    fun head(s: HasParms): Boolean = value(s, 0)


    // For compatibility
    companion object {
        fun make(name: Key): BooleanKey = BooleanKey(name)
    }
}

data class StringKey(override val name: Key) : IsKey {
    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: String, vararg values: String): Sstore =
        Sstore(name, STRING, KeyHelpers.aencode(arrayOf(value) + values))

    fun set(value: Char, vararg values: Char): Sstore =
        set(value.toString(), *(values.map { it.toString() }).toTypedArray())

    fun set(value: Array<String>): Sstore = Sstore(name, STRING, KeyHelpers.aencode(value))

    fun contains(target: HasParms): Boolean = target.exists(this)
    fun isIn(target: HasParms): Boolean = contains(target)

    fun get(target: HasParms): Option<Scalar> = Sstore.getStored(name, target).map { Scalar(it.value) }

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