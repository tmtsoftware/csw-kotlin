@file:UseSerializers(
    OptionSerializer::class
)

package csw.params.commands

import arrow.core.Option
import arrow.core.serialization.OptionSerializer
import csw.params.core.models.ObsId
import csw.params.core.models.Prefix
import csw.params.keys.HasKey
import csw.params.keys.IsKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

typealias CommandName = String
//value class CommandName(val name: String)

sealed interface Command : PrefixData {

    /**
     * Prefix representing source of the command
     */
    val source: Prefix

    /**
     * Provided for filtering
     */
    override fun prefix() = source

    /**
     * The name of command
     */
    val commandName: CommandName

    /**
     * An optional obsId for command
     */
    val maybeObsId: Option<ObsId> // Note: "maybeObsId" is part of csw JSON contract
}


/**
 * Marker trait for sequence parameter sets which is applicable to Sequencer type of components
 */
sealed interface SequenceCommand : Command

/**
 * Marker trait for control parameter sets which i applicable to Assembly and HCD type of components
 */
sealed interface ControlCommand : SequenceCommand

@Serializable
data class Setup(
    override val source: Prefix,
    override val commandName: CommandName,
    override val maybeObsId: Option<ObsId>,
    override var paramSet: List<HasKey> = emptyList(),
) : HasParms, ControlCommand {
    

    override fun toString(): String = "Setup(source=$source, cmdName=$commandName, obsId=$maybeObsId, ${paramSet})"

    fun add(item1: HasKey, vararg items: HasKey): Setup =
        copy(paramSet = padd(this.paramSet, listOf(item1) + items.toList()))

    fun madd(parmsToAdd: ParmsList): Setup = copy(paramSet = padd(paramSet, parmsToAdd))

    fun madd(vararg items: HasKey): Setup = copy(paramSet = padd(this.paramSet, items.toList()))

    fun remove(item: IsKey): Setup = copy(paramSet = removeOne(this.paramSet, item))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Setup) return false
        // Compares properties for structural equality
        if (this.source != other.source) return false
        if (this.commandName != other.commandName) return false
        return this.source == other.source && this.commandName == other.commandName &&
                this.paramSet.containsAll(other.paramSet) && other.paramSet.containsAll(paramSet)
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + commandName.hashCode()
        result = 31 * result + paramSet.hashCode()
        return result
    }
}

@Serializable
data class Observe(
    override val source: Prefix,
    override val commandName: CommandName,
    override val maybeObsId: Option<ObsId>,
    override var paramSet: List<HasKey> = emptyList()
) : HasParms, ControlCommand {
    

    override fun toString(): String = "Observe(source=$source, cmdName=$commandName, obsId=$maybeObsId, ${paramSet})"

    fun add(item1: HasKey, vararg items: HasKey): Observe =
        copy(paramSet = padd(this.paramSet, items.toList() + item1))

    fun madd(parmsToAdd: ParmsList): Observe = copy(paramSet = padd(paramSet, parmsToAdd))

    fun madd(vararg items: HasKey): Observe = copy(paramSet = padd(this.paramSet, items.toList()))

    fun remove(item: IsKey): Observe = copy(paramSet = removeOne(this.paramSet, item))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Setup) return false

        // Compares properties for structural equality
        return this.source == other.source && this.commandName == other.commandName &&
                this.paramSet.containsAll(other.paramSet) && other.paramSet.containsAll(paramSet)
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + commandName.hashCode()
        result = 31 * result + paramSet.hashCode()
        return result
    }
}

@Serializable
data class Wait(
    override val source: Prefix,
    override val commandName: CommandName,
    override val maybeObsId: Option<ObsId>,
    override var paramSet: List<HasKey> = emptyList()
) : HasParms, SequenceCommand {
    

    override fun toString(): String = "Observe(source=$source, cmdName=$commandName, obsId=$maybeObsId, ${paramSet})"

    fun add(item1: HasKey, vararg items: HasKey): Wait =
        copy(paramSet = padd(this.paramSet, items.toList() + item1))

    fun madd(parmsToAdd: ParmsList): Wait = copy(paramSet = padd(paramSet, parmsToAdd))

    fun madd(vararg items: HasKey): Wait = copy(paramSet = padd(this.paramSet, items.toList()))

    fun remove(item: IsKey): Wait = copy(paramSet = removeOne(this.paramSet, item))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Setup) return false

        // Compares properties for structural equality
        return this.source == other.source && this.commandName == other.commandName &&
                this.paramSet.containsAll(other.paramSet) && other.paramSet.containsAll(paramSet)
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + commandName.hashCode()
        result = 31 * result + paramSet.hashCode()
        return result
    }
}
