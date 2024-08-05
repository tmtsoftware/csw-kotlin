package csw.params.core.models

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.Unit

class TYPLevelTest: FunSpec( {

    test("should create TYPLevel | CSW-121") {
        val typLevel = TYPLevel("SCI0")
        typLevel.toString() shouldBe "SCI0"
        typLevel shouldBe TYPLevel(TYP.SCI, CalibrationLevel.Raw)
        typLevel.calibrationLevel shouldBe CalibrationLevel.Raw
        typLevel.calibrationLevelNumber() shouldBe 0
        typLevel.typ.longName() shouldBe "SCI-Science Exposure"
        typLevel.typ.description shouldBe "Science Exposure"
    }

    test("should throw exception if invalid TYP | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            TYPLevel("XYZ0")
        }
        ex.message shouldBe ("TYP: XYZ must be one of: [SCI, CAL, ARC, IDP, DRK, MDK, FFD, NFF, BIA, TEL, FLX, SKY].")
    }

    test("should throw exception if invalid calibrationLevel | CSW-121"){
        val ex = shouldThrow<IllegalArgumentException> {
            TYPLevel("SCI5")
        }
        ex.message shouldBe ("TYP calibration level: 5 must be one of: [0, 1, 2, 3, 4].")
    }

    test("should throw exception if no calibrationLevel | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            TYPLevel("SCI")
        }
        ex.message shouldBe ("TYPLevel must be a 3 character TYP followed by a calibration level 0,1,2,3,4.")
    }

    test("should throw exception if calibrationLevel is char | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            TYPLevel("SCIC")
        }
        ex.message shouldBe ("Failed to parse calibration level: C. Calibration level should be a digit 0,1,2,3,4.")
    }

    fun <T : Any> T?.notNull(f: (it: T) -> Unit) {
        if (this != null) f(this)
    }

    test("Checking stuff") {
        val exposureIdPattern = "uMMdd-HHmmss"
        val dateTimeFormat = LocalDateTime.Format { byUnicodePattern(exposureIdPattern) }
        println(DateTimeFormat.formatAsKotlinBuilderDsl(dateTimeFormat))
    }

    test("Should serialize and deserialize TypLevel") {
        val typLevel = TYPLevel("SCI0")
        val json = Json.encodeToString(typLevel)
        println("json: $json")
        val objIn = Json.decodeFromString<TYPLevel>(json)
        //println("objIn: $objIn")
        objIn shouldBe typLevel
    }
}
)

