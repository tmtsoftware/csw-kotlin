package csw.location.api.models

import csw.location.api.codecs.ModelCodecs
import csw.params.core.models.Prefix
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.net.URI

class TrackingEventTest : FunSpec({

    val id1 = ComponentId(Prefix("IRIS.filter.wheel"), ComponentType.HCD)
    val c1 = HttpConnection(id1)
    val l1 = HttpLocation(c1, URI("http://localhost:8765/AUTH"), Metadata())

    test("Check tracking event serialization") {
        val obj = LocationUpdated(l1)
        val jsonOut = ModelCodecs.trackingToJson(obj)

        val expected = """
            {
                "_type": "LocationUpdated",
                "location": {
                    "_type": "HttpLocation",
                    "connection": {
                        "prefix": "IRIS.filter.wheel",
                        "componentType": "HCD",
                        "connectionType": "http"
                    },
                    "uri": "http://localhost:8765/AUTH",
                    "metadata": {}
                }
            }""".trimIndent()

        jsonOut shouldBe expected
    }

    test("Check LocationRemoved event serialization") {
        val obj = LocationRemoved(c1)
        val jsonOut = ModelCodecs.trackingToJson(obj)

        val expected = """
            {
                "_type": "LocationRemoved",
                "connection": {
                    "prefix": "IRIS.filter.wheel",
                    "componentType": "HCD",
                    "connectionType": "http"
                }
            }""".trimIndent()

        jsonOut shouldBe expected
    }
})
