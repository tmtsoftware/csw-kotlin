package csw.params.commands

import arrow.core.*
import csw.params.core.models.ObsId
import csw.params.core.models.Prefix
import csw.params.keys.*

interface CommandInOut<C> {

    val sender: Prefix

    val commandName: CommandName

    val obsId: Option<ObsId>

    fun toStore(): Setup

    object VError

    fun validate(setup: Setup):Either<VError, Boolean>

    fun fromSetup(storeIn: Setup): C
}

enum class Selection(name: String) {
    A("A"), B("B"), C("C")
}

data class Command1(val keyName: String, val voltageIn: Double, val sel: Selection, override val obsId: Option<ObsId>): CommandInOut<Command1> {

    override val sender = Prefix("ESW.test")
    override val commandName:CommandName = "testCommand"

    val key1 = NumberKey("voltage", Units.volt)
    val key2 = ChoiceKey("option", Choices("A", "B", "C"))

    override fun toStore(): Setup {
        var x = Setup(sender, commandName, obsId)
        x = x.add(key1.set(voltageIn), key2.set("B"))
        return x
    }

    private val keys = listOf("voltage").map { it }

    override fun validate(setup: Setup):Either<CommandInOut.VError, Boolean> {
        return Either.Right(true)
    }

    override fun fromSetup(storeIn: Setup): Command1 {
        require(validate(storeIn).isRight() )

        val arg1 = key1.head(storeIn)
        val arg2: Choice = key2.head(storeIn)

        return Command1("bob", arg1, Selection.B, storeIn.obsId)
    }
}
