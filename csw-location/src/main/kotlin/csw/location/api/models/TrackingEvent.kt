package csw.location.api.models

import csw.location.api.codecs.ModelCodecs
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TrackingEvent is used to represent location events while tracking the connection
 */
@Serializable
sealed interface TrackingEvent {

    /**
     * The connection for which this TrackingEvent is created
     */
    val connection: Connection
}

/**
 * This event represents modification in location details
 *
 * @param location the updated location for the tracked connection
 */
@Serializable
@SerialName("LocationUpdated")
data class LocationUpdated(val location: Location): TrackingEvent {

    /**
     * The connection for which this TrackingEvent is created
     */
    override val connection: Connection = location.connection
}

/**
 * This event represents unavailability of a location
 *
 * @param connection for which the location no longer exists
 */
@Serializable
@SerialName("LocationRemoved")
data class LocationRemoved(
    @Serializable(with=ModelCodecs.ConnectionSerializer::class)
    override val connection: Connection): TrackingEvent