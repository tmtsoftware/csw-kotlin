package csw.params.commands

/**
 * a sequence of [[csw.params.commands.SequenceCommand]]
 */
final internal data class SSequence(val commands: List<SequenceCommand>) {
    fun add(vararg others: SequenceCommand): SSequence = copy(commands = (commands + (others.toList())))
    fun add(other: SSequence): SSequence = copy(commands = commands + other.commands)
}

object Sequence {
    internal operator fun invoke(command: SequenceCommand, vararg commands: SequenceCommand): SSequence = SSequence(listOf(command) + commands.toList())

    /**
     * Create a Sequence  model from a  list of [[csw.params.commands.SequenceCommand]]
     */
    //fun create(commands: java.util.List[SequenceCommand]): Sequence = Sequence(commands.asScala.toList)
}
