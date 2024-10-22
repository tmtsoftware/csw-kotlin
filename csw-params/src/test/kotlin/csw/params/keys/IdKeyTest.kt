package csw.params.keys

import csw.params.commands.CommandName
import csw.params.core.models.Prefix
import csw.params.commands.Setup
import csw.params.core.models.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IdKeyTest: FunSpec( {

    val testP = Prefix("ESW.test")

    fun testS(p: Prefix = testP, cname: CommandName = "test"): Setup =  Setup(p, cname)


    test("Create a new ExposureId Key") {
        val key1 = ExposureIdKey("key1")

        val exp1 = ExposureId("2031A-001-123-CSW-IMG1-SCI0-0001")

        val store = key1.set(exp1)
        store shouldBe IdStore(key1.name, IdType.EXID, "2031A-001-123-CSW-IMG1-SCI0-0001")
    }

    test("Add and get from expId param set") {
        val key1 = ExposureIdKey("key1")

        val exp1 = ExposureId("2031A-001-123-CSW-IMG1-SCI0-0001")

        var s = testS()

        s = s.add(key1.set(exp1))

        with(key1(s)) {
            typLevel shouldBe TYPLevel("SCI0")
            subsystem shouldBe Subsystem.CSW
            det shouldBe "IMG1"
            obsId().onSome { it shouldBe ObsId("2031A-001-123") }
            exposureNumber shouldBe ExposureNumber("0001")
        }
    }

    test("Create a new ObsId Key") {
        val key1 = ObsIdKey("key1")

        val exp1 = ObsId("2031A-001-123")

        val store = key1.set(exp1)
        store shouldBe IdStore(key1.name, IdType.OBSID, "2031A-001-123")
    }

    test("Add and get ObsId from parm set") {
        val key1 = ObsIdKey("key1")

        val obsId1 = ObsId("2031A-001-123")

        var s = testS()

        s = s.add(key1.set(obsId1))
        //key1(s).

        with(key1(s)) {
            programId shouldBe ProgramId("2031A-001")
            observationNumber shouldBe 123
        }

        key1.semesterId(s) shouldBe SemesterId("2031A")
        key1.semester(s) shouldBe Semester.A
        key1.year(s) shouldBe 2031
    }
}
)