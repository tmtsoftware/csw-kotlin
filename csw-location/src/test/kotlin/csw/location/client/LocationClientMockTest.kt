package csw.location.client

import csw.location.api.codecs.ModelCodecs
import csw.location.api.models.*
import csw.params.core.models.Prefix
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import java.net.URI

class LocationClientMockTest : FunSpec({
    val id1 = ComponentId(Prefix("IRIS.filter.wheel"), ComponentType.HCD)
    val id2 = ComponentId(Prefix("TCS.ptk"), ComponentType.Assembly)
    val id3 = ComponentId(Prefix("DMS.engIngest"), ComponentType.Service)

    test("Mock Register Call") {
        runBlocking {
            val mockEngine: HttpClientEngine = MockEngine { request ->
                val jsonText = (request.body as TextContent).text
                val json = Json.parseToJsonElement(jsonText).jsonObject
                val name = (json.jsonObject["_type"] as JsonPrimitive).contentOrNull
                name shouldBe "Register"

                val payload = json.jsonObject["registration"] as JsonElement
                val registration = ModelCodecs.jsonToReg(payload.toString())

                respond(
                    content = ModelCodecs.locationToJson(registration.location("localhost")),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val apiClient = LocationServiceClient(mockEngine)

            val c1 = HttpConnection(id1)
            val port = 8765
            val path = "AUTH"

            val m = Metadata().add("key1", "value1")
            val reg1 = HttpRegistration(c1, port, path, m)

            val result = apiClient.register(reg1)
            result shouldBe HttpLocation(c1, URI("http://localhost:8765/AUTH"), m)
        }
    }

    test("Mock Unregister Call") {
        runBlocking {
            val mockEngine: HttpClientEngine = MockEngine { request ->
                val jsonText = (request.body as TextContent).text
                val json = Json.parseToJsonElement(jsonText).jsonObject
                val name = (json.jsonObject["_type"] as JsonPrimitive).contentOrNull
                name shouldBe "Unregister"

                val payload = json.jsonObject["connection"] as JsonElement
                ModelCodecs.jsonToConnection(payload.toString())

                respond(
                    content = "",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val apiClient = LocationServiceClient(mockEngine)

            val c1 = HttpConnection(id1)

            val result = apiClient.unregister(c1)
            result shouldBe Unit
        }
    }

    test("Mock UnregisterAll") {
        runBlocking {
            val mockEngine: HttpClientEngine = MockEngine { request ->
                val jsonText = (request.body as TextContent).text
                val json = Json.parseToJsonElement(jsonText).jsonObject
                val name = (json.jsonObject["_type"] as JsonPrimitive).contentOrNull
                name shouldBe "UnregisterAll"

                respond(
                    content = "",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val apiClient = LocationServiceClient(mockEngine)

            val result = apiClient.unregisterAll()
            result shouldBe Unit
        }
    }

    test("Mock List Call") {
        val c1 = HttpConnection(id1)
        val c2 = AkkaConnection(id2)
        val m1 = Metadata().add("key1", "value1")
        val m2 = Metadata()
        val l1 = HttpLocation(c1, URI("http://localhost:8765/AUTH"), m1)
        val l2 = AkkaLocation(c2, URI("http://localhost:8765/AUTH"), m2)
        val locs = listOf(l1, l2)

        runBlocking {
            val mockEngine: HttpClientEngine = MockEngine { request ->
                val jsonText = (request.body as TextContent).text
                val json = Json.parseToJsonElement(jsonText).jsonObject
                val name = (json.jsonObject["_type"] as JsonPrimitive).contentOrNull
                name shouldBe "ListEntries"

                respond(
                    content = ModelCodecs.locationsToJson(locs),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val apiClient = LocationServiceClient(mockEngine)
            val result = apiClient.list()
            result shouldBe locs
        }
    }

    test("Mock Find Call") {
        runBlocking {
            val mockEngine: HttpClientEngine = MockEngine { request ->
                val jsonText = (request.body as TextContent).text
                val json = Json.parseToJsonElement(jsonText).jsonObject
                val name = (json.jsonObject["_type"] as JsonPrimitive).contentOrNull
                name shouldBe "Find"

                val payload = json.jsonObject["connection"] as JsonElement
                val connection = ModelCodecs.jsonToConnection(payload.toString())

                val m = Metadata()
                val response = when(connection.connectionType) {
                    ConnectionType.HttpType ->
                        HttpLocation(connection as HttpConnection, URI("http://localhost:8765/AUTH"), m)
                    ConnectionType.AkkaType ->
                        AkkaLocation(connection as AkkaConnection, URI("http://localhost:8765/AUTH"), m)
                    ConnectionType.TcpType ->
                        TcpLocation(connection as TcpConnection, URI("http://localhost:8765/AUTH"), m)
                }

                respond(
                    content = ModelCodecs.locationsToJson(listOf(response)),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val apiClient = LocationServiceClient(mockEngine)

            val c1 = HttpConnection(id1)

            val result = apiClient.find(c1)
            result!!.prefix shouldBe c1.prefix
        }
    }

    test("Mock ListByComponent Call") {
        val c1 = HttpConnection(id1)
        val c2 = AkkaConnection(id2)
        val m1 = Metadata().add("key1", "value1")
        val m2 = Metadata()
        val l1 = HttpLocation(c1, URI("http://localhost:8765/AUTH"), m1)
        val l2 = AkkaLocation(c2, URI("http://localhost:8765/AUTH"), m2)
        val locs = listOf(l1, l2)

        runBlocking {
            val mockEngine: HttpClientEngine = MockEngine { request ->
                val jsonText = (request.body as TextContent).text
                val json = Json.parseToJsonElement(jsonText).jsonObject
                val name = (json.jsonObject["_type"] as JsonPrimitive).contentOrNull
                name shouldBe "ListByComponentType"

                val jsonCompType = (json.jsonObject["componentType"] as JsonPrimitive).content
                val componentType = ComponentType(jsonCompType)

                respond(
                    content = ModelCodecs.locationsToJson(locs.filter{ it.connection.componentId.componentType == componentType}),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val apiClient = LocationServiceClient(mockEngine)
            val result = apiClient.list(ComponentType.HCD)
            result shouldHaveSize 1
        }
    }

    test("Mock ListByConnectionType Call") {
        val c1 = HttpConnection(id1)
        val c2 = AkkaConnection(id2)
        val m1 = Metadata().add("key1", "value1")
        val m2 = Metadata()
        val l1 = HttpLocation(c1, URI("http://localhost:8765/AUTH"), m1)
        val l2 = AkkaLocation(c2, URI("http://localhost:8765/AUTH"), m2)
        val locs = listOf(l1, l2)

        runBlocking {
            val mockEngine: HttpClientEngine = MockEngine { request ->
                val jsonText = (request.body as TextContent).text
                val json = Json.parseToJsonElement(jsonText).jsonObject
                val name = (json.jsonObject["_type"] as JsonPrimitive).contentOrNull
                name shouldBe "ListByConnectionType"

                val jsonConnType = (json.jsonObject["connectionType"] as JsonPrimitive).content
                val connectionType = ConnectionType(jsonConnType)

                respond(
                    content = ModelCodecs.locationsToJson(locs.filter{ it.connection.connectionType == connectionType}),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val apiClient = LocationServiceClient(mockEngine)
            val result = apiClient.list(ConnectionType.HttpType)
            result shouldHaveSize 1
        }
    }

    test("Mock ListByHostName Call") {
        val c1 = HttpConnection(id1)
        val c2 = AkkaConnection(id2)
        val c3 = TcpConnection(id3)
        val m1 = Metadata().add("key1", "value1")
        val m2 = Metadata()
        val l1 = HttpLocation(c1, URI("http://localhost1:8765/AUTH"), m1)
        val l2 = AkkaLocation(c2, URI("http://localhost2:8766/AUTH"), m2)
        val l3 = TcpLocation(c3, URI("http://localhost2:8795/AUTH"), m2)
        val locs = listOf(l1, l2, l3)

        runBlocking {
            val mockEngine: HttpClientEngine = MockEngine { request ->
                val jsonText = (request.body as TextContent).text
                val json = Json.parseToJsonElement(jsonText).jsonObject
                val name = (json.jsonObject["_type"] as JsonPrimitive).contentOrNull
                name shouldBe "ListByHostname"

                val hostname = (json.jsonObject["hostname"] as JsonPrimitive).content

                respond(
                    content = ModelCodecs.locationsToJson(locs.filter{ it.uri.host == hostname }),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val apiClient = LocationServiceClient(mockEngine)
            val result = apiClient.list("localhost2")
            result shouldHaveSize 2
        }
    }

    test("Mock ListByPrefix Call") {
        val c1 = HttpConnection(id1)
        val c2 = AkkaConnection(id2)
        val c3 = TcpConnection(id3)
        val m1 = Metadata().add("key1", "value1")
        val m2 = Metadata()
        val l1 = HttpLocation(c1, URI("http://localhost1:8765/AUTH"), m1)
        val l2 = AkkaLocation(c2, URI("http://localhost2:8766/AUTH"), m2)
        val l3 = TcpLocation(c3, URI("http://localhost2:8795/AUTH"), m2)
        val locs = listOf(l1, l2, l3)

        runBlocking {
            val mockEngine: HttpClientEngine = MockEngine { request ->
                val jsonText = (request.body as TextContent).text
                val json = Json.parseToJsonElement(jsonText).jsonObject
                val name = (json.jsonObject["_type"] as JsonPrimitive).contentOrNull
                name shouldBe "ListByPrefix"

                val prefix = (json.jsonObject["prefix"] as JsonPrimitive).content

                respond(
                    content = ModelCodecs.locationsToJson(locs.filter{ it.prefix.toString() == prefix }),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val apiClient = LocationServiceClient(mockEngine)
            val result = apiClient.listByPrefix(c2.prefix)
            result shouldHaveSize 1
        }
    }

})