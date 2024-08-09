package csw.params.keys

import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import csw.params.commands.HasParms
import csw.params.core.models.*
import kotlinx.serialization.Serializable


enum class CoordType { EQ, AltAz, SSO, COM, MP, CAT }

@Serializable
data class CoordStore(override val name: Key, val ctype: CoordType, val value: String) : HasKey {
    companion object {
        internal fun getStored(name: Key, target: HasParms): CoordStore? {
            val s: HasKey? = target.nget(name)
            return if (s is CoordStore) s else null
        }
    }
}

@Serializable
data class CatStore(override val name: Key, val ctype: CoordType, val catalogName: String, val catalogObject: String) :
    HasKey


data class EqCoordKey(val tag: Tag) : IsKey {
    override val name = tag.name
    private val notFound = "Parameter set does not contain key: $name"

    fun set(raH: Angle, decD: Angle, frame: EqFrame = EqCoord.DEFAULT_FRAME) =
        encode(tag, CoordType.EQ, raH, decD, frame, EqCoord.DEFAULT_PMX, EqCoord.DEFAULT_PMY)

    fun set(raH: Angle, decD: Angle, pmra: Double, pmdec: Double): HasKey {
        return encode(tag, CoordType.EQ, raH, decD, EqFrame.ICRS, pmra, pmdec)
    }

    fun set(catalogName: String, catalogObject: String) = CatStore(tag.name, CoordType.CAT, catalogName, catalogObject)

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<EqCoord> {
        val eqCoord: EqCoord? = CoordStore.getStored(name, target)?.let { stored ->
            decodeToEqCoord(stored)
        }
        return eqCoord?.let { Option.fromNullable(it) } ?: None
    }

    operator fun invoke(target: HasParms): EqCoord = get(target).getOrElse { throw NoSuchElementException(notFound) }

    companion object {
        private val DELIM = ","
        fun encode(
            tag: Tag,
            ctype: CoordType,
            ra: Angle,
            dec: Angle,
            frame: EqFrame,
            pmra: Double,
            pmdec: Double
        ): CoordStore =
            CoordStore(tag.name, ctype, encodeValues(ra, dec, frame, "none", pmra, pmdec))

        fun encodeValues(
            ra: Angle, dec: Angle, frame: EqFrame = EqCoord.DEFAULT_FRAME,
            catName: String = EqCoord.DEFAULT_CATNAME,
            pmra: Double = EqCoord.DEFAULT_PMX, pmdec: Double = EqCoord.DEFAULT_PMY
        ): String {
            return "${ra.uas}$DELIM${dec.uas}$DELIM$frame$DELIM$catName$DELIM$pmra$DELIM$pmdec"
        }

        fun decodeToEqCoord(cstore: CoordStore): EqCoord? {
            val items = cstore.value.split(DELIM)
            require(cstore.ctype == CoordType.EQ)
            require(items.size == 6) { "Expected exactly 6 items for EqCoord, got ${items.size}" }
            // Test first
            val tag = if (Tag.names.contains(cstore.name)) {
                Tag.valueOf(cstore.name)
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

            return EqCoord(tag, Angle(rauas), Angle(decuas), frame, ProperMotion(pmra, pmdec), catName)
        }
    }
}

data class AltAzCoordKey(val tag: Tag) : IsKey {
    override val name = tag.name
    private val notFound = "Parameter set does not contain key: $name"

    fun set(alt: Angle, az: Angle): HasKey {
        if (alt.toDegree() > 90.0 || alt.toDegree() < -90.0) throw IllegalArgumentException("Altitude must be between +/- 90.0")
        if (az.toDegree() > 360.0 || az.toDegree() < -360.0) throw IllegalArgumentException("Azimuth must be between +/- 360.0")
        return CoordStore(tag.name, CoordType.AltAz, encode(alt, az))
    }

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<AltAzCoord> {
        val altAzCoord: AltAzCoord? = CoordStore.getStored(name, target)?.let { stored ->
            decodeToAltAzCoord(stored)
        }
        return altAzCoord?.let { Option.fromNullable(it) } ?: None
    }

    operator fun invoke(target: HasParms): AltAzCoord = get(target).getOrElse { throw NoSuchElementException(notFound) }

    companion object {
        private val DELIM = ","

        fun encode(alt: Angle, az: Angle): String = "${alt.uas}$DELIM${az.uas}"

        fun decodeToAltAzCoord(cstore: CoordStore): AltAzCoord? {
            val items = cstore.value.split(DELIM)
            require(cstore.ctype == CoordType.AltAz)
            require(items.size == 2) { "Expected exactly 2 items for AltAzCoord, but got ${items.size}" }
            // Test first
            val tag = if (Tag.names.contains(cstore.name)) {
                Tag.valueOf(cstore.name)
            } else return null

            val alt = items[0].toLong()
            val az = items[1].toLong()
            return AltAzCoord(tag, Angle(alt), Angle(az))
        }
    }
}

data class SolarSystemCoordKey(val tag: Tag) : IsKey {
    override val name = tag.name
    private val notFound = "Parameter set does not contain key: $name"

    fun set(body: SolarSystemObject): HasKey = CoordStore(tag.name, CoordType.SSO, encode(body))

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<SolarSystemCoord> {
        val ssCoord: SolarSystemCoord? = CoordStore.getStored(name, target)?.let { stored ->
            decodeToSolarSystem(stored)
        }
        return ssCoord?.let { Option.fromNullable(it) } ?: None
    }

    operator fun invoke(target: HasParms): SolarSystemCoord =
        get(target).getOrElse { throw NoSuchElementException(notFound) }

    companion object {

        fun encode(body: SolarSystemObject): String = "${body.name}"

        fun decodeToSolarSystem(cstore: CoordStore): SolarSystemCoord? {
            require(cstore.ctype == CoordType.SSO)
            // Test first
            val tag = if (Tag.names.contains(cstore.name)) {
                Tag.valueOf(cstore.name)
            } else return null

            if (!SolarSystemObject.names.contains(cstore.value))
                throw IllegalArgumentException("Value \"${cstore.value}\" is not a Solar System object.")
            val body = SolarSystemObject.valueOf(cstore.value)
            return SolarSystemCoord(tag, body)
        }
    }
}

data class CometCoordKey(val tag: Tag) : IsKey {
    override val name = tag.name
    private val notFound = "Parameter set does not contain key: $name"

    fun set(
        epochOfPerihelion: EpochOfPerihelion, inclination: Inclination, longAscendingNode: LongAscendingNode,
        argOfPerihelion: ArgOfPerihelion, perihelionDistance: PerihelionDistance, eccentricity: Eccentricity
    ): HasKey =
        CoordStore(
            tag.name, CoordType.COM, encode(
                epochOfPerihelion, inclination, longAscendingNode,
                argOfPerihelion, perihelionDistance, eccentricity
            )
        )

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<CometCoord> {
        val ssCoord: CometCoord? = CoordStore.getStored(name, target)?.let { stored ->
            decodeToCometCoord(stored)
        }
        return ssCoord?.let { Option.fromNullable(it) } ?: None
    }

    operator fun invoke(target: HasParms): CometCoord = get(target).getOrElse { throw NoSuchElementException(notFound) }

    companion object {
        private val DELIM = ","

        fun encode(
            epochOfPerihelion: EpochOfPerihelion, inclination: Inclination, longAscendingNode: LongAscendingNode,
            argOfPerihelion: ArgOfPerihelion, perihelionDistance: PerihelionDistance, eccentricity: Eccentricity
        ): String =
            "${epochOfPerihelion}$DELIM${inclination.uas}$DELIM${longAscendingNode.uas}$DELIM${argOfPerihelion.uas}$DELIM$perihelionDistance$DELIM$eccentricity"

        fun decodeToCometCoord(cstore: CoordStore): CometCoord? {
            val items = cstore.value.split(CometCoordKey.DELIM)
            require(cstore.ctype == CoordType.COM)
            require(items.size == 6) { "Expected exactly 6 items for CometCoord, but got ${items.size}" }

            // Test first
            val tag = if (Tag.names.contains(cstore.name)) {
                Tag.valueOf(cstore.name)
            } else return null

            val epochOfPerihelion = items[0].toDouble()
            val inclination = items[1].toLong()
            val longAscendingNode = items[2].toLong()
            val argOfPerihelion = items[3].toLong()
            val perihelionDistance = items[4].toDouble()
            val eccentricity = items[5].toDouble()

            return CometCoord(
                tag,
                epochOfPerihelion,
                Angle(inclination),
                Angle(longAscendingNode),
                Angle(argOfPerihelion),
                perihelionDistance,
                eccentricity
            )
        }
    }
}

data class MinorPlanetCoordKey(val tag: Tag) : IsKey {
    override val name = tag.name
    private val notFound = "Parameter set does not contain key: $name"

    fun set(
        epoch: Epoch,
        inclination: Inclination,
        longAscendingNode: LongAscendingNode,
        argOfPerihelion: ArgOfPerihelion,
        meanDistance: MeanDistance,
        eccentricity: Eccentricity,
        meanAnomaly: MeanAnomaly
    ): HasKey =
        CoordStore(
            tag.name, CoordType.MP, encode(
                epoch, inclination, longAscendingNode,
                argOfPerihelion, meanDistance, eccentricity, meanAnomaly
            )
        )

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): Option<MinorPlanetCoord> {
        val ssCoord: MinorPlanetCoord? = CoordStore.getStored(name, target)?.let { stored ->
            decode(stored)
        }
        return ssCoord?.let { Option.fromNullable(it) } ?: None
    }

    operator fun invoke(target: HasParms): MinorPlanetCoord =
        get(target).getOrElse { throw NoSuchElementException(notFound) }

    companion object {
        private val DELIM = ","

        fun encode(
            epoch: Epoch,
            inclination: Inclination,
            longAscendingNode: LongAscendingNode,
            argOfPerihelion: ArgOfPerihelion,
            meanDistance: MeanDistance,
            eccentricity: Eccentricity,
            meanAnomoly: MeanAnomaly
        ): String =
            "${epoch}$DELIM${inclination.uas}$DELIM${longAscendingNode.uas}$DELIM${argOfPerihelion.uas}$DELIM$meanDistance$DELIM$eccentricity$DELIM${meanAnomoly.uas}"

        fun decode(cstore: CoordStore): MinorPlanetCoord? {
            val items = cstore.value.split(DELIM)
            require(cstore.ctype == CoordType.MP)
            require(items.size == 7) { "Expected exactly 7 items for MinorPlanetCoord, but got ${items.size}" }

            // Test first
            val tag = if (Tag.names.contains(cstore.name)) {
                Tag.valueOf(cstore.name)
            } else return null

            val epochOfPerihelion = items[0].toDouble()
            val inclination = items[1].toLong()
            val longAscendingNode = items[2].toLong()
            val argOfPerihelion = items[3].toLong()
            val meanDistance = items[4].toDouble()
            val eccentricity = items[5].toDouble()
            val meanAnomoly = items[6].toLong()

            return MinorPlanetCoord(
                tag,
                epochOfPerihelion,
                Angle(inclination),
                Angle(longAscendingNode),
                Angle(argOfPerihelion),
                meanDistance,
                eccentricity,
                Angle(meanAnomoly)
            )
        }
    }
}