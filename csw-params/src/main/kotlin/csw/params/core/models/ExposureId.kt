@file:UseSerializers(
    OptionSerializer::class
)
package csw.params.core.models

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.serialization.OptionSerializer
import csw.time.core.models.UTCTime
import kotlinx.datetime.*
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers


/**
 * A standalone ExposureId is an exposureId without an ObsId.
 * Instances are created using the ExposureId object.
 */
@Serializable
internal data class StandaloneExposureId (
    val utcTime: UTCTime,
    override val subsystem: Subsystem,
    override val det: String,
    override val typLevel: TYPLevel,
    override val exposureNumber: ExposureNumber
): ExposureId {

    override fun obsId(): Option<ObsId> = None

    override fun toString() =
        Separator.hyphenate(ExposureId.utcAsStandaloneString(utcTime), "$subsystem", det, "$typLevel", "$exposureNumber")
}

/**
 * An ExposureIdWithObsId is an ExposureId with an included ObsId.
 * Instances are created through the ExposureId object.
 */
@Serializable
internal data class ExposureIdWithObsId (
    val obsId: Option<ObsId>,
    override val subsystem: Subsystem,
    override val det: String,
    override val typLevel: TYPLevel,
    override val exposureNumber: ExposureNumber
): ExposureId {

    override fun toString(): String =
        Separator.hyphenate("${obsId.getOrElse {  }}", "$subsystem", det, "$typLevel", "$exposureNumber")

    override fun obsId(): Option<ObsId> = obsId
}


/**
 * ExposureId is an identifier in ESW/DMS for a single exposure.
 * The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with
 * an included ObsId or when no ObsId is present, in the standalone
 * format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time when the
 * ExposureId is created.
 */
sealed interface ExposureId {

    /**
     * The Observation Id for the exposure.
     * @return an [[csw.params.core.models.ObsId]] as an option
     */
    fun obsId(): Option<ObsId>

    /**
     * The Subsystem that produced the exposure.
     *  @return a valid [[csw.prefix.models.Subsystem]]
     */
    val subsystem: Subsystem

    /**
     * The detector name associated with the exposure.
     * @return detector description as a [[java.lang.String]]
     */
    val det: String

    /**
     * The exposure type and calibration level
     * @return a [[csw.params.core.models.TYPLevel]]
     */
    val typLevel: TYPLevel

    /**
     * The number of the exposure in a series.
     * @return the number as an [[csw.params.core.models.ExposureNumber]]
     */
    val exposureNumber: ExposureNumber

    /** Factory for ExposureId instances and helper functions. */
    @Suppress("UNUSED_EXPRESSION")
    companion object {

        // Used to format standalone ExposureId
        private val exposureIdPattern = "uMMdd-HHmmss"
        val dateTimeFormat = LocalDateTime.Format { byUnicodePattern(exposureIdPattern) }
        val dateTimeFormatX = DateTimeComponents.Format {
            """
        year(Padding.NONE)
        monthNumber()
        dayOfMonth()
        char('-')
        hour()
        minute()
        second()
        """
        }

        /**
         * A convenience function to create a new ExposureId with a specific exposure number.
         * Example: 2020A-001-123-WFOS-IMG1-SCI0-0001 with 3 => 2020A-001-123-WFOS-IMG1-SCI0-0003
         * @param exposureId current ExposureId
         * @param exposureNumber desired exposure number
         * @return ExposureId with specified exposure number
         */
        fun withExposureNumber(exposureId: ExposureId, exposureNumber: Int): ExposureId =
            updateExposureNumber(exposureId, ExposureNumber(exposureNumber))

        /**
         * A convenience function to create a new ExposureId with the next higher exposure number.
         * Example: 2020A-001-123-WFOS-IMG1-SCI0-0001 => 2020A-001-123-WFOS-IMG1-SCI0-0002
         * @param exposureId current ExposureId
         * @return ExposureId with next higher exposure number
         */
        fun nextExposureNumber(exposureId: ExposureId): ExposureId =
            updateExposureNumber(exposureId, exposureId.exposureNumber.next())

        /**
         * A convenience function to create a new ExposureId with the same exposure number and
         * specified sub array number
         * Example: 2020A-001-123-WFOS-IMG1-SCI0-0001, 3 => 2020A-001-123-WFOS-IMG1-SCI0-0002-03.
         * Example: 2020A-001-123-WFOS-IMG1-SCI0-0002-00, 4 => 2020A-001-123-WFOS-IMG1-SCI0-0002-04.
         * @param exposureId current ExposureId
         * @param subArrayNumber specified subArray number
         * @return ExposureId with next higher ExposureNumber
         */
        fun withSubArrayNumber(exposureId: ExposureId, subArrayNumber: Int): ExposureId =
            updateExposureNumber(exposureId, ExposureNumber(exposureId.exposureNumber.exposureNumber, subArrayNumber))

        /**
         * A convenience function to create a new ExposureId with the next higher sub array number.
         * Example: 2020A-001-123-WFOS-IMG1-SCI0-0001 => 2020A-001-123-WFOS-IMG1-SCI0-0002-00.
         * Example: 2020A-001-123-WFOS-IMG1-SCI0-0002-00 => 2020A-001-123-WFOS-IMG1-SCI0-0002-01.
         * @param exposureId current ExposureId
         * @return ExposureId with next higher ExposureNumber
         */
        fun nextSubArrayNumber(exposureId: ExposureId): ExposureId =
            updateExposureNumber(exposureId, exposureId.exposureNumber.nextSubArray())

        /** Updates ExposureId with new ExposureNumber */
        private fun updateExposureNumber(exposureId: ExposureId, update: ExposureNumber): ExposureId =
            when (exposureId) {
                is ExposureIdWithObsId -> exposureId.copy(exposureNumber = update)
                is StandaloneExposureId -> exposureId.copy(exposureNumber = update)
            }

        /**
         * A convenience function to create a new ExposureId with a new ObsId object.
         * Example: 2020A-001-123-WFOS-IMG1-SCI0-0001 => 2020A-001-228-WFOS-IMG1-SCI0-0001.
         * Note that a standalone ExposureId will be changed to an ExposureId with an ObsId
         * @param exposureId current ExposureId
         * @param obsId new ObsId as an [[csw.params.core.models.ObsId]]
         * @return a new ExposureId with given new ObsId
         */
        fun withObsId(exposureId: ExposureId, obsId: ObsId): ExposureId =
            when (exposureId) {
                is ExposureIdWithObsId -> exposureId.copy(obsId = Some(obsId))
                is StandaloneExposureId ->
                    ExposureIdWithObsId(
                        Some(obsId),
                        exposureId.subsystem,
                        exposureId.det,
                        exposureId.typLevel,
                        exposureId.exposureNumber
                    )
            }


        /**
         * A convenience function to create a new ExposureId with a new ObsId as a String.
         * Example: 2020A-001-123-WFOS-IMG1-SCI0-0001 => 2020A-001-228-WFOS-IMG1-SCI0-0001.
         * Note that a standalone ExposureId will be changed to an ExposureId with an ObsId.
         * @param exposureId current ExposureId
         * @param obsIdString new ObsId as a String
         * @return ExposureId with given new [[csw.params.core.models.ObsId]]
         */
        fun withObsId(exposureId: ExposureId, obsIdString: String): ExposureId =
            withObsId(exposureId, ObsId(obsIdString))

        /**
         * A convenience function that allows creating a standalone ExposureId at a specific UTC date and time.
         * Note than an ExposureId with an ObsId can be changed to a standalone ExposureId.
         * @param exposureId current ExposureId
         * @param utc a [[csw.time.core.models.UTCTime]] for the ExposureId
         * @return a standalone ExposureId at the provided UTC
         */
        fun withUTC(exposureId: ExposureId, utc: UTCTime): ExposureId =
            StandaloneExposureId(
                utc,
                exposureId.subsystem,
                exposureId.det,
                exposureId.typLevel,
                exposureId.exposureNumber
            )

        /**
         * The UTC time formatted as needed for a standalone ExposureId: YYYYMMDD-HHMMSS.
         * @return  the UTCTime formatted String
         */
        fun utcAsStandaloneString(utcTime: UTCTime): String =
            utcTime.value.toLocalDateTime(TimeZone.UTC).format(dateTimeFormat)

        /**
         * A helper function that allows creating exposure id from string in java file.
         * @param exposureId proper ExposureId as a String
         * @return instance of ExposureId
         */
        fun fromString(exposureId: String): ExposureId? = invoke(exposureId)

        /**
         * Create an ExposureId from a String of the 4 forms with and without an ObsId and with and without a subarray:
         * IRIS-IMG-SCI0-0001,IRIS-IMG-SCI0-0001-02 when no ObsId is present. Or
         * 2020A-001-123-IRIS-IMG-SCI0-0001 or 2020A-001-123-IRIS-IMG-SCI0-0001-02 when an ObsId is present.
         * @param exposureId proper ExposureId as a String
         * @return instance of ExposureId
         * @throws java.lang.IllegalArgumentException if the String does not follow the correct structure
         */
        operator fun invoke(exposureId: String): ExposureId {
            val maxArgs: Int = 8
            val items = exposureId.split(Separator.Hyphen)
            require(items.size <= maxArgs) { " TOO MANY ARGS" }

            val noHope = "An ExposureId must be a ${Separator.Hyphen} separated string of the form: " +
                    "SemesterId-ProgramNumber-ObservationNumber-Subsystem-DET-TYPLevel-ExposureNumber."

            when (items.size) {
                // 8 Args
                8 -> {
                    println("items case 8: $items")
                    val obs1 = items[0]
                    val obs2 = items[1]
                    val obs3 = items[2]
                    val subsystemStr = items[3]
                    val detStr = items[4]
                    val typStr = items[5]
                    val expNumStr = items[6]
                    val subArrayStr = items[7]

                    // This is the case with an ObsId and a sub array
                    return ExposureIdWithObsId(
                        Some(ObsId(Separator.hyphenate(obs1, obs2, obs3))),
                        Subsystem(subsystemStr),
                        detStr,
                        TYPLevel(typStr),
                        ExposureNumber(expNumStr + Separator.Hyphen + subArrayStr)
                    )
                }

                // 7 args
                // This is the case with an ObsId and no subarray
                // Or Standalone with subarray
                // If it is with ObsId, the first part will be a semester ID which is always length 5
                7 -> {
                    println("items case 7: $items")

                    val date = items[0]
                    val time = items[1]
                    val p3 = items[2]
                    val p4 = items[3]
                    val p5 = items[4]
                    val p6 = items[5]
                    val p7 = items[6]

                    if (date.length == 5) {
                        return ExposureIdWithObsId(
                            Some(ObsId(Separator.hyphenate(date, time, p3))),
                            Subsystem(p4),
                            p5,
                            TYPLevel(p6),
                            ExposureNumber(p7)
                        )
                    } else {
                        toTimeDateAtUTC(date, time)?.let {
                            return StandaloneExposureId(
                                it,
                                Subsystem(p3),
                                p4,
                                TYPLevel(p5),
                                ExposureNumber(p6 + Separator.Hyphen + p7)
                            )
                        }
                        throw IllegalArgumentException("Case 7 failed time date")
                    }
                }

                6 -> {
                    println("items case 6: $items")

                    val date = items[0]
                    val time = items[1]
                    val subStr = items[2]
                    val detStr = items[3]
                    val typStr = items[4]
                    val expNumStr = items[5]

                    if (date.length != 5) {
                        toTimeDateAtUTC(date, time)?.let {
                            return StandaloneExposureId(
                                it,
                                Subsystem(subStr),
                                detStr,
                                TYPLevel(typStr),
                                ExposureNumber(expNumStr)
                            )
                        }
                    } else {
                        throw IllegalArgumentException(noHope)
                    }
                    throw IllegalArgumentException("Case 6 failed time date")
                }

                5 -> {
                    println("items case 5: $items")

                    val subStr = items[0]
                    val detStr = items[1]
                    val typStr = items[2]
                    val expNumStr = items[3]
                    val subArrayStr = items[4]

                    return StandaloneExposureId(
                        UTCTime.now(),
                        Subsystem(subStr),
                        detStr,
                        TYPLevel(typStr),
                        ExposureNumber(expNumStr + Separator.Hyphen + subArrayStr)
                    )
                }

                4 -> {
                    println("items case 4: $items")

                    val subStr = items[0]
                    val detStr = items[1]
                    val typStr = items[2]
                    val expNumStr = items[3]

                    return StandaloneExposureId(
                        UTCTime.now(),
                        Subsystem(subStr),
                        detStr,
                        TYPLevel(typStr),
                        ExposureNumber(expNumStr)
                    )
                }

                else ->
                    throw IllegalArgumentException(noHope)
            }
        }


        /** Convert an input date and time string to an Instant.  Throws parse exception on failure */
        fun toTimeDateAtUTC(dateStr: String, timeStr: String): UTCTime? {

            val ldt: LocalDateTime = dateTimeFormat.parse("$dateStr-$timeStr")
            return try {
                UTCTime(ldt.toInstant(TimeZone.UTC))
            } catch (e: Exception) {
                return null
            }
        }


        /**
         * This creates a stand-alone ExposureId for the case when there is no [[csw.params.core.models.ObsId]] available.
         * @param subsystem [[csw.prefix.models.Subsystem]] associated with exposure
         * @param det a valid detector String
         * @param typLevel the exposure's [[csw.params.core.models.TYPLevel]]
         * @param exposureNumber the exposure's Exposure Number [[csw.params.core.models.ExposureNumber]]
         * @return A stand-alone ExposureId
         */
        operator fun invoke(
            subsystem: Subsystem,
            det: String,
            typLevel: TYPLevel,
            exposureNumber: ExposureNumber
        ): ExposureId =
            StandaloneExposureId(UTCTime.now(), subsystem, det, typLevel, exposureNumber)

        /**
         * This creates an ExposureId with an ObsId.
         * @param obsId a valid [[csw.params.core.models.ObsId]]
         * @param subsystem [[csw.prefix.models.Subsystem]] associated with exposure
         * @param det a valid detector String
         * @param typLevel the exposure's [[csw.params.core.models.TYPLevel]]
         * @param exposureNumber the exposure's Exposure Number [[csw.params.core.models.ExposureNumber]]
         * @return A standalone ExposureId
         */
        operator fun invoke(
            obsId: ObsId,
            subsystem: Subsystem,
            det: String,
            typLevel: TYPLevel,
            exposureNumber: ExposureNumber
        ): ExposureId =
            ExposureIdWithObsId(Some(obsId), subsystem, det, typLevel, exposureNumber)
    }
}
