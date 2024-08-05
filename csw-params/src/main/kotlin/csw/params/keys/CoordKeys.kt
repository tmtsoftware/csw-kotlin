package csw.params.keys

import arrow.core.*
import csw.params.commands.HasParms
import csw.params.core.models.*
import kotlinx.serialization.Serializable


enum class CoordType { EQ, AltAz, CAT }

@Serializable
data class EqStore(override val name: Key, val ctype: CoordType, val value: String): HasKey

@Serializable
data class CatStore(override val name: Key, val ctype: CoordType, val catalogName: String, val catalogObject: String): HasKey


data class EqCoordKey(val tag: Tag): IsKey {
    override val name = tag.name

    fun set(raH: Angle, decD: Angle, frame: EqFrame = EqCoord.DEFAULT_FRAME) =
        encode(tag, CoordType.EQ, raH, decD, frame, EqCoord.DEFAULT_PMX, EqCoord.DEFAULT_PMY)
        //EqStore(name, CoordType.EQ, encodeValues(raH, decD))

    fun set(raH: Angle, decD: Angle, pmra: Double, pmdec: Double) =
        encode(tag, CoordType.EQ, raH, decD, EqFrame.ICRS, pmra, pmdec)

    fun set(catalogName: String, catalogObject: String) = CatStore(tag.name, CoordType.CAT, catalogName, catalogObject)

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<EqCoord> {
        val x:EqCoord? = getStored(name, target)?.let { stored ->
            decodeToEqCoord(stored.name, stored.value)
        }
        return x?.let { Option.fromNullable(it) } ?: None
    }

    operator fun invoke(target: HasParms):EqCoord = get(target).getOrElse { throw NoSuchElementException("Setup doesn't have it") }

    companion object {
        private val DELIM = ","
        fun encode(tag: Tag, ctype: CoordType, ra: Angle, dec: Angle, frame: EqFrame, pmra: Double, pmdec: Double): EqStore =
            EqStore(tag.name, ctype, encodeValues(ra, dec, frame, "none", pmra, pmdec))

        fun encodeValues(ra: Angle, dec: Angle, frame: EqFrame = EqCoord.DEFAULT_FRAME,
                   catName:String = EqCoord.DEFAULT_CATNAME,
                   pmra: Double = EqCoord.DEFAULT_PMX, pmdec: Double = EqCoord.DEFAULT_PMY): String {
            return "${ra.uas}$DELIM${dec.uas}$DELIM$frame$DELIM$catName$DELIM$pmra$DELIM$pmdec"
        }

        fun decodeToEqCoord(tagStr: String, eqs: String): EqCoord? {
            val items = eqs.split(DELIM)
            println("items: $items")
            require(items.size == 6) { "Expected exactly 6 items for EqCoord, got ${items.size}" }
            // Test first
            val tag = if (Tag.names.contains(tagStr)) {
                Tag.valueOf(tagStr)
            } else return null

            val frameStr = items[2]
            val frame = if (EqFrame.names.contains(frameStr)) {
                EqFrame.valueOf(frameStr)
            } else return null

            val rauas = items[0].toLong()
            val decuas = items[1].toLong()

            val catName = items[3]

            val pmra = items[4].toDouble()
            val pmdec = items[5].toDouble()

            return EqCoord(tag, Angle(rauas), Angle(decuas), frame, ProperMotion(pmra, pmdec), catName )
        }

        private fun getStored(name: Key, target: HasParms): EqStore? {
            val s: HasKey? = target.nget(name)
            return if (s is EqStore) s else null
        }

    }
}

