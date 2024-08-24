package csw.location.client

import csw.csw.location.api.ILocationService
import csw.location.api.codecs.ModelCodecs
import csw.location.api.models.*
import csw.params.core.models.Prefix
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.time.Duration

class LocationServiceClient(engine: HttpClientEngine /*, val cswVersion: CswVersion*/): ILocationService, Closeable {

    private val client = HttpClient(engine) {
        install(WebSockets)
        install(Logging)
    }

    private val baseUri = "http://127.0.0.1:7654/"
    private val postUri = "${baseUri}post-endpoint"

    // Required by Closeable
    override fun close() {
        client.close()
    }

    override suspend fun register(registration: Registration): Location {
        val response = client.post(postUri) {
            contentType(ContentType.Application.Json)
            // Create a JSON object to match CSW
            val map:Map<String, JsonElement> =
                mapOf("_type" to JsonPrimitive("Register"),
                    "registration" to ModelCodecs.regToJsonElement(registration))
            val root = JsonObject(map)
            setBody(root.toString())
        }

        if (response.status.value in 200..299) {
            println("Register Success")
            val stringBody: String = response.body()
            return ModelCodecs.jsonToLocation(stringBody)
        }
        // Else?
        throw IllegalArgumentException("Not sure what happened: ${response.status.value}")
    }

    override suspend fun unregister(connection: Connection) {
        val response = client.post(postUri) {
            contentType(ContentType.Application.Json)
            // Create a JSON object to match CSW
            val map:Map<String, JsonElement> =
                mapOf("_type" to JsonPrimitive("Unregister"),
                    "connection" to ModelCodecs.connectionToJsonElement(connection))
            val root = JsonObject(map)
            setBody(root.toString())
        }

        if (response.status.value in 200..299) {
            println("Unregister Success")
            return
        }
        // Else?
        throw IllegalArgumentException("Not sure what happened: ${response.status.value}")
    }

    override suspend fun unregisterAll() {
        val response = client.post(postUri) {
            contentType(ContentType.Application.Json)
            // Create a JSON object to match CSW
            val map:Map<String, JsonElement> =
                mapOf("_type" to JsonPrimitive("UnregisterAll"))
            val root = JsonObject(map)
            setBody(root.toString())
        }

        if (response.status.value in 200..299) {
            println("UnregisterAll Success")
        }
        // Else?
        throw IllegalArgumentException("Not sure what happened: ${response.status.value}")
    }

    override suspend fun list(): List<Location> {
        val response = client.post(postUri) {
            contentType(ContentType.Application.Json)
            val map:Map<String, JsonElement> =
                mapOf("_type" to JsonPrimitive("ListEntries"))
            val root = JsonObject(map)
            setBody(root.toString())
        }

        if (response.status.value in 200..299) {
            println("List Success")
            val stringBody: String = response.body()
            return ModelCodecs.jsonToLocations(stringBody)
        }
        // Else?
        throw IllegalArgumentException("Not sure what happened: ${response.status.value}")
    }

    override suspend fun find(connection: Connection):Location? {
        val response = client.post(postUri) {
            contentType(ContentType.Application.Json)
            // Create a JSON object to match CSW
            val map:Map<String, JsonElement> =
                mapOf("_type" to JsonPrimitive("Find"),
                    "connection" to ModelCodecs.connectionToJsonElement(connection))
            val root = JsonObject(map)
            setBody(root.toString())
        }

        if (response.status.value in 200..299) {
            println("Find Success")
            val stringBody: String = response.body()
            return if (stringBody == "[]") {
                null
            } else
                ModelCodecs.jsonToLocations(stringBody)[0]
        }
        // Else?
        throw IllegalArgumentException("Not sure what happened: ${response.status.value}:${response.status.description}")
    }

    override suspend fun resolve(connection: Connection, within: Duration): List<Location> {
        val response = client.post(postUri) {
            contentType(ContentType.Application.Json)
            // Create a JSON object to match CSW
            val map:Map<String, JsonElement> =
                mapOf("_type" to JsonPrimitive("Resolve"),
                    "connection" to ModelCodecs.connectionToJsonElement(connection),
                    "within" to JsonPrimitive("${within.inWholeSeconds} seconds"))
            val root = JsonObject(map)
            setBody(root.toString())
        }

        if (response.status.value in 200..299) {
            println("Resolve Success")
            val stringBody: String = response.body()
            return if (stringBody == "[]") emptyList() else ModelCodecs.jsonToLocations(stringBody)
        }
        // Else?
        throw IllegalArgumentException("Not sure what happened: ${response.status.value}:${response.status.description}")
    }

    override suspend fun list(componentType: ComponentType): List<Location> {
        val response = client.post(postUri) {
            contentType(ContentType.Application.Json)
            val map:Map<String, JsonElement> =
                mapOf("_type" to JsonPrimitive("ListByComponentType"),
                    "componentType" to JsonPrimitive(componentType.name))
            val root = JsonObject(map)
            setBody(root.toString())
        }

        if (response.status.value in 200..299) {
            println("ListByComp Success")
            val stringBody: String = response.body()
            return ModelCodecs.jsonToLocations(stringBody)
        }
        // Else?
        throw IllegalArgumentException("Not sure what happened: ${response.status.value}")
    }

    override suspend fun list(connectionType: ConnectionType): List<Location> {
        val response = client.post(postUri) {
            contentType(ContentType.Application.Json)
            val map:Map<String, JsonElement> =
                mapOf("_type" to JsonPrimitive("ListByConnectionType"),
                    "connectionType" to JsonPrimitive(connectionType.cname))
            val root = JsonObject(map)
            setBody(root.toString())
        }

        if (response.status.value in 200..299) {
            println("ListByConn Success")
            val stringBody: String = response.body()
            return ModelCodecs.jsonToLocations(stringBody)
        }
        // Else?
        throw IllegalArgumentException("Not sure what happened: ${response.status.value}")
    }

    override suspend fun list(hostname: String): List<Location> {
        // Note that hostname can never be null
        val response = client.post(postUri) {
            contentType(ContentType.Application.Json)
            val map:Map<String, JsonElement> =
                mapOf("_type" to JsonPrimitive("ListByHostname"),
                    "hostname" to JsonPrimitive(hostname))
            val root = JsonObject(map)
            setBody(root.toString())
        }

        if (response.status.value in 200..299) {
            println("ListByHostName Success")
            val stringBody: String = response.body()
            return ModelCodecs.jsonToLocations(stringBody)
        }
        // Else?
        throw IllegalArgumentException("Not sure what happened: ${response.status.value}")
    }

    override suspend fun listByPrefix(prefix: Prefix): List<Location> {
        // Note that prefix can never be null
        val response = client.post(postUri) {
            contentType(ContentType.Application.Json)
            val map:Map<String, JsonElement> =
                mapOf("_type" to JsonPrimitive("ListByPrefix"),
                    "prefix" to JsonPrimitive(prefix.toString()))
            val root = JsonObject(map)
            setBody(root.toString())
        }

        if (response.status.value in 200..299) {
            println("ListByPrefix Success")
            val stringBody: String = response.body()
            return ModelCodecs.jsonToLocations(stringBody)
        }
        // Else?
        throw IllegalArgumentException("Not sure what happened: ${response.status.value}")
    }

    override suspend fun track(connection: Connection) {
        //TODO
    }

    override suspend fun subscribe(connection: Connection, callback: (TrackingEvent) -> Int): Int {
        // TODO
        return -1 /*Subscription */
    } 

}