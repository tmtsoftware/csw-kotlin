package csw.params.events.observeevents

import csw.params.core.models.ExposureId
import csw.params.core.models.ObsId
import csw.params.keys.*

object ObserveEventKeys {

    val obsId:StringKey        = StringKey("obsId")
    val exposureId:StringKey   = StringKey("exposureId")
    val coaddsInExposure:IntegerKey = IntegerKey("coaddsInExposure")
    val coaddsDone:IntegerKey = IntegerKey("coaddsDone")
    val detector:StringKey     = StringKey("detector")
    val operationalState: ChoiceKey = ChoiceKey("operationalState", OperationalState.toChoices())
    val errorMessage: StringKey        = StringKey("errorMessage")
    val exposureInProgress: BooleanKey = BooleanKey("exposureInProgress")
    val abortInProgress: BooleanKey    = BooleanKey("abortInProgress")
    val isAborted: BooleanKey          = BooleanKey("isAborted")
    val exposureTime: IntegerKey       = IntegerKey("exposureTime")
    val remainingExposureTime: IntegerKey = IntegerKey("remainingExposureTime")
    val readsInRamp: IntegerKey          = IntegerKey("readsInRamp")
    val readsComplete: IntegerKey          = IntegerKey("readsComplete")
    val rampsInExposure: IntegerKey        = IntegerKey("rampsInExposure")
    val rampsComplete: IntegerKey          = IntegerKey("rampsComplete")
    val downTimeReason: StringKey      = StringKey("reason")
    val filename: StringKey            = StringKey("filename")
}

// commonly used params factories
object ParamFactories {
    fun obsIdParam(obsId: ObsId) = ObserveEventKeys.obsId.set(obsId.toString())

    fun exposureIdParam(exposureId: ExposureId) = ObserveEventKeys.exposureId.set(exposureId.toString())

    fun coaddsInExposureParam(coaddsInExposure: Int) = ObserveEventKeys.coaddsInExposure.set(coaddsInExposure)

    fun coaddsDoneParam(coaddsDone: Int) = ObserveEventKeys.coaddsDone.set(coaddsDone)

    fun detectorParam(detector: String) = ObserveEventKeys.detector.set(detector)

    fun operationalStateParam(operationalState: OperationalState) = ObserveEventKeys.operationalState.set(operationalState.name)

    fun errorMessageParam(errorMessage: String) = ObserveEventKeys.errorMessage.set(errorMessage)

    fun exposureInProgressParam(exposureInProgress: Boolean) = ObserveEventKeys.exposureInProgress.set(exposureInProgress)

    fun abortInProgressParam(abortInProgress: Boolean) = ObserveEventKeys.abortInProgress.set(abortInProgress)

    fun isAbortedParam(isAborted: Boolean) = ObserveEventKeys.isAborted.set(isAborted)

    fun exposureTimeParam(exposureTime: Long) = ObserveEventKeys.exposureTime.set(exposureTime)

    fun remainingExposureTimeParam(remainingExposureTime: Long) = ObserveEventKeys.remainingExposureTime.set(remainingExposureTime)

    fun readsInRampParam(readsInRamp: Int) = ObserveEventKeys.readsInRamp.set(readsInRamp)

    fun readsCompleteParam(readsComplete: Int) = ObserveEventKeys.readsComplete.set(readsComplete)

    fun rampsInExposureParam(rampsInExposure: Int) = ObserveEventKeys.rampsInExposure.set(rampsInExposure)

    fun rampsCompleteParam(rampsComplete: Int) = ObserveEventKeys.rampsComplete.set(rampsComplete)

    fun downTimeReasonParam(reasonForDownTime: String) = ObserveEventKeys.downTimeReason.set(reasonForDownTime)

    fun filenameParam(filename: String) = ObserveEventKeys.filename.set(filename)
}
