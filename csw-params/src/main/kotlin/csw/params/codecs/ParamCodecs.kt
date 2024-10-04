package csw.params.codecs

import csw.params.codecs.NumberSerializer.NumberCore
import csw.params.keys.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

object NumberSerializer: KSerializer<Qstore> {
    @Serializable
    data class NumberCore(val keyName: String, val values: DoubleArray, val units: Units)
    private val sss1:KSerializer<Map<String,NumberCore>> = MapSerializer(String.serializer(), NumberCore.serializer())
    override val descriptor = sss1.descriptor

    override fun serialize(encoder: Encoder, value: Qstore) {
        require(value.stype == StoredType.NUMBER)
        encoder.encodeSerializableValue(sss1, hashMapOf("DoubleKey" to NumberCore(value.name, value.asDoubles, value.units)))
    }

    override fun deserialize(decoder: Decoder): Qstore {
        val hmap = decoder.decodeSerializableValue(sss1)
        val (keyType, param) = hmap.entries.first()
        require(keyType == "DoubleKey" || keyType == "FloatKey")
        return Qstore(param.keyName, StoredType.NUMBER, param.values.map { it.toString() }.toTypedArray(), param.units)
     }
}

object IntegerSerializer: KSerializer<Qstore> {
    @Serializable
    data class IntegerCore(val keyName: String, val values: LongArray, val units: Units)
    private val sss1:KSerializer<Map<String,IntegerCore>> = MapSerializer(String.serializer(), IntegerCore.serializer())
    override val descriptor = sss1.descriptor

    override fun serialize(encoder: Encoder, value: Qstore) {
        require(value.stype == StoredType.INTEGER)
        encoder.encodeSerializableValue(sss1, hashMapOf("LongKey" to IntegerCore(value.name, value.values.map { it.toLong()}.toLongArray(), value.units)))
    }

    private fun allowed(keyType: String): Boolean =
        keyType == "IntKey" || keyType == "LongKey" || keyType == "ShortKey"

    override fun deserialize(decoder: Decoder): Qstore {
        val spmap = decoder.decodeSerializableValue(sss1)
        val (keyType, param) = spmap.entries.first()
        require (allowed(keyType))
        return Qstore(param.keyName, StoredType.INTEGER, param.values.map { it.toString() }.toTypedArray(), param.units)
    }
}

object StringSerializer: KSerializer<Sstore> {
    @Serializable
    data class StringCore(val keyName: String, val values: Array<String>, val units: Units)
    private val sss1:KSerializer<Map<String,StringCore>> = MapSerializer(String.serializer(), StringCore.serializer())

    override val descriptor = sss1.descriptor

    override fun serialize(encoder: Encoder, sstore: Sstore) {
        require(sstore.stype == StoredType.STRING)
        encoder.encodeSerializableValue(sss1,
            hashMapOf("StringKey" to StringCore(sstore.name, sstore.value, Units.NoUnits)))
    }

    override fun deserialize(decoder: Decoder): Sstore {
        val spmap = decoder.decodeSerializableValue(sss1)
        val (keyType, param) = spmap.entries.first()
        require(keyType == "StringKey" || keyType == "CharKey")
        return Sstore(param.keyName, StoredType.STRING, param.values)
    }
}

object BooleanSerializer: KSerializer<Sstore> {
    @Serializable
    data class BooleanCore(val keyName: String, val values: BooleanArray, val units: Units)

    private val sss1:KSerializer<Map<String,BooleanCore>> = MapSerializer(String.serializer(), BooleanCore.serializer())
    override val descriptor = sss1.descriptor

    override fun serialize(encoder: Encoder, sstore: Sstore) {
        require(sstore.stype == StoredType.BOOLEAN)
        encoder.encodeSerializableValue(sss1, hashMapOf("BooleanKey" to BooleanCore(sstore.name,
            sstore.value.map { it.toBoolean() }.toBooleanArray(), Units.NoUnits)))
    }

    override fun deserialize(decoder: Decoder): Sstore {
        val spmap = decoder.decodeSerializableValue(sss1)
        val (keyType, param) = spmap.entries.first()
        require(keyType == "BooleanKey")
        return Sstore(param.keyName, StoredType.BOOLEAN, param.values.map { it.toString() }.toTypedArray())
    }
}

object ChoiceSerializer: KSerializer<ChoiceStore> {
    @Serializable
    data class ChoiceCore(val keyName: String, val values: Array<String>, val units: Units)

    private val sss1:KSerializer<Map<String,ChoiceCore>> = MapSerializer(String.serializer(), ChoiceCore.serializer())
    override val descriptor = sss1.descriptor

    override fun serialize(encoder: Encoder, cstore: ChoiceStore) {
        encoder.encodeSerializableValue(sss1, hashMapOf("ChoiceKey" to ChoiceCore(cstore.name, cstore.choice, cstore.units)))
    }

    override fun deserialize(decoder: Decoder): ChoiceStore {
        val spmap = decoder.decodeSerializableValue(sss1)
        val (keyType, param) = spmap.entries.first()
        require(keyType == "ChoiceKey")
        return ChoiceStore(param.keyName, param.values, param.units)
    }
}

/* ------------------ */

object ParamDeserializer : JsonContentPolymorphicSerializer<HasKey>(HasKey::class) {
    override fun selectDeserializer(content: JsonElement) = when {
        "DoubleKey" in content.jsonObject -> NumberSerializer
        "FloatKey" in content.jsonObject -> NumberSerializer
        "LongKey" in content.jsonObject -> IntegerSerializer
        "IntKey" in content.jsonObject -> IntegerSerializer
        "StringKey" in content.jsonObject -> StringSerializer
        "CharKey" in content.jsonObject -> StringSerializer
        "BooleanKey" in content.jsonObject -> BooleanSerializer
        "ChoiceKey" in content.jsonObject -> ChoiceSerializer
        else -> throw IllegalArgumentException("Bad case")
    }
}

object ParamDeserializer2: KSerializer<HasKey> {
    override val descriptor: SerialDescriptor = HasKey.serializer().descriptor

    //private val sss1:KSerializer<Map<String, NumberCore>> = MapSerializer(String.serializer(), NumberCore.serializer())
    private val sss1 = String.serializer()


    override fun deserialize(decoder: Decoder): HasKey {
        //val hmap = decoder.decodeSerializableValue(sss1)
        //val (keyType, param) = hmap.entries.first()
        val st = decoder.beginStructure(descriptor)
        val xx = st.decodeStringElement(descriptor, 0)
        println("keyType = $xx")
        //println("param = $param")
        val de = decoder.beginStructure(descriptor)
        val x = NumberCore.serializer().deserialize(decoder) as HasKey
        println("x: $x")
        de.endStructure(descriptor)
        return x
    }

    override fun serialize(encoder: Encoder, value: HasKey) {
        TopParamSerializer.serialize(encoder, value)
    }
}

/* ------------------------- */

object ParamSerializer: KSerializer<HasKey> {

    override val descriptor: SerialDescriptor = HasKey.serializer().descriptor

    override fun serialize(encoder: Encoder, value: HasKey) {
        when (value) {
            is Qstore ->
                if (value.stype == StoredType.INTEGER)
                    IntegerSerializer.serialize(encoder, value)
                else
                    NumberSerializer.serialize(encoder, value)
            is Sstore ->
                if (value.stype == StoredType.STRING)
                    StringSerializer.serialize(encoder, value)
                else
                    BooleanSerializer.serialize(encoder, value)
            is ChoiceStore -> ChoiceSerializer.serialize(encoder, value)
            else -> throw IllegalArgumentException("Don't know it: $value")
        }

    }

    override fun deserialize(decoder: Decoder): HasKey {
        error("Serialization is not supported")
    }
}

object TopParamSerializer: KSerializer<HasKey> {
    override val descriptor: SerialDescriptor = HasKey.serializer().descriptor

    override fun serialize(encoder: Encoder, value: HasKey) = ParamSerializer.serialize(encoder, value)

    override fun deserialize(decoder: Decoder): HasKey = ParamDeserializer.deserialize(decoder)
}

@SerialName("Param2")
@Serializable
data class BSurrogate(
    @SerialName("ChoiceKey")
    val string: Value<String>? = null,
) {
    @Serializable
    class Value<T>(
        val keyName: String,
        val values: List<T>,
        val units: Units,
    )
}


object ChoiceSerial2 : KSerializer<ChoiceStore> {
    override val descriptor: SerialDescriptor = BSurrogate.serializer().descriptor
    override fun deserialize(decoder: Decoder): ChoiceStore {
        val surrogate = decoder.decodeSerializableValue(BSurrogate.serializer())
        val value = surrogate.string
            ?: throw SerializationException("Unknown type")
        return ChoiceStore(value.keyName,value.values.toTypedArray(), value.units )
    }

    override fun serialize(encoder: Encoder, value: ChoiceStore) {
        TODO ("Not yet done")
    }

}