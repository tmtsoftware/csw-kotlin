package csw.params.events.observeevents

import csw.params.core.models.Prefix
import csw.params.core.models.ExposureId
import csw.params.core.models.ObsId
import csw.params.events.*

object ObserveEvents {

    /**
     * This event indicates the start of the preset phase of  acquisition
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun presetStart(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.PresetStart, obsId)

    /**
     * This event indicates the end of the preset phase of  acquisition
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun presetEnd(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.PresetEnd, obsId)

    /**
     * This event indicates the start of locking the telescope to the  sky with guide and WFS targets
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun guidestarAcqStart(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source,ObserveEventNames.GuidestarAcqStart, obsId)

    /**
     * This event indicates the end of locking the telescope to the sky with guide and WFS targets
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun guidestarAcqEnd(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.GuidestarAcqEnd, obsId)

    /**
     * This event indicates the start of acquisition phase where  science target is peaked up as needed after  guidestar locking
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun scitargetAcqStart(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.ScitargetAcqStart, obsId)

    /**
     * This event indicates the end of acquisition phase where  science target is centered as needed after  guidestar locking
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun scitargetAcqEnd(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.ScitargetAcqEnd, obsId)

    /**
     * This event indicates the start of execution of actions related  to an observation including acquisition and  science data acquisition.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun observationStart(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.ObservationStart, obsId)

    /**
     * This event indicates the end of execution of actions related  to an observation including acquisition and  science data acquisition.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun observationEnd(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.ObservationEnd, obsId)

    /**
     * This event indicates the start of execution of actions related to an Observe command.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun observeStart(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.ObserveStart, obsId)

    /**
     * This event indicates the end of execution of actions related  to an Observe command.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun observeEnd(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.ObserveEnd, obsId)

    /**
     * This event indicates that a user has paused the current  observation Sequence which will happen after  the current step concludes
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun observePaused(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.ObservePaused, obsId)

    /**
     * This event indicates that a user has resumed a paused  observation Sequence.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun observeResumed(source: Prefix, obsId: ObsId): ObserveEvent =
        create(source, ObserveEventNames.ObserveResumed, obsId)

    /**
     * This event indicates the start of data acquisition that  results in a file produced for DMS. This is a potential metadata event for DMS.
     * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
     *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
     *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
     *                   when the ExposureId is created.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun prepareStart(source: Prefix, exposureId: ExposureId): ObserveEvent =
        create(source, ObserveEventNames.PrepareStart, exposureId)

    /**
     * This event indicates the start of data acquisition that  results in a file produced for DMS. This is a potential metadata event for DMS.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
     *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
     *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
     *                   when the ExposureId is created.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun exposureStart(source: Prefix, exposureId: ExposureId): ObserveEvent =
        create(source, ObserveEventNames.ExposureStart, exposureId)

    /**
     * This event indicates the end of data acquisition that results  in a file produced for DMS. This is a potential metadata event for DMS.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
     *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
     *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
     *                   when the ExposureId is created.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun exposureEnd(source: Prefix, exposureId: ExposureId): ObserveEvent =
        create(source, ObserveEventNames.ExposureEnd, exposureId)

    /**
     * This event indicates that a readout that is part of a ramp  has completed.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
     *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
     *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
     *                   when the ExposureId is created.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun readoutEnd(source: Prefix, exposureId: ExposureId): ObserveEvent =
        create(source, ObserveEventNames.ReadoutEnd, exposureId)

    /**
     * This event indicates that a readout that is part of a ramp  has failed indicating transfer failure or some  other issue.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
     *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
     *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
     *                   when the ExposureId is created.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun readoutFailed(source: Prefix, exposureId: ExposureId): ObserveEvent =
        create(source, ObserveEventNames.ReadoutFailed, exposureId)

    /**
     * This event indicates that the instrument has started writing  the exposure data file or transfer of exposure  data to DMS.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
     *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
     *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
     *                   when the ExposureId is created.
     * @param filename   [[java.lang.String]] the path of the file.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun dataWriteStart(source: Prefix, exposureId: ExposureId, filename: String): ObserveEvent =
        create(source, exposureId, ObserveEventNames.DataWriteStart, filename)

    /**
     * This event indicates that the instrument has finished  writing the exposure data file or transfer of  exposure data to DMS.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
     *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
     *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
     *                   when the ExposureId is created.
     * @param filename   [[java.lang.String]] the path of the file.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun dataWriteEnd(source: Prefix, exposureId: ExposureId, filename: String): ObserveEvent =
        create(source, exposureId, ObserveEventNames.DataWriteEnd, filename)

    /**
     * This event indicates that a request was made to abort the  exposure, and it has completed. Normal data events should occur if data is  recoverable.
     * Abort should not fail
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
     *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
     *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
     *                   when the ExposureId is created.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun exposureAborted(source: Prefix, exposureId: ExposureId): ObserveEvent =
        create(source, ObserveEventNames.ExposureAborted, exposureId)

    /**
     * This event indicates DMS has ingested the metadata following the exposureEnd.
     * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
     *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
     *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
     *                   when the ExposureId is created.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun metadataAvailable(source: Prefix, exposureId: ExposureId): ObserveEvent =
        create(source, ObserveEventNames.MetadataAvailable, exposureId)

    /**
     * This event  indicates that the raw science exposure has been stored and internal databases have been updated such that a client can request the exposure.
     * @param exposureId [[csw.params.core.models.ExposureId]] is an identifier in ESW/DMS for a single exposure.
     *                   The ExposureId follows the structure: 2020A-001-123-WFOS-IMG1-SCI0-0001 with an included ObsId or
     *                   when no ObsId is present, in the standalone format: 20200706-190204-WFOS-IMG1-SCI0-0001 with a UTC time
     *                   when the ExposureId is created.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun exposureAvailable(source: Prefix, exposureId: ExposureId): ObserveEvent =
        create(source, ObserveEventNames.ExposureAvailable, exposureId)

    /**
     * This event indicates the WFS or guider detector system has successfully published an image to VBDS.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun publishSuccess(source: Prefix): ObserveEvent = create(source, ObserveEventNames.PublishSuccess)

    /**
     * This event indicates that a WFS or guider detector system  has failed while publishing an image to VBDS.
     * @param source [[csw.params.commands.Prefix]] the prefix identifier of the source which is generating this event.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun publishFail(source: Prefix): ObserveEvent = create(source, ObserveEventNames.PublishFail)

    /**
     * This event indicates that something has occurred that  interrupts the normal observing workflow and  time accounting.
     * This event will have a hint (TBD) that indicates  the cause of the downtime for statistics.
     * Examples are: weather, equipment or other  technical failure, etc.
     * Downtime is ended by the start of an observation  or exposure.
     * @param obsId [[csw.params.core.models.ObsId]] Represents a unique observation id
     * @param reasonForDowntime [[java.lang.String]] a hint that indicates the cause of the downtime for statistics.
     * @return [[csw.params.events.observeevents.ObserveEvent]]
     */
    fun downtimeStart(source: Prefix, obsId: ObsId, reasonForDowntime: String): ObserveEvent {
        val obsIdParam          = ParamFactories.obsIdParam(obsId)
        val downtimeReasonParam = ParamFactories.downTimeReasonParam(reasonForDowntime)
        return ObserveEvent(source, ObserveEventNames.DowntimeStart).add(obsIdParam, downtimeReasonParam)
    }

    private fun create(source: Prefix, eventName: EventName): ObserveEvent = ObserveEvent(source, eventName)

    private fun create(source: Prefix, eventName: EventName, obsId: ObsId): ObserveEvent =
            ObserveEvent(source, eventName).add(ParamFactories.obsIdParam(obsId))

    private fun create(source: Prefix, eventName: EventName, exposureId: ExposureId): ObserveEvent =
        ObserveEvent(source, eventName).add(ParamFactories.exposureIdParam(exposureId))

    private fun create(
        source: Prefix,
        exposureId: ExposureId,
        eventName: EventName,
        filename: String
    ): ObserveEvent =
        ObserveEvent(source, eventName).add(
            ParamFactories.exposureIdParam(exposureId),
            ParamFactories.filenameParam(filename)
        )

}