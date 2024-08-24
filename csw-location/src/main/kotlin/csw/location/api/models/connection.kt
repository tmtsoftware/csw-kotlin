package csw.location.api.models

import csw.location.api.codecs.ModelCodecs.ConnectionSerializer
import csw.params.core.models.Prefix
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a connection based on a componentId and the type of connection offered by the component
 */
@Serializable(with= ConnectionSerializer::class)
sealed interface Connection {

    /**
     * The component that is providing this connection
     */
    val componentId: ComponentId

    /**
     * connectionType represents a type of connection offered by the Component
     */
    val connectionType: ConnectionType

    /**
     * Returns a ConnectionInfo which represents component name, component type and connection type for this Connection
     */
    fun connectionInfo(): ConnectionInfo =
        ConnectionInfo(componentId.prefix, componentId.componentType, connectionType)

    /**
     * Creates a unique name for Connection based on Component name, ComponentType and ConnectionType
     */
    val name: String
        get() = connectionInfo().toString()

    /**
     * Represents the fully qualified component name along with the subsystem for e.g. tcs.filter.wheel
     */
    val prefix: Prefix
        get() = componentId.prefix

    /**
     * A helper method to cast this Connection to TypedConnection
     *
     * @tparam T A covariant of Location type that TypedConnection uses
     * @return A TypedConnection cast from this Connection
     */
    //fun of[T <: Location]: TypedConnection[T] = self.asInstanceOf[TypedConnection[T]]
    companion object {
        /**
         * Create a Connection from provided String input
         *
         * @param input is the string representation of connection e.g. TromboneAssembly-assembly-akka
         * @return a Connection model created from string
         */
        operator fun invoke(input: String): Connection {
            val items = input.split("-")

            return if (items.size == 3) {
                val name = items[0]
                val componentType = ComponentType(items[1])
                val connectionType = ConnectionType(items[2])
                from(ConnectionInfo(Prefix(name), componentType, connectionType))
            } else
                throw IllegalArgumentException("Unable to parse '$input' to make Connection object")
        }

        /**
         * Create a Connection from provided ConnectionInfo
         *
         * @param connectionInfo represents component name, component type and connection type
         * @return A Connection created from connectionInfo
         */
        fun from(connectionInfo: ConnectionInfo): Connection =
            from(ComponentId(connectionInfo.prefix, connectionInfo.componentType), connectionInfo.connectionType)

        private fun from(componentId: ComponentId, connectionType: ConnectionType): Connection =
            when (connectionType) {
                ConnectionType.AkkaType -> AkkaConnection(componentId, ConnectionType.AkkaType)
                ConnectionType.TcpType -> TcpConnection(componentId, ConnectionType.TcpType)
                ConnectionType.HttpType -> HttpConnection(componentId, ConnectionType.HttpType)
            }
    }
}

/**
 * Represents a connection offered by remote Actors e.g. nfiraos.TromboneAssembly-assembly-akka or nfiraos.TromboneHcd-hcd-akka
 */
@Serializable
@SerialName("akka")
class AkkaConnection(override val componentId: ComponentId,
                     override val connectionType: ConnectionType = ConnectionType.AkkaType): Connection {
    override fun toString(): String = "AkkaConnection(componentId=$componentId, connectionType=$connectionType)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AkkaConnection
        if (componentId != other.componentId) return false
        if (connectionType != ConnectionType.AkkaType) return false
        return true
    }

    override fun hashCode(): Int {
        var result = componentId.hashCode()
        result = 31 * result + connectionType.hashCode()
        return result
    }
}
/**
 * Represents a http connection provided by the component e.g. csw.ConfigServer-service-http
 */
@Serializable
@SerialName("http")
class HttpConnection(override val componentId: ComponentId,
                     override val connectionType: ConnectionType = ConnectionType.HttpType): Connection {
    override fun toString(): String = "HttpConnection(componentId: $componentId, connectionType: $connectionType)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HttpConnection
        if (componentId != other.componentId) return false
        if (connectionType != ConnectionType.HttpType) return false
        return true
    }
    override fun hashCode(): Int {
        var result = componentId.hashCode()
        result = 31 * result + connectionType.hashCode()
        return result
    }
}

/**
 * represents a tcp connection provided by the component e.g. csw.EventService-service-tcp
 */
@Serializable
@SerialName("tcp")
data class TcpConnection(override val componentId: ComponentId,
                         override val connectionType: ConnectionType = ConnectionType.TcpType): Connection {
    override fun toString(): String = "TcpConnection(componentId: $componentId, connectionType: $connectionType)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TcpConnection
        if (componentId != other.componentId) return false
        if (connectionType != ConnectionType.TcpType) return false
        return true
    }

    override fun hashCode(): Int {
        var result = componentId.hashCode()
        result = 31 * result + connectionType.hashCode()
        return result
    }
}