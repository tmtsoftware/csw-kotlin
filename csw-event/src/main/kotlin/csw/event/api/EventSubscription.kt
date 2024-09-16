package csw.event.api

/**
 * An interface to represent a subscription. On subscribing to one or more Event Keys using the [[csw.event.api.scaladsl.EventSubscriber]],
 * the subscriber gets a handle to that particular subscription so as to perform some subscription specific tasks.
 */
interface EventSubscription {

    /**
     * To unsubscribe a given subscription. This will also clean up subscription specific underlying resources
     *
     * @return a future which completes when the unsubscribe is completed
     */
    fun unsubscribe(): Unit //Future[Done]

    /**
     * To check if the underlying subscription is ready to emit elements
     *
     * @return a future which completes when the underlying subscription is ready to emit elements
     */
    fun ready(): Unit // Future[Done]
}
