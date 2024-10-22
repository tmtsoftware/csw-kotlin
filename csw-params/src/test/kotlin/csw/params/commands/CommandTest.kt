package csw.params.commands

import csw.params.core.models.ObsId
import csw.params.core.models.Prefix
import csw.params.keys.*
import csw.time.core.models.UTCTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

@kotlinx.serialization.ExperimentalSerializationApi
class CommandTests: FunSpec( {
    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"): Setup =  Setup(p, cname, ObsId.NullObsId)

    val key1 = IntegerKey("key1", Units.degree)
    val key2 = NumberKey("key2")
    val key3 = UTCTimeKey("key3")

    test("Basic adds") {
        var s = testS()

        s = s.add(key1.set(100, 200), key3.set(UTCTime.now()))
        s = s.add(key2.set(-100.123))

        s.apply {
            exists(key1) shouldBe true
            exists(key3) shouldBe true

            size shouldBe 3
        }
    }

    test("Duplicate key name should replace") {
        var s = testS()

        val lkey1 = IntegerKey("key1", Units.degree)
        s = s.add(lkey1.set(100L, 200L))

        s.exists(lkey1) shouldBe true
        s.size shouldBe 1
        lkey1.value(s) shouldBe arrayOf(100L, 200L)

        s = s.add(lkey1.set(-100, -200))
        s.size shouldBe 1
        lkey1.value(s) shouldBe arrayOf(-100L,-200L)
    }

    test("madd vararg tests") {
        var s = testS()

        s = s.madd(key1.set(100), key2.set(200.0))
        println("s: $s")
        s.size shouldBe 2
        s.exists(key1) shouldBe true
        s.exists(key2) shouldBe true
    }

    test("madd parm set") {
        var s = testS()

        s = s.add(key1.set(100, 200), key2.set(1000.0, -1000.0))

        // Use the parms from setup 1
        var s2 = testS()
        s2 = s2.add(key3.set(UTCTime.now()))
        s2.size shouldBe 1

        // Now add s2's parms to s
        s = s.madd(s2.parms)

        s.size shouldBe 3
        s.exists(key1) shouldBe true
        s.exists(key2) shouldBe true
        s.exists(key3) shouldBe true
    }

    test("madd varargs set") {
        var s = testS()

        s = s.madd(key1.set(100, 200), key2.set(1000.0, -1000.0))

        s.size shouldBe 2

        s.exists(key1) shouldBe true
        s.exists(key2) shouldBe true
    }

    test("remove one item") {
        var s = testS()

        s = s.add(key1.set(100, 200), key2.set(1000.0, -1000.0))

        s.size shouldBe 2

        s.exists(key1) shouldBe true
        s.exists(key2) shouldBe true

        s = s.remove(key1)
        s.size shouldBe 1
        s.exists(key1) shouldBe false
        s.exists(key2) shouldBe true

        // Test for item not present
        s = s.remove(key1)
        s.size shouldBe 1
        s.exists(key1) shouldBe false
        s.exists(key2) shouldBe true

        // Test for empty remove
        s = s.remove(key2)
        s.size shouldBe 0
        s.exists(key1) shouldBe false
        s.exists(key2) shouldBe false

        s = s.remove(key2)
        s.size shouldBe 0
        s.exists(key1) shouldBe false
        s.exists(key2) shouldBe false
    }


    test("Should serialize and deserialize Setup") {
        val parmFormat = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
//        classDiscriminator = "_type"
            //encodeDefaults = true
        }

        var s = testS()

        s = s.add(key1.set(100, 200), key2.set(1000.0, -1000.0))

        val json = parmFormat.encodeToString(s)
        println("json: $json")
        val objIn = parmFormat.decodeFromString<Setup>(json)
        println("objIn: $objIn")
        objIn shouldBe s
    }

    fun ByteArray.toAsciiHexString() = joinToString("") {
        if (it in 32..127) it.toInt().toChar().toString() else
            "{${it.toUByte().toString(16).padStart(2, '0').uppercase()}}"
    }

    // Prefix should serialize to Json and cbor
    @OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
    test("should serialize to CBOR") {
        var s = testS()

        s = s.add(key1.set(100, 200), key2.set(1000.0, -1000.0))

        val bytes = Cbor.encodeToByteArray(s)
        println("bytes: $bytes")
        println(bytes.toAsciiHexString())
        val x = Base64.Default.encode(bytes)//.encodeToByteArray()
        println("x: $x")
        //val obj = Cbor.decodeFromByteArray<Setup>(bytes)
        //println(obj)
        //s shouldBe obj
    }

}
)


