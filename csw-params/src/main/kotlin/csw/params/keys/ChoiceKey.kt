package csw.params.keys

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import csw.params.commands.HasParms

import kotlinx.serialization.Serializable

typealias Choice = String

data class Choices(val values: Set<Choice>) {
    /**
     * Creates Choices from provided String values
     *
     * @param choices one or more choices in string format
     * @return an instance of Choices
     */
    constructor(vararg choices: Choice): this(choices.toSet())

    companion object {
        /**
         * Creates Choices from provided values
         *
         * @param choices one or more choices
         * @return an instance of Choices
         */
        fun fromChoices(vararg choices: Choice): Choices = Choices(choices.toSet())
    }
}

@Serializable
data class ChoiceStore(override val name: Key, val choice: Array<String>, val units: Units): HasKey {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ChoiceStore
        if (name != other.name) return false
        if (!choice.contentDeepEquals(other.choice)) return false
        return true
    }
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + choice.hashCode()
        return result
    }
}


data class ChoiceKey(override val name: String, val choices: Choices, val units: Units = Units.NoUnits): IsKey {

    fun set(value: Choice, vararg values: Choice): ChoiceStore {
        val all = Choices((arrayOf(value) + values).toSet())
        validate(all)
        return ChoiceStore(name, KeyHelpers.aencode(all.values.toTypedArray()), units)
    }

    fun get(target: HasParms): Option<Choices> {
        val s: HasKey? = target.nget(name)
        return if (s is ChoiceStore) Some(Choices.fromChoices(*s.choice)) else None
    }

    fun isIn(target: HasParms): Boolean = target.exists(this)

    operator fun invoke(target: HasParms): Choices = get(target).getOrElse { throw NoSuchElementException("The key is missing from the command.") }
    fun value(target: HasParms): Choices = invoke(target)
    fun choices(target: HasParms): Choices = invoke(target)

    fun head(target: HasParms): Choice = value(target).values.first()
    fun choice(target: HasParms): Choice = head(target)

    private fun validate(possibles: Choices) {
        possibles.values.forEach {
            if (!choices.values.contains(it))
                throw IllegalArgumentException("Choice \"$it\" not within the key's choice set: ${choices.values}.")
        }
    }

    // For compatibility
    companion object {
        fun make(name: Key, values: Choices, units: Units = Units.NoUnits): ChoiceKey = ChoiceKey(name, values, units)
    }
}

