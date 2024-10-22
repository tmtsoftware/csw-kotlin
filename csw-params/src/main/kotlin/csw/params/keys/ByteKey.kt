package csw.params.keys

import csw.params.commands.HasParms
import kotlinx.serialization.*
import kotlinx.serialization.cbor.Cbor
import kotlin.NoSuchElementException

@OptIn(ExperimentalSerializationApi::class)
data class ByteKey(override val name: Key) : IsKey {
    enum class StoreType { BYTE, CBOR }

    @Serializable
    data class ByteStore(override val name: Key, val storeType: StoreType, val data: ByteArray) : HasKey {

        companion object {
            internal fun store(name: Key, value: ByteArray) = ByteStore(name, StoreType.BYTE, value)
            internal fun cstore(name: Key, value: ByteArray) = ByteStore(name, StoreType.CBOR, cborEncode(value))

            private fun cborEncode(value: ByteArray): ByteArray = Cbor.encodeToByteArray(value)

            internal fun cborDecode(value: ByteArray): ByteArray = Cbor.decodeFromByteArray<ByteArray>(value)

           // internal fun getStored(name: Key, target: HasParms): ByteStore? =
//                target.nget(name) as ByteStore?
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as ByteStore
            if (name != other.name) return false
            if (storeType != other.storeType) return false
            if (!data.contentEquals(other.data)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + storeType.hashCode()
            result = 31 * result + data.contentHashCode()
            return result
        }

    }

    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: Byte, vararg values: Byte): HasKey =
        ByteStore.store(name, byteArrayOf(value, *values))

    fun set(value: ByteArray): HasKey = ByteStore.store(name, value)

    fun setC(value: ByteArray): HasKey = ByteStore.cstore(name, value)

    fun contains(target: HasParms): Boolean = target.exists(this)
    fun isIn(target: HasParms): Boolean = contains(target)

    fun get(target: HasParms): ByteArray? =
        KeyHelpers.getStored<ByteStore>(this, target)?.let { store ->
            if (store.storeType == StoreType.CBOR)
                ByteStore.cborDecode(store.data)
            else store.data
        }

    fun asString(target: HasParms): String = value(target).decodeToString()

    operator fun invoke(target: HasParms): ByteArray = get(target) ?:  throw NoSuchElementException(notFound)
    fun value(s: HasParms): ByteArray = invoke(s)
    fun head(s: HasParms): Byte = invoke(s)[0]

    // For compatibility
    companion object {
        fun make(name: Key, units: Units = Units.NoUnits): ByteKey = ByteKey(name)
    }
}
