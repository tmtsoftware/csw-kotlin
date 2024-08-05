package csw.params.keys

import arrow.core.None
import csw.params.commands.CommandName
import csw.params.core.models.Prefix
import csw.params.commands.Setup
import csw.time.core.models.TAITime
import csw.time.core.models.UTCTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class TimeKeyTests: FunSpec( {

    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"): Setup =  Setup(p, cname, None)

    test("Basic Time Key Tests") {

        val key1 = UTCTimeKey("key1")
        val key2 = TAITimeKey("key2")

        val now = Clock.System.now()

        val ss = now.epochSeconds
        val nn = now.nanosecondsOfSecond
        val testI = Instant.fromEpochSeconds(ss, nn)

        println("now: $ss")
        println("now: $nn")

        var s = testS()
        s.size shouldBe 0

        s = s.add(key1.set(UTCTime(testI)))
        s.size shouldBe 1

        assert(key1.isIn(s))

        val r1 = key1(s)
        r1.size shouldBe 1
        r1[0] shouldBeEqual UTCTime(testI)
    }

    test("Multiple adds") {
        val key1 = UTCTimeKey("key1")
        val key2 = TAITimeKey("key2")

        var s = testS()
        s.size shouldBe 0

        s = s.add(key1.set(UTCTime.now()), key2.set(TAITime.now()))
        s.size shouldBe 2
    }
}
)