/*
 * Copyright (c) 2022 Thirty Meter Telescope International Observatory
 * SPDX-License-Identifier: Apache-2.0
 */

package csw.time.clock.natives.models

import java.util.Locale

sealed interface OS
data object Linux: OS
data object Other: OS

class OSType() {

    companion object {
        val value: OS
            get() {
                val OS = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
                return if (OS.indexOf("nux") >= 0) Linux else Other
            }
    }
}

