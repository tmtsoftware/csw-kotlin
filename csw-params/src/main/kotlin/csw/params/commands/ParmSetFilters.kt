package csw.params.commands

import csw.params.keys.Key

/**
 * A collection of Utility functions for filtering Commands from an input sequence
 */
object ParmSetFilters {

    /**
     * Returns unique prefixes in their entirety from the provided parameter list
     *
     * @param items a List of HasKey
     * @return a Set of unique Keys
     */
    fun keys(items: ParmsList): Set<Key> = items.map { it.name }.toSet()

    /**
     * Returns the List of final members of all key names in ParmsList
     *
     * @param parms a List of HasKey called a ParmsList
     * @return a list of Strings which are the keys with only final part
     */
    fun finalKeyNames(items: ParmsList): List<String> = items.map { it.name.split('.').last() }

    /**
     * Returns the Set of unique paths/roots up to final name.
     * If a simple name with no ".", the name is returned.
     *
     * @param parms a List of HasKey called a ParmsList
     * @return a list of Strings which are the keys with only final part
     */
    fun keyRoots(items: ParmsList): Set<String> =
        items.map { splitKey( it.name ).first }.toSet()

    /** Internal function that returns a pair of Strings where first is the root of the key, and second is the last name **/
    internal fun splitKey(key: Key): Pair<String, String> {
        with (key) {
            val lastDelim = lastIndexOf('.')
            val first = if (lastDelim == -1) key else substring(0, lastDelim)
            val last = substring(lastDelim+1)
            return Pair(first, last)
        }
    }

    /**
     * Returns a List of the keys that start with and match the query string.
     *
     * @param parms a List of HasKey called a ParmsList
     * @return a list of keys as Strings that match the query
     */
    fun startWith(query: String, items: ParmsList): List<String> =
        items.filter { it.name.startsWith(query) }.map { it.name }

    /**
     * Returns the set of final members of a key names
     *
     * @param parms a List of HasKey called a ParmsList
     * @return a list of keys as Strings that match the query
     */
    fun keysStartExactly(query: String, items: ParmsList): List<String> =
        items.filter { splitKey(it.name).first contentEquals query }.map { it.name }

}
