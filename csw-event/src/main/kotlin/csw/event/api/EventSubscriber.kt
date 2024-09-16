package csw.event.api

import csw.params.core.models.Subsystem
import csw.params.events.Event
import csw.params.events.EventKey
import kotlin.time.Duration

/**
 * An EventSubscriber interface to subscribe events. The events can be subscribed on [[csw.params.events.EventKey]]. All events published on this key
 * will be received by subscribers.
 */
interface EventSubscriber {

    /**
     * Subscribe to multiple Event Keys and get a single stream of events for all event keys. The latest events available for the given
     * Event Keys will be received first. If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * At the time of invocation, in case the underlying server is not available [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately. In all other cases of exception, as per the default behavior, the stream will stop.
     * To avoid that, user should provide a resuming materializer while running the stream.
     *
     * @note All the other APIs of [[csw.event.api.scaladsl.EventSubscriber]] that do not return a [[akka.stream.scaladsl.Source]],
     *       internally use the resuming materializer which will ignore the failed event and resume receiving further events.
     * @param eventKeys a set of [[csw.params.events.EventKey]] to subscribe to
     * @return a [[akka.stream.scaladsl.Source]] of [[csw.params.events.Event]]. The materialized value of the source provides
     *         an [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from all the Event Keys which were subscribed to
     */
    fun subscribe(eventKeys: Set<EventKey>): Unit // Source[Event, EventSubscription]

    /**
     * Subscribe to multiple eventKeys and receive events at `every` frequency according to the specified `mode` (RateAdapter or RateLimiter).
     * The latest events available for the given Event Keys will be received first.
     * If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately. In all other cases of exception, as per the default behavior, the stream will stop.
     * To avoid that, user should provide a resuming materializer while running the stream.
     *
     * @note All the other APIs of [[csw.event.api.scaladsl.EventSubscriber]] that do not return a [[akka.stream.scaladsl.Source]],
     *       internally use the resuming materializer which will ignore the failed event and resume receiving further events.
     * @param eventKeys a set of [[csw.params.events.EventKey]] to subscribe to
     * @param every the duration which determines the frequency with which events are received
     * @param mode an appropriate [[csw.event.api.scaladsl.SubscriptionMode]] to control the behavior of rate of events w.r.t. the given frequency.
     *             Refer the API documentation for SubscriptionMode for more details
     * @return a [[akka.stream.scaladsl.Source]] of [[csw.params.events.Event]]. The materialized value of the source provides an
     *         [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from all the Event Keys which were subscribed to
     */
    fun subscribe(eventKeys: Set<EventKey>, every: Duration, mode: SubscriptionMode): Unit //Source[Event, EventSubscription]

    /**
     * Subscribes an asynchronous callback function to events from multiple eventKeys. The callback is of type event => future
     * and it ensures that the event callbacks are called sequentially in such a way that the subsequent execution will
     * start only after the prior one completes. This API gives the guarantee of ordered execution of the asynchronous callbacks.
     *
     * The latest events available for the given Event Keys will be received first.
     * If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the subscription is stopped after logging appropriately. [[csw.event.api.scaladsl.EventSubscription!.ready]] method can be used to determine
     * this state. In all other cases of exception, the subscription resumes to receive remaining elements.
     *
     * @note Callbacks are not thread-safe on the JVM. If you need to do side effects/mutations, prefer using `subscribeActorRef` API.
     * @param eventKeys a set of [[csw.params.events.EventKey]] to subscribe to
     * @param callback a function to execute asynchronously on each received event
     * @return an [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from all the Event Keys which were subscribed to
     */
    //fun subscribeAsync(eventKeys: Set[EventKey], callback: Event => Future[_]): EventSubscription

    /**
     * Overload for above `subscribeAsync` for receiving event at a `every` frequency according to the specified `mode`. The latest events available for the given
     * Event Keys will be received first. If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the subscription is stopped after logging appropriately. [[csw.event.api.scaladsl.EventSubscription!.ready]] method can be used to determine
     * this state. In all other cases of exception, the subscription resumes to receive remaining elements.
     *
     * @param eventKeys a set of [[csw.params.events.EventKey]] to subscribe to
     * @param callback a function to execute on each received event
     * @param every the duration which determines the frequency with which events are received
     * @param mode an appropriate [[csw.event.api.scaladsl.SubscriptionMode]] to control the behavior of rate of events w.r.t. the given frequency.
     *             Refer the API documentation for SubscriptionMode for more details
     * @return an [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from all the Event Keys which were subscribed to
     */
    /*
    def subscribeAsync(
            eventKeys: Set[EventKey],
    callback: Event => Future[_],
    every: FiniteDuration,
    mode: SubscriptionMode
    ): EventSubscription
*/
    /**
     * Subscribes a callback function to events from multiple event keys. The latest events available for the given Event Keys will be received first.
     * If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the subscription is stopped after logging appropriately. [[csw.event.api.scaladsl.EventSubscription!.ready]] method can be used to determine this
     * state. In all other cases of exception, the subscription resumes to receive remaining elements.
     *
     * @note Callbacks are not thread-safe on the JVM. If you need to do side effects/mutations, prefer using `subscribeActorRef` API.
     * Also note that any exception thrown from `callback` is expected to be handled by the component developers.
     * @param eventKeys a set of [[csw.params.events.EventKey]] to subscribe to
     * @param callback a function to execute on each received event
     * @return an [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from all the Event Keys which were subscribed to
     */
    fun subscribeCallback(eventKeys: Set<EventKey>, callback: (Event) -> Unit): EventSubscription

    /**
     * Overload for above `subscribeCallback` for receiving event at a `every` frequency according to the specified `mode`.
     * The latest events available for the given Event Keys will be received first.
     * If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the subscription is stopped after logging appropriately. [[csw.event.api.scaladsl.EventSubscription!.ready]] method can be used to determine this
     * state. In all other cases of exception, the subscription resumes to receive remaining elements.
     *
     * @param eventKeys a set of [[csw.params.events.EventKey]] to subscribe to
     * @param callback a function to execute on each received event
     * @param every the duration which determines the frequency with which events are received
     * @param mode an appropriate [[csw.event.api.scaladsl.SubscriptionMode]] to control the behavior of rate of events w.r.t. the given frequency.
     *             Refer the API documentation for SubscriptionMode for more details
     * @return an [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from all the Event Keys which were subscribed to
     */
    fun subscribeCallback(
        eventKeys: Set<EventKey>,
        callback: (Event) -> Unit,
        every: Duration,
        mode: SubscriptionMode
    ): EventSubscription

    /**
     * Subscribes an actor to events from multiple event keys. The latest events available for the given
     * Event Keys will be received first. If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the subscription is stopped after logging appropriately. [[csw.event.api.scaladsl.EventSubscription!.ready]] method can be used to determine this
     * state. In all other cases of exception, the subscription resumes to receive remaining elements.
     *
     * @param eventKeys a set of [[csw.params.events.EventKey]] to subscribe to
     * @param actorRef an actorRef of an actor which handles each received event
     * @return an [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from all the Event Keys which were subscribed to
     */
    //def subscribeActorRef(eventKeys: Set[EventKey], actorRef: ActorRef[Event]): EventSubscription

    /**
     * Overload for above `subscribeActorRef` for receiving event at a `every` frequency according to the specified `mode`.
     * The latest events available for the given Event Keys will be received first.
     * If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the subscription is stopped after logging appropriately. [[csw.event.api.scaladsl.EventSubscription!.ready]] method can be used to determine this
     * state. In all other cases of exception, the subscription resumes to receive remaining elements.
     *
     * @param eventKeys a set of [[csw.params.events.EventKey]] to subscribe to
     * @param actorRef an actorRef of an actor to which each received event is redirected
     * @param every the duration which determines the frequency with which events are received
     * @param mode an appropriate [[csw.event.api.scaladsl.SubscriptionMode]] to control the behavior of rate of events w.r.t. the given frequency.
     *             Refer the API documentation for SubscriptionMode for more details
     * @return an [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from all the Event Keys which were subscribed to
     */
    /*
    def subscribeActorRef(
            eventKeys: Set[EventKey],
    actorRef: ActorRef[Event],
    every: FiniteDuration,
    mode: SubscriptionMode
    ): EventSubscription
*/
    /**
     * Subscribe to events from Event Keys specified using a subsystem and a pattern to match the remaining Event Key. The latest events available for the given
     * Event Keys will be received first. If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the subscription is stopped after logging appropriately. [[csw.event.api.scaladsl.EventSubscription!.ready]] method can be used to determine this
     * state. In all other cases of exception, the subscription resumes to receive remaining elements.
     *
     * @param subsystem a valid `Subsystem` which represents the source of the events
     * @param pattern   Subscribes the client to the given patterns. Supported glob-style patterns:
     *                  - h?llo subscribes to hello, hallo and hxllo
     *                  - h*llo subscribes to hllo and heeeello
     *                  - h[ae]llo subscribes to hello and hallo, but not hillo
     *                  Use \ to escape special characters if you want to match them verbatim.
     * @return a [[akka.stream.scaladsl.Source]] of [[csw.params.events.Event]]. The materialized value of the source provides
     *         an [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from all the Event Keys which were subscribed to
     */
    fun pSubscribe(subsystem: Subsystem, pattern: String):Unit // Source[Event, EventSubscription]

    /**
     * Subscribes a callback to events from Event Keys specified using a subsystem and a pattern to match the remaining Event Key.
     *
     * The latest events available for the given Event Keys will be received first.
     * If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the subscription is stopped after logging appropriately. [[csw.event.api.scaladsl.EventSubscription!.ready]] method can be used to determine this
     * state. In all other cases of exception, the subscription resumes to receive remaining elements.
     *
     * @note Callbacks are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * @param subsystem a valid `Subsystem` which represents the source of the events
     * @param pattern   Subscribes the client to the given patterns. Supported glob-style patterns:
     * - h?llo subscribes to hello, hallo and hxllo
     * - h*llo subscribes to hllo and heeeello
     * - h[ae]llo subscribes to hello and hallo, but not hillo
     *                  Use \ to escape special characters if you want to match them verbatim.
     * @param callback  a function to execute on each received event
     * @return an [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from all the Event Keys which were subscribed to
     */
    fun pSubscribeCallback(subsystem: Subsystem, pattern: String, callback: (Event) -> Unit): EventSubscription

    /**
     * ************ IMPORTANT ************
     * This API uses redis pattern subscription. Having many live pattern subscriptions causes overall latency degradation.
     * Prefer subscribe API over this whenever possible.
     * *********** ********* ************
     *
     * Subscribe to all the observe events
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the subscription is stopped after logging appropriately. [[csw.event.api.scaladsl.EventSubscription!.ready]] method can be used to determine this
     * state. In all other cases of exception, the subscription resumes to receive remaining elements.
     *
     * @return a [[akka.stream.scaladsl.Source]] of [[csw.params.events.Event]]. The materialized value of the source provides
     *         an [[csw.event.api.scaladsl.EventSubscription]] which can be used to unsubscribe from observe events subscription
     */
    fun subscribeObserveEvents():Unit //Source[Event, EventSubscription]

    /**
     * Get latest events for multiple Event Keys. The latest events available for the given Event Keys will be received first.
     * If event is not published for one or more event keys, `invalid event` will be received for those Event Keys.
     *
     * In case the underlying server is not available, the future fails with [[csw.event.api.exceptions.EventServerNotAvailable]] exception.
     * In all other cases of exception, the future fails with the respective exception
     *
     * @param eventKeys a set of [[csw.params.events.EventKey]] to subscribe to
     * @return a future which completes with a set of latest [[csw.params.events.Event]] for the provided Event Keys
     */
    fun get(eventKeys: Set<EventKey>): Set<Event>

    /**
     * Get latest event for the given Event Key. If an event is not published for any eventKey, then `invalid event` is returned for that Event Key.
     *
     * In case the underlying server is not available, the future fails with [[csw.event.api.exceptions.EventServerNotAvailable]] exception.
     * In all other cases of exception, the future fails with the respective exception
     *
     * @param eventKey an [[csw.params.events.EventKey]] to subscribe to
     * @return a future which completes with the latest [[csw.params.events.Event]] for the provided Event Key
     */
    fun get(eventKey: EventKey): Event? //Future[Event]
}