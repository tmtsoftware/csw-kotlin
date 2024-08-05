package csw.params.commands

import arrow.core.None
import csw.params.core.models.Prefix
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SequenceTest: FunSpec( {

    test("apply - create sequence from provided list of commands") {
        val setup    = Setup(Prefix("csw.setup"), "setup-test", None)
        val observe  = Observe(Prefix("csw.observe"), "observe-test", None)
        val sequence = Sequence(setup, observe)

        sequence.commands shouldBe listOf(setup, observe)
    }

    test("add - allow adding list of commands to existing sequence") {
        val setup    = Setup(Prefix("csw.setup"), "setup-test", None)
        val observe  = Observe(Prefix("csw.observe"), "observe-test", None)
        val sequence = Sequence(setup, observe)

        val newSetup   = Setup(Prefix("csw.setup3"), "setup-test", None)
        val newObserve = Observe(Prefix("csw.observe4"), "setup-test", None)

        val updateSequence = sequence.add(newSetup, newObserve)
        updateSequence.commands shouldBe listOf(setup, observe, newSetup, newObserve)
    }

    test("add - allow adding new sequence to existing sequence") {
        val setup    = Setup(Prefix("csw.setup"), "setup-test", None)
        val observe  = Observe(Prefix("csw.observe"),"observe-test", None)
        val sequence = Sequence(setup, observe)

        val newSetup    = Setup(Prefix("csw.setup3"),"setup-test", None)
        val newObserve  = Observe(Prefix("csw.observe3"),"observe-test", None)
        val newSequence = Sequence(newSetup, newObserve)

        val updateSequence = sequence.add(newSequence)
        updateSequence.commands shouldBe listOf(setup, observe, newSetup, newObserve)
    }
}
)