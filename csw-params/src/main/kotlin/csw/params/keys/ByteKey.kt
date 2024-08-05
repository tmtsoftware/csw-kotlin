package csw.params.keys

import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import csw.params.commands.HasParms
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlin.NoSuchElementException


@kotlinx.serialization.ExperimentalSerializationApi
data class ByteKey(override val name: Key) : IsKey {
    enum class StoreType { BYTE, CBOR }

    @Serializable
    data class ByteStore(override val name: Key, val storeType: StoreType, val value: ByteArray): HasKey {

        companion object {
            internal fun store(name: Key, value: ByteArray) = ByteStore(name, StoreType.BYTE, value)
            internal fun cstore(name: Key, value: ByteArray) = ByteStore(name, StoreType.CBOR, cborEncode(value))

            private fun cborEncode(value: ByteArray):ByteArray = Cbor.encodeToByteArray(value)

            internal fun cborDecode(value: ByteArray): ByteArray = Cbor.decodeFromByteArray<ByteArray>(value)

            internal fun getStored(name: Key, target: HasParms): Option<ByteStore> {
                val s: HasKey? = target.nget(name)
                return if (s is ByteStore) Option(s) else None
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as ByteStore
            if (name != other.name) return false
            if (storeType != other.storeType) return false
            if (!value.contentEquals(other.value)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + storeType.hashCode()
            result = 31 * result + value.contentHashCode()
            return result
        }

    }

    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: Byte, vararg values: Byte): HasKey =
        ByteStore.store(name, byteArrayOf(value, *values))

    fun set(value: ByteArray): HasKey = ByteStore.store(name, value)

    fun setC(value: ByteArray): HasKey = ByteStore.cstore(name, value)

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun isNotIn(target: HasParms): Boolean = !isIn(target)

    fun get(target: HasParms): Option<ByteArray> =
        ByteStore.getStored(name, target).map {
            if (it.storeType == StoreType.CBOR)
                ByteStore.cborDecode(it.value)
            else it.value
        }

    fun asString(target: HasParms): String = value(target).decodeToString()

    operator fun invoke(target: HasParms): ByteArray = get(target).getOrElse { throw NoSuchElementException(notFound) }
    fun value(s: HasParms): ByteArray = invoke(s)
    fun head(s: HasParms): Byte = invoke(s)[0]

    // For compatibility
    companion object {
        fun make(name: Key, units: Units = Units.NoUnits): ByteKey = ByteKey(name)
    }

//    override fun equals(other: Any?): Boolean { true }
}