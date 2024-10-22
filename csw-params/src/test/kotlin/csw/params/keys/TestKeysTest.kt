package csw.params.keys

import arrow.core.None
import csw.params.commands.CommandName
import csw.params.commands.Setup
import csw.params.core.models.Prefix
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.json.Json
import csw.params.keys.TestKeys.Qstore
import kotlinx.serialization.*

class TestKeysTest: FunSpec( {

    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"): Setup =  Setup(p, cname)

    test("What does serialize look like") {


        val myj = Json {
            prettyPrint = true
        }

        val key1 = TestKeys.NumberKey("key1", Units.kilogram)

        val st1 = key1.set(23.3, 34.5)
        println("st1: $st1")

        val jout = myj.encodeToString(holderSerializer, ParamCore.fromParam(st1))
        println(jout)

        val objIn = ParamCore.toParam(myj.decodeFromString(holderSerializer, jout))
        println("ObjIn: $objIn")

        val key2 = TestKeys.NumberKey("key2", Units.angstrom)
        val data = doubleArrayOf(1.0, 2.3, 3.4, 4.123, 5.6)

        val xx = key2.set(data)
        println("xx: $xx")

        val jout2 = myj.encodeToString(holderSerializer, ParamCore.fromParam(xx))
        println(jout2)

        val key3 = TestKeys.NumberKey("key3", Units.kilogram)
        val data2 = floatArrayOf(1.2f, 1.4f, 1.6f, 1.8f, 1.9f, 1.99f)
        val xxx = key3.set(1.2f, 2.3f)

        var s = testS()
        s = s.add(st1, xxx)
        println("s: $s")

        val d1 = 4.567f
        val d2 = d1.toString().toDouble()
        println("d1: $d1")
        println("d2: $d2")

        val yyy = key3.get(s)
        println("yyy: $yyy")

    }
}
)

typealias SPMap = HashMap<String, ParamCore>
val holderSerializer: KSerializer<SPMap> = serializer()

@Serializable
data class ParamCore(val keyName: String, val values: List<String>, val units: Units) {

    companion object {
        fun toParam(map: SPMap): TestKeys.Qstore {
            val (keyType, param) = map.entries.first()
            val stored = when (keyType) {
                "DoubleKey" -> StoredType.NUMBER
                "FloatKey" -> StoredType.NUMBER
                "IntKey" -> StoredType.INTEGER
                "LongKey" -> StoredType.INTEGER
                "ShortKey" -> StoredType.INTEGER
                "StringKey" -> StoredType.STRING
                "BooleanKey" -> StoredType.BOOLEAN
                else -> throw IllegalArgumentException("Key type is not supported: $keyType")
            }
            return Qstore(param.keyName, stored, param.values.toTypedArray(), param.units)
        }
        fun fromParam(param: Qstore): SPMap {
            val keyType: String = when (param.stype) {
                StoredType.NUMBER -> "DoubleKey"
                StoredType.INTEGER -> "IntKey"
                StoredType.STRING -> "StringKey"
                StoredType.BOOLEAN -> "BooleanKey"
                else -> throw IllegalArgumentException("Key type is not supported: $param")
            }
            return hashMapOf(keyType to ParamCore(param.name, param.values.toList(), param.units))
        }
    }


}




/*
object KeySerializer: KSerializer<Qstore> {

    @Serializable
    override val descriptor: SerialDescriptor = MapSerializer(String.serializer().descriptor, ParamCore.serializer())

    override fun serialize(encoder: Encoder, v: Qstore) {
        val parmH: Map<String, ParamCore> = ParamCore.fromParam(v)
        encoder.encodeSerializableValue(parmH.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Qstore {
        val surrogate = decoder.decodeSerializableValue(ParmHolder.serializer())
        return Qstore(surrogate.parm.keyName, StoredType.NUMBER, surrogate.parm.values, Units.valueOf(surrogate.parm.units))
    }
}

 */