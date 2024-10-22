package csw.params.keys

//import arrow.core.*
import csw.params.commands.HasParms
import csw.params.core.models.*
import kotlinx.serialization.Serializable

enum class CoordType { EQ, AltAz, SSO, COM, MP, CAT }

interface Tagged {
    val tag: Tag
}

@Serializable
data class CoordStore(override val name: Key, val ctype: CoordType, val data: String) : HasKey {
    override fun toString(): String = "CoordXStore($name,$ctype,$data)"
    companion object {
        internal fun getStored(name: Key, target: HasParms): CoordStore? {
            val s: HasKey? = target.nget(name)
            return if (s is CoordStore) s else null
        }

        /*
         * Returns a Coord that is represented by the CoordStore
         */
        fun coord(c: CoordStore): Coord? =
            when (c.ctype) {
                CoordType.EQ -> EqCoordKey.decode(c)
                CoordType.SSO -> SolarSystemCoordKey.decode(c)
                CoordType.AltAz -> AltAzCoordKey.decode(c)
                CoordType.COM -> CometCoordKey.decode(c)
                CoordType.MP -> MinorPlanetCoordKey.decode(c)
                CoordType.CAT -> CatalogCoordKey.decode(c)
            }
        }
}

data class CatalogCoordKey(override val tag: Tag): IsKey, Tagged {
    override val name = tag.name
    private val notFound = "Parameter set does not contain key: $name"

    fun set(catalogName: CatalogName, catalogObject: CatalogObject): CoordStore =
        CoordStore(name, CoordType.CAT, encode(catalogName, catalogObject))

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): CatalogCoord? = CoordStore.getStored(name, target)?.let {decode(it) }

    operator fun invoke(target: HasParms): CatalogCoord = get(target) ?: throw NoSuchElementException(notFound)

    companion object {
        private const val DELIM = ","
        private fun encode(catalogName: CatalogName, catalogObject: CatalogObject): String =
            "${catalogName.name}$DELIM${catalogObject.name}"

        fun decode(cstore: CoordStore): CatalogCoord? {
            require(cstore.ctype == CoordType.CAT)
            // Test first
            val tag = if (Tag.names.contains(cstore.name)) {
                Tag.valueOf(cstore.name)
            } else return null

            val items = cstore.data.split(DELIM)
            require(items.size == 2) { "Expected exactly 2 items for CatalogCoord, got ${items.size}" }

            val catalogName = CatalogName(items[0])
            val catalogObject = CatalogObject(items[1])
            return CatalogCoord(tag, catalogName, catalogObject)
        }
    }
}

data class EqCoordKey(override val tag: Tag) : IsKey, Tagged {
    override val name = tag.name
    private val notFound = "Parameter set does not contain key: $name"

    fun set(raH: Angle, decD: Angle, frame: EqFrame = EqCoord.DEFAULT_FRAME) =
        encode(tag, CoordType.EQ, raH, decD, frame, EqCoord.DEFAULT_PMX, EqCoord.DEFAULT_PMY)

    fun set(raH: Angle, decD: Angle, frame: EqFrame, pmra: Double, pmdec: Double) =
        encode(tag, CoordType.EQ, raH, decD, frame, pmra, pmdec)

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): EqCoord? =
        CoordStore.getStored(name, target)?.let { decode(it) }

    operator fun invoke(target: HasParms): EqCoord = get(target) ?: throw NoSuchElementException(notFound)

    companion object {
        private const val DELIM = ","
        fun encode(
            tag: Tag,
            ctype: CoordType,
            ra: Angle,
            dec: Angle,
            frame: EqFrame,
            pmra: Double,
            pmdec: Double
        ): CoordStore =
            CoordStore(tag.name, ctype, encode(ra, dec, frame, "none", pmra, pmdec))

        fun encode(
            ra: Angle, dec: Angle, frame: EqFrame = EqCoord.DEFAULT_FRAME,
            catName: String = EqCoord.DEFAULT_CATNAME,
            pmra: Double = EqCoord.DEFAULT_PMX, pmdec: Double = EqCoord.DEFAULT_PMY
        ): String {
            return "${ra.uas}$DELIM${dec.uas}$DELIM$frame$DELIM$catName$DELIM$pmra$DELIM$pmdec"
        }

        fun decode(cstore: CoordStore): EqCoord? {
            require(cstore.ctype == CoordType.EQ)
            // Test first
            val tag = if (Tag.names.contains(cstore.name)) {
                Tag.valueOf(cstore.name)
            } else return null

            val items = cstore.data.split(DELIM)
            require(items.size == 6) { "Expected exactly 6 items for EqCoord, got ${items.size}" }

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

data class AltAzCoordKey(override val tag: Tag) : IsKey, Tagged {
    override val name = tag.name
    private val notFound = "Parameter set does not contain key: $name"

    fun set(alt: Angle, az: Angle): CoordStore {
        if (alt.toDegree() > 90.0 || alt.toDegree() < -90.0) throw IllegalArgumentException("Altitude must be between +/- 90.0")
        if (az.toDegree() > 360.0 || az.toDegree() < -360.0) throw IllegalArgumentException("Azimuth must be between +/- 360.0")
        return CoordStore(tag.name, CoordType.AltAz, encode(alt, az))
    }

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): AltAzCoord? =
        CoordStore.getStored(name, target)?.let { decode(it) }

    operator fun invoke(target: HasParms): AltAzCoord = get(target) ?: throw NoSuchElementException(notFound)

    companion object {
        private const val DELIM = ","

        fun encode(alt: Angle, az: Angle): String = "${alt.uas}$DELIM${az.uas}"

        fun decode(cstore: CoordStore): AltAzCoord? {
            val items = cstore.data.split(DELIM)
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

data class SolarSystemCoordKey(override val tag: Tag) : IsKey, Tagged {
    override val name = tag.name
    private val notFound = "Parameter set does not contain key: $name"

    fun set(body: SolarSystemObject) = CoordStore(tag.name, CoordType.SSO, encode(body))

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): SolarSystemCoord? =
        CoordStore.getStored(name, target)?.let {decode(it) }

    operator fun invoke(target: HasParms): SolarSystemCoord =
        get(target) ?: throw NoSuchElementException(notFound)

    companion object {

        fun encode(body: SolarSystemObject): String = body.name

        fun decode(cstore: CoordStore): SolarSystemCoord? {
            require(cstore.ctype == CoordType.SSO)
            // Test first
            val tag = if (Tag.names.contains(cstore.name)) {
                Tag.valueOf(cstore.name)
            } else return null

            if (!SolarSystemObject.names.contains(cstore.data))
                throw IllegalArgumentException("Value \"${cstore.data}\" is not a Solar System object.")
            val body = SolarSystemObject.valueOf(cstore.data)
            return SolarSystemCoord(tag, body)
        }
    }
}

data class CometCoordKey(override val tag: Tag) : IsKey, Tagged {
    override val name = tag.name
    private val notFound = "Parameter set does not contain key: $name"

    fun set(
        epochOfPerihelion: EpochOfPerihelion, inclination: Inclination, longAscendingNode: LongAscendingNode,
        argOfPerihelion: ArgOfPerihelion, perihelionDistance: PerihelionDistance, eccentricity: Eccentricity
    ) = CoordStore(
            tag.name, CoordType.COM, encode(
                epochOfPerihelion, inclination, longAscendingNode,
                argOfPerihelion, perihelionDistance, eccentricity
            )
        )

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): CometCoord? =
        CoordStore.getStored(name, target)?.let { decode(it) }

    operator fun invoke(target: HasParms): CometCoord = get(target) ?: throw NoSuchElementException(notFound)

    companion object {
        private const val DELIM = ","

        fun encode(
            epochOfPerihelion: EpochOfPerihelion, inclination: Inclination, longAscendingNode: LongAscendingNode,
            argOfPerihelion: ArgOfPerihelion, perihelionDistance: PerihelionDistance, eccentricity: Eccentricity
        ): String =
            "${epochOfPerihelion}$DELIM${inclination.uas}$DELIM${longAscendingNode.uas}$DELIM${argOfPerihelion.uas}$DELIM$perihelionDistance$DELIM$eccentricity"

        fun decode(cstore: CoordStore): CometCoord? {
            val items = cstore.data.split(DELIM)
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

data class MinorPlanetCoordKey(override val tag: Tag) : IsKey, Tagged {
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
    ) =
        CoordStore(
            tag.name, CoordType.MP, encode(
                epoch, inclination, longAscendingNode,
                argOfPerihelion, meanDistance, eccentricity, meanAnomaly
            )
        )

    fun isIn(target: HasParms): Boolean = target.exists(this)

    fun get(target: HasParms): MinorPlanetCoord? =
        CoordStore.getStored(name, target)?.let { decode(it) }

    operator fun invoke(target: HasParms): MinorPlanetCoord =
        get(target) ?: throw NoSuchElementException(notFound)

    companion object {
        private const val DELIM = ","

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
            val items = cstore.data.split(DELIM)
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


@Serializable
data class Coordinates(override val name: Key, val data: List<CoordStore>) : HasKey {
    /**
     * Returns all tags in the CoordContainer
     */
    val tags: List<Tag>
        get() = data.map { Tag.valueOf(it.name) }

    /**
     * Returns the number of coords in the Coordinates
     */
    val size: Int
        get() = data.size

    /**
     * Check to see if a tag is present in Coordinates
     */
    fun contains(tag: Tag): Boolean = tags.contains(tag)

    /**
     * Returns the Coord for the tag if it exists
     */
    fun tag(tag: Tag) : Coord? {
        if (!contains(tag)) return null
        return CoordStore.coord(data.first { it.name == tag.value })
    }
}

/**
 * A CoordKey contains one or more Coordinate types like in CSW
 */
data class CoordKey(override val name: Key): IsKey {
    private val notFound = "Parameter set does not contain key: $name"

    fun set(value: CoordStore, vararg values: CoordStore): Coordinates =
        Coordinates(name, arrayOf(value, *values).toList())

    fun contains(target: HasParms): Boolean = target.exists(this)
    fun isIn(target: HasParms): Boolean = contains(target)

    fun get(target: HasParms): Coordinates? = KeyHelpers.getStored<Coordinates>(this, target)

    fun tag(target: HasParms, tag: Tag): Coord? = get(target)?.tag(tag)

    operator fun invoke(target: HasParms): Coordinates = get(target) ?: throw NoSuchElementException(notFound)

    fun value(s: HasParms): Coordinates = invoke(s)

    companion object {
        fun make(name: Key, units: Units = Units.NoUnits): NumberKey = NumberKey(name, units)
    }
}
