package csw.location.api.codecs

import csw.location.api.models.*
import csw.location.api.codecs.ModelCodecs.jsonToConnection
import csw.location.api.codecs.ModelCodecs.connectionToJson
import csw.params.core.models.Prefix
import csw.params.core.models.Subsystem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.net.URI

class ModelCodecsTest : FunSpec({
    val id1 = ComponentId(Prefix("TCS.filter.wheel"), ComponentType.HCD)
    val uri = URI("http://localhost:8080")
    val m = Metadata().add("key1", "value")

    // Test serialization to JSON
    test("Akka connection serialization to JSON matches contracts") {
        val c1 = AkkaConnection(
            ComponentId(Prefix(Subsystem.NFIRAOS, "tromboneHcd"), ComponentType.HCD),
            ConnectionType.AkkaType
        )
        val jsonOut = connectionToJson(c1)
        val expected = """
            {
                "prefix": "NFIRAOS.tromboneHcd",
                "componentType": "HCD",
                "connectionType": "akka"
            }
        """.trimIndent()
        jsonOut shouldBe expected
        jsonToConnection(jsonOut) shouldBe c1
    }

    test("HTTP connection serialization to JSON matches contracts") {
        val c1 =
            HttpConnection(ComponentId(Prefix(Subsystem.CSW, "config"), ComponentType.Service), ConnectionType.HttpType)
        val jsonOut = connectionToJson(c1)
        val expected = """
            {
                "prefix": "CSW.config",
                "componentType": "Service",
                "connectionType": "http"
            }
        """.trimIndent()
        jsonOut shouldBe expected
        jsonToConnection(jsonOut) shouldBe c1
    }

    test("TCP connection serialization to JSON matches contracts") {
        val c1 =
            TcpConnection(ComponentId(Prefix(Subsystem.CSW, "redis"), ComponentType.Service), ConnectionType.TcpType)
        val jsonOut = connectionToJson(c1)
        val expected = """
            {
                "prefix": "CSW.redis",
                "componentType": "Service",
                "connectionType": "tcp"
            }
        """.trimIndent()
        jsonOut shouldBe expected
        jsonToConnection(jsonOut) shouldBe c1
    }

    test("Akka locations serialize properly for CSW") {

        // ------------  Akka Location
        val akkaConnection = AkkaConnection(
            ComponentId(
                Prefix(Subsystem.NFIRAOS, "tromboneHcd"),
                ComponentType.HCD
            ),
            ConnectionType.AkkaType
        )

        val loc1 = AkkaLocation(akkaConnection, uri, m)

        val jsonOut1 = ModelCodecs.locationToJson(loc1)
        val expected1 = """
            {
                "_type": "AkkaLocation",
                "connection": {
                    "prefix": "NFIRAOS.tromboneHcd",
                    "componentType": "HCD",
                    "connectionType": "akka"
                },
                "uri": "http://localhost:8080",
                "metadata": {
                    "key1": "value"
                }
            }
        """.trimIndent()

        jsonOut1 shouldBe expected1
        val locIn1 = ModelCodecs.jsonToLocation(jsonOut1)
        locIn1 shouldBe loc1
    }

    test("Http Locations serialize properly for CSW") {

        // -----------  HTTP Location
        val httpConnection = HttpConnection(
            ComponentId(
                Prefix(Subsystem.TCS, "tpk"),
                ComponentType.Assembly
            ),
            ConnectionType.HttpType
        )
        val loc2 = HttpLocation(httpConnection, uri, m)

        val jsonOut2 = ModelCodecs.locationToJson(loc2)
        val expected2 = """
            {
                "_type": "HttpLocation",
                "connection": {
                    "prefix": "TCS.tpk",
                    "componentType": "Assembly",
                    "connectionType": "http"
                },
                "uri": "http://localhost:8080",
                "metadata": {
                    "key1": "value"
                }
            }
        """.trimIndent()
        jsonOut2 shouldBe expected2
        val locIn2 = ModelCodecs.jsonToLocation(jsonOut2)
        locIn2 shouldBe loc2
    }

    test("TCP locations serialize properly for CSW") {

        // ---------------- Tcp Location
        val tcpConnection = TcpConnection(
            ComponentId(
                Prefix(Subsystem.DMS, "ingestor"),
                ComponentType.Service
            ),
            ConnectionType.TcpType
        )
        val loc3 = TcpLocation(tcpConnection, uri, m)

        val jsonOut3 = ModelCodecs.locationToJson(loc3)
        val expected3 = """
            {
                "_type": "TcpLocation",
                "connection": {
                    "prefix": "DMS.ingestor",
                    "componentType": "Service",
                    "connectionType": "tcp"
                },
                "uri": "http://localhost:8080",
                "metadata": {
                    "key1": "value"
                }
            }
        """.trimIndent()
        jsonOut3 shouldBe expected3
        val locIn3 = ModelCodecs.jsonToLocation(jsonOut3)
        locIn3 shouldBe loc3
    }

    test("Akka Registration should serialize JSON as needed for CSW") {
        val c1 = AkkaConnection(id1)
        val reg1 = AkkaRegistration(c1, URI("path"), m)

        // Test connection json against CSW format
        val jsonOut = ModelCodecs.regToJson(reg1)
        val expected = """
            {
                "_type": "AkkaRegistration",
                "connection": {
                    "prefix": "TCS.filter.wheel",
                    "componentType": "HCD",
                    "connectionType": "akka"
                },
                "actorRefURI": "path",
                "metadata": {
                    "key1": "value"
                }
            }
        """.trimIndent()
        jsonOut shouldBe expected

        val objIn = ModelCodecs.jsonToReg(jsonOut)
        objIn shouldBe reg1
    }

    test("HTTP Registration should serialize JSON as needed for CSW") {
        val c1 = HttpConnection(id1)
        val port = 8765
        val path = "AUTH"
        val reg1 = HttpRegistration(c1, port, path, m)

        // Test connection json against CSW format
        val jsonOut = ModelCodecs.regToJson(reg1)
        val expected = """
            {
                "_type": "HttpRegistration",
                "connection": {
                    "prefix": "TCS.filter.wheel",
                    "componentType": "HCD",
                    "connectionType": "http"
                },
                "port": 8765,
                "path": "AUTH",
                "networkType": {
                    "_type": "Inside"
                },
                "metadata": {
                    "key1": "value"
                }
            }
        """.trimIndent()
        jsonOut shouldBe expected

        val objIn = ModelCodecs.jsonToReg(jsonOut)
        objIn shouldBe reg1
    }

    test("TCP Registration should serialize JSON as needed for CSW") {
        val c1 = TcpConnection(id1)
        val port = 8765
        val reg1 = TcpRegistration(c1, port, m)

        // Test connection json against CSW format
        val jsonOut = ModelCodecs.regToJson(reg1)
        val expected = """
            {
                "_type": "TcpRegistration",
                "connection": {
                    "prefix": "TCS.filter.wheel",
                    "componentType": "HCD",
                    "connectionType": "tcp"
                },
                "port": 8765,
                "metadata": {
                    "key1": "value"
                }
            }
        """.trimIndent()
        jsonOut shouldBe expected

        val objIn = ModelCodecs.jsonToReg(jsonOut)
        objIn shouldBe reg1
    }

    test("Location result list serialization works properly") {
        val httpConnection = HttpConnection(
            ComponentId(
                Prefix(Subsystem.TCS, "tpk"),
                ComponentType.Assembly
            ),
            ConnectionType.HttpType
        )
        val akkaConnection = AkkaConnection(
            ComponentId(
                Prefix(Subsystem.NFIRAOS, "tromboneHcd"),
                ComponentType.HCD
            ),
            ConnectionType.AkkaType
        )

        val loc1 = AkkaLocation(akkaConnection, uri, m)
        val loc2 = HttpLocation(httpConnection, uri, m)
        val objs = listOf(loc1, loc2)

        val jsonOut = ModelCodecs.locationsToJson(objs)
        // Note expected must be exactly as below
        val expected = """
        [
            {
                "_type": "AkkaLocation",
                "connection": {
                    "prefix": "NFIRAOS.tromboneHcd",
                    "componentType": "HCD",
                    "connectionType": "akka"
                },
                "uri": "http://localhost:8080",
                "metadata": {
                    "key1": "value"
                }
            },
            {
                "_type": "HttpLocation",
                "connection": {
                    "prefix": "TCS.tpk",
                    "componentType": "Assembly",
                    "connectionType": "http"
                },
                "uri": "http://localhost:8080",
                "metadata": {
                    "key1": "value"
                }
            }
        ]""".trimIndent()
        jsonOut shouldBe expected


        val objsOut: List<Location> = ModelCodecs.jsonToLocations(jsonOut)
        objsOut.size shouldBe 2
        objsOut shouldBe objs
    }
})


// This is being saved for a while to show a technique that may be needed later
@Serializable(with=IncomingSerializer::class)
data class IncomingCommand(val _type: String, val payload: Map<String, JsonElement>)

// @Serializer(forClass = Incoming::class)
object IncomingSerializer : KSerializer<IncomingCommand> {
    private val mapSerializer = MapSerializer(String.serializer(), JsonObject.serializer())
    override val descriptor: SerialDescriptor = mapSerializer.descriptor

    override fun deserialize(decoder: Decoder): IncomingCommand {
        require(decoder is JsonDecoder)
        val json = decoder.decodeJsonElement().jsonObject
        println("Json: $json")
        val comm = json.getValue("_type").jsonPrimitive.content
        println("Comm: $comm")
        //val reg = decoder.decodeSerializableValue(mapSerializer)
        val payload = json.toMutableMap()
        payload.remove("_type")
        println("payload: $payload")
        return IncomingCommand(comm, payload)
    }

    override fun serialize(encoder: Encoder, value: IncomingCommand) {
        error("Serialization not supported")
    }
}

