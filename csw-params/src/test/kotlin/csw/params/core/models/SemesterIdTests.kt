package csw.params.core.models

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*
 * Tests for SemesterId
 */
class SemesterIdTest: FunSpec( {

    test("should create semesterId with valid year and semester | CSW-121") {
        val semesterId = SemesterId("2010B")
        semesterId.toString() shouldBeEqual "2010B"
        semesterId.year shouldBe 2010
        semesterId.semester shouldBe Semester.B
    }

    test("should throw exception if semester is invalid | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            SemesterId("2010C")
        }
        ex.message shouldBe("Failed to parse semester C. Semester ID must be A or B.")
    }

    test("should throw exception if year is invalid | CSW-121") {
        val ex = shouldThrow<IllegalArgumentException> {
            SemesterId("ABCDA")
        }
        ex.message?.contains("could not be converted") shouldBe true
    }

    test("should throw exception if SemesterId is not 5 long") {
        val ex = shouldThrow<IllegalArgumentException> {
            SemesterId("1000000000A")
        }
        ex.message?.startsWith("Semester ID must be length") shouldBe true
    }

    test("Should serialize and deserialize semesterId") {
        val semesterId = SemesterId("2010B")
        val json = Json.encodeToString(semesterId)
        //println("json: $json")
        val objIn = Json.decodeFromString<SemesterId>(json)
        //println("objIn: $objIn")
        objIn shouldBe semesterId
    }

}
)
