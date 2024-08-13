package csw.location.api.models

import arrow.core.Some
import arrow.core.none
import csw.params.core.models.Prefix
import csw.params.core.models.Subsystem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MetadataTest: FunSpec( {

    test("should be able to create metadata with agent prefix and PID | CSW-108") {
        val agentPrefix = Prefix(Subsystem.ESW, "agent1")
        val pid         = 1234L

        val metadata = Metadata().withAgentPrefix(agentPrefix).withPid(pid)

        metadata.values shouldBe mapOf("agentPrefix" to agentPrefix.toString(), "PID" to pid.toString())
    }

    test("should be able to create metadata with given key | CSW-108") {
        val value1 = "1234"
        val value2 = "abc"

        val metadata = Metadata().add("customKey1", value1).add("customKey2", value2)

        metadata.values shouldBe mapOf("customKey1" to value1, "customKey2" to value2)
    }

    test("should be able to get value from metadata for given key | CSW-108") {
        val customKey = "customKey1"
        val value     = "value1"
        val metadata  = Metadata().add(customKey, value)

        metadata.get(customKey).onSome { it shouldBe value }

        metadata.get("invalidKey") shouldBe none()
        metadata.getAgentPrefix() shouldBe none()
        metadata.getPid() shouldBe none()
    }

    test("should be able to get value from metadata for agentPrefix key | CSW-108") {
        val agentPrefix = Prefix(Subsystem.ESW, "agent1")
        val metadata    = Metadata().withAgentPrefix(agentPrefix)

        metadata.getAgentPrefix().onSome { it shouldBe agentPrefix }
    }

    test("should be able to get value from metadata for PID key | CSW-108") {
        val pid      = 1234L
        val metadata = Metadata().withPid(pid)

        metadata.getPid().onSome { it shouldBe pid }
    }

    test("should return None from metadata for invalid keys | CSW-108, CSW-133") {
        val customKey = "customKey1"
        val value     = "value1"
        val metadata  = Metadata().add(customKey, value)

        metadata.get("invalidKey") shouldBe none()
        metadata.getAgentPrefix() shouldBe none()
        metadata.getPid() shouldBe none()
        metadata.getSequenceComponentPrefix() shouldBe none()
    }

    test("should be able to create metadata with sequence component prefix | CSW-133") {
        val sequenceComponentPrefix = Prefix(Subsystem.ESW, "seqcomp1")

        val metadata = Metadata().withSequenceComponentPrefix(sequenceComponentPrefix)

        metadata.values shouldBe mapOf("sequenceComponentPrefix" to sequenceComponentPrefix.toString())
        metadata.getSequenceComponentPrefix() shouldBe Some(sequenceComponentPrefix)
    }
}
)