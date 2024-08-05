package csw.params.events.observeevents

import csw.params.core.models.Prefix
import csw.params.core.models.ExposureId
import csw.params.events.EventName

/**
 * Shared class for state events
 */
private fun exposureState(
    eventName: EventName,
    sourcePrefix: Prefix,
    exposureId: ExposureId,
    exposureInProgress: Boolean,
    abortInProgress: Boolean,
    isAborted: Boolean,
    operationalState: OperationalState,
    errorMessage: String): ObserveEvent =
        ObserveEvent(sourcePrefix, eventName).add(
            ParamFactories.exposureIdParam(exposureId),
            ParamFactories.operationalStateParam(operationalState),
            ParamFactories.errorMessageParam(errorMessage),
            ParamFactories.exposureInProgressParam(exposureInProgress),
            ParamFactories.abortInProgressParam(abortInProgress),
            ParamFactories.isAbortedParam(isAborted)
        )

/**
 * A state variable to indicate the current state of the IR detector system.
 * The Exposure State Event groups  parameters that change relatively slowly, and
 * this event should be published whenever any of its  parameters changes.
 * @param sourcePrefix [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
 * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
 *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
 *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
 *                   when the ExposureId is created.
 * @param exposureInProgress [[java.lang.Boolean]] indicating if detector system is acquiring an exposure.
 *                           Delimited by exposureStart and exposureEnd. exposureInProgress should be false if abortInProgress is true (TBD)
 * @param abortInProgress [[java.lang.Boolean]] indicates that an abort has been requested and is underway.
 * @param isAborted [[java.lang.Boolean]] Indicates that an abort has occurred and is completed. abortInProgress should be false when isAborted is true.
 *                  isAborted should be set to false with the next exposure
 * @param errorMessage [[java.lang.String]] An parameter that can be included when the detector system  is in the ERROR operationalState.
 *                     This value should be cleared and removed from the state when the  operationalState returns to READY
 * @param operationalState [[csw.params.events.observeevents.OperationalState]] indicating if the detector system is available and  operational.
 * @return [[csw.params.events.observeevents.ObserveEvent]]
 */
fun irExposureStateEvent(
    sourcePrefix: Prefix,
    exposureId: ExposureId,
    exposureInProgress: Boolean,
    abortInProgress: Boolean,
    isAborted: Boolean,
    errorMessage: String,
    operationalState: OperationalState): ObserveEvent =
        exposureState(
            ObserveEventNames.IRDetectorExposureState,
            sourcePrefix,
            exposureId,
            exposureInProgress,
            abortInProgress,
            isAborted,
            operationalState,
            errorMessage)


/**
 * A state variable to indicate the current state of an optical detector system.
 * The Exposure State Event groups  parameters that change relatively slowly, and
 * this event should be published whenever any of its  parameters changes.
 * @param sourcePrefix [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
 * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
 *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
 *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
 *                   when the ExposureId is created.
 * @param exposureInProgress [[java.lang.Boolean]] indicating if detector system is acquiring an exposure.
 *                           Delimited by exposureStart and exposureEnd. exposureInProgress should be false if abortInProgress is true (TBD)
 * @param abortInProgress [[java.lang.Boolean]] indicates that an abort has been requested and is underway.
 * @param isAborted [[java.lang.Boolean]] Indicates that an abort has occurred and is completed. abortInProgress should be false when isAborted is true.
 *                  isAborted should be set to false with the next exposure
 * @param errorMessage [[java.lang.String]] An parameter that can be included when the detector system  is in the ERROR operationalState.
 *                     This value should be cleared and removed from the state when the  operationalState returns to READY
 * @param operationalState [[csw.params.events.observeevents.OperationalState]] indicating if the detector system is available and  operational.
 * @return [[csw.params.events.observeevents.ObserveEvent]]
 */
fun opticalExposureStateEvent(
    sourcePrefix: Prefix,
    exposureId: ExposureId,
    exposureInProgress: Boolean,
    abortInProgress: Boolean,
    isAborted: Boolean,
    operationalState: OperationalState,
    errorMessage: String
    ): ObserveEvent =
    exposureState(
        ObserveEventNames.OpticalDetectorExposureState,
        sourcePrefix,
        exposureId,
        exposureInProgress,
        abortInProgress,
        isAborted,
        operationalState,
        errorMessage)

/**
 * A state variable to indicate the current state of a guider or wavefront sensing detector system.
 * The Exposure State Event groups  parameters that change relatively slowly, and
 * this event should be published whenever any of its  parameters changes.
 * @param sourcePrefix [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
 * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
 *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
 *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
 *                   when the ExposureId is created.
 * @param exposureInProgress [[java.lang.Boolean]] indicating if detector system is acquiring an exposure.
 *                           Delimited by exposureStart and exposureEnd. exposureInProgress should be false if abortInProgress is true (TBD)
 * @param abortInProgress [[java.lang.Boolean]] indicates that an abort has been requested and is underway.
 * @param isAborted [[java.lang.Boolean]] Indicates that an abort has occurred and is completed. abortInProgress should be false when isAborted is true.
 *                  isAborted should be set to false with the next exposure
 * @param errorMessage [[java.lang.String]] An parameter that can be included when the detector system  is in the ERROR operationalState.
 *                     This value should be cleared and removed from the state when the  operationalState returns to READY
 * @param operationalState [[csw.params.events.observeevents.OperationalState]] indicating if the detector system is available and  operational.
 * @return [[csw.params.events.observeevents.ObserveEvent]]
 */
fun wfsGuiderExposureStateEvent(
    sourcePrefix: Prefix,
    exposureId: ExposureId,
    exposureInProgress: Boolean,
    abortInProgress: Boolean,
    isAborted: Boolean,
    operationalState: OperationalState,
    errorMessage: String): ObserveEvent =
    exposureState(
        ObserveEventNames.WfsDetectorExposureState,
        sourcePrefix,
        exposureId,
        exposureInProgress,
        abortInProgress,
        isAborted,
        operationalState,
        errorMessage)
