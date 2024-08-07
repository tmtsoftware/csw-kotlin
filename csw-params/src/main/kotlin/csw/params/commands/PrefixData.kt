package csw.params.commands

import csw.params.core.models.Prefix
import csw.params.core.models.Subsystem


/**
 * A trait to be mixed in that provides a parameter set and prefix info
 */
interface PrefixData {

    /**
     * Returns an object providing the subsystem and prefix for the parameter set
     */
    fun prefix(): Prefix

    /**
     * The subsystem for the parameter set
     */
    val subsystem: Subsystem
        get() = prefix().subsystem

    /**
     * The prefix for the parameter set
     */
    val prefixStr: String
        get() = prefix().toString()

    /**
     * A String representation for concrete implementation of this trait
     */
    //override fun toString(): String = "$typeName([$prefix]$dataToString)"
}
