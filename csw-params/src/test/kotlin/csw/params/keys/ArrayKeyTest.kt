package csw.params.keys

import csw.params.commands.CommandName
import csw.params.core.models.Prefix
import csw.params.commands.Setup
import csw.params.keys.KeyHelpers.toByteArray
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import csw.params.keys.KeyHelpers.toDoubleArray
import csw.params.keys.KeyHelpers.toFloatArray
import csw.params.keys.KeyHelpers.toIntArray
import csw.params.keys.KeyHelpers.toLongArray
import csw.params.keys.KeyHelpers.toShortArray

class ArrayKeyTest : FunSpec({

    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"): Setup = Setup(p, cname)

    test("Basic tests for NumberArrayKey") {
        val a1 = doubleArrayOf(-10.0, -200.0, 400.0)
        val a2 = floatArrayOf(120.0f, -210.123f, 401.567f)

        val k1 = NumberArrayKey("key1", Units.volt)
        val k2 = NumberArrayKey("key2", Units.ampere)

        val st1 = k1.set(a1)
        println("st1: $st1")
        st1 shouldBe NumberArrayKey.NAStore("key1", NumberArrayKey.StoreType.DOUBLE, Units.volt, arrayOf(a1))

        val st2 = k2.set(a2)
        println("st2: $st2")
        st2 shouldBe NumberArrayKey.NAStore("key2", NumberArrayKey.StoreType.FLOAT, Units.ampere, arrayOf(a2.toDoubleArray()))
    }

    test("Basic tests for IntegerArrayKey") {
        val a3 = shortArrayOf(1, 2, 3, 4, 5)
        val a4 = intArrayOf(10, 20, 30, 40, 50)
        val a5 = longArrayOf(100, 200, 300, 400, 500)

        val k1 = IntegerArrayKey("key1", Units.volt)
        val k2 = IntegerArrayKey("key2", Units.ampere)
        val k3 = IntegerArrayKey("key3", Units.NoUnits)

        val st1 = k1.set(a3)
        st1 shouldBe IntegerArrayKey.IAStore("key1", IntegerArrayKey.StoreType.SHORT, Units.volt, arrayOf(a3.toLongArray()))

        val st2 = k2.set(a4)
        println("st2: $st2")
        st2 shouldBe IntegerArrayKey.IAStore("key2", IntegerArrayKey.StoreType.INT, Units.ampere, arrayOf(a4.toLongArray()))

        val st3 = k3.set(a5)
        println("st3: $st3")
        st3 shouldBe IntegerArrayKey.IAStore("key3", IntegerArrayKey.StoreType.LONG, Units.NoUnits, arrayOf(a5))
    }

    test("NumberArrayKey in Setup") {
        val key1 = NumberArrayKey("key1", Units.ampere)

        val a1 = doubleArrayOf(1.0, 2.0, 3.0, -1.0, -2.0, -3.0)
        val a2 = doubleArrayOf(100.1, 200.2, 300.3)
        val a3 = floatArrayOf(100.0f, 210.123f, 401.567f)

        var s = testS()
        s.size shouldBe 0

        s = s.add(key1.set(a1, a2))
        s.size shouldBe 1

        key1.isIn(s) shouldBe true
        key1.get(s)?.size shouldBe 2
        val out1 = key1.value(s)
        out1 shouldBe arrayOf(a1, a2)

        key1.value(s) shouldBe arrayOf(a1, a2)
        key1.get(s, 1) shouldBe a2
        key1.valueAt(s, 0) shouldBe a1
        key1.head(s) shouldBe a1
        key1.get(s, 1, 1) shouldBe a2[1]
        key1.item(s, 0, 1) shouldBe a1[1]
        // Out of range
        key1.get(s, 4) shouldBe null
        key1.get(s, 1, 10) shouldBe null

        val ex = shouldThrow<ArrayIndexOutOfBoundsException> {
            key1.valueAt(s, 10) shouldBe null
        }
        ex.message shouldBe "Index 10 out of bounds for length 2"

    }

    test("IntegerArray in Setup1") {
        val key1 = IntegerArrayKey("key1", Units.ampere)

        val i1 = longArrayOf(100, 200, 300, 400, 500)

        var s = testS()
        s.size shouldBe 0

        s = s.add(key1.set(i1))
        s.size shouldBe 1

        key1.isIn(s) shouldBe true
        key1.get(s)?.size shouldBe 1
        val out1 = key1.value(s)
        out1 shouldBe arrayOf(i1)

        key1.valueAt(s, 0) shouldBe i1
        key1.item(s, 0, 1) shouldBe i1[1]
    }

    test("IntegerArray in Setup") {
        val key1 = IntegerArrayKey("key1", Units.ampere)

        val i1 = longArrayOf(100, 200)
        val i2 = longArrayOf(300, 400)
        val i3 = longArrayOf(500, 600)

        var s = testS()
        s.size shouldBe 0

        s = s.add(key1.set(i1, i2, i3))
        s.size shouldBe 1

        key1.isIn(s) shouldBe true
        key1.get(s)?.size shouldBe 3

        key1.get(s, 0) shouldBe i1
        key1.get(s, 1) shouldBe i2
        key1.get(s, 2) shouldBe i3

        key1.get(s, 0, 0) shouldBe i1[0]
        key1.get(s, 4) shouldBe null

        //        val out1 = key1.value(s)
//        out1 shouldBe a1

//        key1.value(s)[0] shouldBe a1[0]
//        key1.value(s, 1) shouldBe a1[1]
    }

    test("Equality Tests") {
        val key1 = IntegerArrayKey("key1", Units.ampere)
        val key2 = IntegerArrayKey("key2", Units.ampere)
        val key3 = IntegerArrayKey("key1", Units.volt)

        val a1 = longArrayOf(100, 200)
        val a2 = longArrayOf(300, 400)
        val a3 = longArrayOf(100, 200)  // same value, different ref

        // Check for equals
        val st1 = key1.set(a1, a2)
        val st2 = key1.set(a1, a2)
        val st3 = key1.set(a3, a2)

        st1 shouldBe st2  // same refs
        st1 shouldBe st3  // This shows deep equal, different refs

        // Shows different values makes false
        val st4 = key1.set(a1, a3)
        st4 shouldNotBe st1

        // Shows different name makes false
        val st5 = key2.set(a1, a2)
        st5 shouldNotBe st1

        // Check for units not equal
        val st6 = key3.set(a1, a2)
        st6 shouldNotBe st1

    }

    test("conversion tests") {
        val i1 = longArrayOf(100, 200, 500, Long.MAX_VALUE)
        val i2 = doubleArrayOf(100.123, 200.234, 300.345)

        val out1 = i1.toIntArray()
        out1 shouldBe intArrayOf(100, 200, 500, -1)

        val out2 = i1.toShortArray()
        out2 shouldBe shortArrayOf(100, 200, 500, -1)

        val out3 = i1.toByteArray()
        out3 shouldBe byteArrayOf(100, -56, -12, -1)  // This is what it is, but no exception

        val out4 = i2.toFloatArray()
        out4 shouldBe floatArrayOf(100.123f, 200.234f, 300.345f)
    }

    test("Array Key can hold them all") {

        val a1 = doubleArrayOf(-10.0, -200.0, 400.0)
        val a2 = floatArrayOf(120.0f, -210.123f, 401.567f)

        val a3 = shortArrayOf(1, 2, 3, 4, 5)
        val a4 = intArrayOf(10, 20, 30, 40, 50)
        val a5 = longArrayOf(100, 200, 300, 400, 500)

        val c1 = ArrayKey("container")
        val k1 = IntegerArrayKey("key1", Units.volt)
        val k2 = NumberArrayKey("key2", Units.ampere)

        val st1 = c1.set(k1.set(a5), k2.set(a1))
        println("st1: $st1")


    }
}
)