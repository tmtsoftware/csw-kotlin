package csw.location.api.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents a type of connection offered by the Component
 *
 * @param entryName A name of the connection type e.g. akka, http or tcp
 */
@Serializable(with = ConnectionTypeSerializer::class)
enum class ConnectionType(val cname: String) {

    /**
     * Represents a HTTP type of connection
     */
    HttpType("http"),

    /**
     * Represents a TCP type of connection
     */
    TcpType("tcp"),

    /**
     * Represents an Akka type of connection
     */
    AkkaType("akka");

    companion object {
        operator fun invoke(name: String): ConnectionType {
            for (entry in entries) {
                if (entry.cname == name) return entry
            }
            throw IllegalArgumentException("No connection type with name: $name")
        }
    }
}

object ConnectionTypeSerializer : KSerializer<ConnectionType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("connectionType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ConnectionType) {
        val string = value.cname
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): ConnectionType {
        val string = decoder.decodeString()
        return ConnectionType(string)
    }
}