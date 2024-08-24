package csw.location.api.models

import csw.params.core.models.Prefix
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ConnectionInfoTest: FunSpec({

    test("Test creation from strings for serialization") {
        val p = Prefix("ESW.test.test")
        val comp = ComponentType.Assembly
        val conn = ConnectionType.HttpType
        val connInfo = ConnectionInfo(p, comp, conn)

        val connInfoIn = ConnectionInfo("ESW.test.test", "assembly", "http")
        connInfo shouldBe connInfoIn
    }

    test("Should serialize and deserialize") {

        val p = Prefix("ESW.test.test")
        val comp = ComponentType.Assembly
        val conn = ConnectionType.HttpType
        val connInfo = ConnectionInfo(p, comp, conn)

        val jsonOut = Json.encodeToString(connInfo)
        println("jsonOut: $jsonOut")

        Json.decodeFromString<ConnectionInfo>(jsonOut) shouldBe connInfo
    }
})