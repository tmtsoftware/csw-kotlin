package csw.time.core.models

import csw.time.core.TMTTimeHelper

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.toKotlinInstant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class TMTTimeHelperTest : FunSpec({
    val instant = ZonedDateTime.of(
        2007, 12, 3, 10,
        15, 30, 11, ZoneOffset.UTC
    ).toInstant().toKotlinInstant()

    /* TMTTime contains ZonedDateTime which has all the date time info along with Zone
     * Below tests shows irrespective of local or remote time,
     * how you can access different parts of the ZonedDateTime structure from java
     */

    // DEOPSCSW-533: Access parts of UTC date.time in Java and Scala
    // DEOPSCSW-549: Time service api
    // This test is sufficient to show code works in both Scala and Java
    // since UTCTime.toZonedDateTime is used in both languages.

    test("should access parts of UTC time | DEOPSCSW-533, DEOPSCSW-549") {
        val utcTime = UTCTime(instant)
        val zonedDateTime = TMTTimeHelper.toZonedDateTime(utcTime)

        zonedDateTime.year shouldBe 2007
        zonedDateTime.month.value shouldBe 12
        zonedDateTime.dayOfMonth shouldBe 3
        zonedDateTime.hour shouldBe 10
        zonedDateTime.minute shouldBe 15
        zonedDateTime.second shouldBe 30
    }

    // DEOPSCSW-536: Access parts of TAI date.time in Java and Scala
    // DEOPSCSW-549: Time service api
    // This test is sufficient to show code works in both Scala and Java
    // since TaiTime.value.atZone is used in both languages.

    test("should access parts of TAI time | DEOPSCSW-536, DEOPSCSW-549") {
        val taiTime = TAITime(instant)
        val zonedDateTime = TMTTimeHelper.atZone(taiTime, ZoneOffset.UTC)

        zonedDateTime.year shouldBe 2007
        zonedDateTime.month.value shouldBe 12
        zonedDateTime.dayOfMonth shouldBe 3
        zonedDateTime.hour shouldBe 10
        zonedDateTime.minute shouldBe 15
        zonedDateTime.second shouldBe 30
    }

    // DEOPSCSW-540: Access parts of Remote time/date in Java and Scala
    // DEOPSCSW-549: Time service api
    // This test is sufficient to show code works in both Scala and Java
    // since UTCTime.atZone is used in both languages.
    test("should access parts of a remote time | DEOPSCSW-540, DEOPSCSW-549") {
        val utcTime = UTCTime(instant)
        val zonedDateTime = TMTTimeHelper.atZone(utcTime, ZoneId.of("Asia/Kolkata"))

        zonedDateTime.year shouldBe 2007
        zonedDateTime.month.value shouldBe 12
        zonedDateTime.dayOfMonth shouldBe 3
        zonedDateTime.hour shouldBe 10 + 5    //with zone offset
        zonedDateTime.minute shouldBe 15 + 30 //with zone offset
        zonedDateTime.second shouldBe 30
    }
}
)
