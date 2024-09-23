package csw.params.codecs

import csw.params.keys.*
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json

class ParmCodecsTest: FunSpec( {
    val parmFormat = Json {
        prettyPrint = true
        //classDiscriminatorMode = ClassDiscriminatorMode.NONE
    }

    test("NumberKey CSW Serialization") {

        // Double Test
        val key1 = NumberKey("key1name", Units.meter)
        val st1 = key1.set(1.23, 4.56, 7.89)

        val jsonOut =  parmFormat.encodeToString(QstoreSerializer, st1)
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

        val objIn = parmFormat.decodeFromString(QstoreSerializer, jsonOut)
        objIn shouldBe st1
    }

    test("NumberKey Float CSW Serialization") {

        // Float Test
        val key1 = NumberKey("key1name", Units.meter)
        val st1 = key1.set(1.23f, 4.56f, 7.89f)

        val jsonOut =  parmFormat.encodeToString(QstoreSerializer, st1)
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
        println("jsonOut: $jsonOut")

        val objIn = parmFormat.decodeFromString(QstoreSerializer, jsonOut)
        objIn shouldBe st1
    }

    test("IntegerKey Int CSW Serialization") {

        // Float Test
        val key1 = IntegerKey("key1name", Units.meter)
        val st1 = key1.set(1, 4, 7)

        val jsonOut =  parmFormat.encodeToString(QstoreSerializer, st1)
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

        val objIn = parmFormat.decodeFromString(QstoreSerializer, jsonOut)
        objIn shouldBe st1
    }

    fun <K,V> Pair<K,V>.toEntry() = object: Map.Entry<K,V> {
        override val key: K = first
        override val value: V = second
    }

    fun getEntry(key: String, value: CSWValues): Map.Entry<String, CSWValues> =
        object: Map.Entry<String, CSWValues> {
            override val key: String = key
            override val value: CSWValues = value
        }

    test("name WTF") {


        val t1 = NCore("kimkey", doubleArrayOf(1.0, 2.0, 3.0), Units.ampere)
        val t2 = ICore("kimkey", longArrayOf(1, 2, 3), Units.ampere)


        //val d1 = Data(t1)

        val x = getEntry("LongKey", t2)
        val jsonOut = parmFormat.encodeToString(x)

        println("Jsonout: $jsonOut")

        val x2 = getEntry("DoubleKey", t1)
        val jsonOut2 = parmFormat.encodeToString(x2)

        println("JsonOut: $jsonOut2")

        val obj2 = parmFormat.decodeFromString<CSWValues>(jsonOut2)
        println("Objs2: $obj2")
    }


    test("Deserialize is the challenge I guess") {


//        val t1 = NCore("kimkey", doubleArrayOf(1.0, 2.0, 3.0), Units.ampere)
//        val t2 = ICore("kimkey", longArrayOf(1, 2, 3), Units.ampere)

        val t3 = Data2("LongKey", ICore("Kim1", longArrayOf(1, 2, 3), Units.ampere))
        val jout1 = parmFormat.encodeToString(TestSerializer, t3)
        println("JOut: $jout1")

        val example = """
            {
            "LongKey" : {
              "keyName" : "key1name",
              "values" : [ 1, 4, 7 ],
              "units" : "meter"
            }
          }
        """.trimIndent()

        println("Jsonout: $example")

        val obj2:Data2 = parmFormat.decodeFromString(TestSerializer, example)
        println("Objs2: $obj2")
    }

    test("PACKET serialization") {

        val t1 = NCore("kimkey", doubleArrayOf(1.0, 2.0, 3.0), Units.ampere)
        val t2 = ICore("kimkey", longArrayOf(1, 2, 3), Units.ampere)

        val t3 = Packet("LongKey", ICore("Kim1", longArrayOf(1, 2, 3), Units.ampere))
        println("T3: $t3")
        val jout1 = parmFormat.encodeToString(PacketSerializer, t3)
        println("JOut: $jout1")

        val obj1 = parmFormat.decodeFromString(PacketSerializer, jout1)
        println("Obj1: $obj1")

    }




    test("Deserialize is the challenge I with Packet") {


        val t1 = NCore("kimkey", doubleArrayOf(1.0, 2.0, 3.0), Units.ampere)
        val t2 = ICore("kimkey", longArrayOf(1, 2, 3), Units.ampere)

        val p1 = Packet3("LongKey", ICore("kimkey", longArrayOf(1, 2, 3), Units.ampere))
        val jout1 = parmFormat.encodeToString(TestSerializer3, p1)
//        println("JOut1: $jout1")

        val example = """
            {
            "LongKey" : {
              "keyName" : "key1name",
              "values" : [ 1, 4, 7 ],
              "units" : "meter"
            }
          }
        """.trimIndent()

        //println("Jsonout: $example")
        //jout1 shouldEqualJson example

//        val obj1:Packet3 = parmFormat.decodeFromString(TestSerializer3, jout1)
//        println("Obj1: $obj1")
        //obj1 shouldBe p1

        val p3 = Packet3("DoubleKey", t1)
        val jout2 = parmFormat.encodeToString(TestSerializer3, p3)
        println("JOut2: $jout2")

        val obj2:Packet3 = parmFormat.decodeFromString(TestSerializer3, jout2)
        println("Obj2: $obj2")
        //obj2 shouldBe p2

    }



})