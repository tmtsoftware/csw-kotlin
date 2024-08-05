package csw.params.keys

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import csw.params.commands.HasParms

import kotlinx.serialization.Serializable

typealias Choice = String


data class ChoiceSet(val values: Set<Choice>) {
    /**
     * Creates Choices from provided String values
     *
     * @param choices one or more choices in string format
     * @return an instance of Choices
     */
    constructor(vararg choices: String): this(choices.toSet())

    companion object {
        /**
         * Creates Choices from provided values
         *
         * @param choices one or more choices
         * @return an instance of Choices
         */
        fun fromChoices(vararg choices: Choice): ChoiceSet = ChoiceSet(choices.toSet())
    }
}

@Serializable
data class ChoiceStore(override val name: Key, val choice: String, val choices: String): HasKey {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ChoiceStore
        if (name != other.name) return false
        if (choice != other.choice) return false
        if (choices != other.choices) return false
        return true
    }
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + choice.hashCode()
        result = 31 * result + choices.hashCode()
        return result
    }
}


data class ChoiceKey(override val name: String, val choices: ChoiceSet): IsKey {

    fun set(value: Choice, vararg values: Choice): ChoiceStore {
        val all = ChoiceSet(values.toSet() + value)
        validate(all)
        return ChoiceStore(name, cencode(all), cencode(choices))
    }

    fun get(target: HasParms): Option<Choice> {
        val s: HasKey? = target.nget(name)
        return if (s is ChoiceStore) Some(s.choice) else None
    }

    fun isIn(target: HasParms): Boolean = target.exists(this)

    operator fun invoke(target: HasParms): Choice = get(target).getOrElse { throw NoSuchElementException("The key is missing from the command.") }
    fun value(target: HasParms): Choice = invoke(target)
    fun head(target: HasParms): Choice = value(target)

    private fun cencode(cs: ChoiceSet): String = cs.values.joinToString(",") { it }
    private fun validate(possibles: ChoiceSet) {
        possibles.values.forEach {
            if (!choices.values.contains(it))
                throw IllegalArgumentException("Choice \"$it\" not within the key's choice set: ${choices.values}.")
        }
    }

    // For compatibility
    companion object {
        fun make(name: Key, values: ChoiceSet): ChoiceKey = ChoiceKey(name, values)
    }

}

