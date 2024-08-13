package csw.location.api.models

import csw.params.core.models.Prefix

/**
 * ConnectionInfo represents a component name, component type and connection type
 *
 * @param prefix represents a prefix of a component e.g. nfiraos.TromboneAssembly
 * @param componentType represents the type of component e.g. Assembly, HCD, etc
 * @param connectionType represents the type of connection e.g. akka, http, tcp
 */
data class ConnectionInfo(val prefix: Prefix, val componentType: ComponentType, val connectionType: ConnectionType) {
    override fun toString(): String = "$prefix-${componentType.name}-${connectionType.cname}"
}
