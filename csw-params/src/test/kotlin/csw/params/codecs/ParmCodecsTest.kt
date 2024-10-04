package csw.params.codecs

import csw.params.keys.*
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

class ParmCodecsTest : FunSpec({
    val parmFormat = Json {
        prettyPrint = true
        isLenient = true
        allowStructuredMapKeys = true
    }

    test("NumberKey CSW Serialization") {

        // Double Test
        val key1 = NumberKey("key1name", Units.meter)
        val st1 = key1.set(1.23, 4.56, 7.89)

        val jsonOut = parmFormat.encodeToString(NumberSerializer, st1)
        val expected = """
            {
            "DoubleKey" : {
              "keyName" : "key1name",
              "values" : [ 1.23, 4.56, 7.89 ],
              "units" : "meter"
            }
          }
        """.trimIndent()
        jsonOut shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(NumberSerializer, jsonOut)
        objIn shouldBe st1
    }

    test("NumberKey Float CSW Serialization") {

        // Float Test
        val key1 = NumberKey("key1name", Units.meter)
        val st1 = key1.set(1.23f, 4.56f, 7.89f)

        val jsonOut = parmFormat.encodeToString(NumberSerializer, st1)
        val expected = """
            {
            "DoubleKey" : {
              "keyName" : "key1name",
              "values" : [ 1.23, 4.56, 7.89 ],
              "units" : "meter"
            }
          }
        """.trimIndent()
        jsonOut shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(NumberSerializer, jsonOut)
        objIn shouldBe st1
    }

    test("NumberKey Float CSW Serialization TEST TEST") {

        // Float Test
        val key1 = NumberKey("key1name", Units.meter)
        val st1 = key1.set(1.23f, 4.56f, 7.89f)

        val jsonOut = parmFormat.encodeToString(ParamDeserializer2, st1)
        val expected = """
            {
            "DoubleKey" : {
              "keyName" : "key1name",
              "values" : [ 1.23, 4.56, 7.89 ],
              "units" : "meter"
            }
          }
        """.trimIndent()
        //jsonOut shouldEqualJson expected
        // println("jsonOut: $jsonOut")

        val objIn = parmFormat.decodeFromString(ParamDeserializer2, jsonOut)
        println("objIn: $objIn")
        //objIn shouldBe st1
    }

    test("IntegerKey Int CSW Serialization") {

        // Integer Test
        val key1 = IntegerKey("key1name", Units.meter)
        val st1 = key1.set(1, 4, 7)

        val jsonOut = parmFormat.encodeToString(IntegerSerializer, st1)
        val expected = """
            {
            "LongKey" : {
              "keyName" : "key1name",
              "values" : [ 1, 4, 7 ],
              "units" : "meter"
            }
          }
        """.trimIndent()
        jsonOut shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(IntegerSerializer, jsonOut)
        objIn shouldBe st1
    }

    test("IntegerKey Int CSW Serialization with TopLevel") {
        // Integer Test
        val key1 = IntegerKey("key1name", Units.meter)
        val st1 = key1.set(1, 4, 7)

        val expected = """
            {
            "LongKey" : {
              "keyName" : "key1name",
              "values" : [ 1, 4, 7 ],
              "units" : "meter"
            }
          }
        """.trimIndent()
        val jout = parmFormat.encodeToString(TopParamSerializer, st1)
        jout shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(TopParamSerializer, jout)
        objIn shouldBe st1
    }

    test("StringKey CSW TopLevelSerializer") {

        // String Test
        val key1 = StringKey("key1name")
        val st1 = key1.set("A", "B", "C")

        val jout = parmFormat.encodeToString(TopParamSerializer, st1)
        val expected = """
            {
            "StringKey" : {
              "keyName" : "key1name",
              "values" : [ "A", "B", "C" ],
              "units" : "NoUnits"
            }
          }
        """.trimIndent()
        jout shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(TopParamSerializer, jout)
        objIn shouldBe st1
    }

    test("BooleanKey CSW TopLevelSerializer") {
        // Boolean Test
        val key1 = BooleanKey("key1name")
        val st1 = key1.set(true, true, false)

        val jout = parmFormat.encodeToString(TopParamSerializer, st1)
        val expected = """
            {
            "BooleanKey" : {
              "keyName" : "key1name",
              "values" : [ true, true, false ],
              "units" : "NoUnits"
            }
          }
        """.trimIndent()
        jout shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(TopParamSerializer, jout)
        objIn shouldBe st1
    }

    test("ChoiceKey CSW TopLevelSerializer") {
        // Boolean Test
        val key1 = ChoiceKey("key1name", Choices("A", "B", "C"))
        val st1 = key1.set("A", "B")

        val jout = parmFormat.encodeToString(TopParamSerializer, st1)
        val expected = """
            {
            "ChoiceKey" : {
              "keyName" : "key1name",
              "values" : [ "A", "B" ],
              "units" : "NoUnits"
            }
          }
        """.trimIndent()
        jout shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(TopParamSerializer, jout)
        objIn shouldBe st1
    }

    test("All CSW Keys Test") {

        val e1 = """{
            "StringKey" : {
              "keyName" : "StringKey",
              "values" : [ "Str1", "Str2" ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in1 = parmFormat.decodeFromString(TopParamSerializer, e1)
        in1 shouldBe Sstore("StringKey", StoredType.STRING, arrayOf("Str1", "Str2"))

        val e2 = """{
            "IntKey" : {
              "keyName" : "IntKey",
              "values" : [ 70, 80 ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in2 = parmFormat.decodeFromString(TopParamSerializer, e2)
        in2 shouldBe Qstore("IntKey", StoredType.INTEGER, arrayOf("70", "80"), Units.NoUnits)

        val e3 = """{
            "FloatKey" : {
              "keyName" : "FloatKey",
              "values" : [ 90, 100 ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in3 = parmFormat.decodeFromString(TopParamSerializer, e3)
        in3 shouldBe Qstore("FloatKey", StoredType.NUMBER, arrayOf("90.0", "100.0"), Units.NoUnits)

        val e4 = """{
            "CharKey" : {
              "keyName" : "CharKey",
              "values" : [ "A", "B" ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in4 = parmFormat.decodeFromString(TopParamSerializer, e4)
        in4 shouldBe Sstore("CharKey", StoredType.STRING, arrayOf("A", "B"))

        val e5 = """{
            "LongKey" : {
              "keyName" : "LongKey",
              "values" : [ 50, 60 ],
              "units" : "meter"
            }
          }""".trimIndent()
        val in5 = parmFormat.decodeFromString(TopParamSerializer, e5)
        in5 shouldBe Qstore("LongKey", StoredType.INTEGER, arrayOf("50", "60"), Units.meter)

        val e6 = """{
            "ChoiceKey" : {
              "keyName" : "ChoiceKey",
              "values" : [ "First", "Second" ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in6 = parmFormat.decodeFromString(TopParamSerializer, e6)
        in6 shouldBe ChoiceStore("ChoiceKey", arrayOf("First", "Second"), Units.NoUnits)
    }

    test("Param tester from Kotlin slack") {
        val e5 = """{
            "LongKey" : {
              "keyName" : "LongKey",
              "values" : [ 50, 60 ],
              "units" : "meter"
            }
          }""".trimIndent()
        val in5 = parmFormat.decodeFromString(Param.Serializer, e5)
        println("in5: $in5")

        val e2 = """
            {
            "DoubleKey" : {
              "keyName" : "key1name",
              "values" : [ 1.23, 4.56, 7.89 ],
              "units" : "meter"
            }
          }
        """.trimIndent()
        val in2 = parmFormat.decodeFromString(Param.Serializer, e2)
        println("in2: $in2")


        val e3 = """{
            "ChoiceKey" : {
              "keyName" : "ChoiceKey",
              "values" : [ "First", "Second" ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in3 = parmFormat.decodeFromString(Param.Serializer, e3)
        println("in3: $in3")

        val e4 = """{
            "CoordKey": {
                "keyName" : "CoordKey",
                "values" : [{
                    "_type" : "EqCoord",
                    "tag" : "BASE",
                    "ra" : 659912250000,
                    "dec" : -109892300000,
                    "frame" : "FK5",
                    "catalogName" : "none"
                } ]
            }
          }""".trimMargin()
        val in4 = parmFormat.decodeFromString(Param.Serializer, e4)
        println("in4: $in4")
    }
})

@Serializable
@SerialName("Param")
private data class ParamSurrogate(
    @SerialName("DoubleKey")
    val double: Value<Double>? = null,
    @SerialName("LongKey")
    val long: Value<Long>? = null,
    @SerialName("StringKey")
    val string: Value<String>? = null,
    @SerialName("BooleanKey")
    val boolean: Value<Boolean>? = null,
    @SerialName("ChoiceKey")
    val choice: Value<String>? = null,
    @SerialName("CoordKey")
    val coord: Value<CoordKind>? = null,
) {
    @Serializable
    class Value<T>(
        val keyName: String,
        val values: List<T>,
        val units: Units = Units.NoUnits,
    )
    /*
    @Serializable
    class CoordValue(
        val keyName: String,
        val values: List<CoordKind>
        val units: Units,
    )

     */
    @Serializable
    data class CoordKind(
        val _type: String,
        val tag: String,
        val ra: Long,
        val dec: Long,
        val frame: String,
        val catalogName: String,
    )
}

@Serializable(with = Param.Serializer::class)
data class Param(
    val keyName: String,
    val values: List<Any>,
    val units: Units,
) {
    object Serializer : KSerializer<Param> {
        override val descriptor: SerialDescriptor = ParamSurrogate.serializer().descriptor
        override fun deserialize(decoder: Decoder): Param {
            val surrogate = decoder.decodeSerializableValue(ParamSurrogate.serializer())
            val value = surrogate.double
                ?: surrogate.long
                ?: surrogate.string
                ?: surrogate.boolean
                ?: surrogate.choice
                ?: surrogate.coord
                ?: throw SerializationException("Unknown type")
            return Param(value.keyName, value.values, value.units)
        }

        override fun serialize(encoder: Encoder, value: Param) {
            TODO("do the same but in reverse")
        }
    }
}

