package csw.params.core.models

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Tests for ProgramId
 */
class ProgramIdTest: FunSpec({
    test("should create ProgramId | CSW-121") {
        val programId = ProgramId(SemesterId("2030A"), 1)
        programId shouldBe ProgramId("2030A-001")
        programId.semesterId shouldBe SemesterId("2030A")
        programId.programNumber shouldBeEqual 1
        programId.toString() shouldBeEqual "2030A-001"
    }

    test("should throw exception if invalid program id | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ProgramId("2020A-1234")
        }
        ex.message shouldBe ("Program Number should be integer in the range of 1 to 999.")
    }

    test("should throw length exception if invalid semester in SemesterId | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ProgramId("202C-123")
        }
        ex.message shouldBe ("Semester ID must be length 5 in format YYYY[A|B].")
    }

    test("should throw exception if invalid semester in SemesterId | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ProgramId("2024C-123")
        }
        ex.message shouldBe ("Failed to parse semester C. Semester ID must be A or B.")
    }

    test("should throw exception if program id is invalid | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            ProgramId("2020A-001-123")
        }
        ex.message shouldBe ("A program Id consists of a semester Id and program number separated by '-' ex: 2020A-001.")
    }

    test("Should serialize and deserialize semesterId") {
        val programId = ProgramId(SemesterId("2030A"), 1)
        val json = Json.encodeToString(programId)
        println("json: $json")
        val objIn = Json.decodeFromString<ProgramId>(json)
        //println("objIn: $objIn")
        objIn shouldBe programId
    }

}
)
