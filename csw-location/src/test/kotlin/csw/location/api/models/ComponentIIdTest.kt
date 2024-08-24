package csw.location.api.models

import csw.params.core.models.Prefix
import csw.params.core.models.Subsystem
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ComponentIdTest: FunSpec( {

    test("Successful creation") {
        val prefix = Prefix("ESW.test")
        val componentType = ComponentType.Assembly

        val cid = ComponentId(prefix, componentType)
        cid.prefix shouldBe prefix
        cid.componentType shouldBe componentType
        cid.fullName shouldBe "ESW.test-Assembly"
    }

    // DEOPSCSW-14: Codec for data model
    // The following tests are actually Prefix tests, which have been added.
    test("should not contain leading or trailing spaces in component's name | DEOPSCSW-14") {
        val ex = shouldThrow<IllegalArgumentException> {
            ComponentId(Prefix(Subsystem.CSW, " redis "), ComponentType.Service)
        }
        ex.message shouldBe "A component name should not have leading or trailing whitespaces"
    }

    // DEOPSCSW-14: Codec for data model
    test("should not contain '-' in component's name | DEOPSCSW-14") {
        val ex = shouldThrow<IllegalArgumentException> {
            ComponentId(Prefix(Subsystem.CSW, "redis-service"), ComponentType.Service)
        }
        ex.message shouldBe "A component name cannot contain a '-'"
    }

    // Test connection json against CSW format
    test("should serialize JSON as needed for CSW") {
        val c1 = ComponentId(Prefix(Subsystem.IRIS, "filter.wheel"), ComponentType.HCD)
        val jsonOut = Json.encodeToString(c1)

        jsonOut shouldBe """{"prefix":"IRIS.filter.wheel","componentType":"HCD"}"""

        val obj = Json.decodeFromString<ComponentId>(jsonOut)
        obj shouldBe c1
    }
}
)