package csw.params.keys

import csw.params.commands.CommandName
import csw.params.core.models.Prefix
import csw.params.commands.Setup
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SimpleKeysTest: FunSpec ( {

    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"): Setup =  Setup(p, cname)

    test("Basic Number Tests") {
        val t1 = NumberKey("key1", Units.kilogram)
        val v1 = 22.0
        val st1 = t1.set(v1)
        println(st1)
    }

    //fun testBoolean(s: String): Boolean = s == "t"
    //fun asBoolean(a: Array<String>): Boolean  = testBoolean(a[0])
    //fun asBooleanArray(a: Array<String>): BooleanArray = a.map { testBoolean(it) }.toBooleanArray()

    test("Full tests for NumberKey") {
        // Number key is for real values, input can be float or double, single values or arrays
        data class Ntest<T>(val key: NumberKey, val value: T)

        val k1 = Ntest(NumberKey("key1", Units.ampere), 22.34f) // float
        val k2 = Ntest(NumberKey("key2"), value = 100.345) // double
        val k3 = Ntest(NumberKey("key3", Units.degree), floatArrayOf(22.34f, -100.34f, 200.0045f))
        val k4 = Ntest(NumberKey("key4"), value = doubleArrayOf(2000.102, 1234.567, -23.532, 12000.0))

        val r1 = Qstore(k1.key.name, StoredType.NUMBER, arrayOf("22.34"), Units.ampere)
        val r2 = Qstore(k2.key.name, StoredType.NUMBER, arrayOf("100.345"))
        val r3 = Qstore(k3.key.name, StoredType.NUMBER, arrayOf("22.34","-100.34","200.0045"), Units.degree)
        val r4 = Qstore(k4.key.name, StoredType.NUMBER, arrayOf("2000.102","1234.567","-23.532","12000.0"))

        val badKey = NumberKey("BadKey")

        var s = testS()
        s.size shouldBe 0

        s = s.add(k1.key.set(k1.value))
        s.size shouldBe 1

        s = s.add(k2.key.set(k2.value)).add(k3.key.set(k3.value), k4.key.set(k4.value))
        s.size shouldBe 4

        s.exists(k1.key) shouldBe true
        k1.key.isIn(s) shouldBe true

        s.exists(badKey) shouldBe false
        badKey.isIn(s) shouldBe false

        // k1.key.get(s).onSome { it shouldBe Quantity(decodeValue(sencode(k1.value)), k1.key.units) }
        k1.key.get(s) shouldBe Quantity(r1.data, r1.units)
        k1.key(s) shouldBe Quantity(r1.data, r1.units)

        //k1.key.scalar(s) shouldBe Scalar(r1.values)
        k1.key.head(s) shouldBe Scalar(r1.data).asDouble()
        k1.key.value(s) shouldBe Scalar(r1.data).asDoubleArray()

        val jsonOut = Json.encodeToString(s)
        println(jsonOut)
        val sin = Json.decodeFromString<Setup>(jsonOut)
        println(s)
        println(sin)
        s shouldBe sin

        k2.key.get(s) shouldBe Quantity(r2.data, r2.units)
        k2.key(s) shouldBe Quantity(r2.data, r2.units)

        //k2.key.scalar(s) shouldBe Scalar(r2.values)
        k2.key.head(s) shouldBe Scalar(r2.data).asDouble()
        k2.key.value(s) shouldBe Scalar(r2.data).asDoubleArray()

        k3.key.get(s) shouldBe Quantity(r3.data, r3.units)
        k3.key(s) shouldBe Quantity(r3.data, r3.units)

        //k3.key.scalar(s) shouldBe Scalar(r3.values)
        k3.key.head(s) shouldBe Scalar(r3.data).asDouble()
        k3.key.value(s) shouldBe Scalar(r3.data).asDoubleArray()

        val xx = s.missingKeys(k1.key, k2.key, k3.key, k4.key, badKey)
        println("Diff: $xx")

        k4.key.get(s) shouldBe Quantity(r4.data, r4.units)
        k4.key(s) shouldBe Quantity(r4.data, r4.units)

        //k4.key.scalar(s) shouldBe Scalar(r4.values)
        k4.key.head(s) shouldBe Scalar(r4.data).asDouble()
        k4.key.value(s) shouldBe Scalar(r4.data).asDoubleArray()

        shouldThrow<NoSuchElementException> {
            badKey(s)
        }
    }

    test("full tests for IntegerKey") {
        // Number key is for real values, input can be float or double, single values or arrays
        data class Ntest<T>(val key: IntegerKey, val value: T)

        val k1 = Ntest(IntegerKey("key1", Units.ampere), 199) // int
        val k2 = Ntest(IntegerKey("key2"), value = 10000L) // long
        val k3 = Ntest(IntegerKey("key3", Units.degree), intArrayOf(22, -100, 200))
        val k4 = Ntest(IntegerKey("key4"), value = longArrayOf(2000L, 1234L, -23L, 12000L))

        val r1 = Qstore(k1.key.name, StoredType.NUMBER, arrayOf("199"), Units.ampere)
        val r2 = Qstore(k2.key.name, StoredType.NUMBER, arrayOf("10000"))
        val r3 = Qstore(k3.key.name, StoredType.NUMBER, arrayOf("22","-100","200"), Units.degree)
        val r4 = Qstore(k4.key.name, StoredType.NUMBER, arrayOf("2000","1234","-23","12000"))

        val badKey = IntegerKey("BadKey")

        var s = testS()
        s.size shouldBe 0

        s = s.add(k1.key.set(k1.value))
        s.size shouldBe 1

        s = s.add(k2.key.set(k2.value)).add(k3.key.set(k3.value), k4.key.set(k4.value))
        s.size shouldBe 4

        s.exists(k1.key) shouldBe true
        k1.key.isIn(s) shouldBe true

        s.exists(badKey) shouldBe false
        badKey.isIn(s) shouldBe false

        k1.key.get(s) shouldBe Quantity(r1.data, r1.units)
        k1.key(s) shouldBe Quantity(r1.data, r1.units)

        //k1.key.scalar(s) shouldBe Scalar(r1.values)
        k1.key.head(s) shouldBe Scalar(r1.data).asLong()
        k1.key.value(s) shouldBe Scalar(r1.data).asLongArray()

        val jsonOut = Json.encodeToString(s)
        val sin = Json.decodeFromString<Setup>(jsonOut)
        s shouldBe sin

        k2.key.get(s) shouldBe Quantity(r2.data, r2.units)
        k2.key(s) shouldBe Quantity(r2.data, r2.units)

        //k2.key.scalar(s) shouldBe Scalar(r2.values)
        k2.key.head(s) shouldBe Scalar(r2.data).asLong()
        k2.key.value(s) shouldBe Scalar(r2.data).asLongArray()

        k3.key.get(s) shouldBe Quantity(r3.data, r3.units)
        k3.key(s) shouldBe Quantity(r3.data, r3.units)

        //k3.key.scalar(s) shouldBe Scalar(r3.values)
        k3.key.head(s) shouldBe Scalar(r3.data).asLong()
        k3.key.value(s) shouldBe Scalar(r3.data).asLongArray()

        k4.key.get(s) shouldBe Quantity(r4.data, r4.units)
        k4.key(s) shouldBe Quantity(r4.data, k4.key.units)

        //k4.key.scalar(s) shouldBe Scalar(r4.values)
        k4.key.head(s) shouldBe Scalar(r4.data).asLong()
        k4.key.value(s) shouldBe Scalar(r4.data).asLongArray()

        shouldThrow<NoSuchElementException> {
            badKey(s)
        }
    }

    test("full tests for BooleanKey") {
        // Number key is for real values, input can be float or double, single values or arrays
        data class Ntest<T>(val key: BooleanKey, val value: T) {
            fun svalue(): Array<String> =
                when (value) {
                    is Boolean -> arrayOf(value.toString())
                    is BooleanArray -> value.map { it.toString() }.toTypedArray()
                    else -> throw AssertionError()
                }
        }

        val k1 = Ntest(BooleanKey("key1"), true) // Boolean
        val k2 = Ntest(BooleanKey("key2"), value = false) // Boolean
        val k3 = Ntest(BooleanKey("key4"), value = booleanArrayOf(false, true, false, true))

       // val r1 = Qstore(k1.key.name, StoredType.BOOLEAN, "true")
        //val r2 = Qstore(k2.key.name, StoredType.BOOLEAN, "false")
        //val r3 = Qstore(k3.key.name, StoredType.BOOLEAN, "false,true,false,true")

        val badKey = BooleanKey("BadKey")

        var s = testS()
        s.size shouldBe 0

        s = s.add(k1.key.set(k1.value))
        s.size shouldBe 1

        s = s.add(k2.key.set(k2.value)).add(k3.key.set(k3.value))
        s.size shouldBe 3
        println("s: $s")

        s.exists(k1.key) shouldBe true
        k1.key.isIn(s) shouldBe true

        s.exists(badKey) shouldBe false
        badKey.isIn(s) shouldBe false

        // k1.key.get(s).onSome { it shouldBe Quantity(decodeValue(sencode(k1.value)), k1.key.units) }
        k1.key.get(s) shouldBe Scalar(k1.svalue())
        k1.key(s) shouldBe Scalar(k1.svalue())

        k1.key.scalar(s) shouldBe Scalar(k1.svalue())
        k1.key.head(s) shouldBe k1.svalue()[0].toBoolean()
        k1.key.value(s) shouldBe k1.svalue().map { it.toBoolean() }.toTypedArray()

        val jsonOut = Json.encodeToString(s)
        println(jsonOut)
        val sin = Json.decodeFromString<Setup>(jsonOut)
        println(sin)
        s shouldBe sin

        k2.key.get(s) shouldBe Scalar(k2.svalue())
        k2.key(s) shouldBe Scalar(k2.svalue())

        k2.key.scalar(s) shouldBe Scalar(k2.svalue())
        k2.key.head(s) shouldBe k2.svalue()[0].toBoolean()
        k2.key.value(s) shouldBe k2.svalue().map { it.toBoolean() }.toTypedArray()

        k3.key.get(s) shouldBe Scalar(k3.svalue())
        k3.key(s) shouldBe Scalar(k3.svalue())

        k3.key.scalar(s) shouldBe Scalar(k3.svalue())
        k3.key.head(s) shouldBe k3.svalue()[0].toBoolean()
        k3.key.value(s) shouldBe k3.svalue().map { it.toBoolean() }.toTypedArray()

        shouldThrow<NoSuchElementException> {
            badKey(s)
        }
    }

    test("full tests for StringKey") {
        // String key is for strings, either individuals or arrays of strings
        data class Ntest<T>(val key: StringKey, val value: T)

        val k1 = Ntest(StringKey("key1"), "the string value") //
        val k2 = Ntest(StringKey("key4"), value = arrayOf("ZERO", "ONE", "TWO", "THREE"))

        val r1 = Qstore(k1.key.name, StoredType.STRING, arrayOf(k1.value))
        val r2 = Qstore(k2.key.name, StoredType.STRING, k2.value)
        println("r2: $r2")

        val badKey = BooleanKey("BadKey")

        var s = testS()
        s.size shouldBe 0

        s = s.add(k1.key.set(k1.value))
        s.size shouldBe 1

        s = s.add(k2.key.set(k2.value))
        s.size shouldBe 2
        println("ss: $s")

        s.exists(k1.key) shouldBe true
        k1.key.isIn(s) shouldBe true

        s.exists(badKey) shouldBe false
        badKey.isIn(s) shouldBe false

//         k1.key.get(s).onSome { it shouldBe Quantity(decodeValue(sencode(k1.value)), k1.key.units) }
        k1.key.get(s) shouldBe Scalar(r1.data)
        println("s: $s")
        k1.key(s) shouldBe Scalar(r1.data)

        k1.key.scalar(s) shouldBe Scalar(r1.data)
        k1.key.head(s) shouldBe r1.data[0]
        k1.key.value(s) shouldBe r1.data

        val jsonOut = Json.encodeToString(s)
        println(jsonOut)
        val sin = Json.decodeFromString<Setup>(jsonOut)
        println(sin)
        s shouldBe sin

        k2.key.get(s) shouldBe Scalar(r2.data)
        k2.key(s) shouldBe Scalar(r2.data)

        k2.key.scalar(s) shouldBe Scalar(r2.data)
        k2.key.head(s) shouldBe r2.data[0]
        k2.key.value(s) shouldBe r2.data

        shouldThrow<NoSuchElementException> {
            badKey(s)
        }
    }
}
)