package csw.csw.location.api.models

import arrow.core.MemoizedDeepRecursiveFunction
import csw.location.api.models.*
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

    /**
     * metadata represents any additional information (metadata) associated with registration
     */
    val metadata: Metadata

    fun withCswVersion(version: String): Registration
}

/**
 * AkkaRegistration holds the information needed to register an akka location
 *
 * @param connection the `Connection` to register with `LocationService`
 * @param actorRefURI Provide a remote actor uri that is offering a connection. Local actors cannot be registered since they can't be
 *                 communicated from components across the network
 * @param metadata represents additional metadata information associated with location. Defaulted to empty if not provided.
 */
internal data class AkkaRegistration (
    override val connection: AkkaConnection,
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
final data class TcpRegistration(override val connection: TcpConnection, val port: Int, override val metadata: Metadata): Registration {

    //Used for JAVA API
//    def this(connection: TcpConnection, port: Int) = this(connection, port, Metadata.empty)

    /**
     * Create a TcpLocation that represents the live Tcp service
     *
     * @param hostname provide the hostname where Tcp service is available
     * @return an TcpLocation location representing a live connection at provided hostname
     */
    override fun location(hostname: String): Location = TcpLocation(connection, URI("tcp://$hostname:$port"), metadata)

    override fun withCswVersion(version: String): TcpRegistration = this.copy(metadata = metadata.withCSWVersion(version))

    companion object {
        operator fun invoke(connection: TcpConnection, port: Int): TcpRegistration = TcpRegistration(connection, port, Metadata.empty)
    }
}

/**
 * HttpRegistration holds information needed to register a Http service
 *
 * @param port provide the port where Http service is available
 * @param path provide the path to reach the available http service
 * @param metadata represents additional metadata information associated with location. Defaulted to empty if not provided.
 */
data class HttpRegistration(
    override val connection: HttpConnection,
    val port: Int,
    val path: String,
    val networkType: NetworkType,
    override val metadata: Metadata
): Registration {

    //Used for JAVA API
    //def this(connection: HttpConnection, port: Int, path: String, metadata: Metadata) =
//        this(connection, port, path, NetworkType.Inside, metadata)

    //  def this(connection: HttpConnection, port: Int, path: String) =
//        this(connection, port, path, NetworkType.Inside, Metadata.empty)

    //  def this(connection: HttpConnection, port: Int, path: String, networkType: NetworkType) =
//        this(connection, port, path, networkType, Metadata.empty)

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
