package csw.params.commands

import arrow.core.None
import csw.params.core.models.ObsId
import csw.params.core.models.Prefix
import csw.params.core.models.Subsystem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class PrefixDataFiltersTest: FunSpec( {

    val s1 = Setup(Prefix("ESW.seq"), "setup1" )
    val s2 = Setup(Prefix("ESW.seq"), "setup2" )
    val s3 = Setup(Prefix("TCS.point"), "setup3" )
    val s4 = Setup(Prefix("IRIS.seq"), "setup4")

    val o1 = Observe(Prefix("IRIS.det"), "observe1")
    val o2 = Observe(Prefix("IRIS.det"), "observe2")
    val o3 = Observe(Prefix("WFOS.blue.det"), "observe3")
    val o4 = Observe(Prefix("WFOS.red.det"), "observe4")

    val w1 = Wait(Prefix("ESW.seq"), "wait1")
    val w2 = Wait(Prefix("IRIS.seq"), "wait2")

    val seqCommands = listOf(s1, s2, s3, s4, o1, o2, o3, o4, w1, w2)

    test("Test Prefix Filter") {
        val prefixes = PrefixDataFilters.prefixes(seqCommands)
        prefixes.size shouldBe 6
        prefixes shouldContainExactlyInAnyOrder setOf("ESW.seq", "TCS.point", "IRIS.seq", "IRIS.det", "WFOS.blue.det", "WFOS.red.det")
    }

    test("Test Only Setups") {
        val setups = PrefixDataFilters.onlySetups(seqCommands)
        setups.size shouldBe 4
        setups shouldContainExactlyInAnyOrder listOf(s1, s2, s3, s4)
    }

    test("Test Only Observes") {
        val observes = PrefixDataFilters.onlyObserves(seqCommands)
        observes.size shouldBe 4
        observes shouldContainExactlyInAnyOrder listOf(o1, o2, o3, o4)
    }

    test("Test Only Waits") {
        val waits = PrefixDataFilters.onlyWaits(seqCommands)
        waits.size shouldBe 2
        waits shouldContainExactlyInAnyOrder listOf(w1, w2)
    }

    test("Test Prefix Starts with") {
        val result = PrefixDataFilters.prefixStartsWith("ESW", seqCommands)
        result.size shouldBe 3
        result shouldContainExactlyInAnyOrder listOf(s1, s2, w1)
    }

    test("Test Prefix Contains") {
        val result = PrefixDataFilters.prefixContains("blue", seqCommands)
        result.size shouldBe 1
        result shouldContainExactlyInAnyOrder listOf(o3)

        val result2 = PrefixDataFilters.prefixContains("seq", seqCommands)
        result2.size shouldBe 5
        result2 shouldContainExactlyInAnyOrder listOf(s1, s2, s4, w1, w2)
    }

    test("Test IsSubsystem") {
        val result = PrefixDataFilters.prefixIsSubsystem(Subsystem.ESW, seqCommands)
        result.size shouldBe 3
        result shouldContainExactlyInAnyOrder listOf(s1, s2, w1)
    }

    /** Piggyback PrefixData test */
    test("Test Prefix Data") {
        // works for setup
        s1.prefix() shouldBe Prefix("ESW.seq")
        s1.subsystem shouldBe Subsystem.ESW
        s1.prefixStr shouldBe "ESW.seq"
        // works for observe
        o1.prefix() shouldBe Prefix("IRIS.det")
        o1.subsystem shouldBe Subsystem.IRIS
        o1.prefixStr shouldBe "IRIS.det"
        // works for wait
        w2.prefix() shouldBe Prefix("IRIS.seq")
        w2.subsystem shouldBe Subsystem.IRIS
        w2.prefixStr shouldBe "IRIS.seq"
    }
}
)