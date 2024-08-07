package csw.params.commands

import csw.params.core.models.Subsystem

/**
 * A collection of Utility functions for filtering Commands from an input sequence
 */
object PrefixDataFilters {

    /**
     * Returns the Set of (unique) items implementing PrefixData in the provided items list
     *
     * @param items a List of objects that implement PrefixData
     * @return a Set of prefixes
     */
    fun prefixes(items: List<PrefixData>): Set<String> = items.map { it.prefixStr }.toSet()

    /**
     * Gives only Setup type of commands from given Seq of SequenceCommand
     *
     * @param sequenceCommands a Seq of SequenceCommand
     * @return a Seq of Setup commands
     */
    fun onlySetups(sequenceCommands: List<SequenceCommand>): List<Setup> = sequenceCommands.filterIsInstance<Setup>()

    /**
     * Gives only Observe type of commands from given Seq of SequenceCommand
     *
     * @param sequenceCommands a Seq of SequenceCommand
     * @return a Seq of Observe commands
     */
    fun onlyObserves(sequenceCommands: List<SequenceCommand>): List<Observe> = sequenceCommands.filterIsInstance<Observe>()

    /**
     * Gives only Wait type of commands from given Seq of SequenceCommand
     *
     * @param sequenceCommands a Seq of SequenceCommand
     * @return a Seq of Wait commands
     */
    fun onlyWaits(sequenceCommands: List<SequenceCommand>): List<Wait> = sequenceCommands.filterIsInstance<Wait>()

    /**
     * Returns a list of PrefixData for which the prefix starts with given query string
     *
     * @param query a String to which the prefix is matched at the beginning
     * @param items a List of items implementing PrefixData
     * @return a filtered List of items implementing PrefixData that match the startsWith query
     */
    fun prefixStartsWith(query: String, items: List<PrefixData>): List<PrefixData> =
        items.filter { it.prefixStr.startsWith(query) }

    /**
     * Returns a list of PrefixData for which the prefix contains the given query string
     *
     * @param query a String to which the prefix is matched anywhere in the word
     * @param items a List of items implementing PrefixData
     * @return a filtered List of items implementing PrefixData that match the contains query
     */
    fun prefixContains(query: String, items: List<PrefixData>): List<PrefixData> =
        items.filter { it.prefixStr.contains(query) }

    /**
     * Returns a list of PrefixData that have the same subsystem as the query Subsystem
     *
     * @param query a Subsystem that should be used to match the items
     * @param items a List of items implementing PrefixData
     * @return a filtered List of items implementing PrefixData that have the provided Subsystem
     */
    fun prefixIsSubsystem(query: Subsystem, items: List<PrefixData>): List<PrefixData> =
        items.filter { it.subsystem == query }

}
