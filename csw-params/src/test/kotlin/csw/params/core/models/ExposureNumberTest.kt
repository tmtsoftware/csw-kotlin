package csw.params.core.models

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class ExposureNumberTest: FunSpec(
{
    test("should create valid ExposureNumber | CSW-121") {
        val exposureNumber = ExposureNumber("0001")
        exposureNumber shouldBeEqual ExposureNumber(1)
        exposureNumber.toString() shouldBe "0001"
    }

    test("should create valid ExposureNumber with subArray | CSW-121") {
        val exposureNumber = ExposureNumber("0001-01")
        exposureNumber shouldBe ExposureNumber(1, 1)
        exposureNumber.toString() shouldBeEqual "0001-01"
    }

    test("should throw exception if ExposureNumber is invalid | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ExposureNumber("10000")
        }
        ex.message shouldBe ("Invalid exposure number: 10000. An ExposureNumber must be a 4 digit number and optional 2 digit subarray in format XXXX or XXXX-XX.")
    }

    test("should throw exception if subarray in exposure number is invalid | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ExposureNumber("0002-123")
        }
        ex.message shouldBe ("Invalid exposure number: 123. An ExposureNumber must be a 4 digit number and optional 2 digit subarray in format XXXX or XXXX-XX.")
    }

    test("should throw exception if exposure number contains more than one '-'  | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ExposureNumber("0001-01-hhhs")
        }
        ex.message shouldBe ("An exposure number consists of up to 2 parts in format: XXXX or XXXX-XX.")
    }

    test("should throw exception if exposure number if non-numeric | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ExposureNumber("X001-01")
        }
        ex.message shouldBe ("A non-numeric exposure number: X001 was provided.")
    }

    test("Checking next actions") {
        val exNum1 = ExposureNumber.default()
        exNum1.next() shouldBe ExposureNumber(1)

        val exNum2 = ExposureNumber(0, 0)
        exNum2.nextSubArray() shouldBe ExposureNumber(0, 1)
    }

    test("Should serialize and deserialize ExposureNumber") {
        val exposureNumber = ExposureNumber("0001")
        val json = Json.encodeToString(exposureNumber)
        //println("json: $json")
        val objIn = Json.decodeFromString<ExposureNumber>(json)
        //println("objIn: $objIn")
        objIn shouldBe exposureNumber
    }
}
)