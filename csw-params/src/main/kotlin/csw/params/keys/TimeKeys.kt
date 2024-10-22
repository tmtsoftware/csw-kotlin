package csw.params.keys

import csw.params.commands.HasParms
import csw.time.core.models.TAITime
import csw.time.core.models.TMTTime
import csw.time.core.models.UTCTime
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import java.time.ZonedDateTime

enum class TimeType { UTC, TAI }

data class TimeStore(override val name: Key, val ttype: TimeType, val data: Array<String>) : HasKey {
    val svalue: Array<String> get() = data
    //fun toStringAsTimes(): String = TimeHelpers.instantsFromStrings(svalue).joinToString(",")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TimeStore
        if (name != other.name) return false
        if (ttype != other.ttype) return false
        if (!data.contentEquals(other.data)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + ttype.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

data class TAITimeKey(override val name: Key) : IsKey {

    fun set(value: TAITime, vararg values: TAITime): TimeStore =
        TimeStore(name, TimeType.TAI, TimeHelpers.tencode((values.toList() + value).toTypedArray()))

    fun contains(target: HasParms): Boolean = target.exists(this)
    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun show(target: HasParms): String = value(target).joinToString(",", "[", "]")

    fun get(target: HasParms): Array<TAITime>? {
        val s = KeyHelpers.getStored<TimeStore>(this, target)
        return s?.let { s -> TimeHelpers.instantsFromStrings(s.svalue).map { TAITime(it) }.toTypedArray() }
    }

    operator fun invoke(target: HasParms): Array<TAITime> =
        get(target) ?:  throw NoSuchElementException("Setup doesn't have it")

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

    fun contains(target: HasParms): Boolean = target.exists(this)
    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Array<UTCTime>? {
        val s = KeyHelpers.getStored<TimeStore>(this, target)
        return s?.let { s -> TimeHelpers.instantsFromStrings(s.svalue).map { UTCTime(it) }.toTypedArray() }
    }

    fun show(target: HasParms): String = value(target).joinToString(",", "[", "]")

    operator fun invoke(target: HasParms): Array<UTCTime> =
        get(target) ?: throw NoSuchElementException("Setup doesn't have it")

    fun value(s: HasParms): Array<UTCTime> = invoke(s)
    fun head(s: HasParms): UTCTime = invoke(s)[0]

    // For compatibility
    companion object {
        fun make(name: Key): UTCTimeKey = UTCTimeKey(name)
    }
}

private object TimeHelpers {

    fun toInstant(s: String): Instant = ZonedDateTime.parse(s).toInstant().toKotlinInstant()

    fun instantsFromStrings(tin: Array<String>): Array<Instant> =
        tin.map { toInstant(it) }.toTypedArray()

    fun tencode(t: Array<TMTTime>): Array<String> =  t.map { it.value.toString() }.toTypedArray() //.joinToString(",")
}