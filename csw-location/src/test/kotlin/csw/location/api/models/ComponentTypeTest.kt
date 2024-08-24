package csw.location.api.models

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class ComponentTypeTest: FunSpec( {

    // DEOPSCSW-14: Codec for data model
    test(
        "ComponentType should be any one of this types : 'Container', 'HCD', 'Assembly', 'Service', 'Machine', 'Sequencer' and  'SequenceComponent' | DEOPSCSW-14"
    ) {
        val expectedComponentTypeValues = listOf("Container", "HCD", "Assembly", "Service", "Machine", "Sequencer", "SequenceComponent")
        val actualComponentTypeValues: List<String> = ComponentType.entries.map { it.name }
        actualComponentTypeValues shouldContainExactlyInAnyOrder  expectedComponentTypeValues
    }

    test("Should create from string for serialization") {
        ComponentType("HCD") shouldBe ComponentType.HCD
        ComponentType("hcD") shouldBe ComponentType.HCD
        ComponentType("cONTainer") shouldBe ComponentType.Container
        ComponentType("Assembly") shouldBe ComponentType.Assembly
        ComponentType("sErvice") shouldBe ComponentType.Service
        ComponentType("Machine") shouldBe ComponentType.Machine
        ComponentType("sequencer") shouldBe ComponentType.Sequencer
        ComponentType("SequenceComponent") shouldBe ComponentType.SequenceComponent

        val ex = shouldThrow<IllegalArgumentException> {
            ComponentType("HCC")
        }
        ex.message shouldBe "ComponentType HCC not supported"
    }
}
)
