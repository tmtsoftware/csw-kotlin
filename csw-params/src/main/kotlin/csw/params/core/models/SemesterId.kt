package csw.params.core.models

import kotlinx.serialization.Serializable

enum class Semester {
    A, B
}

typealias Year = Int
/**
 * Represents a unique semester id
 *
 * @param year year for semester
 * @param semester observing semester
 */
@Serializable
data class SemesterId(val year: Year, val semester: Semester) {
    override fun toString(): String = "$year$semester"

    companion object {

        private fun parseSemester(semesterStr: String): Semester {
            // Note this will throw IllegalArgumentException
            try {
                return enumValueOf<Semester>(semesterStr.uppercase())
            } catch (ex: Exception) {
                throw IllegalArgumentException("Failed to parse semester $semesterStr. Semester ID must be A or B.")
            }
        }

        operator fun invoke(semesterId: String): SemesterId {
            val REQUIRED_LENGTH = 5
            require(semesterId.isNotEmpty() && semesterId.length == REQUIRED_LENGTH) { "Semester ID must be length $REQUIRED_LENGTH in format YYYY[A|B]." }
            val semesterStr = semesterId[REQUIRED_LENGTH-1].uppercase()
            val semester = parseSemester(semesterStr)
            val yearStr = semesterId.substring(0, REQUIRED_LENGTH-1)
            require(yearStr.toIntOrNull() != null) { throw IllegalArgumentException("$yearStr could not be converted to an integer in YYYY[A|B].") }
            return SemesterId(yearStr.toInt(), semester)
        }
    }
}