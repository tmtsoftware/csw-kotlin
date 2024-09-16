package csw.params.keys

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe



class ScalarTests: FunSpec( {

    val key: Key = "key1"

    test("Should create scalars") {

        val s1 = Sstore(key, StoredType.INTEGER, arrayOf("100","200","300"))

        val r1 = Scalar.create(s1)
        println("r1: $r1")

        r1.value shouldBe arrayOf("100", "200", "300")
    }


    test("conversion tests") {

        val s1 = Sstore(key, StoredType.INTEGER, arrayOf("100","200","300"))

        val r1 = Scalar.create(s1)
        println("r1: $r1")

        r1.asInt() shouldBe 100
        r1.asIntArray() shouldBe intArrayOf(100, 200, 300)

        r1.asLong() shouldBe 100L
        r1.asLongArray() shouldBe longArrayOf(100, 200, 300)

        r1.asDouble() shouldBe 100.0
        r1.asDoubleArray() shouldBe doubleArrayOf(100.0, 200.0, 300.0)

        r1.asFloat() shouldBe 100.0f
        r1.asFloatArray() shouldBe floatArrayOf(100.0f, 200.0f, 300.0f)

        r1.asBoolean() shouldBe false
        r1.asBooleanArray() shouldBe booleanArrayOf(false, false, false)

        r1.asString() shouldBe "100"
        r1.asStringArray() shouldBe arrayOf("100", "200", "300")

        //printArray(r1.asStringArray())
    }


    test ("conversionDoubleTests") {

        val s1 = Sstore(key, StoredType.NUMBER, arrayOf("100.23","200.456","300.7890"))

        val r1 = Scalar.create(s1)

        r1.asInt() shouldBe 100
        r1.asIntArray() shouldBe intArrayOf(100, 200, 300)

        r1.asLong() shouldBe 100L
        r1.asLongArray() shouldBe longArrayOf(100, 200, 300)

        r1.asDouble() shouldBe 100.23
        r1.asDoubleArray() shouldBe doubleArrayOf(100.23, 200.456, 300.7890)

        r1.asFloat() shouldBe 100.23f
        r1.asFloatArray() shouldBe floatArrayOf(100.23f, 200.456f, 300.7890f)

        r1.asBoolean() shouldBe false
        r1.asBooleanArray() shouldBe booleanArrayOf(false, false, false)

        r1.asString() shouldBe "100.23"
        r1.asStringArray() shouldBe arrayOf("100.23", "200.456", "300.7890")

        //printArray(r1.asStringArray())
    }

    test ("conversionBooleanTests()") {

        val s1 = Sstore(key, StoredType.BOOLEAN, arrayOf("true","false","true"))

        val r1 = Scalar.create(s1)
        println("r1: $r1")
/*
        r1.asInt() shouldBe 1
        r1.asIntArray() shouldBe intArrayOf(1, 0, 1)

        r1.asLong() shouldBe 1L
        r1.asLongArray() shouldBe longArrayOf(1, 0, 1)

        r1.asDouble() shouldBe 1.0
        r1.asDoubleArray() shouldBe doubleArrayOf(1.0, 0.0, 1.0)

        r1.asFloat() shouldBe 1.0f
        r1.asFloatArray() shouldBe floatArrayOf(1.0f, 0.0f, 1.0f)
*/
        r1.asBoolean() shouldBe true
        r1.asBooleanArray() shouldBe booleanArrayOf(true, false, true)

        r1.asString() shouldBe "t"
        r1.asStringArray() shouldBe arrayOf("t", "f", "t")

        //printArray(r1.asBooleanArray().toTypedArray())
    }


    fun <T> toArrayString(a: Iterable<T>): String = a.joinToString(",", "", "")

    fun <T> printArray(a: Array<T>) = println(toArrayString(a.asIterable()))
}
)