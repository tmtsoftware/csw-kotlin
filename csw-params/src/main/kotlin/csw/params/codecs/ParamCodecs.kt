package csw.params.codecs

import csw.params.keys.KeyHelpers.toDoubleArray
import csw.params.keys.Qstore
import csw.params.keys.StoredType
import csw.params.keys.Units
import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import kotlin.collections.HashMap

//private typealias SPMap = HashMap<String, ParamCodecs.ParmCore>
private typealias SPMap = HashMap<String, Qstore>
private val holderSerializer: KSerializer<SPMap> = serializer()

val parmFormat = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

//fun toJson(qs: Qstore): String = parmFormat.encodeToString(holderSerializer, ParamCodecs.ParmCore.fromParam(qs))
//fun fromJson(qsstr: String): Qstore = ParamCodecs.ParmCore.toParam(
//    parmFormat.decodeFromString(holderSerializer, qsstr))

//fun toJson(qs: Qstore): String = parmFormat.encodeToString(holderSerializer, ParamCodecs.ParmCore.fromParam(qs))
//fun fromJson(qsstr: String): Qstore = ParamCodecs.ParmCore.toParam(
//    parmFormat.decodeFromString(holderSerializer, qsstr))

val dconvert = { sa:  Array<String> -> sa.toDoubleArray() }
val lconvert = { sa: Array<String> -> sa.map { it.toLong() }.toLongArray() }

@Serializable
data class NumberCore(val keyName: String, val values: DoubleArray, val units: Units)
@Serializable
data class IntegerCore(val keyName: String, val values: LongArray, val units: Units)

enum class CSW(
    val view: () -> Unit
) {
    TEST1(
        view = { println("TEST1")}
    )
}


@Serializable
sealed interface CSWValues

@Serializable
data class NCore(val keyName: String,
                 val values: DoubleArray,
                 val units: Units): CSWValues {
                     override fun equals(other: Any?): Boolean {
                         if (this === other) return true
                         if (javaClass != other?.javaClass) return false
                         other as NCore
                         if (keyName != other.keyName) return false
                         if (!values.contentEquals(other.values)) return false
                         if (units != other.units) return false
                         return true
                     }
                     override fun hashCode(): Int {
                         var result = keyName.hashCode()
                         result = 31 * result + values.contentHashCode()
                         result = 31 * result + units.hashCode()
                         return result
                     }

                 }
@Serializable
data class ICore(val keyName: String,
                 val values: LongArray, val units: Units): CSWValues

fun getEntry(key: String, value: CSWValues): Map.Entry<String, CSWValues> =
    object: Map.Entry<String, CSWValues> {
        override val key: String = key
        override val value: CSWValues = value
    }


@Serializable(with = PacketSerializer::class)
data class Packet(val keyType: String, val payload: CSWValues) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Packet
        if (keyType != other.keyType) return false
        if (payload != other.payload) return false
        return true
    }
}

sealed interface MyData
@Serializable
data class Data(val keyType: String, val data: NCore): MyData


// Note: Can't use (with = TestSerializer) here, due to circular reference: See https://github.com/Kotlin/kotlinx.serialization/issues/1169
@OptIn(InternalSerializationApi::class)
@Serializable(with = TestSerializer::class)
@KeepGeneratedSerializer
data class Data2(val keyName: String, val data: ICore): MyData

object TestSerializer: JsonTransformingSerializer<Data2>(Data2.generatedSerializer()) {
    override fun transformSerialize(element: JsonElement): JsonElement {
        require(element is JsonObject)
        val keyName = element.getValue("keyName").jsonPrimitive.content
        val data = element.getValue("data").jsonObject
        return JsonObject(mapOf(keyName to data))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
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



object PacketSerializer: KSerializer<Packet> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Packet") {
        element("keyType", serialDescriptor<String>())
        element("payload", buildClassSerialDescriptor("CSWValues"))
    }

     val descriptor2: SerialDescriptor = buildClassSerialDescriptor("Packet") {
        element("entry", mapSerialDescriptor<String, CSWValues>())
     }

    //private fun getPayloadSerializer2(keyType: String): KSerializer<Map<String, CSWValues>> =
//        MapSerializer(String.serializer(), TestSerializer3.getPayloadSerializer(keyType))

    @Suppress("UNCHECKED_CAST")
    private val dataTypeSerializers: Map<String, KSerializer<CSWValues>> =
        mapOf(
            "String" to serializer<String>(),
            "DoubleKey" to serializer<NCore>(),
            "LongKey" to serializer<ICore>()
        ).mapValues { (_, v) -> v as KSerializer<CSWValues> }

    private fun getPayloadSerializer(keyType: String): KSerializer<CSWValues> = dataTypeSerializers[keyType]
        ?: throw SerializationException("Serializer for class $keyType is not registered in PacketSerializer")

    override fun serialize(encoder: Encoder, value: Packet) {
        encoder.encodeStructure(descriptor) {
            //encodeStringElement(descriptor, 0, value.keyType)
            encodeSerializableElement(descriptor, 1, getPayloadSerializer(value.keyType), value.payload)
        }
    }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): Packet = decoder.decodeStructure(descriptor) {
        if (decodeSequentially()) {
            println("1")
            val keyType = decodeStringElement(descriptor, 0)
            val payload = decodeSerializableElement(descriptor, 1, getPayloadSerializer(keyType))
            Packet(keyType, payload)
        } else {
            println("2")
            //require(decodeElementIndex(descriptor) == 0) { "keyType field should precede payload field" }
            val keyType = decodeStringElement(descriptor, 0)
            println("keyType: $keyType")
            val payload = when (val index = decodeElementIndex(descriptor)) {
                1 -> decodeSerializableElement(descriptor, 1, getPayloadSerializer(keyType))
                CompositeDecoder.DECODE_DONE -> throw SerializationException("payload field is missing")
                else -> error("Unexpected index: $index")
            }
            Packet(keyType, payload)
        }
    }}

//private typealias SPMap = HashMap<String, ParamCodecs.ParmCore>
private typealias SPMap2 = HashMap<String, CSWValues>
private val holderSerializer2: KSerializer<SPMap2> = serializer()

@Serializable
//data class Packet2 <T>(val keyType: String, val keyName: String, val values: T, val units: Units )
data class Packet2(val keyType: String, val keyName: String, val values: LongArray, val units: Units )
private val packet2Serializer:KSerializer<Packet2> = serializer()
private val mapSerializer: KSerializer<Map<String, IntegerCore>> = serializer()

object TestSerializer2: KSerializer<Packet2> {
    override val descriptor: SerialDescriptor = packet2Serializer.descriptor

    override fun serialize(encoder: Encoder, value: Packet2) {
        encoder.encodeSerializableValue(mapSerializer, hashMapOf(value.keyType to IntegerCore(value.keyName, value.values, value.units)))
    }

    override fun deserialize(decoder: Decoder): Packet2 {
        val map = decoder.decodeSerializableValue(mapSerializer)
        val p = map.map { Packet2(it.key, it.value.keyName, it.value.values, it.value.units) }.first()
        return p
    }
}


@Serializable
//data class Packet2 <T>(val keyType: String, val keyName: String, val values: T, val units: Units )
data class Packet3(val keyType: String, val payload: CSWValues )
private val packet3Serializer:KSerializer<Packet3> = serializer()

object TestSerializer3: KSerializer<Packet3> {

    private fun getPayloadSerializer(keyType: String): KSerializer<CSWValues> = p3TypeSerializers[keyType]
        ?: throw SerializationException("Serializer for class $keyType is not registered in PacketSerializer")

    private fun getPayloadSerializer2(keyType: String): KSerializer<Map<String, CSWValues>> =
        MapSerializer(String.serializer(), getPayloadSerializer(keyType))

    //private val deseriale = MapSerializer(String.serializer(), Any.serializer())

    @Suppress("UNCHECKED_CAST")
    private val p3TypeSerializers: Map<String, KSerializer<CSWValues>> =
        mapOf(
            "String" to serializer<String>(),
            "DoubleKey" to serializer<NCore>(),
            "LongKey" to serializer<ICore>()
        ).mapValues { (_, v) -> v as KSerializer<CSWValues> }

    override val descriptor: SerialDescriptor = packet3Serializer.descriptor

    override fun serialize(encoder: Encoder, value: Packet3) {
        //val des = gDescriptor2(value.keyType)
        encoder.encodeSerializableValue(getPayloadSerializer2(value.keyType), hashMapOf(value.keyType to value.payload))
    }

    override fun deserialize(decoder: Decoder): Packet3 {
        val map = decoder.decodeSerializableValue(packet3Serializer)
        println("Map: $map")
        //println("${map.keys}")
        //val p = map.map { Packet2(it.key, it.value.keyName, it.value.values, it.value.units) }.first()
        return Packet3("doubleKey", payload = ICore("kim", longArrayOf(1, 2, 3), Units.ampere))
    }

}