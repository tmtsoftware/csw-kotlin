package csw.params.keys

import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import csw.params.commands.HasParms
import csw.params.core.models.*
import kotlinx.serialization.Serializable

enum class IdType { EXID, OBSID }

@Serializable
data class IdStore(override val name: Key, val idtype: IdType, val value: String): HasKey {
    companion object {
        internal fun getStored(name: Key, target: HasParms): Option<IdStore> {
            val s: HasKey? = target.nget(name)
            return if (s is IdStore) Option(s) else None
        }
    }
}

@Serializable
data class ExposureIdKey(override val name: Key): IsKey {

    fun set(value: ExposureId, vararg values: ExposureId): IdStore =
        IdStore(name, IdType.EXID, KeyHelpers.lencode((arrayOf(value) + values).toList()))

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<ExposureId> =
        IdStore.getStored(name, target).map { ExposureId(it.value) }

    operator fun invoke(target: HasParms): ExposureId = get(target).getOrElse { throw NoSuchElementException("The key is missing from the parameter set.") }
}

@Serializable
data class ObsIdKey(override val name: Key): IsKey {

    fun set(value: ObsId, vararg values: ObsId): IdStore =
        IdStore(name, IdType.OBSID, KeyHelpers.lencode((arrayOf(value) + values).toList()))

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<ObsId> =
        IdStore.getStored(name, target).map { ObsId(it.value) }

    fun semesterId(target: HasParms):SemesterId = invoke(target).programId.semesterId

    fun year(target: HasParms): Year = semesterId(target).year

    fun semester(target: HasParms): Semester = semesterId(target).semester

    operator fun invoke(target: HasParms): ObsId = get(target).getOrElse { throw NoSuchElementException("The ObsId key is missing from the parameter set.") }
}