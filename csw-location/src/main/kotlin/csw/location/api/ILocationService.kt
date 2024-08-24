package csw.csw.location.api

import csw.location.api.models.*
import csw.params.core.models.Prefix
import kotlin.time.Duration

/**
 * A LocationService interface to manage registrations. All operations are non-blocking.
 */
interface ILocationService {

    /**
     * Registers a connection -> location in cluster
     *
     * @param registration the Registration holding connection and it's corresponding location to register with `LocationService`
     * @return a future which completes with Registration result or can fail with
     *         [[csw.location.api.exceptions.RegistrationFailed]] or [[csw.location.api.exceptions.OtherLocationIsRegistered]]
     */
    suspend fun register(registration: Registration): Location // RegistrationResult. // Was Future

    /**
     * Unregisters the connection
     *
     * @note this method is idempotent, which means multiple call to unregister the same connection will be no-op once successfully
     *       unregistered from location service
     * @param connection an already registered connection
     * @return a future which completes after un-registration happens successfully and fails otherwise with
     *         [[csw.location.api.exceptions.UnregistrationFailed]]
     */
    suspend fun unregister(connection: Connection): Unit     //Future[Done]

    /**
     * Unregisters all connections
     *
     * @note it is highly recommended to use this method for testing purpose only
     * @return a future which completes after all connections are unregistered successfully or fails otherwise with
     *         [[csw.location.api.exceptions.RegistrationListingFailed]]
     */
    suspend fun unregisterAll():Unit // Future[Done]

    /**
     * Resolves the location for a connection from the local cache
     *
     * @param connection a connection to resolve to with its registered location
     * @return a future which completes with the resolved location if found or None otherwise. It can fail with
     *         [[csw.location.api.exceptions.RegistrationListingFailed]].
     */
    suspend fun find(connection: Connection):Location? //Future[Option[L]]

    /**
     * Resolves the location for a connection from the local cache, if not found waits for the event to arrive
     * within specified time limit. Returns None if both fail.
     *
     * @param connection a connection to resolve to with its registered location
     * @param within max wait time for event to arrive
     * @tparam L the concrete Location type returned once the connection is resolved
     * @return a future which completes with the resolved location if found or None otherwise. It can fail with
     *         [[csw.location.api.exceptions.RegistrationListingFailed]].
     */
    suspend fun resolve(connection: Connection, within: Duration): List<Location> //Future[Option[L]]

    /**
     * Lists all locations registered
     *
     * @return a future which completes with a List of all registered locations or can fail with
     *         [[csw.location.api.exceptions.RegistrationListingFailed]]
     */
    suspend fun list():List<Location>  // Future[List[Location]]

    /**
     * Filters all locations registered based on a component type
     *
     * @param componentType list components of this `componentType`
     * @return a future which completes with filtered locations or can fail with
     *         [[csw.location.api.exceptions.RegistrationListingFailed]]
     */
    suspend fun list(componentType: ComponentType):List<Location> // Future[List[Location]]

    /**
     * Filters all locations registered based on a hostname
     *
     * @param hostname list components running on this `hostname`
     * @return a future which completes with filtered locations or can fail with
     *         [[csw.location.api.exceptions.RegistrationListingFailed]]
     */
    suspend fun list(hostname: String):List<Location>  //Future[List[Location]]

    /**
     * Filters all locations registered based on a connection type
     *
     * @param connectionType list components of this `connectionType`
     * @return a future which completes with filtered locations or can fail with
     *         [[csw.location.api.exceptions.RegistrationListingFailed]]
     */
    suspend fun list(connectionType: ConnectionType):List<Location> //Future[List[Location]]

    /**
     * Filters all locations registered based on a prefix.
     *
     * @note all locations having subsystem prefix that starts with the given prefix
     *       value will be listed.
     * @param prefix list components by this `prefix`
     * @return a future which completes with filtered locations or can fail with
     *         [[csw.location.api.exceptions.RegistrationListingFailed]]
     */
    suspend fun listByPrefix(prefix: Prefix):List<Location>  //Future[List[Location]]

    /**
     * Tracks the connection and send events for modification or removal of its location
     *
     * @param connection the `connection` that is to be tracked
     * @return A stream that emits events related to the connection. It can be cancelled using KillSwitch. This will stop giving
     *         events for earlier tracked connection
     */
    suspend fun track(connection: Connection):Unit //Source[TrackingEvent, Subscription]

    /**
     * Subscribe to tracking events for a connection by providing a callback
     * For each event the callback is invoked.
     * Use this method if you do not want to handle materialization and happy with a side-effecting callback instead.
     *
     * @note Callbacks are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * @param connection the `connection` that is to be tracked
     * @param callback the callback function of type `TrackingEvent` => Unit which gets executed on receiving any `TrackingEvent`
     * @return a killswitch which can be shutdown to unsubscribe the consumer
     */
    suspend fun subscribe(connection: Connection, callback: (TrackingEvent) -> Int): Int /*Subscription */
}
