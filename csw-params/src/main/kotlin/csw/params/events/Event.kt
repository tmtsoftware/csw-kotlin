package csw.params.events

import csw.params.core.models.Prefix
import csw.params.core.models.Id
import csw.time.core.models.UTCTime
import kotlinx.datetime.Instant


typealias EventName = String

interface Event {

    /**
     * A helper to give access of public members of ParameterSetType
     *
     * @return a handle to ParameterSetType extended by concrete implementation of this class
     */
//    def paramType: ParameterSetType[_] = self

    /**
     * unique Id for event
     */
    val eventId: Id

    /**
     * Prefix representing source of the event
     */
    val source: Prefix

    /**
     * The name of event
     */
    val eventName: EventName

    /**
     * The time of event creation
     */
    val eventTime: UTCTime

    /**
     * A name identifying the type of parameter set, such as "SystemEvent", "ObserveEvent".
     * This is used in the JSON and toString output.
     *
     * @return a string representation of concrete type of this class
     */
    val typeName: String

    /*
     * The EventKey used to publish or subscribe an event
     *
     * @return an EventKey formed by combination of prefix and eventName of an event
     */
    val eventKey: EventKey
        get() = EventKey(source, eventName)


    fun isInvalid(): Boolean = eventTime == invalidEventTime

    companion object {
        private val invalidEventTime = UTCTime(Instant.fromEpochMilliseconds(-1L))

        /**
         * A helper method to create an event which is provided to subscriber when there is no event available at the
         * time of subscription
         *
         * @param eventKey the Event Key for which subscription was made
         * @return an event with the same key as provided but with id and timestamp denoting an invalid event
         */
        fun invalidEvent(eventKey: EventKey): SystemEvent =
            SystemEvent(eventKey.source, eventKey.eventName).copy(eventId = Id("-1"), eventTime = invalidEventTime)

        /**
         * A helper method to create an event which is provided to subscriber when the received bytes could not be
         * decoded into a valid event
         *
         * @return an invalid event with the key representing a bad key by using a BAD subsystem
         */
        //      fun badEvent(): SystemEvent = Event.invalidEvent(EventKey("${Subsystem.CSW}.parse.fail"))

    }
}