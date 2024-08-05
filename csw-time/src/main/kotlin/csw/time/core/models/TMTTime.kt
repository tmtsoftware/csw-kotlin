/*
 * Copyright (c) 2022 Thirty Meter Telescope International Observatory
 * SPDX-License-Identifier: Apache-2.0
 */

package csw.time.core.models

//import java.time.Duration
//import java.time.Instant

import csw.time.clock.natives.models.clock
import kotlin.time.Duration
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlinx.serialization.Serializable

//import kotlin.time.Duration.Companion.days

//import scala.concurrent.duration.FiniteDuration

/**
 * Represents an instantaneous point in time. It's a wrapper around `java.time.Instant`and provides nanosecond precision.
 * Supports 2 timescales:
 * - [[UTCTime]] for Coordinated Universal Time (UTC) and
 * - [[TAITime]] for International Atomic Time (TAI)
 */

@Serializable
sealed interface TMTTime {
    val value: Instant

    // This is reversed because there is no plus withthe proper types
    fun durationFromNow(): Duration = this.value.minus(currentInstant())

    private fun currentInstant(): Instant {
        val time = when (this) {
            is UTCTime -> UTCTime.now().value
            is TAITime -> TAITime.now().value
        }
        return time
    }
}

/*
object TMTTime {
  // Allows UTCTime and TAITime to be sorted
  //implicit def orderByInstant[A <: TMTTime]: Ordering[A] = Ordering.by(e => e.value)
}

 */

/**
 * Represents an instantaneous point in time in the UTC scale.
 * Does not contain zone information. To represent this instance in various zones, use [[csw.time.core.TMTTimeHelper]].
 *
 * @param value the underlying `java.time.Instant`
 */
@Serializable
data class UTCTime(override val value: Instant) : TMTTime {

    /**
     * Converts the [[UTCTime]] to [[TAITime]] by adding the UTC-TAI offset.
     * UTC-TAI offset is fetched by doing a native call to `ntp_gettimex`. It ensures to get the latest offset as updated by the PTP Grandmaster.
     *
     * @return TAI time at the given UTC time
     */
    fun toTAI(): TAITime = TAITime(value + clock.offset)

    companion object {
        /**
         * Obtains the PTP (Precision Time Protocol) synchronized current UTC time.
         * In case of a Linux machine, this will make a native call `clock_gettime` inorder to get time from the system clock with nanosecond precision.
         * In case of all the other operating systems, nanosecond precision is not supported, hence no native call is made.
         *
         * @return current time in UTC scale
         */
        fun now(): UTCTime = UTCTime(clock.utcInstant())

        fun after(duration: Duration): UTCTime = UTCTime(now().value + duration)
    }
}

/**
 * Represents an instantaneous point in International Atomic Time (TAI).
 *
 * @param value the underlying `java.time.Instant`
 */
@Serializable
data class TAITime(override val value: Instant) : TMTTime {

    /**
     * Converts the [[TAITime]] to [[UTCTime]] by subtracting the UTC-TAI offset.
     * UTC-TAI offset is fetched by doing a native call to `ntp_gettimex`. It ensures to get the latest offset as updated by the PTP Grandmaster.
     *
     * @return UTC time at the given TAI time
     */
    fun toUTC(): UTCTime = UTCTime(value - clock.offset)

    companion object {
        /**
         * Obtains the PTP (Precision Time Protocol) synchronized current time in TAI timescale.
         * In case of a Linux machine, this will make a native call `clock_gettime` inorder to get time from the system clock with nanosecond precision
         * In case of all the other operating systems, nanosecond precision is not supported, hence no native call is made.
         *
         * @return current time in TAI scale
         */
        fun now(): TAITime = TAITime(clock.taiInstant())

        fun after(duration: Duration): TAITime = TAITime(now().value + duration)

        /**
         * Fetches UTC to TAI offset by doing a native call to `ntp_gettimex` in case of a Linux machine.
         * It ensures to get the latest offset as updated by the PTP Grandmaster.
         *
         * @return offset of UTC to TAI in seconds
         */
        fun offset(): Duration = clock.offset
    }
}
