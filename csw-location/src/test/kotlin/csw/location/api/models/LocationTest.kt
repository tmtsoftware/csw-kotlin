package csw.location.api.models

import csw.params.core.models.Prefix
import csw.params.core.models.Subsystem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.net.URI

class LocationTest: FunSpec({
    val uriIn = URI("http://localhost:8080")
    val mIn = Metadata().add("key1", "value")

    test("Basic create tests") {
        val akkaConnectionIn = AkkaConnection(
            ComponentId(
                Prefix(Subsystem.NFIRAOS, "tromboneHcd"),
                ComponentType.HCD
            ),
            ConnectionType.AkkaType
        )

        with(AkkaLocation(akkaConnectionIn, uriIn, mIn)) {
            prefix shouldBe Prefix(Subsystem.NFIRAOS, "tromboneHcd")
            connection shouldBe akkaConnectionIn
            uri shouldBe uriIn
            metadata shouldBe mIn
        }
    }
})
