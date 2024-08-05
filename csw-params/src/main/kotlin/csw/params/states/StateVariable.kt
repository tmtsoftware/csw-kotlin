package csw.params.states

import csw.params.commands.HasParms
import csw.params.commands.Nameable
import csw.params.commands.ParmsList
import csw.params.core.models.Prefix
import csw.params.keys.HasKey
import csw.params.keys.IsKey


typealias StateName = String

//typeAlias Matcher = (DemandState, CurrentState) => Boolean

/**
 * Base trait for state variables
 */
sealed interface StateVariable {

    /**
     * identifies the target subsystem
     */
    val prefix: Prefix

    /**
     * identifies the name of the state
     */
    val stateName: StateName

    /**
     * A name identifying the type of command, such as "setup", "observe".
     * This is used in the JSON and toString output.
     */
    val typeName: String

    /**
     * A common toString method for all concrete implementation
     *
     * @return the string representation of command
     */
    //override fun toString(): String = "$typeName(source=$prefix, stateName=$stateName, paramSet=$parms)"

    companion object {
        /**
         * The default matcher for state variables tests for an exact match
         *
         * @param demand the demand state
         * @param current the current state
         * @return true if the demand and current states match (in this case, are equal)
         */
        fun defaultMatcher(demand: DemandState, current: CurrentState): Boolean =
            demand.stateName == current.stateName && demand.prefix == current.prefix && demand.parms == current.parms
    }

    /**
     * A state variable that indicates the ''demand'' or requested state.
     *
     * @param prefix identifies the target subsystem
     * @param stateName identifies the name of the state
     * @param parms initial set of items (keys with values)
     */
    data class DemandState private constructor (
        override val prefix: Prefix,
        override val stateName: StateName,
        override var parms: List<HasKey> = emptyList()
    ) : HasParms,
        StateVariable {


        /**
         * A Java helper method to create a DemandState from a Setup
         */
        //fun constructor(stateName: StateName, command: Setup) = this(command.source, stateName, command.paramSet)

        fun add(item1: HasKey, vararg items: HasKey): DemandState =
            copy(parms = padd(this.parms, listOf(item1) + items.toList()))

        fun madd(parmsToAdd: ParmsList): DemandState = copy(parms = padd(parms, parmsToAdd))

        fun madd(vararg items: HasKey): DemandState = copy(parms = padd(this.parms, items.toList()))

        fun remove(item: IsKey): DemandState = copy(parms = removeOne(this.parms, item))

        override val typeName = "DemandState"

        companion object {

            /**
             * A helper method to create DemandState
             *
             * @param prefix identifies the target subsystem
             * @param stateName identifies the name of the state
             * @param parms an optional initial set of items (keys with values)
             * @return an instance of DemandState
             */
            operator fun invoke(prefix: Prefix, stateName: StateName, parms: List<HasKey> = emptyList()): DemandState =
                DemandState(prefix, stateName).madd(parms)
        }
    }

    /**
     * A state variable that indicates the ''current'' or actual state.
     *
     * @param prefix       identifies the target subsystem
     * @param stateName identifies the name of the state
     * @param parms     an optional initial set of items (keys with values)
     */
    data class CurrentState private constructor (
        override val prefix: Prefix, override val stateName: StateName,
        override var parms: List<HasKey> = emptyList()
    ) : HasParms, StateVariable {

        fun add(item1: HasKey, vararg items: HasKey): CurrentState =
            copy(parms = padd(this.parms, listOf(item1) + items.toList()))

        fun madd(parmsToAdd: ParmsList): CurrentState = copy(parms = padd(parms, parmsToAdd))

        fun madd(vararg items: HasKey): CurrentState = copy(parms = padd(this.parms, items.toList()))

        fun remove(item: IsKey): CurrentState = copy(parms = removeOne(this.parms, item))

        override val typeName = "CurrentState"

        companion object {

            /**
             * A helper method to create CurrentState
             *
             * @param prefix identifies the target subsystem
             * @param stateName identifies the name of the state
             * @param parms an optional initial set of items (keys with values)
             * @return an instance of CurrentState
             */
            operator fun invoke(
                prefix: Prefix,
                stateName: StateName,
                parms: List<HasKey> = emptyList()
            ): CurrentState = CurrentState(prefix, stateName).madd(parms)

            object NameableCurrentState : Nameable<CurrentState> {
                override fun name(state: CurrentState): StateName = state.stateName
            }
        }
    }
}