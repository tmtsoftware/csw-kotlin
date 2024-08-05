package csw.params.events.observeevents

import csw.params.core.models.Prefix
import csw.params.core.models.ExposureId
import csw.params.core.models.ObsId
import csw.params.events.observeevents.ObserveEvents.dataWriteEnd
import csw.params.events.observeevents.ObserveEvents.dataWriteStart
import csw.params.events.observeevents.ObserveEvents.downtimeStart
import csw.params.events.observeevents.ObserveEvents.exposureAborted
import csw.params.events.observeevents.ObserveEvents.exposureAvailable
import csw.params.events.observeevents.ObserveEvents.exposureEnd
import csw.params.events.observeevents.ObserveEvents.exposureStart
import csw.params.events.observeevents.ObserveEvents.guidestarAcqEnd
import csw.params.events.observeevents.ObserveEvents.guidestarAcqStart
import csw.params.events.observeevents.ObserveEvents.metadataAvailable
import csw.params.events.observeevents.ObserveEvents.observationEnd
import csw.params.events.observeevents.ObserveEvents.observationStart
import csw.params.events.observeevents.ObserveEvents.observeEnd
import csw.params.events.observeevents.ObserveEvents.observePaused
import csw.params.events.observeevents.ObserveEvents.observeResumed
import csw.params.events.observeevents.ObserveEvents.observeStart
import csw.params.events.observeevents.ObserveEvents.prepareStart
import csw.params.events.observeevents.ObserveEvents.presetEnd
import csw.params.events.observeevents.ObserveEvents.presetStart
import csw.params.events.observeevents.ObserveEvents.publishFail
import csw.params.events.observeevents.ObserveEvents.publishSuccess
import csw.params.events.observeevents.ObserveEvents.readoutEnd
import csw.params.events.observeevents.ObserveEvents.readoutFailed
import csw.params.events.observeevents.ObserveEvents.scitargetAcqEnd
import csw.params.events.observeevents.ObserveEvents.scitargetAcqStart

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class ObserveEventTest : FunSpec({
    val sourcePrefix = Prefix("DMS.Metadata")
    val obsId = ObsId("2022A-001-123")
    val exposureId = ExposureId("2022A-001-123-IRIS-IMG-DRK1-0023")
    val fileName = "This File Name"
    val reason = "Telescope fails significantly"

    test("should create presetStart observe event") {
        val t1 = presetStart(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.PresetStart
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create presetEnd observe event") {
        val t1 = presetEnd(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.PresetEnd
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create guidestarAcqStart observe event") {
        val t1 = guidestarAcqStart(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.GuidestarAcqStart
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create guidestarAcqEnd observe event") {
        val t1 = guidestarAcqEnd(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.GuidestarAcqEnd
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create scitargetAcqStart observe event") {
        val t1 = scitargetAcqStart(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ScitargetAcqStart
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create scitargetAcqEnd observe event") {
        val t1 = scitargetAcqEnd(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ScitargetAcqEnd
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create observationStart observe event") {
        val t1 = observationStart(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ObservationStart
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create observationEnd observe event") {
        val t1 = observationEnd(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ObservationEnd
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create observeStart observe event") {
        val t1 = observeStart(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ObserveStart
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create observeEnd observe event") {
        val t1 = observeEnd(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ObserveEnd
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create observePaused observe event") {
        val t1 = observePaused(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ObservePaused
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create observeResumed observe event") {
        val t1 = observeResumed(sourcePrefix, obsId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ObserveResumed
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.obsId)
    }

    test("should create prepareStart observe event") {
        val t1 = prepareStart(sourcePrefix, exposureId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.PrepareStart
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.exposureId)
    }

    test("should create exposureStart observe event") {
        val t1 = exposureStart(sourcePrefix, exposureId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ExposureStart
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.exposureId)
    }

    test("should create exposureEnd observe event") {
        val t1 = exposureEnd(sourcePrefix, exposureId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ExposureEnd
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.exposureId)
    }

    test("should create readoutEnd observe event") {
        val t1 = readoutEnd(sourcePrefix, exposureId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ReadoutEnd
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.exposureId)
    }

    test("should create readoutFailed observe event") {
        val t1 = readoutFailed(sourcePrefix, exposureId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ReadoutFailed
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.exposureId)
    }

    test("should create dataWriteStart observe event") {
        val t1 = dataWriteStart(sourcePrefix, exposureId, fileName)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.DataWriteStart
        t1.size shouldBe 2
        t1.exists(ObserveEventKeys.exposureId)
        t1.exists(ObserveEventKeys.filename)
    }

    test("should create dataWriteEnd observe event") {
        val t1 = dataWriteEnd(sourcePrefix, exposureId, fileName)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.DataWriteEnd
        t1.size shouldBe 2
        t1.exists(ObserveEventKeys.exposureId)
        t1.exists(ObserveEventKeys.filename)
    }

    test("should create exposureAborted observe event") {
        val t1 = exposureAborted(sourcePrefix, exposureId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.ExposureAborted
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.exposureId)
    }

    test("should create metadata available observe event") {
        val t1 = metadataAvailable(sourcePrefix, exposureId)
        t1.typeName shouldBe "ObserveEvent"

        t1.source shouldBe sourcePrefix
        t1.eventName shouldBe ObserveEventNames.MetadataAvailable
        t1.size shouldBe 1
        t1.exists(ObserveEventKeys.exposureId)
    }

    test("should create exposure available observe event") {
        val t1 = exposureAvailable(sourcePrefix, exposureId)
        t1.apply {
            typeName shouldBe "ObserveEvent"
            source shouldBe sourcePrefix
            eventName shouldBe ObserveEventNames.ExposureAvailable
            size shouldBe 1
            exists(ObserveEventKeys.exposureId)
        }
    }

    test("should create publishSuccess observe event") {
        val t1 = publishSuccess(sourcePrefix)
        t1.apply {
            typeName shouldBe "ObserveEvent"
            source shouldBe sourcePrefix
            eventName shouldBe ObserveEventNames.PublishSuccess
            size shouldBe 0
        }
    }

    test("should create publish fail observe event") {
        val t1 = publishFail(sourcePrefix)
        t1.apply {
            typeName shouldBe "ObserveEvent"
            source shouldBe sourcePrefix
            eventName shouldBe ObserveEventNames.PublishFail
            size shouldBe 0
        }
    }

    test("should create downtimeStart observe event") {
        val t1 = downtimeStart(sourcePrefix, obsId, reason)
        t1.apply {
            typeName shouldBe "ObserveEvent"
            source shouldBe sourcePrefix
            eventName shouldBe ObserveEventNames.DowntimeStart
            size shouldBe 2
            exists(ObserveEventKeys.exposureId)
            exists(ObserveEventKeys.downTimeReason)
        }
    }

    val readsInRamp = 10
    val readsComplete = 4
    val rampsInExposure = 25
    val rampsComplete = 10
    val exposureTime = 1000L
    val remainingExposureTime = 250L

    test("should create irExposureDataEvent observe event") {
        val t1 = irExposureDataEvent(sourcePrefix, exposureId,
            readsInRamp = readsInRamp, readsComplete = readsComplete,
            rampsInExposure = rampsInExposure, rampsComplete = rampsComplete,
            exposureTime = exposureTime, remainingExposureTime = remainingExposureTime)
        t1.apply {
            typeName shouldBe "ObserveEvent"
            source shouldBe sourcePrefix
            eventName shouldBe ObserveEventNames.IRDetectorExposureData
            size shouldBe 7
            exists(ObserveEventKeys.exposureId)
            exists(ObserveEventKeys.exposureTime)
            exists(ObserveEventKeys.readsInRamp)
            exists(ObserveEventKeys.readsComplete)
            exists(ObserveEventKeys.rampsInExposure)
            exists(ObserveEventKeys.rampsComplete)
            exists(ObserveEventKeys.exposureId)
            exists(ObserveEventKeys.remainingExposureTime)
        }
    }

    val coaddsInExposure = 12
    val coaddsDone = 3

    test("should create optExposureDataEvent observe event") {
        val t1 = optExposureDataEvent(sourcePrefix, exposureId,
            exposureTime = exposureTime, coaddsInExposure = coaddsInExposure,
            coaddsDone = coaddsDone, remainingExposureTime = remainingExposureTime)
        t1.apply {
            typeName shouldBe "ObserveEvent"
            source shouldBe sourcePrefix
            eventName shouldBe ObserveEventNames.OpticalDetectorExposureData
            size shouldBe 5
            exists(ObserveEventKeys.exposureId)
            exists(ObserveEventKeys.exposureTime)
            exists(ObserveEventKeys.coaddsInExposure)
            exists(ObserveEventKeys.coaddsDone)
            exists(ObserveEventKeys.remainingExposureTime)
        }
    }

    val exposureInProgress = true
    val abortInProgress = false
    val isAborted = false
    val errorMessage = ""
    val operationalState = OperationalState.BUSY

    test("should create irExposureStateEvent observe event") {
        val t1 = irExposureStateEvent(sourcePrefix, exposureId,
            exposureInProgress = exposureInProgress, abortInProgress = abortInProgress,
            isAborted = isAborted, errorMessage = errorMessage, operationalState = operationalState)
        t1.apply {
            typeName shouldBe "ObserveEvent"
            source shouldBe sourcePrefix
            eventName shouldBe ObserveEventNames.IRDetectorExposureState
            size shouldBe 6
            exists(ObserveEventKeys.exposureId)
            exists(ObserveEventKeys.exposureInProgress)
            exists(ObserveEventKeys.abortInProgress)
            exists(ObserveEventKeys.isAborted)
            exists(ObserveEventKeys.errorMessage)
            exists(ObserveEventKeys.operationalState)
        }
    }

    test("should create optExposureStateEvent observe event") {
        val t1 = opticalExposureStateEvent(sourcePrefix, exposureId,
            exposureInProgress = exposureInProgress, abortInProgress = abortInProgress,
            isAborted = isAborted, errorMessage = errorMessage, operationalState = operationalState)
        t1.apply {
            typeName shouldBe "ObserveEvent"
            source shouldBe sourcePrefix
            eventName shouldBe ObserveEventNames.OpticalDetectorExposureState
            size shouldBe 6
            exists(ObserveEventKeys.exposureId)
            exists(ObserveEventKeys.exposureInProgress)
            exists(ObserveEventKeys.abortInProgress)
            exists(ObserveEventKeys.isAborted)
            exists(ObserveEventKeys.errorMessage)
            exists(ObserveEventKeys.operationalState)
        }
    }

    test("should create wfsGuiderExposureStateEvent observe event") {
        val t1 = wfsGuiderExposureStateEvent(sourcePrefix, exposureId,
            exposureInProgress = exposureInProgress, abortInProgress = abortInProgress,
            isAborted = isAborted, errorMessage = errorMessage, operationalState = operationalState)
        t1.apply {
            typeName shouldBe "ObserveEvent"
            source shouldBe sourcePrefix
            eventName shouldBe ObserveEventNames.WfsDetectorExposureState
            size shouldBe 6
            exists(ObserveEventKeys.exposureId)
            exists(ObserveEventKeys.exposureInProgress)
            exists(ObserveEventKeys.abortInProgress)
            exists(ObserveEventKeys.isAborted)
            exists(ObserveEventKeys.errorMessage)
            exists(ObserveEventKeys.operationalState)
        }
    }

}
)
