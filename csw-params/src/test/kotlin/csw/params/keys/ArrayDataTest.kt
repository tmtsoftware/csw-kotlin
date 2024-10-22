package csw.params.keys

import csw.params.commands.CommandName
import csw.params.core.models.Prefix
import csw.params.commands.Setup
import csw.params.core.models.ArrayData
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ArrayDataTest : FunSpec({

    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"): Setup = Setup(p, cname)

    test("Full tests for ArrayKey") {
        val key1 = NumberArrayKey("key1", Units.ampere)

        val a1: Array<Number> = arrayOf(1.0, 2.0, 3.0)
        val a2: Array<Number> = arrayOf(20.0, 30.0, 40.0)
        val a3: Array<Number> = arrayOf(200, 300, 400)

        val a4 = doubleArrayOf(-10.0, -200.0, 400.0)

        val a5 = floatArrayOf(120.0f, -210.123f, 401.567f)

        val out1 = key1.set(ArrayData.fromArray(a1), ArrayData.fromArray(a2), ArrayData.fromArray(a3), ArrayData(a4), ArrayData.fromArray(a5))
        println("out1: $out1")

        var s = testS()
        s.size shouldBe 0

        s = s.add(out1)
        val xx:Array<ArrayData> = key1.get(s)
        println("XX: $xx")
        for (xx1 in xx) {
            for (i in xx1.data )
                println("i: ${i}")
        }
    }

    test("DoubleArray") {
        val key1 = DoubleArrayKey("key1", Units.ampere)

        val a1 = doubleArrayOf(1.0, 2.0, 3.0, -1.0, -2.0, -3.0)

        val out1 = key1.set(a1)
        println("out1: $out1")

        var s = testS()
        s.size shouldBe 0

        s = s.add(out1)
        println("s: $s")
        val xx = key1.get(s)
        xx.onSome {
            for (xx1 in it) { println(xx1) }
        }
    }
}
)