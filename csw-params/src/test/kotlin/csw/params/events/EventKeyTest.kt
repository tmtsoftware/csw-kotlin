package csw.params.events

import csw.params.core.models.Prefix
import csw.params.core.models.Subsystem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EventKeyTest: FunSpec( {

    test("createEventKey") {
        val prefix = Prefix("wfos.blue.filter")
        val eventName: EventName = "filter wheel"

        val t1 = EventKey(prefix, eventName)
        t1.eventName shouldBe eventName
        t1.source shouldBe prefix
        t1.key shouldBe "$prefix.$eventName"
    }

    test("create by String") {
        val t1 = EventKey("TCS.pk.mount.current")
        t1.eventName shouldBe "current"
        t1.source shouldBe Prefix(Subsystem.TCS, "pk.mount")
        t1.key shouldBe "TCS.pk.mount.current"
    }
}
)