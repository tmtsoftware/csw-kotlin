package csw.params.core.models

import arrow.core.Some
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*
 * Tests for ObservationId
 */
class ObsIdTest: FunSpec( {

    test("should create valid obsId | CSW-121") {
        val obsId = ObsId("2020A-001-123")
        obsId.programId shouldBeEqual ProgramId("2020A-001")
        obsId.observationNumber shouldBeEqual 123
        obsId.toString() shouldBeEqual "2020A-001-123"
    }

    test("Return as an option") {
        ObsId("2020A-001-123").asOption() shouldBe Some(ObsId("2020A-001-123"))
    }

    test("should throw exception if program Id is invalid | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ObsId("2020A-1234-123")
        }
        ex.message shouldBe ("Program Number should be integer in the range of 1 to 999.")
    }

    test("should throw exception if observation number is invalid | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ObsId("2020A-001-2334")
        }
        ex.message shouldBe ("Program Number should be integer in the range of 1 to 999")
    }

    test("should throw exception if observation id is invalid | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ObsId("2020A-001")
        }
        ex.message shouldBe ("An ObsId must consist of a semesterId, programNumber, and observationNumber separated by '-' ex: 2020A-001-123")
    }

    test("Should serialize and deserialize obsId") {
        val obsId = ObsId("2020A-001-123")
        val json = Json.encodeToString(obsId)
        println("json: $json")
        val objIn = Json.decodeFromString<ObsId>(json)
        //println("objIn: $objIn")
        objIn shouldBe obsId
    }

}
)
