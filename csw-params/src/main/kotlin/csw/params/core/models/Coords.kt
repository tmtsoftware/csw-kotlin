package csw.params.core.models

import csw.params.core.models.Angle.Companion.degree


/**
 * A tag is a label to indicate the use of the coordinate
 *
 * @param name what is the role of this coordinate
 */
enum class Tag(val value: String) {

    BASE("BASE"),
    OIWFS1("OIWFS1"),
    OIWFS2("OIWFS2"),
    OIWFS3("OIWFS3"),
    OIWFS4("OIWFS4"),
    ODGW1("ODGW1"),
    ODGW2("ODGW2"),
    ODGW3("ODGW3"),
    ODGW4("ODGW4"),
    GUIDER1("GUIDER1"),
    GUIDER2("GUIDER2");

    override fun toString(): String {
        return value
    }

    companion object {
        val names by lazy {
            Tag.values().map { it.toString() }
        }
    }
}

enum class EqFrame(val description: String) {
    ICRS("International Celestial Reference System"),
    FK5("Fifth Fundamental Catalogue");

    companion object {
        val names by lazy {
            EqFrame.values().map { it.toString() }
        }
    }
}

/**
 * All coordinates has a tag
 * A Coord has a tag.
 */
sealed interface HasTag {
    val tag: Tag
}

data class AltAzCoord(override val tag: Tag, val alt: Angle, val az: Angle) : HasTag {
    override fun toString(): String = "AltAzCoord($tag ${alt.toDegree()}  ${az.toDegree()})"
}

enum class SolarSystemObject {
    Mercury,
    Venus,
    Moon,
    Mars,
    Jupiter,
    Saturn,
    Neptune,
    Uranus;

    companion object {
        val names by lazy {
            SolarSystemObject.values().map { it.toString() }
        }
    }
}

data class SolarSystemCoord(override val tag: Tag, val body: SolarSystemObject) : HasTag

data class MinorPlanetCoord(
    override val tag: Tag,
    val epoch: Double,            // TT as a Modified Julian Date
    val inclination: Angle,       // degrees
    val longAscendingNode: Angle, // degrees
    val argOfPerihelion: Angle,   // degrees
    val meanDistance: Double,     // AU
    val eccentricity: Double,
    val meanAnomaly: Angle // degrees
) : HasTag


data class CometCoord(
    override val tag: Tag,
    val epochOfPerihelion: Double,  // TT as a Modified Julian Date
    val inclination: Angle,         // degrees
    val longAscendingNode: Angle,   // degrees
    val argOfPerihelion: Angle,     // degrees
    val perihelionDistance: Double, // AU
    val eccentricity: Double
) : HasTag


data class EqCoords(val ra: Angle, val dec: Angle, val frame: EqFrame)
/**
 * Equatorial coordinates.
 *
 * @param tag a Tag instance (name for the coordinates)
 * @param ra right ascension, expressed as an Angle instance
 * @param dec declination, expressed as an Angle instance
 * @param frame the IAU celestial reference system
 * @param catalogName  the name of the catalog from which the coordinates were taken (use "none" if unknown)
 * @param pm proper motion
 */
data class EqCoord(
    override val tag: Tag,
    val ra: Angle,
    val dec: Angle,
    val frame: EqFrame,
    val pm: ProperMotion,
    val catalogName: String
    ) : HasTag {

    /**
     * Creates an EqCoord from the given arguments, which all have default values.
     * The values for ra and dec may be an Angle instance, or a String that can be parsed by Angle.parseRa()
     *
     * @param ra may be an Angle instance, or a String (in hms) that can be parsed by Angle.parseRa() or a Double value in degrees (default: 0.0)
     * @param dec may be an Angle instance, or a String that can be parsed by Angle.parseDe() or a Double value in degrees  (default: 0.0)
     * @param frame the the IAU celestial reference system (default: ICRS)
     * @param tag a Tag instance (name for the coordinates, default: "BASE")
     * @param catalogName the name of the catalog from which the coordinates were taken (default: "none")
     * @param pmx proper motion X coordinate (default: 0.0)
     * @param pmy proper motion y coordinate (default: 0.0)
     */
    /*
    constructor(
        ra: Any = 0.0,
        dec: Any = 0.0,
        frame: EqFrame = DEFAULT_FRAME,
        pmx: Double = DEFAULT_PMX,
        pmy: Double = DEFAULT_PMY,
        tag: Tag = DEFAULT_TAG,
        catalogName: String = DEFAULT_CATNAME
        ) : this (
        tag,
        when (ra) {
            is String -> Angle.parseRa(ra)
            is Double -> ra.degree()
            is Angle -> ra
            else -> throw IllegalArgumentException("Unknown ra")
        },
        when (dec) {
            is String -> Angle.parseDe(dec)
            is Double -> dec.degree()
            is Angle -> dec
            else -> throw IllegalArgumentException("Unknown dec")
        },
        frame,
        ProperMotion(pmx, pmy),
        catalogName
    )
*/

    fun withPM(pmx: Double, pmy: Double): EqCoord = this.copy(pm = ProperMotion(pmx, pmy))

    override fun toString(): String =
        "EqCoord($tag,${Angle.raToString(ra.toRadian())},${Angle.deToString(dec.toRadian())},$frame,$catalogName,$pm)"


    companion object {
        val DEFAULT_FRAME: EqFrame = EqFrame.ICRS
        val DEFAULT_TAG: Tag = Tag.BASE
        val DEFAULT_PMX: Double = ProperMotion.DEFAULT_PROPERMOTION.pmx
        val DEFAULT_PMY: Double = ProperMotion.DEFAULT_PROPERMOTION.pmy
        val DEFAULT_CATNAME: String = "none"

        /**
         * Creates an EqCoord from the given arguments, which all have default values.
         * See matching constructior for a description of the arguments.
         */

        operator fun invoke(
            ra: String,
            dec: String,
            frame: EqFrame = DEFAULT_FRAME,
            tag: Tag = DEFAULT_TAG,
            catalogName: String = DEFAULT_CATNAME,
            pmx: Double = DEFAULT_PMX,
            pmy: Double = DEFAULT_PMY
        ): EqCoord = EqCoord(tag, Angle.parseRa(ra), Angle.parseDe(dec), frame, ProperMotion(pmx, pmy), catalogName)

        operator fun invoke(
            ra: Double,
            dec: Double,
            frame: EqFrame = DEFAULT_FRAME,
            tag: Tag = DEFAULT_TAG,
            pmx: Double = DEFAULT_PMX,
            pmy: Double = DEFAULT_PMY,
            catalogName: String = DEFAULT_CATNAME,
            ): EqCoord = EqCoord(tag, ra.degree(), dec.degree(), frame, ProperMotion(pmx, pmy), catalogName)

        /**
         * This allows creation of an EqCoordinate from a string of ra and dec with formats:
         * 20 54 05.689 +37 01 17.38
         * 10:12:45.3-45:17:50
         * 15h17m-11d10m
         * 15h17+89d15
         * 275d11m15.6954s+17d59m59.876s
         * 12.34567h-17.87654d
         *
         * @param radec string includes both the ra a dec continuous
         * @return a new EqCoord
         */
        operator fun invoke(
            radec: String,
            tag: Tag = DEFAULT_TAG,
            frame: EqFrame = DEFAULT_FRAME,
            pmx: Double = DEFAULT_PMX,
            pmy: Double = DEFAULT_PMY,
            catalogName: String = DEFAULT_CATNAME
            ): EqCoord {
            val (ra, dec) = Angle.parseRaDe(radec)
            return EqCoord(tag, ra, dec, frame, ProperMotion(pmx, pmy), catalogName)
        }

        operator fun invoke(
            ra: Angle,
            dec: Angle,
            frame: EqFrame = DEFAULT_FRAME,
            tag: Tag = DEFAULT_TAG,
            pmx: Double = DEFAULT_PMX,
            pmy: Double = DEFAULT_PMY,
            catalogName: String = DEFAULT_CATNAME
        ): EqCoord = EqCoord(tag, ra, dec, frame, ProperMotion(pmx, pmy), catalogName)
    }
}

/**
 * For the Java API
 */
object JCoords {
    val ICRS: EqFrame = EqFrame.ICRS
    val FK5: EqFrame = EqFrame.FK5

    val DEFAULT_FRAME: EqFrame = ICRS
    val DEFAULT_TAG: Tag = Tag.BASE
    val DEFAULT_PMX: Double = ProperMotion.DEFAULT_PROPERMOTION.pmx
    val DEFAULT_PMY: Double = ProperMotion.DEFAULT_PROPERMOTION.pmy
    val DEFAULT_CATNAME: String = "none"

    val Mercury: SolarSystemObject = SolarSystemObject.Mercury
    val Venus: SolarSystemObject = SolarSystemObject.Venus
    val Moon: SolarSystemObject = SolarSystemObject.Moon
    val Mars: SolarSystemObject = SolarSystemObject.Mars
    val Jupiter: SolarSystemObject = SolarSystemObject.Jupiter
    val Saturn: SolarSystemObject = SolarSystemObject.Saturn
    val Neptune: SolarSystemObject = SolarSystemObject.Neptune
    val Uranus: SolarSystemObject = SolarSystemObject.Uranus
    //val Pluto: SolarSystemObject   = SolarSystemObject.Pluto  // Pluto is not a planet!

}
/*
object JEqCoord {
def make(ra: Any, dec: Any): EqCoord = EqCoord(ra, dec)

def asBoth(radec: String, frame: EqFrame, tag: Tag, catalogName: String, pmx: Double, pmy: Double): EqCoord =
EqCoord.asBoth(radec, frame, tag, catalogName, pmx, pmy)
}
*/

