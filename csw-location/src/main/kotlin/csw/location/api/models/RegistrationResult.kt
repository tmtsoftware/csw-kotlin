package csw.location.api.models

/**
 * RegistrationResult represents successful registration of a location
 */
interface RegistrationResult {

    /**
     * The successful registration of location can be unregistered using this method
     *
     * @note this method is idempotent, which means multiple call to unregister the same connection will be no-op once successfully
     *       unregistered from location service
     * @return a future which completes when un-registrstion is done successfully or fails with
     *         [[csw.location.api.exceptions.UnregistrationFailed]]
     */
    fun unregister(): Unit //Future[Done]

    /**
     * The `unregister` method will use the connection of this location to unregister from `LocationService`
     *
     * @return the handle to the `Location` that got registered in `LocationService`
     */
    val location: Location
}
/*
object RegistrationResult {
    fun from(_location: Location, (_unregister: Connection) -> Unit /* Future<Done> */): RegistrationResult =
        RegistrationResult {
            override def unregister(): Future[Done] = _unregister(location.connection)
            override def location: Location         = _location
        }
}

 */
