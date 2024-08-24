package csw.location.api.codecs

import csw.csw.location.api.models.NetworkType
import csw.location.api.models.*
import csw.location.api.models.AkkaRegistration
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.net.URI

object ModelCodecs {

    private val regmodule = SerializersModule {
        /*
        polymorphic((Registration::class)) {
            subclass(AkkaRegistration::class)
            subclass(HttpRegistration::class)
            subclass(TcpRegistration::class)
        }

         */
        /*
        polymorphic((Connection::class)) {
            subclass(AkkaConnection::class)
            subclass(HttpConnection::class)
            subclass(TcpConnection::class)
        }

         */
        /*
        polymorphic(Location::class) {
            subclass(AkkaLocation::class)
            subclass(HttpLocation::class)
            subclass(TcpLocation::class)
        }

         */
         polymorphic(TrackingEvent::class) {
            subclass(LocationUpdated::class)
            subclass(LocationRemoved::class)
        }
     }

    val modelFormat = Json {
        serializersModule = regmodule
        classDiscriminator = "_type"
        prettyPrint = true
    }

    fun regToJson(reg: Registration): String =
        modelFormat.encodeToString(Registration.serializer(), reg)

    fun regToJsonElement(reg: Registration): JsonElement =
        modelFormat.encodeToJsonElement(Registration.serializer(), reg)

    fun jsonToReg(json: String): Registration =
        modelFormat.decodeFromString(Registration.serializer(), json)

    fun jsonObjectToString(jobj: JsonObject): String =
        modelFormat.encodeToString(JsonObject.serializer(), jobj)

    fun connectionToJson(conn: Connection): String =
        modelFormat.encodeToString(Connection.serializer(), conn)

    fun connectionToJsonElement(conn: Connection): JsonElement =
        modelFormat.encodeToJsonElement(Connection.serializer(), conn)

    fun jsonToConnection(json: String): Connection =
        modelFormat.decodeFromString(Connection.serializer(), json)

    fun jsonToConnections(json: String): List<Connection> =
        modelFormat.decodeFromString(ListSerializer(Connection.serializer()), json)

    fun locationToJson(location: Location): String =
        modelFormat.encodeToString(Location.serializer(), location)

    fun jsonToLocation(json: String): Location =
        modelFormat.decodeFromString(Location.serializer(), json)

    fun jsonToLocations(json: String): List<Location> {
        //val stringListSerializer: KSerializer<List<Location>> = ListSerializer(Location.serializer())
        return modelFormat.decodeFromString(ListSerializer(Location.serializer()), json)
    }

    fun locationsToJson(locs: List<Location>): String =
        modelFormat.encodeToString(ListSerializer(Location.serializer()), locs)

    fun trackingToJson(event: TrackingEvent): String =
        modelFormat.encodeToString(TrackingEvent.serializer(), event)

    object ConnectionSerializer : KSerializer<Connection> {
        override val descriptor: SerialDescriptor = ConnectionInfo.serializer().descriptor
        override fun serialize(encoder: Encoder, value: Connection) {
            val surrogate = ConnectionInfo(value.prefix, value.componentId.componentType, value.connectionType)
            encoder.encodeSerializableValue(ConnectionInfo.serializer(), surrogate)
        }

        override fun deserialize(decoder: Decoder): Connection {
            val surrogate = decoder.decodeSerializableValue(ConnectionInfo.serializer())
            return Connection.from(surrogate)
        }
    }

    object NetworkSerializer : KSerializer<NetworkType> {
        @Serializable
        @SerialName("networkType")
        private class NetworkSurrogate(val _type: String)

        override val descriptor: SerialDescriptor = NetworkSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: NetworkType) {
            val surrogate = NetworkSurrogate(value.toString())
            encoder.encodeSerializableValue(NetworkSurrogate.serializer(), surrogate)
        }

        override fun deserialize(decoder: Decoder): NetworkType {
            val surrogate = decoder.decodeSerializableValue(NetworkSurrogate.serializer())
            return NetworkType.valueOf(surrogate._type)
        }
    }

    object URISerializer : KSerializer<URI> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("URI") {}

        override fun serialize(encoder: Encoder, value: URI) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): URI {
            return URI(decoder.decodeString())
        }
    }
}
