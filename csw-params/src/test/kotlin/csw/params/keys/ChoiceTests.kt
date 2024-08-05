package csw.params.keys

import arrow.core.None
import csw.params.commands.CommandName
import csw.params.core.models.Prefix
import csw.params.commands.Setup
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChoiceTests : FunSpec({

    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"):Setup =  Setup(p, cname, None)

    test("Basic ChioceKey tests") {
        val c1 = ChoiceSet("A", "B", "C")
        val c2 = ChoiceSet("AA", "BB", "CC")
        val key1 = ChoiceKey("key1", c1)
        val key2 = ChoiceKey("key2", c2)

        var st1 = ChoiceStore(key1.name, "B", "A,B,C")

        var s = testS()
        s = s.add(key1.set("B", "C"))
        s.size shouldBe 1

        val r1 = key1.get(s)
        r1.onSome { it shouldBe "C,B" }

        shouldThrow<IllegalArgumentException> {
            // Bad Choice
            key1.set("BAD")
        }

    }

    test("Add multiples test") {
        val c1 = ChoiceSet("A", "B", "C")
        val c2 = ChoiceSet("AA", "BB", "CC")
        val key1 = ChoiceKey("key1", c1)
        val key2 = ChoiceKey("key2", c2)

        var s = testS()
        s = s.add(key1.set("B", "C"), key2.set("CC"))
        s.size shouldBe 2

        val r1 = key1.get(s)
        r1.onSome { it shouldBe "C,B" }

        val r2 = key2.get(s)
        r2.onSome { it shouldBe "CC" }
    }

    test("Reject choice not in choiceset") {
        val c1 = ChoiceSet("A", "B", "C")
        val key1 = ChoiceKey("key1", c1)

        var s = testS()
        s = s.add(key1.set("B", "C"))
        s.size shouldBe 1

        shouldThrow<IllegalArgumentException> {
            // Bad Choice
            key1.set("BAD")
        }
    }

    test("Test serialization") {
        val c1 = ChoiceSet("A", "B", "C", "D", "E", "F")
        val c2 = ChoiceSet("AA", "BB", "CC")
        val key1 = ChoiceKey("key1", c1)
        val key2 = ChoiceKey("key2", c2)

        var s = testS()
        s = s.add(key1.set("B", "C"), key2.set("AA"))
        s.size shouldBe 2

        val jsonOut = Json.encodeToString(s)
        val objIn = Json.decodeFromString<Setup>(jsonOut)
        objIn shouldBe s
    }
}
)