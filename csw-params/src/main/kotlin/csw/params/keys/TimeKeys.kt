package csw.params.keys

import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import csw.params.commands.HasParms
import csw.time.core.models.TAITime
import csw.time.core.models.TMTTime
import csw.time.core.models.UTCTime
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

enum class TimeType { UTC, TAI }

@Serializable
data class TimeStore(override val name: Key, val ttype: TimeType, val value: String) : HasKey {
    val svalue: Array<String> get() = KeyHelpers.asStrings(value)
    fun toStringAsTimes(): String = TimeHelpers.instantsFromStrings(svalue).joinToString(",")
}

data class TAITimeKey(override val name: Key) : IsKey {

    fun set(value: TAITime, vararg values: TAITime): TimeStore =
        TimeStore(name, TimeType.TAI, TimeHelpers.tencode((values.toList() + value).toTypedArray()))

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun isNotIn(target: HasParms): Boolean = !isIn(target)

    fun show(target: HasParms): String = value(target).joinToString(",", "[", "]")

    fun get(target: HasParms): Option<Array<TAITime>> {
        val s: HasKey? = target.nget(name)
        return if (s is TimeStore)
            Option(TimeHelpers.instantsFromStrings(s.svalue).map { TAITime(it) }.toTypedArray())
        else
            None
    }

    operator fun invoke(target: HasParms): Array<TAITime> =
        get(target).getOrElse { throw NoSuchElementException("Setup doesn't have it") }

    fun value(s: HasParms): Array<TAITime> = invoke(s)
    fun head(s: HasParms): TAITime = invoke(s)[0]

    // For compatibility
    companion object {
        fun make(name: Key): TAITimeKey = TAITimeKey(name)
    }
}


data class UTCTimeKey(override val name: Key) : IsKey {

    fun set(value: UTCTime, vararg values: UTCTime): TimeStore =
        TimeStore(name, TimeType.UTC, TimeHelpers.tencode((values.toList() + value).toTypedArray()))

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun isNotIn(target: HasParms): Boolean = !isIn(target)

    fun get(target: HasParms): Option<Array<UTCTime>> {
        val s: HasKey? = target.nget(name)
        return if (s is TimeStore)
            Option(TimeHelpers.instantsFromStrings(s.svalue).map { UTCTime(it) }.toTypedArray())
        else
            None
    }

    fun show(target: HasParms): String = value(target).joinToString(",", "[", "]")

    operator fun invoke(target: HasParms): Array<UTCTime> =
        get(target).getOrElse { throw NoSuchElementException("Setup doesn't have it") }

    fun value(s: HasParms): Array<UTCTime> = invoke(s)
    fun head(s: HasParms): UTCTime = invoke(s)[0]

    // For compatibility
    companion object {
        fun make(name: Key): UTCTimeKey = UTCTimeKey(name)
    }
}

private object TimeHelpers {
    fun instantsFromStrings(values: Array<String>): List<Instant> {
        require(values.size % 2 == 0) { "TimeStore should have a multiple of 2 elements" }
        val times = mutableListOf<Instant>()
        for (i in values.indices step 2) {
            val t = Instant.fromEpochSeconds(values[i].toLong(), values[i + 1].toLong())
            times.add(t)
        }
        return times
    }

    fun longsFromStrings(values: Array<String>): List<Long> {
        require(values.size % 2 == 0) { "TimeStore should have a multiple of 2 elements" }
        val times = mutableListOf<Long>()
        for (i in values.indices step 2) {
            times.add(values[i].toLong())
            times.add(values[i + 1].toLong())
        }
        return times
    }

    fun tencode(t: Array<TMTTime>): String = toArray(t).joinToString(",")

    fun toArray(s: Array<TMTTime>): List<Long> {
        var la = emptyList<Long>()
        for (tmtTime in s) {
            val x = tmtTime.value.epochSeconds
            val y = tmtTime.value.nanosecondsOfSecond.toLong()
            la = la + x
            la = la + y
        }
        return la
    }

}