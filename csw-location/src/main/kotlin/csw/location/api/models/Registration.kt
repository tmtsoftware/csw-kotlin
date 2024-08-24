package csw.location.api.models

import csw.csw.location.api.models.NetworkType
import csw.location.api.codecs.ModelCodecs.ConnectionSerializer
import csw.location.api.codecs.ModelCodecs.NetworkSerializer
import csw.location.api.codecs.ModelCodecs.URISerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URI

/**
 * Registration holds information about a connection and its live location. This model is used to register a connection with LocationService.
 */
@Serializable
sealed interface Registration {

    /**
     * The `Connection` to register with `LocationService`
     */
    val connection: Connection

    /**
     * A location represents a live connection available for consumption
     *
     * @param hostname provide a hostname where the connection endpoint is available
     * @return a location representing a live connection at provided hostname
     */
    fun location(hostname: String): Location

    val connectionInfo: ConnectionInfo
        get() = ConnectionInfo(connection.componentId.prefix, connection.componentId.componentType, connection.connectionType)
    /**
     * metadata represents any additional information (metadata) associated with registration
     */
    val metadata: Metadata

    fun withCswVersion(version: String): Registration
}

// TODO: TBD if this is needed
//abstract sealed class TypedConnection<out T : Location>(connectionType: ConnectionType) : Connection(connectionType)

/**
 * AkkaRegistration holds the information needed to register an akka location
 *
 * @param connection the `Connection` to register with `LocationService`
 * @param actorRefURI Provide a remote actor uri that is offering a connection. Local actors cannot be registered since they can't be
 *                 communicated from components across the network
 * @param metadata represents additional metadata information associated with location. Defaulted to empty if not provided.
 */
@Serializable
@SerialName("AkkaRegistration")
data class AkkaRegistration (
    @Serializable(with=ConnectionSerializer::class)
    override val connection: AkkaConnection,
    @Serializable(with= URISerializer::class)
    val actorRefURI: URI,
    override val metadata: Metadata
): Registration {

    /**
     * Create a AkkaLocation that represents the live connection offered by the actor
     *
     * @param hostname provide a hostname where the connection endpoint is available
     * @return an AkkaLocation location representing a live connection at provided hostname
     */
    override fun location(hostname: String): Location = AkkaLocation(connection, actorRefURI, metadata)

    override fun withCswVersion(version: String): AkkaRegistration = this.copy(metadata = metadata.withCSWVersion(version))
}

/**
 * TcpRegistration holds information needed to register a Tcp service
 *
 * @param port provide the port where Tcp service is available
 * @param metadata represents additional metadata information associated with location. Defaulted to empty if not provided.
 */
@Serializable
@SerialName("TcpRegistration")
data class TcpRegistration(
    @Serializable(with=ConnectionSerializer::class)
    override val connection: TcpConnection,
    val port: Int,
    override val metadata: Metadata): Registration {

    /**
     * Create a TcpLocation that represents the live Tcp service
     *
     * @param hostname provide the hostname where Tcp service is available
     * @return an TcpLocation location representing a live connection at provided hostname
     */
    override fun location(hostname: String): Location =
        TcpLocation(connection, URI("tcp://$hostname:$port"), metadata)

    override fun withCswVersion(version: String): TcpRegistration =
        this.copy(metadata = metadata.withCSWVersion(version))

    companion object {
        operator fun invoke(connection: TcpConnection, port: Int): TcpRegistration =
            TcpRegistration(connection, port, Metadata.empty)
    }
}

/**
 * HttpRegistration holds information needed to register a Http service
 *
 * @param port provide the port where Http service is available
 * @param path provide the path to reach the available http service
 * @param metadata represents additional metadata information associated with location. Defaulted to empty if not provided.
 */
@Serializable
@SerialName("HttpRegistration")
data class HttpRegistration(
    @Serializable(with= ConnectionSerializer::class)
    override val connection: HttpConnection,
    val port: Int,
    val path: String,
    @Serializable(with= NetworkSerializer::class)
    val networkType: NetworkType,
    override val metadata: Metadata
): Registration {

    /**
     * Create a HttpLocation that represents the live Http service
     *
     * @param hostname provide the hostname where Http service is available
     */
    override fun location(hostname: String): Location =
        HttpLocation(connection, URI("http://$hostname:$port/$path"), metadata)

    override fun withCswVersion(version: String): HttpRegistration =
        this.copy(metadata = metadata.withCSWVersion(version))

    companion object {
        operator fun invoke(connection: HttpConnection, port: Int, path: String, metadata: Metadata): HttpRegistration =
            HttpRegistration(connection, port, path, NetworkType.Inside, metadata)

        operator fun invoke(connection: HttpConnection, port: Int, path: String, networkType: NetworkType): HttpRegistration =
            HttpRegistration(connection, port, path, networkType, Metadata.empty)

        operator fun invoke(connection: HttpConnection, port: Int, path: String): HttpRegistration =
            HttpRegistration(connection, port, path, NetworkType.Inside, Metadata.empty)
    }
}

