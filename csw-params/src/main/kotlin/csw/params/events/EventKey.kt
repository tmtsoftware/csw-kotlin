package csw.params.events

import csw.params.core.models.Prefix

/**
 * A wrapper class representing the key for an event e.g. IRIS.guide.probe3.oiwfsDemands
 *
 * @param source represents the prefix of the component that publishes this event
 * @param eventName represents the name of the event
 */
data class EventKey(val source: Prefix, val eventName: EventName) {
    override fun toString(): String = key

    val key = "${source}${DELIM}$eventName"

    companion object {
        private const val DELIM = "."

        operator fun invoke(eventKeyStr: String): EventKey {
            val eventNameStr = eventKeyStr.substringAfterLast(DELIM)  // After last .
            val prefixStr = eventKeyStr.substringBeforeLast(DELIM)  // All before last .
            return EventKey(Prefix(prefixStr), eventNameStr)
        }
    }
}