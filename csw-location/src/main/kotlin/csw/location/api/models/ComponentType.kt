package csw.location.api.models

/**
 * Represents a type of the Component. It should be serializable since it has to be transmittable over the network.
 * The type will always be represented in lower case.
 *
 * @param messageManifest represents the class name of message that a component will understand
 */
enum class ComponentType(private val messageManifest: String) {

    /**
     * Represents a container for components e.g. assemblies and HCDs
     */
    Container("ContainerMessage"),

    /**
     * Represents a component that controls a hardware device
     */
    HCD("ComponentMessage"),

    /**
     * Represents a component that controls one or more HCDs or assemblies
     */
    Assembly("ComponentMessage"),

    /**
     * Represents a component that controls one or more assemblies or sequencers
     */
    Sequencer("SequencerMsg"),

    /**
     * Represents a sequence component e.g ocs_1, iris_1
     */
    SequenceComponent("SequenceComponentMsg"),

    /**
     * Represents a general purpose service component e.g. actor and/or web service application
     */
    Service("ServiceAPI"),

    /**
     * Represents a Machine
     */
    Machine("");

    companion object {
        // The following is convoluted to handle case on input, be careful adding new ones
        operator fun invoke(ctIn: String): ComponentType =
            when (ctIn.lowercase()) {
                "container" -> Container
                "hcd" -> HCD
                "assembly" -> Assembly
                "sequencer" -> Sequencer
                "sequencecomponent" -> SequenceComponent
                "service" -> Service
                "machine" -> Machine
                else -> { throw IllegalArgumentException("ComponentType $ctIn not supported") }
            }

    }
}