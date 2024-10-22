package csw.params.keys

import csw.params.commands.CommandName
import csw.params.commands.Setup
import csw.params.core.models.*
import io.kotest.core.spec.style.FunSpec
import csw.params.core.models.Angle.Companion.degree
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class CoordKeyTest: FunSpec( {

    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"):Setup =  Setup(p, cname)

    /** EqCoord tests **/

    test("Create EqCoordKey") {
        val k1 = EqCoordKey(Tag.BASE)

        val ra = 180.0.degree()
        val dec = 32.0.degree()

        val r1 = k1.set(ra, dec)
        val t1 = ra.uas.toString()
        val t2 = dec.uas.toString()
        r1 shouldBe CoordStore("BASE", CoordType.EQ, "$t1,$t2,ICRS,none,0.0,0.0")

        val eq = EqCoordKey.decode(r1)
        eq shouldBe EqCoord(Tag.BASE, ra, dec, EqFrame.ICRS, ProperMotion.DEFAULT_PROPERMOTION, "none" )

        val eq2 = EqCoordKey.decode(r1.copy(name="BAZE"))
        eq2 shouldBe null
    }

    test("Add to setup tests") {
        val k1 = EqCoordKey(Tag.BASE)

        val store1 = k1.set(180.0.degree(), 32.0.degree())
        val t1 = 180.0.degree().uas.toString()
        val t2 = 32.0.degree().uas.toString()
        store1 shouldBe CoordStore("BASE", CoordType.EQ, "$t1,$t2,ICRS,none,0.0,0.0")

        var s = testS()
        s.size shouldBe 0

        s = s.add(store1)
        s.size shouldBe 1

        assert(k1.isIn(s))
    }

    test("Get checks and invoke") {
        val k1 = EqCoordKey(Tag.BASE)
        val k2 = EqCoordKey(Tag.OIWFS1)

        val s = testS().add(k1.set(180.0.degree(), 32.0.degree()), k2.set(180.43.degree(), 32.123.degree()))
        s.size shouldBe 2

        assert(k1.isIn(s) && k2.isIn(s))

        val r1 = k1.get(s)
        r1 shouldBe EqCoord(180.0.degree(), 32.0.degree())

        val r2: EqCoord = k1(s)
        r2 shouldBe EqCoord(180.0.degree(), 32.0.degree())
    }

    test("Test with proper motions") {
        val k1 = EqCoordKey(Tag.BASE)

        val s = testS().add(k1.set(180.0.degree(), 32.0.degree(), EqFrame.ICRS,1.12, -1.344))
        s.size shouldBe 1

        assert(k1.isIn(s))

        val r1 = k1.get(s)
        r1 shouldBe EqCoord(Tag.BASE, 180.0.degree(), 32.0.degree(), EqFrame.ICRS, ProperMotion(1.12, -1.344), "none")
    }

    /** CatalogCoordKey tests **/
    test("Catalog CoordKey tests") {
        val k1 = CatalogCoordKey(Tag.BASE)

        val catalogName = CatalogName("BrightStars")
        val objectName = CatalogObject("HR1099")

        val store1 = k1.set(catalogName, objectName)
        store1 shouldBe CoordStore("BASE", CoordType.CAT, "${catalogName.name},${objectName.name}")

        val catCoord = CatalogCoordKey.decode(store1)

        var s = testS()
        s.size shouldBe 0

        s = s.add(store1)
        s.size shouldBe 1

        assert(k1.isIn(s))

        val r1 = k1.get(s)
        r1 shouldBe CatalogCoord(Tag.BASE, catalogName, objectName)

        val r2 = k1(s)
        r2 shouldBe CatalogCoord(Tag.BASE, catalogName, objectName)
    }

    /** AltAzCoordKey tests **/

    test("Create AltAzCoordKey") {
        val k1 = AltAzCoordKey(Tag.BASE)

        val az = 180.2.degree()
        val alt = 45.0.degree()

        val r1 = k1.set(alt, az)
        val t1 = alt.uas.toString()
        val t2 = az.uas.toString()
        val cstore = CoordStore("BASE", CoordType.AltAz, "$t1,$t2")
        r1 shouldBe cstore

        val altaz = AltAzCoordKey.decode(cstore)
        altaz shouldBe AltAzCoord(Tag.BASE, alt, az)

        val altaz2 = AltAzCoordKey.decode(cstore.copy(name = "BAZE"))
        altaz2 shouldBe null
    }

    test("Add AltAzCoordKey to setup test") {
        val k1 = AltAzCoordKey(Tag.BASE)

        val store1 = k1.set(32.0.degree(), 190.0.degree())
        val t1 = 32.0.degree().uas.toString()
        val t2 = 190.0.degree().uas.toString()
        store1 shouldBe CoordStore("BASE", CoordType.AltAz, "$t1,$t2")

        var s = testS()
        s.size shouldBe 0

        s = s.add(store1)
        s.size shouldBe 1

        assert(k1.isIn(s))
    }

    test(" Get altaz checks and invoke") {
        val k1 = AltAzCoordKey(Tag.BASE)
        val k2 = AltAzCoordKey(Tag.OIWFS1)

        val s = testS().add(k1.set(32.0.degree(), 190.2.degree()), k2.set(21.123.degree(), 180.43.degree()))
        s.size shouldBe 2

        assert(k1.isIn(s) && k2.isIn(s))

        val r1 = k1.get(s)
        r1 shouldBe AltAzCoord(Tag.BASE, 32.0.degree(), 190.2.degree())

        val r2: AltAzCoord = k1(s)
        r2 shouldBe AltAzCoord(Tag.BASE, 32.0.degree(), 190.2.degree())
    }

    /** SolarSystemCoordKey tests **/

    test("Create SolarSystemCoordKey") {
        val k1 = SolarSystemCoordKey(Tag.BASE)

        val sso = SolarSystemObject.Uranus

        val r1 = k1.set(sso)
        val cstore = CoordStore("BASE", CoordType.SSO, sso.name)
        r1 shouldBe cstore

        val ssoCoord = SolarSystemCoordKey.decode(cstore)
        ssoCoord shouldBe SolarSystemCoord(Tag.BASE, sso)

        val sso2 = SolarSystemCoordKey.decode(cstore.copy(name = "BAZE"))
        sso2 shouldBe null
    }

    test("Add SolarSystemCoordKey to setup test") {
        val k1 = SolarSystemCoordKey(Tag.BASE)

        val sso = SolarSystemObject.Uranus

        val r1 = k1.set(sso)
        r1 shouldBe CoordStore("BASE", CoordType.SSO, sso.name)

        var s = testS()
        s.size shouldBe 0

        s = s.add(r1)
        s.size shouldBe 1

        assert(k1.isIn(s))
    }

    test("Get solar system key checks and invoke") {
        val k1 = SolarSystemCoordKey(Tag.BASE)
        val k2 = SolarSystemCoordKey(Tag.OIWFS1)

        val s = testS().add(k1.set(SolarSystemObject.Saturn), k2.set(SolarSystemObject.Uranus))
        s.size shouldBe 2

        assert(k1.isIn(s) && k2.isIn(s))

        val r1 = k1.get(s)
        r1 shouldBe SolarSystemCoord(Tag.BASE, SolarSystemObject.Saturn)

        val r2 = k2(s)
        r2 shouldBe SolarSystemCoord(Tag.OIWFS1, SolarSystemObject.Uranus)
    }

    /** CometCoordKey tests **/

    test("Create CometCoordKey") {
        val k1 = CometCoordKey(Tag.BASE)

        val epoch = 2035.2
        val inclination: Inclination = 2.3.degree()
        val longAscendingNode: LongAscendingNode = 110.23.degree()
        val argOfPerihelion: ArgOfPerihelion = 23.1.degree()
        val perihelionDistance: PerihelionDistance = 5.2
        val eccentricity: Eccentricity = 4.2

        val r1 = k1.set(epoch, inclination, longAscendingNode, argOfPerihelion, perihelionDistance, eccentricity)
        val cstore = CoordStore("BASE", CoordType.COM, "${epoch},${inclination.uas},${longAscendingNode.uas},${argOfPerihelion.uas},${perihelionDistance},${eccentricity}")
        r1 shouldBe cstore

        val comCoord = CometCoordKey.decode(cstore)
        comCoord shouldBe CometCoord(Tag.BASE ,epoch, inclination, longAscendingNode, argOfPerihelion, perihelionDistance, eccentricity)

        val sso2 = CometCoordKey.decode(cstore.copy(name = "BAZE"))
        sso2 shouldBe null
    }

    test("Add CometCoordKey to setup test") {
        val k1 = CometCoordKey(Tag.BASE)

        val epoch = 2035.2
        val inclination: Inclination = 2.3.degree()
        val longAscendingNode: LongAscendingNode = 110.23.degree()
        val argOfPerihelion: ArgOfPerihelion = 23.1.degree()
        val perihelionDistance: PerihelionDistance = 5.2
        val eccentricity: Eccentricity = 4.2


        val r1 = k1.set(epoch, inclination, longAscendingNode, argOfPerihelion, perihelionDistance, eccentricity)
        r1 shouldBe CoordStore("BASE", CoordType.COM, "${epoch},${inclination.uas},${longAscendingNode.uas},${argOfPerihelion.uas},${perihelionDistance},${eccentricity}")

        var s = testS()
        s.size shouldBe 0

        s = s.add(r1)
        s.size shouldBe 1

        assert(k1.isIn(s))
    }

    test("Get CometKey checks and invoke") {
        val k1 = CometCoordKey(Tag.BASE)
        val k2 = CometCoordKey(Tag.OIWFS1)

        val epoch = 2035.2
        val inclination: Inclination = 2.3.degree()
        val longAscendingNode: LongAscendingNode = 110.23.degree()
        val argOfPerihelion: ArgOfPerihelion = 23.1.degree()
        val perihelionDistance: PerihelionDistance = 5.2
        val eccentricity: Eccentricity = 4.2

        val epoch2 = 2035.2
        val inclination2: Inclination = 3.3.degree()
        val longAscendingNode2: LongAscendingNode = (-110.23).degree()
        val argOfPerihelion2: ArgOfPerihelion = 120.23.degree()
        val perihelionDistance2: PerihelionDistance = 7.3
        val eccentricity2: Eccentricity = 0.04

        val s = testS().add(
            k1.set(epoch, inclination, longAscendingNode, argOfPerihelion, perihelionDistance, eccentricity),
            k2.set(epoch2, inclination2, longAscendingNode2, argOfPerihelion2, perihelionDistance2, eccentricity2))
        s.size shouldBe 2

        assert(k1.isIn(s) && k2.isIn(s))

        val r1 = k1.get(s)
        r1 shouldBe CometCoord(Tag.BASE, epoch, inclination, longAscendingNode, argOfPerihelion, perihelionDistance, eccentricity)

        val r2 = k2(s)
        r2 shouldBe CometCoord(Tag.OIWFS1, epoch2, inclination2, longAscendingNode2, argOfPerihelion2, perihelionDistance2, eccentricity2)
    }

    /** MinorPlanetCoordKey tests **/

    test("Create MinorPlanetCoordKey") {
        val k1 = MinorPlanetCoordKey(Tag.BASE)

        val epoch = 2035.2
        val inclination: Inclination = 2.3.degree()
        val longAscendingNode: LongAscendingNode = 110.23.degree()
        val argOfPerihelion: ArgOfPerihelion = 23.1.degree()
        val meanDistance: MeanDistance = 5.2
        val eccentricity: Eccentricity = 4.2
        val meanA: MeanAnomaly = 23.45.degree()

        val r1 = k1.set(epoch, inclination, longAscendingNode, argOfPerihelion, meanDistance, eccentricity, meanA)
        val cstore = CoordStore("BASE", CoordType.MP, "${epoch},${inclination.uas},${longAscendingNode.uas},${argOfPerihelion.uas},${meanDistance},${eccentricity},${meanA.uas}")
        r1 shouldBe cstore

        val comCoord = MinorPlanetCoordKey.decode(cstore)
        comCoord shouldBe MinorPlanetCoord(Tag.BASE, epoch, inclination, longAscendingNode, argOfPerihelion, meanDistance, eccentricity, meanA)

        val sso2 = MinorPlanetCoordKey.decode(cstore.copy(name = "BAZE"))
        sso2 shouldBe null
    }

    test("Add MinorPlanetCoordKey to setup test") {
        val k1 = MinorPlanetCoordKey(Tag.BASE)

        val epoch = 2035.2
        val inclination: Inclination = 2.3.degree()
        val longAscendingNode: LongAscendingNode = 110.23.degree()
        val argOfPerihelion: ArgOfPerihelion = 23.1.degree()
        val meanDistance: MeanDistance = 5.2
        val eccentricity: Eccentricity = 4.2
        val meanA: MeanAnomaly = 23.45.degree()

        val r1 = k1.set(epoch, inclination, longAscendingNode, argOfPerihelion, meanDistance, eccentricity, meanA)

        var s = testS()
        s.size shouldBe 0

        s = s.add(r1)
        s.size shouldBe 1

        assert(k1.isIn(s))
    }

    test("Get MinorPlanetKey checks and invoke") {
        val k1 = MinorPlanetCoordKey(Tag.BASE)
        val k2 = MinorPlanetCoordKey(Tag.OIWFS1)

        val epoch = 2035.2
        val inclination: Inclination = 2.3.degree()
        val longAscendingNode: LongAscendingNode = 110.23.degree()
        val argOfPerihelion: ArgOfPerihelion = 23.1.degree()
        val meanDistance: MeanDistance = 5.2
        val eccentricity: Eccentricity = 4.2
        val meanA: MeanAnomaly = 23.45.degree()

        val epoch2 = 2035.2
        val inclination2: Inclination = 2.3.degree()
        val longAscendingNode2: LongAscendingNode = 110.23.degree()
        val argOfPerihelion2: ArgOfPerihelion = 23.1.degree()
        val meanDistance2: MeanDistance = 5.2
        val eccentricity2: Eccentricity = 4.2
        val meanA2: MeanAnomaly = 23.45.degree()

        val s = testS().add(
            k1.set(epoch, inclination, longAscendingNode, argOfPerihelion, meanDistance, eccentricity, meanA),
            k2.set(epoch2, inclination2, longAscendingNode2, argOfPerihelion2, meanDistance2, eccentricity2, meanA2))
        s.size shouldBe 2

        assert(k1.isIn(s) && k2.isIn(s))

        val r1 = k1.get(s)
        r1 shouldBe MinorPlanetCoord(Tag.BASE, epoch, inclination, longAscendingNode, argOfPerihelion, meanDistance, eccentricity, meanA)

        val r2 = k2(s)
        r2 shouldBe MinorPlanetCoord(Tag.OIWFS1, epoch2, inclination2, longAscendingNode2, argOfPerihelion2, meanDistance2, eccentricity2, meanA2)
    }

    test("Coord Key different Coords") {
        val c1 = CoordKey("Coords")
        val k1 = SolarSystemCoordKey(Tag.BASE)
        val k2 = EqCoordKey(Tag.OIWFS1)
        val k3 = CometCoordKey(Tag.BASE)

        val xra = 190.0.degree()
        val xdec = 32.0.degree()

        val s = testS().add(c1.set(k1.set(SolarSystemObject.Jupiter), k2.set(xra, xdec)))
        s.size shouldBe 1

        assert(c1.isIn(s))
        (k3 in s) shouldBe false

        val out1 = c1.get(s)
        out1?.size shouldBe 2
        out1?.tags shouldContainExactlyInAnyOrder listOf(Tag.BASE, Tag.OIWFS1)
        out1?.tags?.contains(Tag.BASE) shouldBe true

        out1?.tag(Tag.BASE).shouldBeTypeOf<SolarSystemCoord>()
        out1?.tag(Tag.OIWFS1).shouldBeTypeOf<EqCoord>()

        val out2 = c1.tag(s, Tag.BASE)
        out2.shouldBeTypeOf<SolarSystemCoord>()

        val out3 = c1.tag(s, Tag.OIWFS1)
        when(out3) {
            is EqCoord -> {
                out3.ra shouldBe xra
                out3.dec shouldBe xdec
            }
            is SolarSystemCoord -> println("Solar body: ${out3.body}")
            else -> println("Not: $out3")
        }
    }
}
)