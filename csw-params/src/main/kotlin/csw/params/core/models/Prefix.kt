package csw.params.core.models

import kotlinx.serialization.Serializable

/**
 * A top level key for a parameter set: combination of subsystem and the subsystem's prefix
 * e.g. tcs.filter.wheel, wfos.prog.cloudcover, etc
 *
 * @note Component name should not contain
 *  - leading or trailing spaces
 *  - and hyphen (-)
 * @param subsystem     component subsystem - tcs (TCS), wfos (WFOS)
 * @param componentName component name - filter.wheel, prog.cloudcover
 */
@Serializable
data class Prefix(val subsystem: Subsystem, val componentName: String) {

    init {
        require(componentName == componentName.trim()) { "A component name should not have leading or trailing whitespaces" }

        require(!componentName.contains("-")) { "A component name cannot contain a '-'" }
    }

    /**
     * String representation of prefix e.g. tcs.filter.wheel where tcs is the subsystem name and filter.wheel is the component name
     */
    override fun toString(): String = "${subsystem.name}$SEPARATOR$componentName"

    companion object {
        private val SEPARATOR = "."

        /**
         * Creates a Prefix based on the given value of format tcs.filter.wheel and splits it to have tcs as `subsystem` and filter.wheel
         * as `componentName`
         *
         * @param value of format tcs.filter.wheel
         * @return a Prefix instance
         */
        operator fun invoke(value: String): Prefix {
            require(value.contains(SEPARATOR)) { "prefix must have a '$SEPARATOR' separator" }
            val items = value.split(Regex("\\$SEPARATOR"), 2)  // Limit is important!  Allows, TCS.A.B.C
            require(items.size == 2) { println("Prefix must equal exactly 2 items") }
            val subsystem = Subsystem(items[0])
            val componentName = items[1]
            return Prefix(subsystem, componentName)
        }
    }
}
