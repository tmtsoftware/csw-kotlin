package csw.params.core.models

import kotlinx.serialization.Serializable

enum class CalibrationLevel(val level: Int) {
    Raw(0),
    Uncalibrated(1),
    Calibrated(2),
    ScienceProduct(3),
    AfterAnalysisScienceProduct(4)
}

/**
 * Represents a string with entryName and description of a TYP
 * Extension method
 */
fun TYP.longName() = "$name-$description"

/**
 * Represents the name of the TYP e.g. SCI
 */
enum class TYP(val description: String) {
    SCI("Science Exposure"),
    CAL("Calibration Exposure"),
    ARC("Wavelength Calibration"),
    IDP("Instrumental Dispersion"),
    DRK("Dark"),
    MDK("Master Dark"),
    FFD("Flat Field"),
    NFF("Normalized Flat Field"),
    BIA("Bias Exposure"),
    TEL("Telluric Standard"),
    FLX("Flux Standard"),
    SKY("Sky Background Exposure")
}

@Serializable
data class TYPLevel(val typ: TYP, val calibrationLevel: CalibrationLevel) {
    override fun toString(): String = "${typ.name}${calibrationLevel.ordinal}"

    fun calibrationLevelNumber(): Int = calibrationLevel.ordinal

    companion object {
        private fun isNumeric(toCheck: String): Boolean = toCheck.all { char -> char.isDigit() }

        private fun findLevel(level: Int): CalibrationLevel {
            val found: CalibrationLevel? = CalibrationLevel.entries.find { it.level == level }
            if (found != null) {
                return found
            } else {
                throw IllegalArgumentException("TYP calibration level: $level must be one of: ${CalibrationLevel.entries.map { it.level }}.")
            }
        }

        private fun findTYP(typStr: String): TYP {
            val found: TYP? = TYP.entries.find { str -> str.name == typStr }
            if (found != null) {
                return found
            } else {
                throw IllegalArgumentException("TYP: $typStr must be one of: ${TYP.entries.map { it.name }}.")
            }
        }

        private fun parseCalibrationLevel(calibrationLevel: String): CalibrationLevel {
            val mess = "Failed to parse calibration level: $calibrationLevel. Calibration level should be a digit 0,1,2,3,4."
            require(isNumeric(calibrationLevel)) { mess }
            return findLevel(calibrationLevel.toInt())
        }

        operator fun invoke(typLevel: String): TYPLevel {
            require(typLevel.length == 4) { "TYPLevel must be a 3 character TYP followed by a calibration level 0,1,2,3,4." }
            val typStr = typLevel.substring(0, 3)
            val levelStr = typLevel.substring(3)
            val level = parseCalibrationLevel(levelStr)
            val typ = findTYP(typStr)
            return TYPLevel(typ, level)
        }
    }
}