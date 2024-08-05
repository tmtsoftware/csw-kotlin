package csw.params.commands

import csw.params.core.models.Prefix
import csw.params.core.models.Subsystem
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PrefixTest : FunSpec({

    test("should able to create Prefix and access subsystem from valid prefix string") {
        val prefixStr = "TCS.wfos.blue.filter"
        val prefix = Prefix(prefixStr)

        prefix.subsystem shouldBe Subsystem.TCS
    }

    test("should now allow creating Prefix when invalid prefix string provided") {
        val ex = shouldThrow<IllegalArgumentException> {
            val prefixStr = "invalid.prefix"
            Prefix(prefixStr)
        }
        ex.message shouldBe "No enum constant csw.params.core.models.Subsystem.INVALID"
    }

    // CSW-86: Subsystem should be case-insensitive
    test("should access subsystem and componentName in lowercase") {
        val prefix = Prefix("Tcs.Filter.Wheel")
        prefix.toString() shouldBeEqual "TCS.Filter.Wheel"
        prefix.subsystem shouldBe Subsystem.TCS
        prefix.componentName shouldBe "Filter.Wheel"
    }

    fun ByteArray.toAsciiHexString() = joinToString("") {
        if (it in 32..127) it.toInt().toChar().toString() else
            "{${it.toUByte().toString(16).padStart(2, '0').uppercase()}}"
    }

    // Prefix should serialize to Json and cbor
    test("should serialie to JSON") {
        val prefix = Prefix("Tcs.Filter.Wheel")

        val jsonOut = Json.encodeToString(prefix)
        println(jsonOut)
        val sin = Json.decodeFromString<Prefix>(jsonOut)
        println(sin)
        prefix shouldBe sin
    }

    // Prefix should serialize to Json and cbor
    test("should serialie to CBOR") {
        val prefix = Prefix("Tcs.Filter.Wheel")

        val bytes = Cbor.encodeToByteArray(prefix)
        println(bytes.toAsciiHexString())
        val obj = Cbor.decodeFromByteArray<Prefix>(bytes)
        println(obj)
        prefix shouldBe obj
    }

}
)
