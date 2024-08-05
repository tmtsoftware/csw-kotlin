package csw.params.events

import csw.params.core.models.Prefix
import csw.params.events.observeevents.ObserveEvent
import csw.params.keys.ByteKey
import csw.params.keys.IntegerKey
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe


// DEOPSCSW-183: Configure attributes and values
// DEOPSCSW-185: Easy to Use Syntax/Api
// DEOPSCSW-327: Define Event Data Structure
// DEOPSCSW-328: Basic information of Event needed for routing and Diagnostic use
// DEOPSCSW-329: Providing Mandatory information during Event Creation
// DEOPSCSW-330: Include complex payloads - paramset in Event and ObserveEvent
// DEOPSCSW-331: Complex payload - Include byte in paramset for Event and ObserveEvent
// | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331
class EventsTest: DescribeSpec({
    val s1 = "encoder"

    val s1Key = IntegerKey(s1)

    val prefix = Prefix("wfos.blue.filter")
    val eName: EventName = "filter wheel"

    describe("SystemEvent Test") {
        val k1 = IntegerKey("encoder")
        val k2 = IntegerKey("windspeed")
        val k3 = IntegerKey("notUsed")
        val k4 = ByteKey.make("image")

        it(
            "should create with prefix and eventName | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331"
        ) {
            val i1 = k1.set(22)
            val i2 = k2.set(44)

            val sc1 = SystemEvent(prefix, eName).add(i1, i2)
            assert(sc1.size == 2)
            assert(k1.isIn(sc1))
            assert(k2.isIn(sc1))

            k1.head(sc1).toInt() shouldBe 22
            k2.head(sc1).toInt() shouldBe 44
            sc1.missingKeys(k1, k2, k3) shouldContainExactlyInAnyOrder setOf(k3.name)

            val ex = shouldThrow<NoSuchElementException> {
                k4(sc1)
            }
            ex.message shouldBe "Parameter set does not contain key: ${k4.name}"

            val ex2 = shouldThrow<NoSuchElementException> {
                k3(sc1)
            }
            ex2.message shouldBe "Parameter set does not contain key: ${k3.name}"
        }

        it(
            "should create with prefix, eventName, paramSet | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331"
        ) {
            val i1 = k1.set(22)
            val i2 = k2.set(44)
            val str = "sendor image"
            val i4 = k4.set(str.toByteArray())

            val sc1 = SystemEvent(prefix, eName, listOf(i1, i2, i4))
            sc1.size shouldBe 3

            assert(sc1.exists(k1))
            assert(sc1.exists(k2))
            assert(sc1.exists(k4))

            k1.head(sc1).toInt() shouldBe 22
            k2.head(sc1).toInt() shouldBe 44
            k4.value(sc1) shouldBe str.toByteArray()
            sc1.missingKeys(k1, k2, k3) shouldBe setOf(k3.name)
        }

        it(
            "Should allow removing | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331"
        ) {
            val i1 = k1.set(22)
            val i2 = k2.set(44)
            val sc1 = SystemEvent(prefix, eName).madd(i1, i2)

            assert(sc1.size == 2)
            assert(k1.isIn(sc1))

            val mutatedSc1 = sc1.remove(k1)
            assert(!k1.isIn(mutatedSc1))

            mutatedSc1.size shouldBe 1
            assert(mutatedSc1.eventId != sc1.eventId)
        }

        it("Should allow adding | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331") {
            val i1 = k1.set(22)
            val i2 = k2.set(44)
            val sc1 = SystemEvent(prefix, eName).madd(i1)

            assert(sc1.size == 1)
            assert(sc1.exists(k1))
            k1.isIn(sc1) shouldBe true

            val mutatedSc1 = sc1.add(i2)

            mutatedSc1.size shouldBe 2
            assert(mutatedSc1.eventId != sc1.eventId)
        }

        it("Should access metadata fields | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331") {
            val i1 = k1.set(22)
            val sc1 = SystemEvent(prefix, eName).madd(i1)

            sc1.size shouldBe 1
            assert(sc1.exists(k1))
            sc1.eventId shouldNotBe null
            sc1.eventTime shouldNotBe null
            sc1.eventName shouldBe eName
            sc1.source shouldBe prefix
            sc1.eventKey.toString() shouldBe "$prefix.$eName"
        }

        it(
            "each event created should be unique | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331"
        ) {
            val ev1 = SystemEvent(prefix, eName)
            val ev2 = ev1.add(s1Key.set(2))
            val ev3 = ev2.remove(s1Key)

            ev1.eventId shouldNotBe ev2.eventId
            ev1.eventName shouldBe ev2.eventName
            ev1.source shouldBe ev2.source

            ev3.eventId shouldNotBe ev2.eventId
            ev3.eventName shouldBe ev2.eventName
            ev3.source shouldBe ev2.source
        }
    }

    describe("ObserveEvent Test") {
        val k1 = IntegerKey.make("encoder")
        val k2 = IntegerKey("windspeed")
        val k3 = IntegerKey("notUsed")
        val k4 = ByteKey("image")

        it(
            "should create with prefix and eventName | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331"
        ) {
            val i1 = k1.set(22)
            val i2 = k2.set(44)

            val sc1 = ObserveEvent(prefix, eName).madd(i1, i2)
            sc1.size shouldBe 2
            assert(sc1.exists(k1))
            assert(sc1.exists(k2))
            k1.head(sc1) shouldBe 22
            k2.head(sc1) shouldBe 44

            sc1.missingKeys(k1, k2, k3) shouldBe setOf(k3.name)

            val ex = shouldThrow<NoSuchElementException> {
                k4(sc1)
            }
            ex.message shouldBe ("Parameter set does not contain key: ${k4.name}")
        }

        it(
            "should create with prefix, eventName and paramSet | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331"
        ) {
            val i1 = k1.set(22)
            val i2 = k2.set(44)
            val str = "sendor image"
            val i4 = k4.set(str.toByteArray())

            val sc1 = ObserveEvent(prefix, eName).add(i1, i2, i4)
            sc1.size shouldBe 3
            k1.isIn(sc1) shouldBe true
            k2.isIn(sc1) shouldBe true
            k4.isIn(sc1) shouldBe true

            k1.head(sc1) shouldBe 22
            k2.head(sc1) shouldBe 44
            k4.asString(sc1) shouldBe str

            sc1.missingKeys(k1, k2, k3) shouldBe setOf(k3.name)
        }

        it(
            "Should allow removing | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331"
        ) {
            val i1 = k1.set(22)
            val i2 = k2.set(44)
            val oc1 = ObserveEvent(prefix, eName).madd(i1, i2)

            assert(oc1.size == 2)
            assert(oc1.exists(k1))

            val mutatedOc1 = oc1.remove(k1)

            assert(!mutatedOc1.exists(k1))
            assert(mutatedOc1.size == 1)
            assert(mutatedOc1.eventId != oc1.eventId)
        }

        it("Should allow adding | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331") {
            val i1  = k1.set(22)
            val i2  = k2.set(44)
            val oc1 = ObserveEvent(prefix, eName).madd(i1)

            assert(oc1.size == 1)
            assert(oc1.exists(k1))

            val mutatedOc1 = oc1.add(i2)

            assert(mutatedOc1.size == 2)
            assert(mutatedOc1.eventId != oc1.eventId)
        }

        it(
            "Should access metadata fields | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331"
        ) {
            val i1  = k1.set(22)
            val oc1 = ObserveEvent(prefix, eName).madd(i1)

            assert(oc1.size == 1)
            assert(oc1.exists(k1))

            with (oc1) {
                eventId shouldNotBe null
                eventTime shouldNotBe null
                eventName shouldBe eventName
                source shouldBe prefix
                eventKey.toString() shouldBe "$prefix.$eName"
            }

        }

        it(
            "each event created should be unique | DEOPSCSW-183, DEOPSCSW-185, DEOPSCSW-327, DEOPSCSW-328, DEOPSCSW-329, DEOPSCSW-330, DEOPSCSW-331"
        ) {
            val ev1 = ObserveEvent(prefix, eName)
            val ev2 = ev1.add(s1Key.set(2))
            val ev3 = ev2.remove(s1Key)

            ev1.eventId shouldNotBe ev2.eventId
            ev1.eventName shouldBe ev2.eventName
            ev1.source shouldBe ev2.source

            ev3.eventId shouldNotBe ev2.eventId
            ev3.eventName shouldBe ev2.eventName
            ev3.source shouldBe ev2.source
        }
    }
}
)


