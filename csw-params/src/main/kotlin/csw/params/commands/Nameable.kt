package csw.params.commands

import csw.params.states.StateName


interface Nameable<T> {
    fun name(state: T): StateName
}
