package csw.params.core.models

import kotlinx.serialization.Serializable

/**
 * Represents a unique program id
 *
 * @param semesterId semesterId for Program
 * @param programNumber programNumber number in pattern P followed by 3 digit number
 */
@Serializable
data class ProgramId(val semesterId: SemesterId, val programNumber: Int) {
    override fun toString(): String = Separator.hyphenate("$semesterId", String.format("%03d", programNumber))
    init {
        require(programNumber >= 1 && programNumber <= 999) {"Program Number should be integer in the range of 1 to 999."}
    }

    companion object {
        private fun isNumeric(toCheck: String): Boolean = toCheck.all { char -> char.isDigit() }

        private val mess = "A program Id consists of a semester Id and program number separated by '${Separator.Hyphen}' ex: 2020A-001."
        operator fun invoke(programId: String): ProgramId {
            val items = programId.split(Separator.Hyphen)
            require(items.size == 2) { mess }
            if (isNumeric(items[1])) {
                return ProgramId(SemesterId(items[0]), items[1].toInt())
            } else {
                throw IllegalArgumentException(mess)
            }
        }
    }
}
