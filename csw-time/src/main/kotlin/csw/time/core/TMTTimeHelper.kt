package csw.time.core

import java.time.*
import java.time.ZoneId
import java.time.ZonedDateTime

import csw.time.core.models.TMTTime
import kotlinx.datetime.toJavaInstant

/**
 * This API allows users to get a representation of [[csw.time.core.models.TMTTime]] in a specific Time Zone,
 * returned as a `java.time.ZonedDateTime`.
 */
object TMTTimeHelper {

    /**
     * Combines the [[csw.time.core.models.TMTTime]] with the given timezone to get a `java.time.ZonedDateTime``
     *
     * @param zoneId id of the required zone
     * @return time at the given zone
     */
    fun atZone(tmtTime: TMTTime, zoneId: ZoneId): ZonedDateTime = tmtTime.value.toJavaInstant().atZone(zoneId)

    /**
     * Combines the [[csw.time.core.models.TMTTime]] with the Local timezone to get a `java.time.ZonedDateTime`.
     * Local timezone is the system's default timezone.
     *
     * @return time at the Local zone
     */
    fun atLocal(tmtTime: TMTTime): ZonedDateTime = atZone(tmtTime, ZoneId.systemDefault())

    /**
     * Combines the [[csw.time.core.models.TMTTime]] with the Hawaii timezone to get a `java.time.ZonedDateTime`.
     *
     * @return time at the Hawaii-Aleutian Standard Time (HST) zone
     */
    fun atHawaii(tmtTime: TMTTime): ZonedDateTime = atZone(tmtTime, ZoneId.of("US/Hawaii"))

    /**
     * Converts the [[csw.time.core.models.TMTTime]] instance to `java.time.ZonedDateTime` by adding 0 offset of UTC timezone.
     *
     * @return zoned representation of the TMTTime
     */
    fun toZonedDateTime(tmtTime: TMTTime): ZonedDateTime = atZone(tmtTime, ZoneOffset.UTC)
}