package csw.params.events.observeevents

import csw.params.events.EventName

object ObserveEventNames {
    // common
    val ObserveStart: EventName = "ObserveStart"
    val ObserveEnd: EventName = "ObserveEnd"
    val ExposureStart: EventName = "ExposureStart"
    val ExposureEnd: EventName = "ExposureEnd"
    val ReadoutEnd: EventName = "ReadoutEnd"
    val ReadoutFailed: EventName = "ReadoutFailed"
    val DataWriteStart: EventName = "DataWriteStart"
    val DataWriteEnd: EventName = "DataWriteEnd"
    val ExposureAborted: EventName = "ExposureAborted"
    val PrepareStart: EventName = "PrepareStart"

    // IRDetector specific
    val IRDetectorExposureData: EventName = "IRDetectorExposureData"
    val IRDetectorExposureState: EventName = "IRDetectorExposureState"

    // OpticalDetector specific
    val OpticalDetectorExposureData: EventName = "OpticalDetectorExposureData"
    val OpticalDetectorExposureState: EventName = "OpticalDetectorExposureState"

    // WFSDetector specific
    val WfsDetectorExposureState: EventName = "WfsDetectorExposureState"
    val PublishSuccess: EventName = "PublishSuccess"
    val PublishFail: EventName = "PublishFail"

    // Sequencer specific
    val PresetStart: EventName = "PresetStart"
    val PresetEnd: EventName = "PresetEnd"
    val GuidestarAcqStart: EventName = "GuidestarAcqStart"
    val GuidestarAcqEnd: EventName = "GuidestarAcqEnd"
    val ScitargetAcqStart: EventName = "ScitargetAcqStart"
    val ScitargetAcqEnd: EventName = "ScitargetAcqEnd"
    val ObservationStart: EventName = "ObservationStart"
    val ObservationEnd: EventName = "ObservationEnd"
    val ObservePaused: EventName = "ObservePaused"
    val ObserveResumed: EventName = "ObserveResumed"
    val DowntimeStart: EventName = "DowntimeStart"

    // DMS specific
    val MetadataAvailable: EventName = "MetadataAvailable"
    val ExposureAvailable: EventName = "ExposureAvailable"
}
