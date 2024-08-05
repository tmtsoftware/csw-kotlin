package csw.params.core.models

import arrow.core.None
import arrow.core.Some
import csw.params.commands.CommandName
import csw.params.commands.Setup
import csw.params.keys.EqCoordKey
import io.kotest.core.spec.style.FunSpec
import csw.params.core.models.Angle.Companion.degree
import csw.params.keys.CoordType
import csw.params.keys.EqStore
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CoordKeyTest: FunSpec( {

    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"):Setup =  Setup(p, cname, None)

    test("Create EqCoordKey") {
        val k1 = EqCoordKey(Tag.BASE)

        val ra = 180.0.degree()
        val dec = 32.0.degree()

        val r1 = k1.set(ra, dec)
        val t1 = ra.uas.toString()
        val t2 = dec.uas.toString()
        r1 shouldBe EqStore("BASE", CoordType.EQ, "$t1,$t2,ICRS,none,0.0,0.0")

        val eq = EqCoordKey.decodeToEqCoord("BASE", r1.value)
        eq shouldBe EqCoord(Tag.BASE, ra, dec, EqFrame.ICRS, ProperMotion.DEFAULT_PROPERMOTION, "none" )

        val eq2 = EqCoordKey.decodeToEqCoord("BAZE", r1.value)
        eq2 shouldBe null
    }


    test("Add to setup tests") {
        val k1 = EqCoordKey(Tag.BASE)

        val store1 = k1.set(180.0.degree(), 32.0.degree())
        val t1 = 180.0.degree().uas.toString()
        val t2 = 32.0.degree().uas.toString()
        store1 shouldBe EqStore("BASE", CoordType.EQ, "$t1,$t2,ICRS,none,0.0,0.0")

        var s = testS()
        s.size shouldBe 0

        s = s.add(store1)
        s.size shouldBe 1

        assert(k1.isIn(s))
    }

    test(" Serialize and Deserialize it") {
        val k1 = EqCoordKey(Tag.BASE)

        var s = testS()
        s.size shouldBe 0

        s = s.add(k1.set(180.0.degree(), 32.0.degree()))
        s.size shouldBe 1

        val out = Json.encodeToString(s)
        val inObj = Json.decodeFromString<Setup>(out)
        inObj shouldBeEqual s
    }

    test(" Get checks and invoke") {
        val k1 = EqCoordKey(Tag.BASE)
        val k2 = EqCoordKey(Tag.OIWFS1)

        var s = testS()
            .add(k1.set(180.0.degree(), 32.0.degree()), k2.set(180.43.degree(), 32.123.degree()))
        s.size shouldBe 2

        assert(k1.isIn(s) && k2.isIn(s))

        val r1 = k1.get(s)
        println("r1: $r1")
        r1 shouldBe Some(EqCoord(180.0.degree(), 32.0.degree()))

        val r2:EqCoord = k1(s)
        println("r2: $r2")
        r2 shouldBe EqCoord(180.0.degree(), 32.0.degree())
    }

    test("Test with proper motions") {
        val k1 = EqCoordKey(Tag.BASE)

        var s = testS().add(k1.set(180.0.degree(), 32.0.degree(), 1.12, -1.344))
        s.size shouldBe 1

        assert(k1.isIn(s))

        val r1 = k1.get(s)
        println("r1: $r1")
        r1.onSome { it shouldBe EqCoord(Tag.BASE, 180.0.degree(), 32.0.degree(), EqFrame.ICRS, ProperMotion(1.12, -1.344), "none")}

    }
}
)