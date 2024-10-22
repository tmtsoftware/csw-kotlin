package csw.params.keys

import arrow.core.None
import arrow.core.flatten
import csw.params.commands.Setup
import csw.params.commands.CommandName
import csw.params.core.models.Prefix
import io.kotest.core.spec.style.FunSpec

class StructKeyTest: FunSpec( {
    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"): Setup =  Setup(p, cname)

    val sk1 = IntegerKey("sk1")
    val sk2 = IntegerKey("sk2", Units.degC)
    val sk3 = NumberKey("sk3")
    val sk4 = ChoiceKey("sk4", Choices("A", "B", "C"))

    test("Basic create ideas") {

        val st1 = StructKey("Test", sk1, sk2, sk3)
        println("St1: $st1")

        val xx = st1.add(sk1.set(2, 3), sk4.set("B"))
        println("x: $xx")

    }

    test("Test with Setup") {

        var s = testS()

        val st1 = StructKey("Test", sk1, sk2, sk3)
        println("St1: $st1")

        s = s.add(st1.add(sk1.set(2, 3), sk4.set("B")), sk2.set(1000))
        println("s: $s")
/*
        val y = st1.get(s, sk4.name).map { sk4.get(it) }.flatten()
        println("y: $y")

        val z = sk1.get2(s, st1)
        println("z: $z")

 */
    }
}
)