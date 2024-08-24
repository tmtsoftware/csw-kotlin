package csw.location.api.models

import csw.location.api.codecs.ModelCodecs.ConnectionSerializer
import csw.location.api.codecs.ModelCodecs.URISerializer
import csw.params.core.models.Prefix
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URI

/**
 * Location represents a live Connection along with its URI
 */
@Serializable
sealed interface Location /*extends LocationSerializable*/ {

    /**
     * Represents a connection based on a componentId and the type of connection offered by the component
     */
    val connection: Connection

    /**
     * Represents the URI of the component
     */
    val uri: URI

    /**
     * metadata represents any additional information (metadata) associated with location
     */
    val metadata: Metadata

    /**
     * Represents the fully qualified component name along with the subsystem for e.g. tcs.filter.wheel
     */
    val prefix: Prefix
        get() = connection.connectionInfo().prefix

}

/**
 * Represents a live Akka connection of an Actor
 *
 * @note Do not directly access actorRef from constructor, use one of component() or containerRef() method
 *       to get the correctly typed actor reference.
 * @param connection represents a connection based on a componentId and the type of connection offered by the component
 * @param uri represents the actor URI of the component. Gateway or router for a component that other components will resolve and talk to.
 * @param metadata represents additional metadata information associated with location. Defaulted to empty is not provided while registration
 */
@Serializable
@SerialName("AkkaLocation")
data class AkkaLocation(
    @Serializable(with= ConnectionSerializer::class)
    override val connection: AkkaConnection,
    @Serializable(with= URISerializer::class)
    override val uri: URI,
    override val metadata: Metadata
) : Location

/**
 * Represents a live Tcp connection
 *
 * @param connection represents a connection based on a componentId and the type of connection offered by the component
 * @param uri represents the remote URI of the component that other components will resolve and talk to
 * @param metadata represents additional metadata information associated with location. Defaulted to empty is not provided while registration.
 */
@Serializable
@SerialName("TcpLocation")
data class TcpLocation(
    @Serializable(with= ConnectionSerializer::class)
    override val connection: TcpConnection,
    @Serializable(with= URISerializer::class)
    override val uri: URI,
    override val metadata: Metadata
) : Location

/**
 * Represents a live Http connection
 *
 * @param connection represents a connection based on a componentId and the type of connection offered by the component
 * @param uri represents the remote URI of the component that other components will resolve and talk to
 * @param metadata represents additional metadata information associated with location. Defaulted to empty is not provided while registration.
 */
@Serializable
@SerialName("HttpLocation")
data class HttpLocation(
    @Serializable(with= ConnectionSerializer::class)
    override val connection: HttpConnection,
    @Serializable(with= URISerializer::class)
    override val uri: URI,
    override val metadata: Metadata
) : Location

