package csw.params.keys

import kotlinx.serialization.Serializable

/**
 * Interface implemented by anything stored in a ParmList
 */
@Serializable
sealed interface HasKey {
    val name: Key
}

/**
 * Interface implemented by any Key
 */
@Serializable
sealed interface IsKey {
    val name: Key
}

typealias Key = String