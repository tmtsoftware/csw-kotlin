package csw.location.client

import csw.location.api.models.*
import csw.params.core.models.Prefix
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.cio.*
import java.net.URI
import kotlin.time.Duration.Companion.seconds

class LocationClientIntTest: FunSpec( {
    val id1 = ComponentId(Prefix("IRIS.filter.wheel"), ComponentType.HCD)
    val c1 = HttpConnection(id1)
    val mWithValue = Metadata().add("key1", "value")
    val m = Metadata()
    val reg1 = HttpRegistration(c1, 8765, "AUTH", mWithValue)
    val l1 = HttpLocation(c1, URI("http://localhost:8765/AUTH"), mWithValue)

    val id2 = ComponentId(Prefix("TCS.ptk"), ComponentType.Assembly)
    val c2 = AkkaConnection(id2)
    val reg2 = AkkaRegistration(c2, URI("akka://localhost:2345/"), m)
    val l2 = AkkaLocation(c2, URI("akka://localhost:2345/"), m)

    suspend fun doRegister(lsc: LocationServiceClient, reg: Registration): Location {
        return lsc.register(reg)
    }

    test("Make sure register/unregister works") {
        val ls = LocationServiceClient(CIO.create())

        // Initialize: No result
        ls.unregisterAll()

        val result = doRegister(ls, reg1)

        result.prefix shouldBe l1.prefix
        result.connection shouldBe l1.connection
        // Ignoring URIs since they are different due to multiple addresses
        result.metadata shouldBe l1.metadata

        // No result form unregister
        ls.unregister(reg1.connection)

        val result3 = ls.list()
        println("result3: $result3")
        result3.size shouldBe 0
        result3 shouldBe emptyList()
    }

    test("Test List Call and unregister all") {
        val ls = LocationServiceClient(CIO.create())
        // No result
        ls.unregisterAll()

        val r1 = ls.list()
        r1 shouldBe emptyList()

        val r2 = doRegister(ls, reg1)
        r2.prefix shouldBe l1.prefix

        val r3 = ls.list()
        r3.size shouldBe 1

        val r4 = doRegister(ls, reg2)
        r4.prefix shouldBe l2.prefix

        val r5 = ls.list()
        r5 shouldHaveSize 2
        println("R5: $r5")

        // No result
        ls.unregisterAll()
        val r6 = ls.list()
        r6 shouldBe emptyList()
    }

    test("Test find Call") {
        val ls = LocationServiceClient(CIO.create())
        // No result
        //ls.unregisterAll()

        val r1 = ls.find(c1)
        println("R1: $r1")
        //r1 shouldBe null

        val r2 = doRegister(ls, reg1)
        r2.prefix shouldBe c1.prefix

        val r3 = ls.list()
        r3.size shouldBe 1

        val r4 = ls.find(c1)
        r4!!.prefix shouldBe c1.prefix

        val r5 = ls.list()
        r5 shouldHaveSize 1

        // No result
        ls.unregisterAll()
        val r6 = ls.list()
        r6 shouldBe emptyList()

    }

    test("Test resolve Call") {
        val ls = LocationServiceClient(CIO.create())
        // No result
        ls.unregisterAll()

        val r1 = ls.resolve(c1, 2.seconds)
        println("R1: $r1")
        //r1 shouldBe null

        val r2 = doRegister(ls, reg1)
        r2.prefix shouldBe c1.prefix

        val r3 = ls.list()
        r3.size shouldBe 1

        val r4 = ls.resolve(c1, 2.seconds)
        println("R4: $r4")
        //r4.prefix shouldBe c1.prefix
/*
        val r5 = ls.list()
        r5 shouldHaveSize 1

        // No result
        ls.unregisterAll()
        val r6 = ls.list()
        r6 shouldBe emptyList()
*/
    }

    test("Test List by ComponentType") {
        val ls = LocationServiceClient(CIO.create())
        // No result
        ls.unregisterAll()

        val r2 = doRegister(ls, reg1)
        r2.prefix shouldBe l1.prefix

        val r3 = doRegister(ls, reg2)
        r3.prefix shouldBe l2.prefix

        val r4 = ls.list(ComponentType.HCD)
        r4.size shouldBe 1
        r4[0].prefix shouldBe l1.prefix

        val r5 = ls.list(ComponentType.Assembly)
        r5.size shouldBe 1
        r5[0].prefix shouldBe l2.prefix

        val r6 = ls.list(ComponentType.Container)
        r6 shouldHaveSize 0

        // No result
        ls.unregisterAll()
    }

    test("Test List by ConnectionType") {
        val ls = LocationServiceClient(CIO.create())
        // No result
        ls.unregisterAll()

        val r2 = doRegister(ls, reg1)
        r2.prefix shouldBe l1.prefix

        val r3 = doRegister(ls, reg2)
        r3.prefix shouldBe l2.prefix

        val r4 = ls.list(ConnectionType.HttpType)
        r4.size shouldBe 1
        r4[0].prefix shouldBe l1.prefix

        val r5 = ls.list(ConnectionType.AkkaType)
        r5.size shouldBe 1
        r5[0].prefix shouldBe l2.prefix

        val r6 = ls.list(ConnectionType.TcpType)
        r6 shouldHaveSize 0

        // No result
        ls.unregisterAll()
    }

    test("Test ListByHostname") {
        val ls = LocationServiceClient(CIO.create())
        // No result
        ls.unregisterAll()

        val r2 = doRegister(ls, reg1)
        r2.prefix shouldBe l1.prefix

        val r3 = doRegister(ls, reg2)
        r3.prefix shouldBe l2.prefix

        val allRegs = ls.list()

        allRegs.size shouldBe 2
        val host1 = allRegs[0].uri.host
        val host2 = allRegs[1].uri.host

        val r4 = ls.list(host1)
        r4.size shouldBeGreaterThan 0

        val r5 = ls.list(host2)
        r5.size shouldBeGreaterThan 0

        // No result
        ls.unregisterAll()
    }

    test("Test listbyPrefix") {
        val ls = LocationServiceClient(CIO.create())
        // No result
        ls.unregisterAll()

        val r2 = doRegister(ls, reg1)
        r2.prefix shouldBe l1.prefix

        val r3 = doRegister(ls, reg2)
        r3.prefix shouldBe l2.prefix

        val r4 = ls.listByPrefix(r2.prefix)
        r4.size shouldBe 1
        r4[0].prefix shouldBe l1.prefix

        val r5 = ls.listByPrefix(l2.prefix)
        r5.size shouldBe 1
        r5[0].prefix shouldBe l2.prefix

        val r6 = ls.listByPrefix(Prefix("ESW.test"))
        r6 shouldHaveSize 0

        // No result
        ls.unregisterAll()
    }
})