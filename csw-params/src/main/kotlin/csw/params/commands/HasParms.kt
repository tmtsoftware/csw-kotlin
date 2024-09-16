package csw.params.commands

import arrow.core.None
import arrow.core.Option
import arrow.core.tail
import arrow.core.toOption
import csw.params.keys.HasKey
import csw.params.keys.IsKey
import csw.params.keys.Key
import csw.params.keys.TestKeys.Qstore

typealias ParmsList = List<HasKey>

interface HasParms {

    var parms: ParmsList

    /**
     * Size property returns the number of entries in the command
     */
    val size: Int
        get() = parms.size

    fun exists(key: IsKey): Boolean = nget(key.name) != null

    operator fun contains(key: IsKey): Boolean = exists(key)

    fun keys(): List<Key> = parms.map { it.name }

    fun nget(name: Key): HasKey? = parms.firstOrNull { it.name == name }

    fun get(name: Key): Option<HasKey> = nget(name).toOption()

    /*
     * Adds all items to current, replacing if item already exists in current
     */
    fun padd(current: ParmsList, items: ParmsList): ParmsList =
        if (items.isEmpty()) current
        else padd(addOne(current, items.first()), items.tail())

    /**
     * Adds one item to current ParmsList, replacing if already present
     */
    private fun addOne(current: ParmsList, item: HasKey): ParmsList =
        removeKey(current, item.name) + item

    fun removeOne(current: ParmsList, item: IsKey): ParmsList =
        removeKey(current, item.name)

    private fun removeKey(current: ParmsList, item: Key): ParmsList =
        if (current.find { it.name == item} != null)
            current.filter { p -> p.name != item }
        else
            current

    /**
     * Returns a set containing the names keys that are missing from the provided keys
     *
     * @param keys one or more keys
     * @return a Set of key names
     */
    fun missingKeys(vararg keys: IsKey): Set<String> {
        val argKeySet        = keys.map { it.name }.toSet()
        val parmsKeys = parms.map { it.name }.toSet()
        return argKeySet - parmsKeys
    }
}

sealed interface HasVersion {
    val version: Version
}

typealias Version = Int