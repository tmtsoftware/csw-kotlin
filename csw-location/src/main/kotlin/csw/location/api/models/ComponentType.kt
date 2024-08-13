package csw.location.api.models

/**
 * Represents a type of the Component. It should be serializable since it has to be transmittable over the network.
 * The type will always be represented in lower case.
 *
 * @param messageManifest represents the class name of message that a component will understand
 */
enum class ComponentType(private val messageManifest: String) {

    /**
     * Returns a sequence of all component types
     */
    //def values: IndexedSeq[ComponentType] = findValues

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
    Machine("")
}