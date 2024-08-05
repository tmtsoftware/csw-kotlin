/*
 * Copyright (c) 2022 Thirty Meter Telescope International Observatory
 * SPDX-License-Identifier: Apache-2.0
 */

package csw.time.clock.natives.models

/**
 * Clock Id argument is the identifier of the particular clock on which to act.
 * It is used by [[csw.time.clock.natives.TimeLibrary.clock_gettime()]] native call.
 *
 * Following are the possible ids in a linux system:
 * CLOCK_REALTIME                  0
 * CLOCK_MONOTONIC                 1
 * CLOCK_PROCESS_CPUTIME_ID        2
 * CLOCK_THREAD_CPUTIME_ID         3
 * CLOCK_MONOTONIC_RAW             4
 * CLOCK_REALTIME_COARSE           5
 * CLOCK_MONOTONIC_COARSE          6
 * CLOCK_BOOTTIME                  7
 * CLOCK_REALTIME_ALARM            8
 * CLOCK_BOOTTIME_ALARM            9
 * CLOCK_SGI_CYCLE                10      // In linux/time.h only.
 * CLOCK_TAI                      11      // In linux/time.h only.
 */
internal object ClockId {
  //  #clock-id
  val ClockRealtime:Int = 0  // system-wide realtime clock. Its time represents seconds and nanoseconds since the Epoch
  val ClockTAI:Int      = 11 // It is basically defined as CLOCK_REALTIME(UTC) + tai_offset.
  //  #clock-id

}
