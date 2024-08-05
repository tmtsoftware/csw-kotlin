package csw.params.core.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.equals.shouldBeEqual


class AngleTests: FunSpec ({

    test("Parsing of string representations") {

        Pair(Angle.parseRa("20 54 05.689"), Angle.parseDe("+37 01 17.38")) shouldBeEqual
                Angle.parseRaDe("20 54 05.689 +37 01 17.38")

        Pair(Angle.parseRa("10:12:45.3"), Angle.parseDe("-45:17:50")) shouldBeEqual
                Angle.parseRaDe("10:12:45.3-45:17:50")

        Pair(Angle.parseRa("15h17m"), Angle.parseDe("-11d10m")) shouldBeEqual
                Angle.parseRaDe("15h17m-11d10m")

        Pair(Angle.parseRa("275d11m15.6954s"), Angle.parseDe("+17d59m59.876s")) shouldBeEqual
                Angle.parseRaDe("275d11m15.6954s+17d59m59.876s")
    }
    /*
        it("should allow using implicits") {

            (12.34567.arcHour, -17.87654d.degree) shouldEqual Angle.parseRaDe("12.34567h-17.87654d")

            (350.123456.degree, -17.33333.degree) shouldEqual Angle.parseRaDe("350.123456d-17.33333d")

            (350.123456.degree, -17.33333.degree) shouldEqual Angle.parseRaDe("350.123456 -17.33333")
        }

    }
    */

    test("should allow testing of parser to microarcsecs -1") {
        Angle.parseRa("1", "2", "3")
            .uas shouldBeEqual 1L * 15L * 60L * 60L * 1000L * 1000L + 2L * 15L * 60L * 1000L * 1000L + 3L * 15L * 1000L * 1000L
        Angle
            .parseDe("+", "1", "2", "3")
            .uas shouldBeEqual 1L * 60L * 60L * 1000L * 1000L + 2L * 60L * 1000L * 1000L + 3L * 1000L * 1000L
    }

    test("should allow parsing to microsarcsecs -2") {
        Angle.parseRa("1h2m3s")
            .uas shouldBeEqual 1L * 15L * 60L * 60L * 1000L * 1000L + 2L * 15L * 60L * 1000L * 1000L + 3L * 15L * 1000L * 1000L
        Angle.parseRa("02 51.2")
            .uas shouldBeEqual 2L * 15L * 60L * 60L * 1000L * 1000L + 512L * 15L * 60L * 1000L * 100L
        Angle.parseDe("+1d2'3\"")
            .uas shouldBeEqual 1L * 60L * 60L * 1000L * 1000L + 2L * 60L * 1000L * 1000L + 3L * 1000L * 1000L
        Angle.parseDe("-1d2'3\"")
            .uas shouldBeEqual -(1L * 60L * 60L * 1000L * 1000L + 2L * 60L * 1000L * 1000L + 3L * 1000L * 1000L)
        Angle.parseDe("+13 12")
            .uas shouldBeEqual 13L * 60L * 60L * 1000L * 1000L + 12L * 60L * 1000L * 1000L
    }


    test("should allow conversions") {
        Angle.D2R * 1.0 /*d*/ shouldBe Math.toRadians(1.0 /*d*/)
        Angle.R2D * 1.0 /*d*/ shouldBe Math.toDegrees(1.0 /*d*/)
        Angle.H2D * 1.0 /*d*/ shouldBe 15.0 /*d*/
        Angle.D2H * 1.0 /*d*/ shouldBe 1.0 /*d*/ / 15.0 /*d*/
        Angle.D2M shouldBe 60.0 /*d*/
        Angle.M2D shouldBe 1.0 /*d*/ / 60.0 /*d*/
        Angle.D2S shouldBe 3600.0 /*d*/
        Angle.S2D shouldBe 1.0 /*d*/ / 3600.0 /*d*/
        Angle.H2R * 1.0 /*d*/ shouldBe Math.toRadians(15.0 /*d*/)
        Angle.R2H * Math.toRadians(15.0 /*d*/) shouldBe 1.0 /*d*/
        Angle.M2R * 60.0 /*d*/ shouldBe Math.toRadians(1.0 /*d*/)
        Angle.R2M * Math.toRadians(1.0 /*d*/) shouldBe 60.0 /*d*/
        Angle.Mas2R shouldBe Angle.D2R / 3600000.0 /*d*/
        Angle.R2Mas shouldBe 1.0 /*d*/ / Angle.Mas2R
    }

    test("Should do distance calculation") {
        Angle.distance(Angle.D2R * 1.0/*d*/, 0.0 /*d*/, Angle.D2R * 2.0 /*d*/, 0.0 /*d*/) shouldBe Angle.D2R * 1.0 /*d*/
        Angle.distance(
            0.0,
            Angle.D2R * 90.0 /*d*/,
            Angle.D2R * 180.0 /*d*/,
            -(Angle.D2R * 90.0 /*d*/)
        ) shouldBe Angle.D2R * 180.0 /*d*/
    }

    test("should convert RA to string") {
        "11h" shouldBe Angle.raToString(Angle.H2R * 11, withColon = false)
        "11:00:00.000" shouldBe Angle.raToString(Angle.H2R * 11)

        "11h 12m" shouldBe Angle.raToString(Angle.H2R * 11 + Angle.H2R * 12 / 60, withColon = false)
        "11:12:00.000" shouldBe Angle.raToString(Angle.H2R * 11 + Angle.H2R * 12 / 60)

        "11h 12m 13s" shouldBe Angle.raToString(
            Angle.H2R * 11 + Angle.H2R * 12 / 60 + Angle.H2R * 13 / 3600,
            withColon = false
        )
        "11:12:13.000" shouldBe Angle.raToString(Angle.H2R * 11 + Angle.H2R * 12 / 60 + Angle.H2R * 13 / 3600)

        "11h 12m 13.3s" shouldBe Angle.raToString(
            Angle.H2R * 11 + Angle.H2R * 12 / 60 + Angle.H2R * 13.3 / 3600,
            withColon = false
        )
        "11:12:13.300" shouldBe Angle.raToString(Angle.H2R * 11.0 + Angle.H2R * 12.0 / 60.0 + Angle.H2R * 13.3 / 3600.0)

        "01:02:03.330" shouldBe Angle.raToString(Angle.parseRa("01:02:03.33").toRadian())
    }

    test("should convert Dec to string") {
        "11" + Angle.DEGREE_SIGN shouldBe Angle.deToString(Angle.D2R * 11, withColon = false)
        "11:00:00.000" shouldBe Angle.deToString(Angle.D2R * 11)

        "11" + Angle.DEGREE_SIGN + "12'" shouldBe Angle.deToString(Angle.D2R * 11 + Angle.M2R * 12, withColon = false)
        "11:12:00.000" shouldBe Angle.deToString(Angle.D2R * 11 + Angle.M2R * 12)

        "11" + Angle.DEGREE_SIGN + "12'13\"" shouldBe Angle.deToString(
            Angle.D2R * 11 + Angle.M2R * 12 + Angle.S2R * 13, withColon = false
        )
        "11:12:13.000" shouldBe Angle.deToString(Angle.D2R * 11 + Angle.M2R * 12 + Angle.S2R * 13)

        "11" + Angle.DEGREE_SIGN + "12'13.3\"" shouldBe Angle.deToString(
            Angle.D2R * 11 + Angle.M2R * 12 + Angle.S2R * 13.3, withColon = false
        )
        "11:12:13.300" shouldBe Angle.deToString(Angle.D2R * 11 + Angle.M2R * 12 + Angle.S2R * 13.3)

        "-11" + Angle.DEGREE_SIGN + "12'" shouldBe Angle.deToString(
            -(Angle.D2R * 11 + Angle.M2R * 12),
            withColon = false
        )
        "-11:12:00.000" shouldBe Angle.deToString(-(Angle.D2R * 11 + Angle.M2R * 12))

        "01:02:03.330" shouldBe Angle.deToString(Angle.parseDe("01:02:03.33").toRadian())
    }

    test("Some other tests") {
        val x = Angle.randomDe()
        val y = Angle.randomRa()

        println("x/y: $x / $y")
    }
}

)

/*
interface UnitOfMeasurement { val v: Float }

inline class Meter(override val v: Float) : UnitOfMeasurement

inline class Second(override val v: Float) : UnitOfMeasurement

inline fun <reified T : UnitOfMeasurement> create(v: Float): T {
    return when (T::class) {
        Meter::class -> Meter(v) as T
        Second::class -> Second(v) as T
        else -> throw IllegalArgumentException("don't know how to create ${T::class}")
    }
}

inline operator fun <reified T : UnitOfMeasurement> T.plus(other: T) = create<T>(v + other.v)

fun main() {
    val a = Meter(10f)
    val b = Meter(5f)
    println(a + b)

    val c = Second(60f)
    val d = Second(30f)
    println(c + d)

    // println(a + c) // throws IllegalArgumentException
}
*/
