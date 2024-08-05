package csw.params.core.models

import arrow.core.None
import arrow.core.Some
import csw.time.core.models.UTCTime
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExposureIdTest : FunSpec({

    test("should create valid ExposureId with ObsId | CSW-121") {
        // Tests case 7 of invoke
        val exposureId = ExposureId("2020A-001-123-CSW-IMG1-SCI0-0001")
        // Verify parts
        exposureId.toString() shouldBe "2020A-001-123-CSW-IMG1-SCI0-0001"
        exposureId.obsId() shouldBe Some(ObsId("2020A-001-123"))
        exposureId.det shouldBe "IMG1"
        exposureId.subsystem shouldBe Subsystem.CSW
        exposureId.typLevel shouldBe TYPLevel("SCI0")
        exposureId.exposureNumber shouldBe ExposureNumber("0001")
        // verify total equality once
        exposureId shouldBe
                ExposureIdWithObsId(
                    Some(ObsId("2020A-001-123")),
                    Subsystem.CSW,
                    "IMG1",
                    TYPLevel("SCI0"),
                    ExposureNumber("0001")
                )

    }

    test("should create valid ExposureId from a String with no ObsId | CSW-121") {
        // For testing only to get at UTC for equality test below
        // Triggers case 4 of invoke
        val utcTime = UTCTime.now()
        val exposureId = ExposureId.withUTC(ExposureId("CSW-IMG1-SCI0-0001"), utcTime)

        if (exposureId is StandaloneExposureId) {
            exposureId.toString() shouldBe "${ExposureId.utcAsStandaloneString(utcTime)}-CSW-IMG1-SCI0-0001"

            // Verify parts are correct once for standalone
            exposureId.obsId() shouldBe None
            exposureId.det shouldBe "IMG1"
            exposureId.subsystem shouldBe Subsystem.CSW
            exposureId.typLevel shouldBe TYPLevel("SCI0")
            exposureId.exposureNumber shouldBe ExposureNumber("0001")
            // verify total equality once
            exposureId shouldBe
                    StandaloneExposureId(
                        exposureId.utcTime,
                        Subsystem.CSW,
                        "IMG1",
                        TYPLevel("SCI0"),
                        ExposureNumber("0001")
                    )
        }
    }

    test("should create valid ExposureId from a String with and without ObsId or subArray | CSW-121") {
        // Case 8
        val exposureId = ExposureId("2031A-001-123-CSW-IMG1-SCI0-0001-00")
        exposureId.toString() shouldBe "2031A-001-123-CSW-IMG1-SCI0-0001-00"
        exposureId.exposureNumber.exposureNumber shouldBe 1
        exposureId.exposureNumber.subArray shouldBe 0

        // Case 7
        val exposureId2 = ExposureId("2031A-001-123-CSW-IMG1-SCI0-0001")
        exposureId2.toString() shouldBe "2031A-001-123-CSW-IMG1-SCI0-0001"
        exposureId2.exposureNumber.exposureNumber shouldBe 1
        exposureId2.exposureNumber.subArray shouldBe null

        // Case 4
        val testUTC = UTCTime.now()
        val exposureId3 = ExposureId.withUTC(ExposureId("CSW-IMG1-SCI0-0001"), testUTC)
        exposureId3.toString() shouldBe "${ExposureId.utcAsStandaloneString(testUTC)}-CSW-IMG1-SCI0-0001"
        exposureId3.exposureNumber.exposureNumber shouldBe 1
        exposureId3.exposureNumber.subArray shouldBe null

        // Case 5
        val exposureId4 = ExposureId.withUTC(ExposureId("CSW-IMG1-SCI0-0001-04"), testUTC)
        exposureId4.toString() shouldBe "${ExposureId.utcAsStandaloneString(testUTC)}-CSW-IMG1-SCI0-0001-04"
        exposureId4.exposureNumber.exposureNumber shouldBe 1
        exposureId4.exposureNumber.subArray shouldBe 4

        // Case 6
        // Should be able to parse a standalone with and without subArray
        val testStandalone = "20210806-005937-CSW-IMG1-SCI0-0001"
        val exposureId5 = ExposureId(testStandalone)
        exposureId5.exposureNumber.exposureNumber shouldBe 1
        exposureId5.toString() shouldBe testStandalone

        // Case 7
        val testStandalone2 = "20210806-005937-CSW-IMG1-SCI0-0002-03"
        val exposureId6 = ExposureId(testStandalone2)
        exposureId6.exposureNumber.exposureNumber shouldBe 2
        exposureId6.exposureNumber.subArray shouldBe 3
        exposureId6.toString() shouldBe testStandalone2
    }

    test("should create valid ExposureId with no ObsId and then add ObsId | CSW-121") {
        val exposureId = ExposureId("CSW-IMG1-SCI0-0001")
        exposureId.obsId() shouldBe None      // ?????????

        val exposureIdWithObsId = ExposureId.withObsId(exposureId, "2020B-100-456")
        // verify total equality
        exposureIdWithObsId shouldBe
                ExposureIdWithObsId(
                    Some(ObsId("2020B-100-456")),
                    Subsystem.CSW,
                    "IMG1",
                    TYPLevel("SCI0"),
                    ExposureNumber("0001")
                )

        val obsId = ObsId("2021B-200-007")
        val exposureIdWithObsId2 = ExposureId.withObsId(exposureId, obsId)
        exposureIdWithObsId2 shouldBe
                ExposureIdWithObsId(
                    Some(ObsId("2021B-200-007")),
                    Subsystem.CSW,
                    "IMG1",
                    TYPLevel("SCI0"),
                    ExposureNumber("0001")
                )
    }

    test("should throw exception if invalid obsId in exposure Id | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ExposureId("2020A-ABC-123-CSW-IMG1-SCI0-0001")
        }
        ex.message shouldBe ("A program Id consists of a semester Id and program number separated by '-' ex: 2020A-001.")
    }

    test("should throw exception if invalid exposure Id: typLevel is missing | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ExposureId("2020A-001-123-CSW-IMG1-0001")
        }
        ex.message shouldBe ("An ExposureId must be a - separated string of the form: " +
                "SemesterId-ProgramNumber-ObservationNumber-Subsystem-DET-TYPLevel-ExposureNumber.")

        val ex2 = shouldThrow<IllegalArgumentException> {
            ExposureId("2020A-001-123-CSW-IMG1-0001-01")
        }
        ex2.message shouldBe
                "TYP: 000 must be one of: [SCI, CAL, ARC, IDP, DRK, MDK, FFD, NFF, BIA, TEL, FLX, SKY]."
    }

    test("should create ExposureId with exposure number with helper | CSW-121") {
        val exposureId = ExposureId("2020A-001-123-CSW-IMG1-SCI0-0001")
        exposureId.exposureNumber shouldBe ExposureNumber(1)
        val exposureId2 = ExposureId.withExposureNumber(exposureId, 5)
        exposureId2.exposureNumber shouldBe ExposureNumber(5)
        exposureId2 shouldBe
                ExposureIdWithObsId(
                    Some(ObsId("2020A-001-123")),
                    Subsystem.CSW,
                    "IMG1",
                    TYPLevel("SCI0"),
                    ExposureNumber("0005")
                )
    }

    test("should increment ExposureId exposure number with helper | CSW-121") {
        val exposureId = ExposureId("2020A-001-123-CSW-IMG1-SCI0-0001")
        exposureId.exposureNumber shouldBe ExposureNumber(1)
        val exposureId2 = ExposureId.nextExposureNumber(exposureId)
        exposureId2.exposureNumber shouldBe ExposureNumber(2)
        exposureId2 shouldBe
                ExposureIdWithObsId(
                    Some(ObsId("2020A-001-123")),
                    Subsystem.CSW,
                    "IMG1",
                    TYPLevel("SCI0"),
                    ExposureNumber("0002")
                )
    }

    test("should add subarray or increment subarray in ExposureId with helper | CSW-121") {
        val exposureId = ExposureId("2031A-001-123-CSW-IMG1-SCI0-0001")
        exposureId.toString() shouldBe "2031A-001-123-CSW-IMG1-SCI0-0001"
        exposureId.exposureNumber shouldBe ExposureNumber(1)
        exposureId.exposureNumber.subArray shouldBe null

        // Should add it at 00
        val exposureId2 = ExposureId.nextSubArrayNumber(exposureId)
        exposureId2.toString() shouldBe "2031A-001-123-CSW-IMG1-SCI0-0001-00"
        exposureId2.exposureNumber shouldBe ExposureNumber(1, 0)
        exposureId2.exposureNumber.subArray shouldBe 0

        // Should now increment
        val exposureId3 = ExposureId.nextSubArrayNumber(exposureId2)
        exposureId3.toString() shouldBe "2031A-001-123-CSW-IMG1-SCI0-0001-01"
        exposureId3.exposureNumber shouldBe ExposureNumber(1, 1)
        exposureId3.exposureNumber.subArray shouldBe 1
    }

    test("should set subarray to ExposureId with helper | CSW-121") {
        val exposureId = ExposureId("2031A-001-123-CSW-IMG1-SCI0-0001")
        exposureId.exposureNumber shouldBe ExposureNumber(1)
        exposureId.exposureNumber.subArray shouldBe null
        // Should set it at 02
        val exposureId2 = ExposureId.withSubArrayNumber(exposureId, 2)
        exposureId2.toString() shouldBe "2031A-001-123-CSW-IMG1-SCI0-0001-02"
        exposureId2.exposureNumber shouldBe ExposureNumber(1, 2)
        exposureId2.exposureNumber.exposureNumber shouldBe 1
        exposureId2.exposureNumber.subArray shouldBe 2
    }

    test("should convert with ObsId to standalone ExposureId | CSW-121") {
        val exposureId = ExposureId("2031A-001-123-CSW-IMG1-SCI0-0001")
        exposureId.obsId() shouldBe Some(ObsId("2031A-001-123"))

        val utcTime = UTCTime.now()
        val exposureId2 = ExposureId.withUTC(exposureId, utcTime)
        exposureId2.obsId() shouldBe None
    }

    test("should convert without ObsId to ExposureId with ObsId | CSW-121") {
        val exposureId = ExposureId("CSW-IMG1-SCI0-0001")
        exposureId.obsId() shouldBe None

        val exposureId2 = ExposureId.withObsId(exposureId, "2031A-001-123")
        exposureId2.obsId() shouldBe Some(ObsId("2031A-001-123"))
    }

    test("should verify String format for standalone ExposureId | CSW-121") {
        // Make up a date time for creating standalone
        val instant = Instant.parse("1980-04-09T15:30:45.123Z")
        val utcTime = UTCTime(instant)
        val utcTimeString = ExposureId.utcAsStandaloneString(utcTime)
        utcTimeString.length shouldBe 15
        utcTimeString shouldBe "19800409-153045"
    }
/*
    test("Should serialize and deserialize ExposureId") {
        val exposureId = ExposureId("2020A-001-123-CSW-IMG1-SCI0-0001")
        val json = Json.encodeToString(exposureId)
        println("json: $json")
        val objIn = Json.decodeFromString<ExposureId>(json)
        //println("objIn: $objIn")
        objIn shouldBe exposureId
    }
*/
}
)