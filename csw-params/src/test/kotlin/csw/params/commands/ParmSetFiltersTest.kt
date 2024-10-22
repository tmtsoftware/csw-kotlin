package csw.params.commands

import arrow.core.None
import csw.params.core.models.ObsId
import csw.params.core.models.Prefix
import csw.params.keys.IntegerKey
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class ParmSetFiltersTest: FunSpec( {

    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"): Setup =  Setup(p, cname)

    val k1 = IntegerKey("k1")
    val k2 = IntegerKey("k2")
    val k3 = IntegerKey("a.b.k3")
    val k4 = IntegerKey("a.b.k4")
    val k5 = IntegerKey("a.k5")
    val k6 = IntegerKey("a.k6")
    val k7 = IntegerKey("a.b.c.k7")

    test("unique keys test") {

        val s = testS().madd(k1.set(1), k2.set(2), k3.set(3), k4.set(4), k5.set(5), k6.set(6), k7.set(7))
        val r1 = ParmSetFilters.keys(s.parms)
        r1.size shouldBe 7
        r1 shouldContainExactlyInAnyOrder listOf("k1", "k2", "a.b.k3", "a.b.k4", "a.k5", "a.k6", "a.b.c.k7")
    }

    test("final names key test") {
        val s = testS().madd(k1.set(1), k2.set(2), k3.set(3), k4.set(4), k5.set(5), k6.set(6), k7.set(7))
        val r1 = ParmSetFilters.finalKeyNames(s.parms)
        r1.size shouldBe 7
        r1 shouldContainExactlyInAnyOrder listOf("k1", "k2", "k3", "k4", "k5", "k6", "k7")
    }

    test("first part of unique key roots test") {
        val s = testS().madd(k1.set(1), k2.set(2), k3.set(3), k4.set(4), k5.set(5), k6.set(6), k7.set(7))
        val r1 = ParmSetFilters.keyRoots(s.parms)
        r1 shouldContainExactlyInAnyOrder listOf("k1", "k2", "a.b", "a", "a.b.c")
    }

    test("match key starting strings test") {
        val s = testS().madd(k1.set(1), k2.set(2), k3.set(3), k4.set(4), k5.set(5), k6.set(6), k7.set(7))
        val r1 = ParmSetFilters.startWith("a.b", s.parms)
        r1 shouldContainExactlyInAnyOrder listOf("a.b.k3", "a.b.k4", "a.b.c.k7")
    }

    test("splitKey test") {
        val r0 = ParmSetFilters.splitKey(k1.name)
        r0.first shouldBe "k1"
        r0.second shouldBe "k1"

        val r1 = ParmSetFilters.splitKey(k3.name)
        r1.first shouldBe "a.b"
        r1.second shouldBe "k3"

        val r2 = ParmSetFilters.splitKey(k7.name)
        r2.first shouldBe "a.b.c"
        r2.second shouldBe "k7"
    }

    test("match key exactly") {
        val s = testS().madd(k1.set(1), k2.set(2), k3.set(3), k4.set(4), k5.set(5), k6.set(6), k7.set(7))
        val r1 = ParmSetFilters.keysStartExactly("a.b", s.parms)
        r1 shouldContainExactlyInAnyOrder listOf("a.b.k3", "a.b.k4")
    }

}
)