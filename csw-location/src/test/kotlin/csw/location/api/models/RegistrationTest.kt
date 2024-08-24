package csw.location.api.models

import csw.location.api.codecs.*
import csw.params.core.models.Prefix
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.json.*
import java.net.URI


class RegistrationTest: FunSpec( {
    val id1 = ComponentId(Prefix("TCS.filter.wheel"), ComponentType.HCD)

    test("HTTPRegistration should serialize JSON as needed for CSW try 2") {

        val c1 = HttpConnection(id1)
        val port = 8765
        val path = "AUTH"

        val m = Metadata().add("key1", "value")
        val reg1 = HttpRegistration(c1, port, path, m)

        // Test registration json against CSW format
        val jsonOut = ModelCodecs.regToJson(reg1)
        println("jsonOut: $jsonOut")

        val map:Map<String, JsonElement> =
            mapOf("_type" to JsonPrimitive("Register"),
                "registration" to ModelCodecs.regToJsonElement(reg1))
        val root = JsonObject(map)

        val xx = ModelCodecs.jsonObjectToString(root)

        println("XX: $xx")

        val x:URI = URI.create("http://localhost:$port/$path")
        println("X: $x")
        //val objIn = jsonToReg(jsonOut)
        //objIn shouldBe reg1
    }
})