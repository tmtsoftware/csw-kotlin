package csw.params.events.observeevents

import csw.params.commands.HasParms
import csw.params.commands.ParmsList
import csw.params.core.models.Prefix
import csw.params.core.models.Id
import csw.params.events.Event
import csw.params.events.EventName
import csw.params.keys.HasKey
import csw.params.keys.IsKey
import csw.time.core.models.UTCTime

/**
 * Defines an observe event. Constructor is private to ensure eventId is created internally to guarantee unique value.
 */
data class ObserveEvent private constructor(
    override val eventId: Id,
    override val source: Prefix,
    override val eventName: EventName,
    override val eventTime: UTCTime,
    override var parms: ParmsList = emptyList()
): HasParms, Event {

    /**
     * Create a new ObserveEvent instance when a parameter is added or removed
     *
     * @param data set of parameters
     * @return a new instance of ObserveEvent with new eventId, eventTime and provided data
     */
    //fun create(data: ParmsList): ObserveEvent =
//        copy(eventId = Id(), eventTime = UTCTime.now(), parms = data)

    override val typeName = "ObserveEvent"

    fun add(item1: HasKey, vararg items: HasKey): ObserveEvent =
        copy(eventId = Id(), parms = padd(this.parms, listOf(item1) + items.toList()))

    fun madd(vararg items: HasKey): ObserveEvent = copy(eventId = Id(), parms = padd(this.parms, items.toList()))

    fun remove(item: IsKey): ObserveEvent = copy(eventId = Id(), parms = removeOne(this.parms, item))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ObserveEvent) return false

        // Compares properties for structural equality
        return this.source == other.source && this.eventName == other.eventName &&
                this.parms.containsAll(other.parms) && other.parms.containsAll(parms)
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + eventName.hashCode()
        result = 31 * result + parms.hashCode()
        return result
    }

    companion object {

        /**
         * The invoke method is used to create ObserveEvent command by end-user. eventId is not accepted and will be created internally to guarantee unique value.
         *
         * @param source prefix representing source of the event
         * @param eventName the name of event
         * @return a new instance of ObserveEvent with auto-generated eventId, eventTime and empty paramSet
         */
        operator fun invoke(source: Prefix, eventName: EventName): ObserveEvent =
            ObserveEvent(Id(), source, eventName, UTCTime.now())

        /**
         * The apply method is used to create ObserveEvent command by end-user. eventId is not accepted and will be created internally to guarantee unique value.
         *
         * @param source prefix representing source of the event
         * @param eventName the name of event
         * @param parms an initial set of parameters (keys with values)
         * @return a new instance of ObserveEvent with auto-generated eventId and eventTime
         */
        operator fun invoke(source: Prefix, eventName: EventName, parms: ParmsList): ObserveEvent =
            invoke(source, eventName, parms)
    }
}
