package csw.params.codecs

import csw.params.core.models.*
import csw.params.core.models.Angle.Companion.degree
import csw.params.keys.*
import csw.time.core.models.TAITime
import csw.time.core.models.UTCTime
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class ParmCodecsTest : FunSpec({
    val parmFormat = Json {
        prettyPrint = true
        isLenient = true
        allowStructuredMapKeys = true
        classDiscriminator = "_type"
    }

    test("NumberKey CSW Serialization") {

        // Double Test
        val key1 = NumberKey("key1name", Units.meter)
        val st1 = key1.set(1.23, 4.56, 7.89)

        val jsonOut = parmFormat.encodeToString(ParamSerializer, st1)
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

        val objIn = parmFormat.decodeFromString(ParamSerializer, jsonOut)
        objIn shouldBe st1
    }

    test("NumberKey Float CSW Serialization") {

        // Float Test
        val key1 = NumberKey("key1name", Units.meter)
        val st1 = key1.set(1.23f, 4.56f, 7.89f)

        val jsonOut = parmFormat.encodeToString(ParamSerializer, st1)
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

        val objIn = parmFormat.decodeFromString(ParamSerializer, jsonOut)
        objIn shouldBe st1
    }

    test("IntegerKey Int CSW Serialization") {

        // Integer Test
        val key1 = IntegerKey("key1name", Units.meter)
        val st1 = key1.set(1, 4, 7)

        val jsonOut = parmFormat.encodeToString(ParamSerializer, st1)
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

        val objIn = parmFormat.decodeFromString(ParamSerializer, jsonOut)
        objIn shouldBe st1
    }

    test("StringKey CSW TopLevelSerializer") {

        // String Test
        val key1 = StringKey("key1name")
        val st1 = key1.set("ABC", "BCD", "CDE")

        val jout = parmFormat.encodeToString(ParamSerializer, st1)
        println("jout: $jout")
        val expected = """
            {
            "StringKey" : {
              "keyName" : "key1name",
              "values" : [ "ABC", "BCD", "CDE" ],
              "units" : "NoUnits"
            }
          }
        """.trimIndent()
        jout shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(ParamSerializer, jout)
        objIn shouldBe st1
    }

    test("BooleanKey CSW TopLevelSerializer") {
        // Boolean Test
        val key1 = BooleanKey("key1name")
        val st1 = key1.set(true, true, false)

        val jout = parmFormat.encodeToString(ParamSerializer, st1)
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

        val objIn = parmFormat.decodeFromString(ParamSerializer, jout)
        objIn shouldBe st1
    }

    test("ChoiceKey CSW TopLevelSerializer") {
        // Boolean Test
        val key1 = ChoiceKey("key1name", Choices("A", "B", "C"))
        val st1 = key1.set("A", "B")

        val jout = parmFormat.encodeToString(ParamSerializer, st1)
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

        val objIn = parmFormat.decodeFromString(ParamSerializer, jout)
        objIn shouldBe st1
    }

    test("TAIKey CSW TopLevelSerializer") {
        val key1 = TAITimeKey("key1name")

        val now = Clock.System.now()

        val ss = now.epochSeconds
        val nn = now.nanosecondsOfSecond
        val testI = Instant.fromEpochSeconds(ss, nn)
        val testStr = testI.toString()

        val st1 = key1.set(TAITime(testI))

        val jout = parmFormat.encodeToString(ParamSerializer, st1)
        val expected = """
            {
            "TAITimeKey" : {
              "keyName" : "key1name",
              "values" : [ "$testStr" ],
              "units" : "tai"
            }
          }
        """.trimIndent()
        jout shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(ParamSerializer, jout)
        objIn shouldBe st1
    }

    test("UTCKey CSW TopLevelSerializer") {
        val key1 = UTCTimeKey("key1name")

        val now = Clock.System.now()

        val ss = now.epochSeconds
        val nn = now.nanosecondsOfSecond
        val testI = Instant.fromEpochSeconds(ss, nn)
        val testStr = testI.toString()

        val st1 = key1.set(UTCTime(testI))

        val jout = parmFormat.encodeToString(ParamSerializer, st1)
        val expected = """
            {
            "UTCTimeKey" : {
              "keyName" : "key1name",
              "values" : [ "$testStr" ],
              "units" : "utc"
            }
          }
        """.trimIndent()
        jout shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(ParamSerializer, jout)
        objIn shouldBe st1
    }

    val bytes1 = "sensor image".toByteArray()

    test("Byte Key Tets") {
        val key1 = ByteKey("key1")

        val st1 = key1.set(bytes1)
        val jout = parmFormat.encodeToString(ParamSerializer, st1)
        println("jout: $jout")
        val expected = """
            {
            "ByteKey": {
                "keyName": "key1",
                "values": [
                    115, 101, 110, 115, 111, 114, 32, 105, 109, 97, 103, 101
                    ],
                "units": "NoUnits"
               }
            }
        """.trimIndent()
        jout shouldEqualJson expected

        val objIn = parmFormat.decodeFromString(ParamSerializer, jout)
        objIn shouldBe st1
    }


    test("CoordKey Tests") {
        val c1 = CoordKey("Coords")
        val k1 = SolarSystemCoordKey(Tag.BASE)
        val k2 = EqCoordKey(Tag.OIWFS1)

        val ra = 180.0.degree()
        val dec = 32.0.degree()

        val st2 = c1.set(k1.set(SolarSystemObject.Jupiter), k2.set(ra, dec, EqFrame.FK5, 1.1, -1.1))

        val jout = parmFormat.encodeToString(ParamSerializer, st2)
        val objIn = parmFormat.decodeFromString(ParamSerializer, jout)
        objIn shouldBe st2

        val az = 180.2.degree()
        val alt = 45.0.degree()
        val k3 = AltAzCoordKey(Tag.BASE)
        val st3 = c1.set(k3.set(alt, az))
        val jout3 = parmFormat.encodeToString(ParamSerializer, st3)
        val objin3 = parmFormat.decodeFromString(ParamSerializer, jout3)
        objin3 shouldBe st3

        val epoch = 2035.2
        val inclination: Inclination = 2.3.degree()
        val longAscendingNode: LongAscendingNode = 110.23.degree()
        val argOfPerihelion: ArgOfPerihelion = 23.1.degree()
        val perihelionDistance: PerihelionDistance = 5.2
        val eccentricity: Eccentricity = 4.2

        val k4 = CometCoordKey(Tag.BASE)
        val st4 = c1.set(k4.set(epoch, inclination, longAscendingNode, argOfPerihelion, perihelionDistance, eccentricity))
        val jout4 = parmFormat.encodeToString(ParamSerializer, st4)
        val objin4 = parmFormat.decodeFromString(ParamSerializer, jout4)
        objin4 shouldBe st4

        val meanDistance: MeanDistance = 5.2
        val meanA: MeanAnomaly = 23.45.degree()

        val k5 = MinorPlanetCoordKey(Tag.BASE)
        val st5 = c1.set(k5.set(epoch, inclination, longAscendingNode, argOfPerihelion, meanDistance, eccentricity, meanA))
        val jout5 = parmFormat.encodeToString(ParamSerializer, st5)
        println("jout5: $jout5")
        val objin5 = parmFormat.decodeFromString(ParamSerializer, jout5)
        objin5 shouldBe st5
    }

    test("All CSW Keys Test") {

        val e1 = """{
            "StringKey" : {
              "keyName" : "StringKey",
              "values" : [ "Str1", "Str2" ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in1 = parmFormat.decodeFromString(ParamSerializer, e1)
        in1 shouldBe Sstore("StringKey", StoredType.STRING, arrayOf("Str1", "Str2"))

        val e2 = """{
            "IntKey" : {
              "keyName" : "IntKey",
              "values" : [ 70, 80 ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in2 = parmFormat.decodeFromString(ParamSerializer, e2)
        in2 shouldBe Qstore("IntKey", StoredType.INTEGER, arrayOf("70", "80"), Units.NoUnits)

        val e3 = """{
            "FloatKey" : {
              "keyName" : "FloatKey",
              "values" : [ 90, 100 ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in3 = parmFormat.decodeFromString(ParamSerializer, e3)
        in3 shouldBe Qstore("FloatKey", StoredType.NUMBER, arrayOf("90.0", "100.0"), Units.NoUnits)

        val e4 = """{
            "CharKey" : {
              "keyName" : "CharKey",
              "values" : [ "A", "B" ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in4 = parmFormat.decodeFromString(ParamSerializer, e4)
        in4 shouldBe Sstore("CharKey", StoredType.STRING, arrayOf("A", "B"))

        val e5 = """{
            "LongKey" : {
              "keyName" : "LongKey",
              "values" : [ 50, 60 ],
              "units" : "meter"
            }
          }""".trimIndent()
        val in5 = parmFormat.decodeFromString(ParamSerializer, e5)
        in5 shouldBe Qstore("LongKey", StoredType.INTEGER, arrayOf("50", "60"), Units.meter)

        val e6 = """{
            "ChoiceKey" : {
              "keyName" : "ChoiceKey",
              "values" : [ "First", "Second" ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in6 = parmFormat.decodeFromString(ParamSerializer, e6)
        in6 shouldBe ChoiceStore("ChoiceKey", arrayOf("First", "Second"), Units.NoUnits)
    }

    test("Param tester from Kotlin slack") {

        val e1 = """{
            "LongKey" : {
              "keyName" : "LongKey",
              "values" : [ 50, 60 ],
              "units" : "meter"
            }
          }""".trimIndent()
        val in1 = parmFormat.decodeFromString(Param.Serializer, e1)
        in1 shouldBe Param("LongKey", keyName="LongKey", values = listOf(50L, 60L), units = Units.meter)

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
        in2 shouldBe Param("DoubleKey", keyName="key1name", values = listOf(1.23, 4.56, 7.89), units = Units.meter)

        val e3 = """{
            "ChoiceKey" : {
              "keyName" : "ChoiceKey",
              "values" : [ "First", "Second" ],
              "units" : "NoUnits"
            }
          }""".trimIndent()
        val in3 = parmFormat.decodeFromString(Param.Serializer, e3)
        in3 shouldBe Param("ChoiceKey", keyName = "ChoiceKey", values = listOf("First", "Second"), Units.NoUnits)

        val e4 = """{
            "CoordKey": {
                "keyName" : "CoordKey",
                "values" : [{
                    "_type" : "EqCoord",
                    "tag" : "BASE",
                    "ra" : 659912250000,
                    "dec" : -109892300000,
                    "frame" : "FK5",
                    "catalogName" : "BrightStar",
                    "pm" : {
                      "pmx" : 0.5,
                      "pmy" : -2.33 
                    } 
                } ], "units" : "NoUnits"
            }
          }""".trimMargin()
        val in4 = parmFormat.decodeFromString(Param.Serializer, e4)
        in4 shouldBe Param("CoordKey", keyName = "CoordKey", listOf(
            EqSurrogate(
                //_type = "EqCoord",
                tag = "BASE",
                ra = 659912250000,
                dec = -109892300000,
                frame = "FK5",
                catalogName = "BrightStar",
                pm = PMSurrogate(0.5, -2.33)
            )), Units.NoUnits)

        val e5 = """{
            "CoordKey": {
                "keyName" : "CoordKey",
                "values" : [{
                    "_type" : "AltAzCoord",
                    "tag" : "BASE",
                    "alt" : 1083600000000,
                    "az" : -153000000000
                } ], "units": "NoUnits"
            }
          }""".trimMargin()
        val in5 = parmFormat.decodeFromString(Param.Serializer, e5)
        in5 shouldBe Param("CoordKey", keyName = "CoordKey", listOf(
            AltAzSurrogate(
                //_type = "AltAzCoord",
                tag = "BASE",
                alt = 1083600000000,
                az = -153000000000
            )), Units.NoUnits)

        val e6 = """{
            "CoordKey": {
                "keyName" : "CoordKey",
                "values" : [{
                    "_type" : "SolarSystemCoord",
                    "tag" : "BASE",
                    "body" : "Venus"
                } ], "units": "NoUnits"
            }
          }""".trimMargin()
        val in6 = parmFormat.decodeFromString(Param.Serializer, e6)
        in6 shouldBe Param("CoordKey", keyName = "CoordKey", listOf(
            SolarSystemSurrogate(
                //_type = "SolarSystemCoord",
                tag = "BASE",
                body = "Venus"
                )), Units.NoUnits)

        val e7 = """{
            "CoordKey": {
                "keyName" : "CoordKey",
                "values" : [{
                    "_type" : "MinorPlanetCoord",
                    "tag" : "BASE",
                    "epoch" : 2000,
                    "inclination" : 324000000000,
                    "longAscendingNode" : 7200000000,
                    "argOfPerihelion" : 360000000000,
                    "meanDistance" : 1.4,
                    "eccentricity" : 0.234,
                    "meanAnomaly" : 792000000000
                } ], "units" : "NoUnits"
            }
          }""".trimMargin()
        val in7 = parmFormat.decodeFromString(Param.Serializer, e7)
        in7 shouldBe Param("CoordKey", "CoordKey", listOf(
            MinorPlanetSurrogate(
               // _type = "MinorPlanetCoord",
                tag=  "BASE",
                epoch = 2000.0,
                inclination = 324000000000,
                longAscendingNode = 7200000000,
                argOfPerihelion = 360000000000,
                meanDistance = 1.4,
                eccentricity = 0.234,
                meanAnomaly = 792000000000
            )), units=Units.NoUnits)

        val e8 = """{
            "CoordKey": {
                "keyName" : "CoordKey",
                "values" : [{
                    "_type" : "CometCoord",
                    "tag" : "BASE",
                    "epochOfPerihelion" : 2000,
                    "inclination" : 324000000000,
                    "longAscendingNode" : 7200000000,
                    "argOfPerihelion" : 360000000000,
                    "perihelionDistance" : 1.4,
                    "eccentricity" : 0.234
                } ], "units" : "NoUnits"
            }
          }""".trimMargin()
        val in8 = parmFormat.decodeFromString(Param.Serializer, e8)
        in8 shouldBe Param("CoordKey", "CoordKey", listOf(
            CometSurrogate(
             //   _type = "CometCoord",
                tag = "BASE",
                epochOfPerihelion = 2000.0,
                inclination = 324000000000,
                longAscendingNode = 7200000000,
                argOfPerihelion = 360000000000,
                perihelionDistance = 1.4,
                eccentricity = 0.234
            )), units=Units.NoUnits)

        val e9 = """{
            "TAITimeKey": {
                "keyName" : "TAITimeKey",
                "values" : [ "1970-01-01T00:00:00Z", "2023-04-13T20:24:28.956187940Z" ],
                "units" : "tai"
            }
          }""".trimMargin()
        val in9 = parmFormat.decodeFromString(Param.Serializer, e9)
        in9 shouldBe Param("TAITimeKey", "TAITimeKey", arrayOf("1970-01-01T00:00:00Z", "2023-04-13T20:24:28.956187940Z").toList(), Units.tai)

        val e10 = """{
            "UTCTimeKey": {
                "keyName" : "UTCTimeKey",
                "values" : [ "1970-01-01T00:00:00Z", "2023-04-13T20:23:51.953536907Z" ],
                "units" : "utc"
            }
          }""".trimMargin()
        val in10 = parmFormat.decodeFromString(Param.Serializer, e10)
        in10 shouldBe Param("UTCTimeKey", "UTCTimeKey", arrayOf("1970-01-01T00:00:00Z", "2023-04-13T20:23:51.953536907Z").toList(), Units.utc)

        val e11 = """{
            "FloatKey": {
                 "keyName" : "FloatKey",
                  "values" : [ 90.12, -100.102 ],
                  "units" : "meter"
            }
          }""".trimMargin()
        val in11 = parmFormat.decodeFromString(Param.Serializer, e11)
        in11 shouldBe Param("FloatKey", "FloatKey", listOf(90.12, -100.102), Units.meter)

        val e12 = """{
            "IntKey": {
                 "keyName" : "IntKey",
                  "values" : [ 90, -100 ],
                  "units" : "meter"
            }
          }""".trimMargin()
        val in12 = parmFormat.decodeFromString(Param.Serializer, e12)
        in12 shouldBe Param("IntKey", "IntKey", listOf(90L, -100L), Units.meter)

        val e13 = """{
            "CharKey": {
                 "keyName" : "CharKey",
                  "values" : [ "A", "B" ],
                  "units" : "NoUnits"
            }
          }""".trimMargin()
        val in13 = parmFormat.decodeFromString(Param.Serializer, e13)
        in13 shouldBe Param("CharKey", "CharKey", arrayOf("A", "B").toList(), Units.NoUnits)

        val e14 = """{
            "ShortKey": {
                 "keyName" : "ShortKey",
                  "values" : [ 12, 13, 14 ],
                  "units" : "NoUnits"
            }
          }""".trimMargin()
        val in14 = parmFormat.decodeFromString(Param.Serializer, e14)
        in14 shouldBe Param("ShortKey", "ShortKey", listOf(12L, 13L, 14L), Units.NoUnits)

        val e15 = """{
            "DoubleArrayKey": {
                 "keyName" : "DoubleArrayKey",
                  "values" : [ [12.23, 13.34, 14.45] ],
                  "units" : "NoUnits"
            }
          }""".trimMargin()
        val in15 = parmFormat.decodeFromString(Param.Serializer, e15)
        println("in15: $in15")
        //in15 shouldBe Param("DoubleArrayKey", "DoubleArrayKey", listOf(doubleArrayOf(12.23, 13.34, 14.45)), Units.NoUnits)

        val e16 = """{
            "IntArrayKey": {
                 "keyName" : "IntArrayKey",
                  "values" : [ [12, 23, 13] ],
                  "units" : "NoUnits"
            }
          }""".trimMargin()
        val in16 = parmFormat.decodeFromString(Param.Serializer, e16)
        println("in16: $in16")
        in16.values.size shouldBe 1
        in16.values[0] shouldBe longArrayOf(12, 23, 13)
        //in16 shouldBe Param("IntArrayKey", "IntArrayKey", listOf(longArrayOf(12L, 13L, 14L)), Units.NoUnits)

        val e17 = """{
            "CoordKey": {
                "keyName" : "CoordKey",
                "values" : [{
                    "_type" : "EqCoord",
                    "tag" : "BASE",
                    "ra" : 659912250000,
                    "dec" : -109892300000,
                    "frame" : "FK5",
                    "catalogName" : "BrightStar",
                    "pm" : {
                      "pmx" : 0.5,
                      "pmy" : -2.33 
                    } 
                },
                 {
                    "_type" : "SolarSystemCoord",
                    "tag" : "BASE",
                    "body" : "Venus"
                }],
                "units" : "NoUnits"
            }
          }""".trimMargin()
        val in17 = parmFormat.decodeFromString(Param.Serializer, e17)
        println("in17: $in17")

        val e18 = """{
            "StringKey": {
                 "keyName" : "StringKey",
                  "values" : [ "ABCD", "EFGH" ],
                  "units" : "NoUnits"
            }
          }""".trimMargin()
        val in18 = parmFormat.decodeFromString(Param.Serializer, e18)
        in18 shouldBe Param("StringKey", "StringKey", arrayOf("ABCD", "EFGH").toList(), Units.NoUnits)

        val e19 = """{
             "TAITimeKey" : {
              "keyName" : "TAITimeKey",
              "values" : [ "2024-10-11T17:55:40.932526Z" ],
              "units" : "tai"
          }
        }""".trimMargin()
        val in19 = parmFormat.decodeFromString(Param.Serializer, e19)
        in19 shouldBe Param("TAITimeKey", "TAITimeKey", arrayOf("2024-10-11T17:55:40.932526Z").toList(), Units.tai)

        val e20 = """{
             "UTCTimeKey" : {
              "keyName" : "UTCTimeKey",
              "values" : [ "2024-10-11T17:55:40.932526Z" ],
              "units" : "utc"
          }
        }""".trimMargin()
        val in20 = parmFormat.decodeFromString(Param.Serializer, e20)
        in20 shouldBe Param("UTCTimeKey", "UTCTimeKey", arrayOf("2024-10-11T17:55:40.932526Z").toList(), Units.utc)
    }
})
