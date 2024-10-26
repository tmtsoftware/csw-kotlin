package csw.params.codecs

import csw.params.core.models.Angle
import csw.params.core.models.EqFrame
import csw.params.keys.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ParamSerializer : KSerializer<HasKey> {

    override val descriptor: SerialDescriptor = HasKey.serializer().descriptor

    override fun serialize(encoder: Encoder, value: HasKey) {
        when (value) {
            is Qstore ->
                when (value.stype) {
                    StoredType.INTEGER -> {
                        val param = Param("LongKey", value.name, value.data.map { it.toLong() }.toList(), value.units)
                        Param.serializer().serialize(encoder, param)
                    }

                    StoredType.NUMBER -> {
                        val param = Param("DoubleKey", value.name, value.asDoubles.toList(), value.units)
                        Param.serializer().serialize(encoder, param)
                    }

                    else -> throw SerializationException("Wrong Qstore stype: ${value.stype}")
                }

            is Sstore ->
                when (value.stype) {
                    StoredType.STRING -> {
                        val param = Param("StringKey", value.name, value.data.toList(), Units.NoUnits)
                        Param.serializer().serialize(encoder, param)
                    }

                    StoredType.BOOLEAN -> {
                        val param =
                            Param("BooleanKey", value.name, value.data.map { it.toBoolean() }.toList(), Units.NoUnits)
                        Param.serializer().serialize(encoder, param)
                    }

                    else -> throw SerializationException("Wrong Sstore stype: ${value.stype}")
                }

            is ChoiceStore -> {
                val param = Param("ChoiceKey", value.name, value.choice.toList(), value.units)
                Param.serializer().serialize(encoder, param)
            }

            is TimeStore -> {
                when (value.ttype) {
                    TimeType.TAI -> {
                        val param = Param("TAITimeKey", value.name, value.svalue.toList(), Units.tai)
                        Param.serializer().serialize(encoder, param)
                    }

                    TimeType.UTC -> {
                        val param = Param("UTCTimeKey", value.name, value.svalue.toList(), Units.utc)
                        Param.serializer().serialize(encoder, param)
                    }
                }
            }

            is ByteKey.ByteStore -> {
                val param = Param("ByteKey", value.name, value.data.toList(), Units.NoUnits)
                Param.serializer().serialize(encoder, param)
            }
            
            is NumberArrayKey.NAStore -> {
                val avalues = value.data.toList()
                val param = Param("NumberArrayKey", value.name, avalues, value.units)

                Param.serializer().serialize(encoder, param)
            }

            is IntegerArrayKey.IAStore -> {
                val avalues = value.data.toList()
                val param = Param("IntegerArrayKey", value.name, avalues, value.units)

                Param.serializer().serialize(encoder, param)
            }

            is Coordinates -> {
                val cvalues = mutableListOf<CoordSurrogate>()
                for (c in value.data) {
                    when (c.ctype) {
                        CoordType.SSO -> cvalues.add(SolarSystemSurrogate(c.name, c.data))
                        CoordType.EQ ->
                            EqCoordKey.decode(c)?.let {
                                cvalues.add(
                                    EqSurrogate(
                                        it.tag.value,
                                        it.ra.uas,
                                        it.dec.uas,
                                        it.frame.toString(),
                                        it.catalogName,
                                        PMSurrogate(it.pm.pmx, it.pm.pmy)
                                    )
                                )
                            }

                        CoordType.AltAz ->
                            AltAzCoordKey.decode(c)?.let {
                                cvalues.add(
                                    AltAzSurrogate(
                                        it.tag.value,
                                        it.alt.uas,
                                        it.az.uas
                                    )
                                )
                            }

                        CoordType.COM ->
                            CometCoordKey.decode(c)?.let {
                                cvalues.add(
                                    CometSurrogate(
                                        it.tag.value,
                                        it.epochOfPerihelion,
                                        it.inclination.uas,
                                        it.longAscendingNode.uas,
                                        it.argOfPerihelion.uas,
                                        it.perihelionDistance,
                                        it.eccentricity
                                    )
                                )
                            }

                        CoordType.MP ->
                            MinorPlanetCoordKey.decode(c)?.let {
                                cvalues.add(
                                    MinorPlanetSurrogate(
                                        it.tag.value,
                                        it.epoch,
                                        it.inclination.uas,
                                        it.longAscendingNode.uas,
                                        it.argOfPerihelion.uas,
                                        it.meanDistance,
                                        it.eccentricity,
                                        it.meanAnomaly.uas
                                    )
                                )
                            }

                        else -> throw SerializationException("Wrong coord_container stype: ${c.ctype}")
                    }

                }
                val param = Param("CoordKey", value.name, cvalues, Units.NoUnits)
                Param.serializer().serialize(encoder, param)
            }

            else -> throw SerializationException("Unknown type during serialization")
        }

    }

    private fun checkStoreType(p: Param): NumberArrayKey.StoreType =
        if (p.keyType == "FloatArrayKey") NumberArrayKey.StoreType.FLOAT else NumberArrayKey.StoreType.DOUBLE


    override fun deserialize(decoder: Decoder): HasKey {
        val param = Param.serializer().deserialize(decoder)
        val result = when (param.keyType) {
            "DoubleKey", "FloatKey" ->
                Qstore(param.keyName, StoredType.NUMBER, param.values.map { it.toString() }.toTypedArray(), param.units)

            "LongKey", "IntKey", "ShortKey" ->
                Qstore(
                    param.keyName,
                    StoredType.INTEGER,
                    param.values.map { it.toString() }.toTypedArray(),
                    param.units
                )

            "StringKey", "CharKey" ->
                Sstore(param.keyName, StoredType.STRING, param.values.map { it.toString() }.toTypedArray())

            "BooleanKey" ->
                Sstore(param.keyName, StoredType.BOOLEAN, param.values.map { it.toString() }.toTypedArray())

            "ChoiceKey" ->
                ChoiceStore(param.keyName, param.values.map { it.toString() }.toTypedArray(), param.units)

            "TAITimeKey" ->
                TimeStore(param.keyName, TimeType.TAI, param.values.map { it.toString() }.toTypedArray())

            "UTCTimeKey" ->
                TimeStore(param.keyName, TimeType.UTC, param.values.map { it.toString() }.toTypedArray())

            "ByteKey" ->
                ByteKey.ByteStore(param.keyName, ByteKey.StoreType.BYTE, param.values.map { it.toString().toByte() }.toByteArray() )
            
            "DoubleArrayKey", "FloatArrayKey" -> {
                val arrs = param.values.filterIsInstance<DoubleArray>()
                NumberArrayKey.NAStore(param.keyName, checkStoreType(param), param.units, arrs.toTypedArray())
            }

            "LongArrayKey" -> {
                val arrs = param.values.filterIsInstance<LongArray>()
                IntegerArrayKey.IAStore(param.keyName, IntegerArrayKey.StoreType.LONG, param.units, arrs.toTypedArray())
            }

            "CoordKey" -> {
                val coords = param.values.filterIsInstance<CoordSurrogate>()
                val valsOut = mutableListOf<CoordStore>()
                coords.map {
                    when (it) {
                        is SolarSystemSurrogate -> valsOut.add(CoordStore(it.tag, CoordType.SSO, it.body))
                        is EqSurrogate ->
                            valsOut.add(
                                CoordStore(
                                    it.tag, CoordType.EQ,
                                    EqCoordKey.encode(
                                        Angle(it.ra),
                                        Angle(it.dec),
                                        EqFrame.valueOf(it.frame),
                                        it.catalogName,
                                        it.pm.pmx,
                                        it.pm.pmy
                                    )
                                )
                            )

                        is AltAzSurrogate ->
                            valsOut.add(
                                CoordStore(
                                    it.tag,
                                    CoordType.AltAz,
                                    AltAzCoordKey.encode(Angle(it.alt), Angle(it.az))
                                )
                            )
                        is CometSurrogate ->
                            valsOut.add(
                                CoordStore(
                                    it.tag,
                                    CoordType.COM,
                                    CometCoordKey.encode(
                                        it.epochOfPerihelion,
                                        Angle(it.inclination),
                                        Angle(it.longAscendingNode),
                                        Angle(it.argOfPerihelion),
                                        it.perihelionDistance,
                                        it.eccentricity
                                    )
                                )
                            )
                        is MinorPlanetSurrogate ->
                            valsOut.add(
                                CoordStore(
                                    it.tag,
                                    CoordType.MP,
                                    MinorPlanetCoordKey.encode(
                                        it.epoch,
                                        Angle(it.inclination),
                                        Angle(it.longAscendingNode),
                                        Angle(it.argOfPerihelion),
                                        it.meanDistance,
                                        it.eccentricity,
                                        Angle(it.meanAnomaly)
                                    )
                                )
                            )
                    }
                }
                Coordinates(param.keyName, valsOut)
            }

            else -> throw SerializationException("Unknown key type during deserialization")
        }
        return result
    }
}

@Serializable
sealed interface CoordSurrogate

@Serializable
data class PMSurrogate(val pmx: Double, val pmy: Double)

@Serializable
@SerialName("EqCoord")
data class EqSurrogate(
    //val _type: String,
    val tag: String,
    val ra: Long,
    val dec: Long,
    val frame: String,
    val catalogName: String,
    val pm: PMSurrogate
) : CoordSurrogate {
    fun toValue(): String =
        EqCoordKey.encode(Angle(ra), Angle(dec), EqFrame.valueOf(frame), catalogName, pm.pmx, pm.pmy)
}

@Serializable
@SerialName("AltAzCoord")
data class AltAzSurrogate(
    //val _type: String,
    val tag: String,
    val alt: Long,
    val az: Long,
) : CoordSurrogate

@Serializable
@SerialName("SolarSystemCoord")
data class SolarSystemSurrogate(
    //val _type: String,
    val tag: String,
    val body: String
) : CoordSurrogate

@Serializable
@SerialName("MinorPlanetCoord")
data class MinorPlanetSurrogate(
    //val _type: String,
    val tag: String,
    val epoch: Double,
    val inclination: Long,
    val longAscendingNode: Long,
    val argOfPerihelion: Long,
    val meanDistance: Double,
    val eccentricity: Double,
    val meanAnomaly: Long
) : CoordSurrogate

@Serializable
@SerialName("CometCoord")
data class CometSurrogate(
    //val _type: String,
    val tag: String,
    val epochOfPerihelion: Double,
    val inclination: Long,
    val longAscendingNode: Long,
    val argOfPerihelion: Long,
    val perihelionDistance: Double,
    val eccentricity: Double
) : CoordSurrogate


@Serializable
private data class ParamSurrogate(
    @SerialName("DoubleKey")
    val double: Value<Double>? = null,
    @SerialName("DoubleArrayKey")
    val doubleArray: Value<DoubleArray>? = null,
    @SerialName("FloatArrayKey")
    val floatArray: Value<DoubleArray>? = null,
    @SerialName("FloatKey")
    val float: Value<Double>? = null,
    @SerialName("ShortKey")
    val short: Value<Long>? = null,
    @SerialName("IntKey")
    val int: Value<Long>? = null,
    @SerialName("LongArrayKey")
    val longArray: Value<LongArray>? = null,
    @SerialName("LongKey")
    val long: Value<Long>? = null,
    @SerialName("CharKey")
    val char: Value<String>? = null,
    @SerialName("StringKey")
    val string: Value<String>? = null,
    @SerialName("BooleanKey")
    val boolean: Value<Boolean>? = null,
    @SerialName("ChoiceKey")
    val choice: Value<String>? = null,
    @SerialName("CoordKey")
    val coord: Value<CoordSurrogate>? = null,
    @SerialName("TAITimeKey")
    val tais: Value<String>? = null,
    @SerialName("UTCTimeKey")
    val utc: Value<String>? = null,
    @SerialName("ByteKey")
    val byte: Value<Byte>? = null,
) {
    @Serializable
    class Value<T>(
        val keyName: String,
        val values: List<T>,
        val units: Units,
    )
}

@Serializable(with = Param.Serializer::class)
data class Param(
    val keyType: String,
    val keyName: Key,
    val values: List<Any>,
    val units: Units,
) {
    override fun toString(): String = "Param(keyType: $keyType, keyName: $keyName, values: ${values.toTypedArray().contentDeepToString()}, units: $units))"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Param
        if (keyType != other.keyType) return false
        // TODO Need to find a way to remove this
        if (!values.toTypedArray().contentDeepEquals(other.values.toTypedArray())) return false
        if (units != other.units) return false
        return true
    }

    override fun hashCode(): Int {
        var result = keyType.hashCode()
        result = 31 * result + values.hashCode()
        result = 31 * result + units.hashCode()
        return result
    }

    object Serializer : KSerializer<Param> {
        override val descriptor: SerialDescriptor = ParamSurrogate.serializer().descriptor
        override fun deserialize(decoder: Decoder): Param {
            val surrogate = decoder.decodeSerializableValue(ParamSurrogate.serializer())
            val (value, keyType) =
                if (surrogate.double != null)
                    Pair(surrogate.double, "DoubleKey")
                else if (surrogate.doubleArray != null)
                    Pair(surrogate.doubleArray, "DoubleArrayKey")
                else if (surrogate.floatArray != null)
                    Pair(surrogate.floatArray, "FloatArrayKey")
                else if (surrogate.float != null)
                    Pair(surrogate.float, "FloatKey")
                else if (surrogate.short != null)
                    Pair(surrogate.short, "ShortKey")
                else if (surrogate.int != null)
                    Pair(surrogate.int, "IntKey")
                else if (surrogate.longArray != null)
                    Pair(surrogate.longArray, "LongArrayKey")
                else if (surrogate.long != null)
                    Pair(surrogate.long, "LongKey")
                else if (surrogate.char != null)
                    Pair(surrogate.char, "CharKey")
                else if (surrogate.string != null)
                    Pair(surrogate.string, "StringKey")
                else if (surrogate.boolean != null)
                    Pair(surrogate.boolean, "BooleanKey")
                else if (surrogate.choice != null)
                    Pair(surrogate.choice, "ChoiceKey")
                else if (surrogate.coord != null)
                    Pair(surrogate.coord, "CoordKey")
                else if (surrogate.tais != null)
                    Pair(surrogate.tais, "TAITimeKey")
                else if (surrogate.utc != null)
                    Pair(surrogate.utc, "UTCTimeKey")
                else if (surrogate.byte != null)
                    Pair(surrogate.byte, "ByteKey")
                else throw SerializationException("Unknown Type")

            return Param(keyType, value.keyName, value.values, value.units)
        }

        override fun serialize(encoder: Encoder, value: Param) {
            when (value.keyType) {
                "DoubleKey" ->
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            double = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<Double>(),
                                value.units
                            )
                        )
                    )

                "NumberArrayKey" -> {
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            doubleArray = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<DoubleArray>(),
                                value.units
                            )
                        )
                    )
                }

                "LongKey" ->
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            long = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<Long>(),
                                value.units
                            )
                        )
                    )

                "IntegerArrayKey" -> {
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            longArray = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<LongArray>(),
                                value.units
                            )
                        )
                    )
                }

                "StringKey" ->
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            string = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<String>(),
                                value.units
                            )
                        )
                    )

                "BooleanKey" ->
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            boolean = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<Boolean>(),
                                value.units
                            )
                        )
                    )

                "ChoiceKey" ->
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            choice = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<String>(),
                                value.units
                            )
                        )
                    )

                "TAITimeKey" ->
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            tais = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<String>(),
                                value.units
                            )
                        )
                    )

                "UTCTimeKey" ->
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            utc = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<String>(),
                                value.units
                            )
                        )
                    )

                "ByteKey" ->
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            byte = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<Byte>(),
                                value.units
                            )
                        )
                    )

                "CoordKey" ->
                    encoder.encodeSerializableValue(
                        ParamSurrogate.serializer(),
                        ParamSurrogate(
                            coord = ParamSurrogate.Value(
                                value.keyName,
                                value.values.filterIsInstance<CoordSurrogate>(),
                                value.units
                            )
                        )
                    )

                else -> throw SerializationException("Unknown Type: ${value.keyType}")
            }
        }
    }
}