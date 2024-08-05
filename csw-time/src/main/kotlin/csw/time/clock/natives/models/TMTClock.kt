/*
 * Copyright (c) 2022 Thirty Meter Telescope International Observatory
 * SPDX-License-Identifier: Apache-2.0
 */

package csw.time.clock.natives.models

import csw.time.clock.natives.TimeLibrary
import csw.time.clock.natives.models.ClockId.ClockRealtime
import csw.time.clock.natives.models.ClockId.ClockTAI
import csw.time.clock.natives.models.Linux
import csw.time.clock.natives.models.OSType
import csw.time.clock.natives.models.Other
import csw.time.clock.natives.models.TimeConstants
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface TMTClock {
    fun utcInstant(): Instant
    fun taiInstant(): Instant
    val offset: Duration
}


internal val clock: TMTClock
    get() =
        when (OSType.value) {
            is Linux -> LinuxClock()
            is Other -> NonLinuxClock()
            else -> error("Unsupported OSType")
        }


internal class LinuxClock : TMTClock {
    override fun utcInstant(): Instant = now(ClockRealtime)
    override fun taiInstant(): Instant = now(ClockTAI)


    private fun now(clockId: Int): Instant {
        val timeSpec = TimeSpec()
        TimeLibrary.clock_gettime(clockId, timeSpec)
        return Instant.fromEpochSeconds(timeSpec.seconds.toLong(), timeSpec.nanoseconds.toLong())
    }

    override val offset: Duration
        get() {
            val timeVal = NTPTimeVal()
            TimeLibrary.ntp_gettimex(timeVal)
            if (timeVal.tai == 0) printWarning()
            return timeVal.tai.seconds
        }

    //#native-calls

    private fun printWarning() {
        println(
            "============================================================================================================================"
        )
        println("WARNING: Value of TAI OFFSET is 0. To set the TAI OFFSET on your machine,")
        println(
            "Please follow instructions in TimeService Documentation [https://tmtsoftware.github.io/csw/services/time.html#dependencies]"
        )
        println(
            "============================================================================================================================"
        )
    }

}


internal class NonLinuxClock: TMTClock {
    override val offset = TimeConstants.taiOffset
    override fun utcInstant(): Instant = Clock.System.now()
    override fun taiInstant(): Instant = Clock.System.now().plus(offset)
}

