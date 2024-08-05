package csw.params.core.models

import arrow.core.Option
import arrow.core.Some
import kotlinx.serialization.Serializable

/**
 * Represents a unique observation id
 *
 * @param programId represents program Id
 * @param observationNumber Unique observation number in pattern O followed by 3-digit number
 */
@Serializable
data class ObsId(val programId: ProgramId, val observationNumber: Int) {
    init {
        require(observationNumber in 1..999) {
            "Program Number should be integer in the range of 1 to 999"
        }
    }
    /**
     * Returns the ObsId in form of Option
     *
     * @return a defined Option with obsId
     */
    fun asOption(): Option<ObsId> = Some(ObsId(programId, observationNumber))

    /**
     * Returns the ObsId in form of Optional
     *
     * @return a defined Optional with obsId
     */
    //fun asOptional(): Optional[ObsId] = Optional.of(new ObsId(programId, observationNumber))  // Not needed for Kotlin

    override fun toString(): String = "$programId-${String.format("%03d", observationNumber)}"

    companion object {
        private fun isNumeric(toCheck: String): Boolean = toCheck.all { char -> char.isDigit() }

        private val mess =
            "An ObsId must consist of a semesterId, programNumber, and observationNumber separated by '${Separator.Hyphen}' ex: 2020A-001-123"

        operator fun invoke(obsId: String): ObsId {
            val items = obsId.split(Separator.Hyphen)
            require(items.size == 3) { mess }
            val semesterId = items[0]
            val programNumber = items[1]
            val obsNumber = items[2]

            // ProgramId is checked in ProgramId
            val programId = ProgramId(Separator.hyphenate(semesterId, programNumber))
            if (isNumeric(items[2])) {
                return ObsId(programId, obsNumber.toInt())
            } else {
                throw IllegalArgumentException(mess)
            }
        }
    }
}

