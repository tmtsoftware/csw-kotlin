package csw.params.commands

import csw.params.keys.HasKey


/**
 * A result containing parameters for command response
 */
data class Result (override var parms: List<HasKey> = emptyList()): HasParms {

    fun nonEmpty(): Boolean = parms.isNotEmpty()

    fun add(item1: HasKey, vararg items: HasKey): Result =
        copy(parms = padd(this.parms, items.toList() + item1))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Result) return false

        // Compares properties for structural equality return
        return this.parms.containsAll(other.parms) && other.parms.containsAll(parms)
    }

    /**
     * A String representation for concrete implementation of this trait
     */
    override fun toString(): String = "Result($parms)"

    companion object {

        fun emptyResult() = Result()

        /**
         * A helper method to create Result instance
         *
         * @param paramSet a Set of parameters (keys with values)
         * @return a Result instance with provided paramSet
         */
        operator fun invoke(parms: List<HasKey>): Result = Result(parms)

        /**
         * A helper method to create Result instance
         *
         * @param params an optional list of parameters (keys with values)
         * @return a Result instance with provided paramSet
         */
        //def apply(params: Parameter[_]*): Result = Result(params.toSet)
    }
}
