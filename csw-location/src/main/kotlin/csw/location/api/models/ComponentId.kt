package csw.location.api.models

import csw.params.core.models.Prefix
import kotlinx.serialization.Serializable

/**
 * Represents a component based on its prefix and type.
 *
 * @note Prefix should not contain
 *  - leading or trailing spaces
 *  - and hyphen (-)
 *  @param prefix represents the prefix (subsystem and name) of the component e.g. tcs.filter.wheel
 *  @param componentType represents a type of the Component e.g. Assembly, HCD, Sequencer etc
 */
@Serializable
data class ComponentId(val prefix: Prefix, val componentType: ComponentType) {

    /**
     * Represents the name and componentType
     */
    val fullName: String = "$prefix-${componentType.name}"
}
