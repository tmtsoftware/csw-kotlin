package csw.params.states

import csw.params.core.models.Prefix
import csw.params.keys.*
import csw.time.core.models.UTCTime
import csw.params.states.StateVariable.DemandState
import csw.params.states.StateVariable.CurrentState
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus


class StateVariablesTest: DescribeSpec({
    describe("Examples of State variables") {

        it("should show usages of DemandState") {

            //#demandstate
            //prefix
            val prefix = Prefix("wfos.prog.cloudcover")

            //key
            val stringKey = StringKey("charKey")
            val intKey = IntegerKey("intKey", Units.meter)
            val booleanKey = BooleanKey.make("booleanKey")
            val utcTimeKey = UTCTimeKey.make("utcTimeKey")
            val notUsedKey = StringKey.make("notUsed")

            //parameters
            val stringParam = stringKey.set('A', 'B', 'C')
            val intParam = intKey.set(1, 2, 3)
            val booleanParam = booleanKey.set(true, false)
            val utcTime = utcTimeKey.set(UTCTime.now())

            //create DemandState and use sequential add
            val ds1 = DemandState(prefix, "testStateName").add(stringParam).add(intParam)
            //create DemandState and add more than one Parameters using madd
            val ds2 = DemandState(prefix, "testStateName").madd(intParam, booleanParam)
            //create DemandState using apply
            val ds3 = DemandState(prefix, "testStateName", listOf(utcTime))

            //access keys
            val stringKeyExists: Boolean = ds1.exists(stringKey) //true
            stringKeyExists shouldBe true

            //access Parameters
            val p1 = intKey.get(ds1)
            p1?.svalue shouldBe intParam.data

            //access values
            val v1: Array<String> = stringKey.value(ds1)
            v1 shouldBe arrayOf("A", "B", "C")

            val v2: BooleanArray = booleanKey.value(ds2)
            v2 shouldBe arrayOf(true, false)

            val missingKeys: Set<String> = ds3.missingKeys(stringKey, intKey, booleanKey, utcTimeKey, notUsedKey)
            missingKeys.size shouldBe 4  // Only utcTieKey is present in ds3

            //remove keys
            val ds4: DemandState = ds3.remove(utcTimeKey)
            ds4.size shouldBe 0
            utcTimeKey.isIn(ds4) shouldBe false

            //update existing keys - set it back by an hour
            val ds5: DemandState =
                ds3.add(utcTimeKey.set(UTCTime(UTCTime.now().value.minus(3600, DateTimeUnit.SECOND))))
            // verify that the ds5 time is less than ds3 time
            utcTimeKey.head(ds5).value < utcTimeKey.head(ds3).value
        }

        it("should show usages of CurrentState") {

            //#currentstate

            //prefix
            val prefix = Prefix("wfos.prog.cloudcover")

            //key
            val stringKey = StringKey("charKey")
            val intKey = IntegerKey("intKey", Units.meter)
            val booleanKey = BooleanKey.make("booleanKey")
            val utcTimeKey = UTCTimeKey.make("utcTimeKey")
            val notUsedKey = StringKey.make("notUsed")

            //parameters
            val stringParam = stringKey.set('A', 'B', 'C')
            val intParam = intKey.set(1, 2, 3)
            val booleanParam = booleanKey.set(true, false)
            val utcTime = utcTimeKey.set(UTCTime.now())

            //create CurrentState and use sequential add
            val cs1 = CurrentState(prefix, "testStateName").add(stringParam).add(intParam)
            //create CurrentState and add more than one Parameters using madd
            val cs2 = CurrentState(prefix, "testStateName").madd(intParam, booleanParam)
            //create CurrentState using invoke
            val cs3 = CurrentState(prefix, "testStateName", listOf(utcTime))

            //access keys
            val stringKeyExists: Boolean = cs1.exists(stringKey) //true
            stringKeyExists shouldBe true
            //access Parameters

            val p1 = intKey.get(cs1)
            p1?.svalue shouldBe intParam.data

            //access values
            val v1: Array<String> = stringKey.value(cs1)
            v1 shouldBe arrayOf("A", "B", "C")

            val v2: BooleanArray = booleanKey.value(cs2)
            v2 shouldBe arrayOf(true, false)

            val missingKeys: Set<String> = cs3.missingKeys(stringKey, intKey, booleanKey, utcTimeKey, notUsedKey)
            missingKeys.size shouldBe 4  // Only utcTieKey is present in ds3

            //remove keys
            val cs4 = cs3.remove(utcTimeKey)
            cs4.size shouldBe 0
            utcTimeKey.isIn(cs4) shouldBe false

            //update existing keys - set it back by an hour
            val cs5 = cs3.add(utcTimeKey.set(UTCTime(UTCTime.now().value.minus(3600, DateTimeUnit.SECOND))))
            // verify that the ds5 time is less than ds3 time
            utcTimeKey.head(cs5).value < utcTimeKey.head(cs3).value
        }
    }
    /*
    describe("Examples of serialization") {
        it("should show reading and writing of State variables") {
            //#json-serialization

            //key
            val k1: Key[MatrixData[Double]] = DoubleMatrixKey.make("myMatrix")
            //values
            val m1: MatrixData[Double] = MatrixData.fromArrays(
            Array(1.0, 2.0, 3.0),
            Array(4.1, 5.1, 6.1),
            Array(7.2, 8.2, 9.2)
            )

            //parameter
            val p1: Parameter[MatrixData[Double]] = k1.set(m1)

            //state variables
            val ds: DemandState = DemandState(Prefix("wfos.blue.filter"), StateName("testStateName")).add(p1)
            val cs: CurrentState = CurrentState(Prefix("wfos.blue.filter"), StateName("testStateName")).add(p1)

            //json support - write
            val dsJson: JsValue = JsonSupport.writeStateVariable(ds)
            val csJson: JsValue = JsonSupport.writeStateVariable(cs)

            //optionally prettify
            val str: String = Json.prettyPrint(dsJson)

            //construct command from string
            val scFromPrettyStr = JsonSupport.readStateVariable[DemandState](Json.parse(str))

            //json support - read
            val ds1: DemandState = JsonSupport.readStateVariable[DemandState](dsJson)
            val cs1: CurrentState = JsonSupport.readStateVariable[CurrentState](csJson)
            //#json-serialization

            //validations
            assert(ds === ds1)
            assert(cs === cs1)
            assert(scFromPrettyStr === ds)
        }
    }
    */

    describe("Examples of unique key constraint") {
        it("should show duplicate keys are removed") {
            //#unique-key
            //keys
            val encoderKey = IntegerKey("encoder")
            val filterKey = IntegerKey.make("filter")
            val miscKey = IntegerKey.make("misc")

            //prefix
            val prefix = Prefix("wfos.blue.filter")

            //params
            val encParam1 = encoderKey.set(1)
            val encParam2 = encoderKey.set(2)
            val encParam3 = encoderKey.set(3)

            val filterParam1 = filterKey.set(1)
            val filterParam2 = filterKey.set(2)
            val filterParam3 = filterKey.set(3)

            val miscParam1 = miscKey.set(100)

            //DemandState with duplicate key via constructor
            val statusEvent = DemandState(
                prefix,
                "testStateName",
                listOf(encParam1, encParam2, encParam3, filterParam1, filterParam2, filterParam3)
            )

            //four duplicate keys are removed; now contains one Encoder and one Filter key
            val uniqueKeys1 = statusEvent.parms.map { it.name }

            //try adding duplicate keys via add + madd
            val changedStatusEvent = statusEvent
                .add(encParam3)
                .madd(
                    filterParam1,
                    filterParam2,
                    filterParam3
                )

            //duplicate keys will not be added. Should contain one Encoder and one Filter key
            val uniqueKeys2 = changedStatusEvent.parms.map { it.name}

            //miscKey(unique) will be added; encoderKey(duplicate) will not be added
            val finalStatusEvent = statusEvent.madd(miscParam1, encParam1)
            //now contains encoderKey, filterKey, miscKey
            val uniqueKeys3 = finalStatusEvent.parms.map { it.name }

            //validations
            uniqueKeys1 shouldContainExactlyInAnyOrder listOf(encoderKey.name, filterKey.name)
            uniqueKeys2 shouldContainExactlyInAnyOrder listOf(encoderKey.name, filterKey.name)
            uniqueKeys3 shouldContainExactlyInAnyOrder listOf(encoderKey.name, filterKey.name, miscKey.name)
        }
    }
}
)