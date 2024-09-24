package csw.params.events

import csw.params.commands.HasParms
import csw.params.commands.ParmsList
import csw.params.core.models.Prefix
import csw.params.core.models.Id
import csw.params.keys.HasKey
import csw.params.keys.IsKey
import csw.time.core.models.UTCTime

data class SystemEvent (
    override val eventId: Id,
    override val source: Prefix,
    override val eventName: EventName,
    override val eventTime: UTCTime,
    override var paramSet: ParmsList = emptyList()
): HasParms, Event {
    override val _type: String = javaClass.simpleName

    /**
     * A common toString method for all concrete implementation
     *
     * @return the string representation of command
     */
    override fun toString(): String =
        "$typeName(eventId=$eventId, source=$source, eventName=$eventName, eventTime=$eventTime, parms=$paramSet)"

    override val typeName = "SystemEvent"

    fun add(item1: HasKey, vararg items: HasKey): SystemEvent =
        copy(eventId = Id(), paramSet = padd(this.paramSet, listOf(item1) + items.toList()))

    fun madd(parmsToAdd: ParmsList): SystemEvent = copy(eventId = Id(), paramSet = padd(paramSet, parmsToAdd))

    fun madd(vararg items: HasKey): SystemEvent = copy(eventId = Id(), paramSet = padd(this.paramSet, items.toList()))

    fun remove(item: IsKey): SystemEvent = copy(eventId = Id(), paramSet = removeOne(this.paramSet, item))

    /**
     * Create a new SystemEvent instance when a parameter is added or removed
     *
     * @param data set of parameters
     * @return a new instance of SystemEvent with new eventId, eventTime and provided data
     */
//    internal fun create(data: ParmsList): SystemEvent =
//        copy(eventId = Id(), eventTime = UTCTime.now(), parms = data)

    companion object {

        operator fun invoke(source: Prefix, eventName: EventName): SystemEvent =
            SystemEvent(Id(), source, eventName, UTCTime.now())

        operator fun invoke(source: Prefix, eventName: EventName, parms: ParmsList): SystemEvent =
            SystemEvent(Id(), source, eventName, UTCTime.now(), parms)
    }
}