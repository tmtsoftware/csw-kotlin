package csw.location.api.models

import csw.params.core.models.Prefix
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ConnectionInfo represents a component name, component type and connection type
 *
 * @param prefix represents a prefix of a component e.g. nfiraos.TromboneAssembly
 * @param componentType represents the type of component e.g. Assembly, HCD, etc
 * @param connectionType represents the type of connection e.g. akka, http, tcp
 */
@Serializable
@SerialName("connection")
data class ConnectionInfo(val prefix: Prefix, val componentType: ComponentType, val connectionType: ConnectionType) {
    override fun toString(): String = "$prefix-${componentType.name}-${connectionType.cname}"

    companion object {
        operator fun invoke(sprefix: String, scomponentType: String, sconnectionType: String): ConnectionInfo {
            val prefix = Prefix(sprefix)
            val componentType = ComponentType(scomponentType)
            val connectionType = ConnectionType(sconnectionType)
            return ConnectionInfo(prefix, componentType, connectionType)
        }
    }
}
