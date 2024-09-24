package csw.params.keys

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import csw.params.commands.HasParms

data class Struct(override val name: Key, override var paramSet: List<HasKey>): HasParms, HasKey {
    

    companion object {
        internal fun getStored(name: Key, target: HasParms): Option<Struct> {
            val s: HasKey? = target.nget(name)
            return if (s is Struct) Option(s) else None
        }
    }

}

data class StructOut(val keys: List<Key>)

typealias toV = (IsKey) -> HasKey

class StructKey private constructor(override val name: Key, val keys: List<IsKey>): IsKey {

    data class StStore(override val name: Key, val value: String): HasKey

    fun setStruct(vararg setResults: HasKey): HasKey {
        for (sr in setResults) {
            println("Sr: $sr")
        }
        return StStore(name, "TEST")
    }


    fun add(item1: HasKey, vararg items: HasKey): Struct {
        val keyList = (arrayOf(item1) + items).toList()

        for (k in keyList) {
            val x = keyList.firstOrNull { it.name == k.name }
            if (x == null) {
                throw NoSuchElementException("The key: ${k.name}, is not in the allowed key set.")
            }
        }
        val s = Struct(name, keyList)
        return s
    }

    fun get(target: HasParms, sname: Key): Option<Struct> {
        val x = Struct.getStored(name, target)
        println("X: $x")
        return x
    }





    override fun toString() = "StructKey(name=$name, keys=$keys)"


    companion object {

        operator fun invoke(name: Key, vararg keys: IsKey) = StructKey(name, keys.toList())

    }

}