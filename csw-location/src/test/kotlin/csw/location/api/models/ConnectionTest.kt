package csw.location.api.models

import csw.params.core.models.Prefix
import csw.params.core.models.Subsystem
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.cbor.Cbor

// CSW-86: Subsystem should be case-insensitive
class ConnectionTest: FunSpec( {
    // DEOPSCSW-14: Codec for data model
    test("should able to form a string representation for akka connection for trombone HCD | DEOPSCSW-14") {
        val expectedAkkaConnectionName = "NFIRAOS.tromboneHcd-HCD-akka"
        val akkaConnection             = AkkaConnection(ComponentId(Prefix(Subsystem.NFIRAOS, "tromboneHcd"), ComponentType.HCD), ConnectionType.AkkaType)
        akkaConnection.name shouldBe expectedAkkaConnectionName
    }

    // DEOPSCSW-14: Codec for data model
    test("should able to form a string representation for tcp connection for redis | DEOPSCSW-14") {
        val expectedTcpConnectionName = "CSW.redis-Service-tcp"
        val tcpConnection             = TcpConnection(ComponentId(Prefix(Subsystem.CSW, "redis"), ComponentType.Service), ConnectionType.TcpType)
        tcpConnection.name shouldBe expectedTcpConnectionName
    }

    // DEOPSCSW-14: Codec for data model
    test("should able to form a string representation for http connection for config service | DEOPSCSW-14") {
        val expectedHttpConnectionName = "CSW.config-Service-http"
        val httpConnection             = HttpConnection(ComponentId(Prefix(Subsystem.CSW, "config"), ComponentType.Service), ConnectionType.HttpType)
        httpConnection.name shouldBe expectedHttpConnectionName
    }

    // DEOPSCSW-14: Codec for data model
    test("should able to form a string representation for akka connection for trombone container | DEOPSCSW-14") {
        val expectedAkkaConnectionName = "Container.tromboneContainer-Container-akka"
        val akkaConnection =
            AkkaConnection(ComponentId(Prefix(Subsystem.Container, "tromboneContainer"), ComponentType.Container), ConnectionType.AkkaType)
        akkaConnection.name shouldBe expectedAkkaConnectionName
    }

    // DEOPSCSW-14: Codec for data model
    test("should able to form a string representation for akka connection for trombone assembly | DEOPSCSW-14") {
        val expectedAkkaConnectionName = "NFIRAOS.tromboneAssembly-Assembly-akka"
        val akkaConnection             = AkkaConnection(ComponentId(Prefix(Subsystem.NFIRAOS, "tromboneAssembly"), ComponentType.Assembly), ConnectionType.AkkaType)
        akkaConnection.name shouldBe expectedAkkaConnectionName
    }

    // DEOPSCSW-14: Codec for data model
    test("should able to form a connection for components from a valid string representation | DEOPSCSW-14") {
        Connection.from("nfiraos.tromboneAssembly-Assembly-akka") shouldBe
                AkkaConnection(ComponentId(Prefix(Subsystem.NFIRAOS, "tromboneAssembly"), ComponentType.Assembly), ConnectionType.AkkaType)

        Connection.from("nfiraos.tromboneHcd-HCD-akka") shouldBe
                AkkaConnection(ComponentId(Prefix(Subsystem.NFIRAOS, "tromboneHcd"), ComponentType.HCD), ConnectionType.AkkaType)

        Connection.from("csw.redis-Service-tcp") shouldBe
                TcpConnection(ComponentId(Prefix(Subsystem.CSW, "redis"), ComponentType.Service), ConnectionType.TcpType)

        Connection.from("csw.configService-Service-http") shouldBe
                HttpConnection(ComponentId(Prefix(Subsystem.CSW, "configService"), ComponentType.Service), ConnectionType.HttpType)
    }

    // DEOPSCSW-14: Codec for data model
    test("should not be able to form a connection for components from an invalid string representation | DEOPSCSW-14") {
        val connection = "nfiraos.tromboneAssembly_assembly_akka"

        val ex = shouldThrow<IllegalArgumentException> {
            Connection.from(connection)
        }
        ex.message shouldBe "Unable to parse '$connection' to make Connection object"

        val connection2 = "nfiraos.trombone-hcd"
        val ex2 = shouldThrow<IllegalArgumentException> {
            Connection.from(connection2)
        }
        ex2.message shouldBe "Unable to parse '$connection2' to make Connection object"
    }

    // Test serialization to JSON
    test("Test connection serialization to JSON") {
        val c1 = AkkaConnection(ComponentId(Prefix(Subsystem.NFIRAOS, "tromboneHcd"), ComponentType.HCD), ConnectionType.AkkaType)

        val jsonOut = Json.encodeToString(c1)
        val jin = Json.decodeFromString<AkkaConnection>(jsonOut)
        jin shouldBe c1

        val c2 = TcpConnection(ComponentId(Prefix(Subsystem.CSW, "redis"), ComponentType.Service), ConnectionType.TcpType)
        val jsonOut2 = Json.encodeToString(c2)
        val jin2 = Json.decodeFromString<TcpConnection>(jsonOut2)
        jin2 shouldBe c2

        val c3 = HttpConnection(ComponentId(Prefix(Subsystem.CSW, "config"), ComponentType.Service), ConnectionType.HttpType)
        val jsonOut3 = Json.encodeToString(c3)
        val jin3 = Json.decodeFromString<HttpConnection>(jsonOut3)
        jin3 shouldBe c3
    }

    // Prefix should serialize to Json and cbor
    test("should serialize connection to CBOR") {
        val c1 = AkkaConnection(ComponentId(Prefix(Subsystem.NFIRAOS, "tromboneHcd"), ComponentType.HCD), ConnectionType.AkkaType)

        val bytes = Cbor.encodeToByteArray(c1)
        val obj = Cbor.decodeFromByteArray<AkkaConnection>(bytes)
        obj shouldBe c1

        val c2 = TcpConnection(ComponentId(Prefix(Subsystem.CSW, "redis"), ComponentType.Service), ConnectionType.TcpType)
        val bytes2 = Cbor.encodeToByteArray(c2)
        val obj2 = Cbor.decodeFromByteArray<TcpConnection>(bytes2)
        obj2 shouldBe c2

        val c3 = HttpConnection(ComponentId(Prefix(Subsystem.CSW, "config"), ComponentType.Service), ConnectionType.HttpType)
        val bytes3 = Cbor.encodeToByteArray(c3)
        val obj3 = Cbor.decodeFromByteArray<HttpConnection>(bytes3)
        obj3 shouldBe c3
    }
}
)
