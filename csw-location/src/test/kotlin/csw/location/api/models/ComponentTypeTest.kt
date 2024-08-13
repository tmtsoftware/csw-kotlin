package csw.location.api.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class ComponentTypeTest: FunSpec( {

    // DEOPSCSW-14: Codec for data model
    test(
        "ComponentType should be any one of this types : 'Container', 'HCD', 'Assembly', 'Service', 'Machine', 'Sequencer' and  'SequenceComponent' | DEOPSCSW-14"
    ) {
        val expectedComponentTypeValues = listOf("Container", "HCD", "Assembly", "Service", "Machine", "Sequencer", "SequenceComponent")
        val actualComponentTypeValues: List<String> = ComponentType.values().map { it.name }
        actualComponentTypeValues shouldContainExactlyInAnyOrder  expectedComponentTypeValues
    }
}
)
