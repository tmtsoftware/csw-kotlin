package csw.time.core

import csw.time.clock.natives.models.TimeConstants
import csw.time.core.models.TAITime
import csw.time.core.models.UTCTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.doubles.plusOrMinus
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Duration.Companion.microseconds


class TMTTimeTest : FunSpec( {

    test("should get utc time | DEOPSCSW-549") {
        val jitter = 100.0  // Not sure if this is reasonable
        val utcTime = UTCTime.now()
        val fixedInstant = java.time.Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val expectedMillis = fixedInstant.toEpochMilli()

        utcTime.value.toEpochMilliseconds().toDouble() shouldBe (expectedMillis.toDouble().plusOrMinus(jitter))
    }

    test("should convert utc to tai | DEOPSCSW-549") {
        val utcTime = UTCTime.now()
        val taiTime = utcTime.toTAI()
        (taiTime.value - utcTime.value) shouldBe TimeConstants.taiOffset
    }

    test("should give time duration between given timestamp and current time | DEOPSCSW-549") {
        val expectedDuration = 1.seconds
        val futureTime       = UTCTime(UTCTime.now().value + 1.seconds)
        futureTime.durationFromNow() - expectedDuration shouldBeLessThanOrEqualTo expectedDuration
    }

    test("should give utc time after specified duration | DEOPSCSW-549") {
        val jitter = 250.microseconds
        val tenSeconds = 10.seconds
        val futureTime = UTCTime.after(tenSeconds)


        Math.abs((futureTime.durationFromNow() - tenSeconds).inWholeMicroseconds) shouldBeLessThanOrEqualTo jitter.inWholeMicroseconds
    }

    test("should give tai time after specified duration | DEOPSCSW-549") {
        val jitter = 250.microseconds
        val tenSeconds = 10.seconds
        val futureTime = TAITime.after(tenSeconds)

        Math.abs((futureTime.durationFromNow() - tenSeconds).inWholeMicroseconds) shouldBeLessThanOrEqualTo jitter.inWholeMicroseconds
    }

}
)