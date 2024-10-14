package csw.params.keys

import arrow.core.None
import arrow.core.none
import csw.params.commands.CommandName
import csw.params.commands.Setup
import csw.params.core.models.Prefix
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

    fun timeNow(): Instant {
        val now = Clock.System.now()

        val ss = now.epochSeconds
        val nn = now.nanosecondsOfSecond
        return Instant.fromEpochSeconds(ss, nn)
    }

    test("Basic UTCTime Key Tests") {

        val key1 = UTCTimeKey("key1")

        val testI = timeNow()
        // For comparing stored value
        val testIStr = testI.toString()

        val t1 = key1.set(UTCTime(testI))
        t1.values.size shouldBe 1
        t1.values[0] shouldBe testIStr
    }

    test("Basic TAITime Key Tests") {

        val key1 = TAITimeKey("key1")

        val testI = timeNow()
        // For comparing stored value
        val testIStr = testI.toString()

        val t1 = key1.set(TAITime(testI))
        t1.values.size shouldBe 1
        t1.values[0] shouldBe testIStr
    }

    test("Add to setup") {
        val key1 = UTCTimeKey("key1")
        val key2 = UTCTimeKey("BadKey")

        val testI = timeNow()

        var s = testS()
        s.size shouldBe 0

        s = s.add(key1.set(UTCTime(testI)))
        s.size shouldBe 1
        assert(key1.isIn(s))

        val bad = key2.get(s)
        bad shouldBe None

        val r1 = key1(s)
        r1.size shouldBe 1
        r1[0] shouldBeEqual UTCTime(testI)
    }

    test("Multiple adds") {
        val key1 = UTCTimeKey("key1")
        val key2 = TAITimeKey("key2")
        val key3 = UTCTimeKey("BadKey")

        var s = testS()
        s.size shouldBe 0

        s = s.add(key1.set(UTCTime.now(), UTCTime.now()), key2.set(TAITime.now()))
        s.size shouldBe 2

        key1.get(s).onSome { it.size shouldBe 2 }
        key2.get(s).onSome { it.size shouldBe 1 }
        key3.get(s) shouldBe none()
    }
}
)