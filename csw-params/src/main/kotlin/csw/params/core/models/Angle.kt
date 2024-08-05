/*
 * Copyright (c) 2022 Thirty Meter Telescope International Observatory
 * SPDX-License-Identifier: Apache-2.0
 */
package csw.params.core.models

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import kotlin.Unit
import kotlin.math.*

/*
 *  Copyright Jan Kotek 2009, http://asterope.org
 *  This program is distributed under GPL Version 3.0 in the hope that
 *  it will be useful, but WITHOUT ANY WARRANTY.
 */


/**
 * An wrapper for angle. Normally angle would be stored in double
 * as radians, but this introduces rounding errors.
 * This class stores value in microarcseconds to prevent rounding errors.
 * <p>
 * Usage examples:
 * <code>
 * //import provides implicit conversions for numbers
 * import Angle._
 * //use implicit conversions to construct angle from number
 * val angle = 10.degree + 0.5.arcSec
 * //convert value to radian and print it
 * println(11.toRadian)
 * </code>
 */
//data class Angle(val uas: Long) : /*AnyVal with*/ Serializable /*, Ordered<Angle>*/ {

@Serializable
data class Angle(val uas: Long) : Comparable<Angle> {

    // Note: extending AnyVal is an optimization, since the value 'uas' is inlined, but it makes Java interop confusing...
    // require(uas> - Angle.CIRCLE && uas < Angle.CIRCLE, "out of range, possible overflow ");

    // operators
    operator fun plus(a2: Angle): Angle = Angle(uas + a2.uas)
    operator fun minus(a2: Angle): Angle = Angle(uas - a2.uas)
    operator fun times(a2: Double): Angle = Angle((uas * a2).toLong())
    operator fun times(a2: Int): Angle = Angle(uas * a2)
    operator fun div(a2: Double): Angle = Angle((uas / a2).toLong())
    operator fun div(a2: Int): Angle = Angle(uas / a2)
    operator fun unaryPlus(): Angle = this
    operator fun unaryMinus(): Angle = Angle(-uas)

    override fun compareTo(other: Angle): Int = uas.compareTo(other.uas)

    /** returns angle value in radians */
    fun toRadian(): Double = Uas2R * uas

    /** returns angle value in degrees */
    fun toDegree(): Double = Uas2D * uas

    /** returns angle value in milliarcseconds */
    fun toMas(): Double = uas * 1e-3

    /** returns angle value in arcseconds */
    fun toArcSec(): Double = 1e-6 * uas

    /** returns Angle with value normalized between 0 to 2*PI */
    fun normalizedRa(): Angle {
        var uas2 = uas
        while (uas2 < 0) {
            uas2 += CIRCLE
        }
        uas2 = uas2 % CIRCLE
        return Angle(uas2)
    }

    override fun toString(): String = "Angle(" + toDegree() + " degree)"

    /** Returns sequence of angles with given max value and increment */
    fun to(maxVal: Angle, increment: Angle): Sequence<Angle> =
        (uas..maxVal.uas step increment.uas).map { Angle( it) }.asSequence()

    /** Returns sequence of angles with given max value and increment */
    fun until(maxVal: Angle, increment: Angle): Sequence<Angle> =
        (uas..<maxVal.uas step increment.uas).map { Angle( it) }.asSequence()


    companion object {

        internal const val CIRCLE: Long = 360L * 60L * 60L * 1000L * 1000L
        /*
        /** used in implicit conversion to support `1.degree`, `1.arcMinute` etc
         - Replaced with extensions of Long */
         */
        /** returns random angle with value between 0 and 2*PI */
        fun randomRa(): Angle = Angle((CIRCLE * Math.random()).toLong())

        /** returns random angle with value between -PI/2 and + PI/2 */
        fun randomDe(): Angle = Angle((CIRCLE / 2 * Math.random() - CIRCLE / 4.0).toLong())

        /** returns maximal angle from two options */
        fun max(a1: Angle, a2: Angle): Angle = if (a1 > a2) a1 else a2

        /** returns minimal angle from two options */
        fun min(a1: Angle, a2: Angle): Angle = if (a1 < a2) a1 else a2

        //implicit conversions
        //fun long2angle(d: Long) = Angle(d.toLong())

        //fun int2angle(d: Int): AngleWrapperDouble = new AngleWrapperDouble(d.toDouble)

        //fun double2angle(d: Double): AngleWrapperDouble = new AngleWrapperDouble(d)

        /**
         * Parse Declination from four values. It uses BigDecimal, so there are no rounding errors
         *
         * @param deSign   signum (ie + or -)
         * @param deDegree declination in degrees
         * @param deMin    remaining part in arcminutes
         * @param deSec    remaining part in arcseconds
         * @return declination in microarcseconds
         */
        fun parseDe(deSign: String, deDegree: String, deMin: String, deSec: String?): Angle {
            val sign: Int = if ("-" == deSign.trim()) -1 else 1

            val deg = BigDecimal(deDegree)
            if (deg.toDouble() < 0 || deg.toDouble() > 89) throw IllegalArgumentException("Invalid deDegree: $deg")
            val min = BigDecimal(deMin) // Can't be null in Kotlin
            if (min.toDouble() < 0 || min.toDouble() >= 60) throw IllegalArgumentException("Invalid deMin: $min")
            val sec: BigDecimal = if (deSec != null) BigDecimal(deSec) else BigDecimal.ZERO
            if (sec.toDouble() < 0 || sec.toDouble() >= 60) throw IllegalArgumentException("Invalid deSec: $sec")

            return Angle(
                sign *
                        (deg.multiply(BigDecimal(D2Uas)).longValueExact() +
                                min.multiply(BigDecimal(M2Uas)).longValueExact() +
                                sec.multiply(BigDecimal(S2Uas)).longValueExact())
            )
        }

        /**
         * Tries to parse Angle from string.
         * It knows common formats used for Declination
         */
        fun parseDe(de: String): Angle {
            //if (de == null) throw IllegalArgumentException("de is null")  Can't be null in Kotlin

            val r1 = Regex("([+|-]?)([0-9]+)[" + DEGREE_SIGN + "d: ]{1,2}([0-9]+)[m': ]{1,2}([0-9\\.]+)[s\\\"]?")
            if (r1.matches(de)) {
                r1.matchEntire(de)?.destructured?.let { (sign, d, m, s) -> return parseDe(sign, d, m, s) }
            }

            val r2 = Regex("([+|-]?)([0-9]+)[" + DEGREE_SIGN + "d: ]{1,2}([0-9]+)[m']?")
            if (r2.matches(de)) {
                r2.matchEntire(de)?.destructured?.let { (sign, d, m) -> return parseDe(sign, d, m, null) }
            }
            throw IllegalArgumentException("Could not parse DE: $de")
        }

        /**
         * parse Right ascencion  from triple values raHour raMin, raSec
         * This method uses big decimal, so there are no rounding errors
         *
         * @param raHour ra hours value as String
         * @param raMin ra minutes value as String
         * @param raSec ra seconds value as String
         * @return result in microarcseconds
         */
        fun parseRa(raHour: String, raMin: String, raSec: String?): Angle {

            val raHour2 = BigDecimal(raHour)
            if (raHour2.toDouble() < 0 || raHour2.toDouble() > 23) throw IllegalArgumentException("Invalid raHour: $raHour2")

            val raMin2 = BigDecimal(raMin)  // Can't be null in Kotlin
            if (raMin2.toDouble() < 0 || raMin2.toDouble() >= 60) throw IllegalArgumentException("Invalid raMin: $raMin2")
            val raSec2 = if (raSec != null) BigDecimal(raSec) else BigDecimal.ZERO
            if (raSec2.toDouble() < 0 || raSec2.toDouble() >= 60) throw IllegalArgumentException("Invalid raSec: $raSec2")

            return Angle(
                raHour2.multiply(BigDecimal(H2Uas)).longValueExact() +
                        raMin2.multiply(BigDecimal(HMin2Uas)).longValueExact() +
                        raSec2.multiply(BigDecimal(HSec2Uas)).longValueExact()
            )
        }

        fun Double.degree(): Angle = Angle((this * D2Uas).toLong())
        fun Double.arcMinute(): Angle = Angle((this * M2Uas).toLong())
        fun Double.arcSec(): Angle = Angle((this * S2Uas).toLong())
        fun Double.arcHour(): Angle = Angle((this * H2Uas).toLong())
        fun Double.radian(): Angle = Angle((this * R2Uas).toLong())
        fun Double.mas(): Angle = Angle((this * 1000).toLong())

        /**
         * Tries to parse Angle from string.
         * It knows common formats used for Right ascencion (including hours)
         */
        fun parseRa(ra: String): Angle {
            //if (ra == null) throw IllegalArgumentException("ra is null") Can't be null in Kotlin

            val r1 = Regex("([0-9]+)[h: ]{1,2}([0-9]+)[m: ]{1,2}([0-9\\.]+)[s]{0,1}")

            if (r1.matches(ra)) {
                r1.matchEntire(ra)?.destructured?.let { (h, m, s) ->
                    return parseRa(h, m, s)
                }
            }
            val r2 = Regex("([0-9]+)[h: ]{1,2}([0-9\\.]+)[m]?")
            if (r2.matches(ra)) {
                r2.matchEntire(ra)?.destructured?.let { (h, m) -> return parseRa(h, m, null) }
            }
            val r3 = Regex("([0-9]+)d([0-9]+)m([0-9\\.]+)s")
            if (r3.matches(ra)) {
                r3.matchEntire(ra)?.destructured?.let { (d, m, s) ->
                    return d.toDouble().degree() + m.toDouble().arcMinute() + s.toDouble().arcSec()
                }
            }
            throw IllegalArgumentException("Could not parse RA: $ra")
        }

        /**
         * Parses pair of RA and De coordinates.
         * This method should handle formats used in vizier.
         * An example:
         * The following writings are allowed:
         * <pre>
         * 20 54 05.689 +37 01 17.38, 10:12:45.3-45:17:50, 15h17m-11d10m, 15h17+89d15, 275d11m15.6954s+17d59m59.876s
         * 12.34567h-17.87654d, 350.123456d-17.33333d <=> 350.123456 -17.33333
         * </pre>
         */
        fun parseRaDe(str: String): Pair<Angle, Angle> {
            // 20 54 05.689 +37 01 17.38
            // 10:12:45.3-45:17:50
            val r1 =
                Regex("([0-9]{2})[ :]([0-9]{2})[ :]([0-9]{2}\\.[0-9]+)[ ]?(\\+|-)([0-9]{2})[ :]([0-9]{2})[ :]([0-9]{2}\\.?[0-9]*)")
            if (r1.matches(str)) {
                r1.matchEntire(str)?.destructured?.let { (rah, ram, ras, ss, d, m, s) ->
                    return Pair(parseRa(rah, ram, ras), parseDe(ss, d, m, s))
                }
            }

            // 15h17m-11d10m
            // 15h17+89d15
            val r2 = Regex("([0-9]{2})h([0-9]{2})[m]?(\\+|-)([0-9]{2})d([0-9]{2})[m]?")
            if (r2.matches(str)) {
                r2.matchEntire(str)?.destructured?.let { (ah, am, ss, d, m) ->
                    return Pair(parseRa(ah, am, null), parseDe(ss, d, m, null))
                }
            }

            // 275d11m15.6954s+17d59m59.876s
            val r3 = Regex("([0-9]{2,3}d[0-9]{2}m[0-9]{2}\\.[0-9]+s)([\\+-][0-9]{2}d[0-9]{2}m[0-9]{2}\\.[0-9]+s)")
            if (r3.matches(str)) {
                r3.matchEntire(str)?.destructured?.let { (ra, de) ->
                    return Pair(parseRa(ra), parseDe(de))
                }
            }

            // 12.34567h-17.87654d
            val r4 = Regex("([0-9]{1,2}\\.[0-9]+)h([\\+-][0-9]{2}\\.[0-9]+)d")
            if (r4.matches(str)) {
                r4.matchEntire(str)?.destructured?.let { (ra, de) ->
                    return Pair(ra.toDouble().arcHour(), de.toDouble().degree())
                }
            }

            // 350.123456d-17.33333d <=> 350.123456 -17.33333
            val r5 = Regex("([0-9]{1,3}\\.?[0-9]*)[d]?[ ]?([\\+-]?[0-9]{1,2}\\.?[0-9]*)[d]?")
            if (r5.matches(str)) {
                r5.matchEntire(str)?.destructured?.let { (ra, de) ->
                    val rra = ra.toDouble().degree()
                    val dec = de.toDouble().degree()
                    return Pair(rra, dec)
                }
            }

            throw IllegalArgumentException("Could not parse RA/Dec: $str")
        }

        /**
         * normalize RA into 0 - 2 * PI range
         */
        fun normalizeRa(ra2: Double): Double {
            var ra = ra2
            while (ra < 0) ra += Math.PI * 2
            while (ra >= Math.PI * 2) ra -= Math.PI * 2
            return ra
        }

        fun assertRa(ra: Double) {
            if (ra < 0 || ra >= Math.PI * 2.0 /*d*/)
                throw IllegalArgumentException("Invalid RA: $ra")
        }

        fun assertDe(de: Double) {
            if (de < -D2R * 90 /*d*/ || de > D2R * 90 /*d*/)
                throw IllegalArgumentException("Invalid DE: $de")
        }

        private fun isNear(x: Double, d: Double): Boolean {
            val tolerance = 1e-7
            return abs(x % d) < tolerance || abs(x % d - d) < tolerance
        }

        private fun formatSecs(sec: Double, withLeadingZero: Boolean): String {
            return if (withLeadingZero)
                String.format("%06.3f", sec)
            else
                if (isNear(sec, 1.0))
                    "${Math.round(sec)}"
                else if (isNear(sec, 0.1))
                    String.format("%2.1f", sec)
                else if (isNear(sec, 0.01))
                    String.format("%2.2f", sec)
                else
                    "$sec"
        }

        /**
         * Converts RA to a string in the format 01:02:03.333 (or '1h 2m 3.33s').
         * Minutes and seconds are auto added as needed.
         *
         * @param ra in radians
         * @param withColon if true format as hh:mm:ss.sss, otherwise XXh XXm XXs
         * @return ra in string form
         */
        fun raToString(ra: Double, withColon: Boolean = true): String =
            if (isNear(ra, H2R)) {
                val hour = Math.round(ra * R2H).toInt()
                if (withColon)
                    "$hour:00:00.000"
                else
                    "${hour}h"
            } else if (isNear(ra, H2R / 60)) {
                val hour = (ra * R2H).toInt()
                val min = Math.round((ra - H2R * hour) * R2H * 60).toInt()
                if (withColon)
                    "$hour:$min:00.000"
                else
                    "${hour}h ${min}m"
            } else {
                val hour = (ra * R2H).toInt()
                val min = ((ra - H2R * hour) * R2H * 60).toInt()
                val sec = (ra - H2R * hour - min * H2R / 60) * R2H * 3600
                val s = formatSecs(sec, withColon)
                if (withColon)
                    String.format("%02d:%02d:%s", hour, min, s)
                else
                    "${hour}h ${min}m ${s}s"
            }


        /**
         * Converts DE to a string in the format 01:02:03.333 (or '1d 2m 3.333s)'
         * Minutes and seconds are auto added as needed.
         *
         * @param de2 in radians
         * @param withColon if true format as dd:mm:ss.sss, otherwise XXd XXm XXs
         * @return de in string form
         */
        fun deToString(de2: Double, withColon: Boolean = true): String {
            val (de, sign) = if (de2 < 0) Pair(-de2, "-") else Pair(de2, "")

            return if (isNear(de, D2R)) {
                val deg = Math.round(de * R2D).toInt()
                if (withColon)
                    "$sign$deg:00:00.000"
                else
                    sign + deg + DEGREE_SIGN
            } else if (isNear(de, M2R)) {
                val deg = (de * R2D).toInt()
                val min = ((de - D2R * deg) * R2M).toInt()
                if (withColon)
                    "$sign$deg:$min:00.000"
                else
                    "$sign$deg$DEGREE_SIGN$min'"
            } else {
                val deg = (de * R2D).toInt()
                val min = ((de - D2R * deg) * R2D * 60).toInt()
                val sec = (de - D2R * deg - min * D2R / 60) * R2D * 3600
                val s = formatSecs(sec, withColon)
                if (withColon)
                    String.format("%s%02d:%02d:%s", sign, deg, min, s)
                else
                    "$sign$deg$DEGREE_SIGN$min'$s\""
            }
        }

        /**
         * calculate great circle distance of two points,
         * coordinates are given in radians
         *
         * @return distance of two points in radians
         */
        fun distance(ra1: Double, de1: Double, ra2: Double, de2: Double): Double {
            // check ranges
            assertRa(ra1)
            assertRa(ra2)
            assertDe(de1)
            assertDe(de2)

            // this code is from JH labs projection lib
            val dlat = sin((de2 - de1) / 2)
            val dlon = sin((ra2 - ra1) / 2)
            val r = sqrt(dlat * dlat + cos(de1) * cos(de2) * dlon * dlon)
            return 2.0 * asin(r)
        }

        /** multiply to convert degrees to radians */
        const val D2R: Double = Math.PI / 180 /* d */

        /** multiply to convert radians to degrees */
        const val R2D: Double = 1.0 /*d*/ / D2R

        /** multiply to convert degrees to archours */
        const val D2H: Double = 1.0 /*d*/ / 15.0 /*d*/

        /** multiply to convert archour to degrees */
        const val H2D: Double = 1 /*d*/ / D2H

        /** multiply to convert degrees to arcminute */
        const val D2M: Int = 60

        /** multiply to convert arcminute  to toDegree */
        const val M2D: Double = 1.0 /*d*/ / D2M

        /** multiply to convert degrees to arcsecond */
        const val D2S: Int = 3600

        /** multiply to convert arcsecond to toDegree */
        const val S2D: Double = 1.0 /*d*/ / D2S

        /** multiply to convert hours to radians */
        const val H2R: Double = H2D * D2R

        /** multiply to convert radians to hours */
        const val R2H: Double = 1.0 /*d*/ / H2R

        /** multiply to convert radians to minutes */
        const val R2M: Double = R2D * D2M

        /** multiply to convert minutes to radians */
        const val M2R: Double = 1.0 /*d*/ / R2M

        /** multiply to convert milliarcseconds to radians */
        const val Mas2R: Double = D2R / 3600000 /*d*/

        /** multiply to convert microarcseconds to radians */
        const val Uas2R: Double = D2R / 3600000000 /*d*/

        /** multiply to convert radians to milliarcseconds */
        const val R2Mas: Double = 1.0 /*d*/ / Mas2R

        /** multiply to convert radians to microarcseconds */
        const val R2Uas: Double = 1.0 /*d*/ / Uas2R

        /** multiply to convert hours to milliarcseconds */
        const val H2Mas: Int = 15 * 60 * 60 * 1000

        /** multiply to convert time minutes to milliarcseconds */
        const val HMin2Mas: Int = 15 * 60 * 1000

        /** multiply to convert time seconds to milliarcseconds */
        const val HSec2Mas: Int = 15 * 1000

        /** multiply to convert hours to microarcseconds */
        const val H2Uas: Long = 15L * 60L * 60L * 1000L * 1000L

        /** multiply to convert time minutes to microarcseconds */
        const val HMin2Uas: Long = 15L * 60L * 1000L * 1000L

        /** multiply to convert time seconds to microarcseconds */
        const val HSec2Uas: Long = 15L * 1000L * 1000L

        /** multiply to convert degrees to milliarcseconds */
        const val D2Mas: Int = 60 * 60 * 1000

        /** multiply to convert minutes to milliarcseconds */
        const val M2Mas: Int = 60 * 1000

        /** multiply to convert Seconds to milliarcseconds */
        const val S2Mas: Int = 1000

        /** multiply to convert degrees to microarcseconds */
        const val D2Uas: Long = 60L * 60L * 1000L * 1000L

        /** multiply to convert minutes to microarcseconds */
        const val M2Uas: Long = 60L * 1000L * 1000L

        /** multiply to convert Seconds to microarcseconds */
        const val S2Uas: Long = 1000L * 1000L

        /** multiply to convert UAS to degrees */
        const val Uas2D: Double = 1.0 /*d*/ / D2Uas

        /** multiply to convert UAS to minutes */
        const val Uas2M: Double = 1.0 /*d*/ / M2Uas

        /** multiply to convert UAS to Seconds */
        const val Uas2S: Double = 1.0 /*d*/ / S2Uas

        /** multiply to convert  arcseconds to radians */
        const val S2R: Double = D2R / 3600 /*d*/

        /** multiply to convert radians to arcseconds */
        const val R2S: Double = 1.0 /*d*/ / S2R

        /** round circle which marks degrees */
        const val DEGREE_SIGN: Char = '\u00B0'


        /**
         * Java API for creating an Angle instance.
         */
        object JAngle {

            /**
             * Creates an Angle instance from the given value in degrees
             */
            fun degree(d: Double): Angle = d.degree()

            /**
             * Creates an Angle instance from the given value in arcMinutes
             */
            fun arcMinute(d: Double): Angle = d.arcMinute()

            /**
             * Creates an Angle instance from the given value in arcSecs
             */
            fun arcSec(d: Double): Angle = d.arcSec()

            /**
             * Creates an Angle instance from the given value in arcHours
             */
            fun arcHour(d: Double): Angle = d.arcHour()

            /**
             * Creates an Angle instance from the given value in radians
             */
            fun radian(d: Double): Angle = d.radian()

            /**
             * Creates an Angle instance from the given value in mas (milliarcseconds)
             */
            fun mas(d: Double): Angle = d.mas()
        }
    }
}