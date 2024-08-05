package csw.params.keys

import arrow.core.None
import csw.params.commands.CommandName
import csw.params.commands.Setup
import csw.params.core.models.Prefix
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

@kotlinx.serialization.ExperimentalSerializationApi
class ByteArrayTest: FunSpec({
    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"): Setup = Setup(p, cname, None)

    val key1 = ByteKey("key1")
    val key2 = IntegerKey("key2", Units.degree)
    val key3 = ByteKey("key3")


    val bytes1 = "sensor image".toByteArray()

    test("Basic Adds") {
        val out1 = key1.set(bytes1)
        out1.name shouldBe key1.name
        println("Out: $out1")

        val out3 = key3.setC(bytes1)
        println("Out3: $out3")
    }

    fun show(a: ByteArray) = a.joinToString(",")

    test("Test setC") {
        var s = testS()

        s = s.add(key3.setC(bytes1))
        key3.isIn(s) shouldBe true
        s.size shouldBe 1

        val out3 = key3.get(s)
        out3.onSome { println("Out3: ${show(it)}") }
        out3.onSome { it shouldBe bytes1 }
    }

    test("Test option get") {
        var s = testS()

        s = s.add(key1.set(bytes1))
        key1.isIn(s) shouldBe true
        key3.isIn(s) shouldBe false

        s.size shouldBe 1

        s = s.add(key3.setC(bytes1))
        key1.isIn(s) shouldBe true
        key3.isIn(s) shouldBe true

        s.size shouldBe 2

        val out1 = key1.get(s)
        out1.onSome { println("Out1: ${it.decodeToString()} - ${show(it)}") }

        val out3 = key3.get(s)
        out3.onSome { println("Out3: ${show(it)}") }
    }

    test("should able to create parameter representing binary image | DEOPSCSW-186, DEOPSCSW-331") {
        val keyName = "imageKey"
        val imageKey = ByteKey(keyName)

        val imgPath = ResourceReader.copyToTmp("smallBinary.bin", ".bin")
        val imgBytes = Files.readAllBytes(imgPath)
        println("Size: ${imgBytes.size}")

        var s = testS()
        s = s.add(imageKey.set(imgBytes))

        imageKey.head(s) shouldBe imgBytes[0]
        imageKey.get(s).onSome { it shouldBe imgBytes }
        imageKey(s) shouldBe imgBytes
        imageKey.value(s) shouldBe imgBytes
        imageKey.name shouldBe keyName


        //binaryParam.keyName shouldBe keyName
        //binaryParam.size shouldBe 1
        //binaryParam.keyType shouldBe KeyType.ByteArrayKey
    }

    test("should be serializable to JSON") {
        val keyName = "imageKey"
        val imageKey = ByteKey(keyName)

        val imgPath = ResourceReader.copyToTmp("smallBinary.bin", ".bin")
        val imgBytes = Files.readAllBytes(imgPath)
        println("Size: ${imgBytes.size}")

        //val binaryImgData: ArrayData[Byte]          = ArrayData.fromArray(imgBytes)
        var s = testS()
        s = s.add(imageKey.set(imgBytes))

        val json = Json.encodeToString(s)
        val objIn = Json.decodeFromString<Setup>(json)
        objIn shouldBe s
    }

    // Prefix should serialize to Json and cbor
    test("should serialize to CBOR") {
        val keyName = "imageKey"
        val imageKey = ByteKey(keyName)

        val imgPath = ResourceReader.copyToTmp("smallBinary.bin", ".bin")
        val imgBytes = Files.readAllBytes(imgPath)

        var s = testS()
        s = s.add(imageKey.set(imgBytes))
        val bytes = Cbor.encodeToByteArray(s)
        val obj = Cbor.decodeFromByteArray<Setup>(bytes)
        s shouldBe obj
    }
}
)


private typealias TempFilePath = Path

object ResourceReader {
    fun copyToTmp(fileName: String, suffix: String = ".tmp"): TempFilePath {
        val resourceStream: InputStream? = this::class.java.getClassLoader().getResourceAsStream(fileName)
        if (resourceStream != null) {
            resourceStream.use { rs ->
                val tempFile: File = File.createTempFile(rs.hashCode().toString(), suffix)
                tempFile.deleteOnExit()
                return Files.write(tempFile.toPath(), rs.readAllBytes())
            }
        } else throw NoSuchElementException("Resource not found: $fileName")
    }
}

