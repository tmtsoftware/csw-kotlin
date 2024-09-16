package csw.event.api

import csw.params.events.Event


/**
 * An EventPublisher interface to publish events. The published events are published on a key determined by [[csw.params.events.EventKey]]
 * in the [[csw.params.events.Event]] model. This key can be used by the subscribers using [[csw.event.api.scaladsl.EventSubscriber]]
 * interface to subscribe to the events.
 */
interface EventPublisher {

    /**
     * Publish a single [[csw.params.events.Event]]
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown,
     * in all other cases [[csw.event.api.exceptions.PublishFailure]] exception is thrown which wraps the underlying exception and
     * also provides the handle to the event which was failed to be published
     *
     * @param event an event to be published
     * @return a future which completes when the event is published
     */
    suspend fun publish(event: Event): Unit // Future[Done]

    /**
     * Publish from a stream of [[csw.params.events.Event]]
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately.
     * In all other cases of exception, the stream receives a [[csw.event.api.exceptions.PublishFailure]] exception
     * which wraps the underlying exception. The stream resumes to publish remaining elements in case of this exception.
     *
     * @param source a [[akka.stream.scaladsl.Source]] of events to be published.
     *               Any resource cleanup or exception handling of the provided source is to be managed by the source provider
     * @tparam Mat represents the type of materialized value as defined in the source to be obtained on running the stream
     * @return the materialized value obtained on running the stream
     */
    //fun publish[Mat](source: Source[Event, Mat]): Mat

    /**
     * Publish from a stream of [[csw.params.events.Event]], and execute `onError` callback for events whose publishing failed.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately.
     * In all other cases of exception, the stream receives a [[csw.event.api.exceptions.PublishFailure]] exception
     * which wraps the underlying exception and also provides the handle to the event which was failed to be published.
     * The provided callback is executed on the failed element and the stream resumes to publish remaining elements.
     *
     * @note Callbacks like `onError` are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * @param source  a [[akka.stream.scaladsl.Source]] of events to be published.
     *                Any resource cleanup or exception handling of the provided source is to be managed by the source provider
     * @param onError a callback to execute for each event for which publishing failed
     * @tparam Mat represents the type of materialized value as defined in the source to be obtained on running the stream
     * @return the materialized value obtained on running the stream
     */
    //def publish[Mat](source: Source[Event, Mat], onError: PublishFailure => Unit): Mat

    /**
     * Publish [[csw.params.events.Event]] from an `eventGenerator` function, which will be executed at `every` frequency. `Cancellable` can be used to cancel
     * the execution of `eventGenerator` function.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately.
     * In all other cases of exception, the stream receives a [[csw.event.api.exceptions.PublishFailure]] exception
     * which wraps the underlying exception. The generator resumes to publish remaining elements in case of this exception.
     *
     * @note Callbacks like `eventGenerator` are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * @param eventGenerator a function which can generate an event to be published at `every` frequency
     * @param every frequency with which the events are to be published
     * @return a handle to cancel the event generation through `eventGenerator`
     */
    //fun publish(eventGenerator: => Option[Event], every: FiniteDuration): Cancellable

    /**
     * Publish [[csw.params.events.Event]] from an `eventGenerator` function, which will be started at the specified `startTime`
     * and will be executed at `every` frequency.
     * `Cancellable` can be used to cancel the execution of `eventGenerator` function.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately.
     * In all other cases of exception, the stream receives a [[csw.event.api.exceptions.PublishFailure]] exception
     * which wraps the underlying exception. The generator resumes to publish remaining elements in case of this exception.
     *
     * @note Callbacks like `eventGenerator` are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * @param eventGenerator a function which can generate an event to be published at `every` frequency
     * @param startTime the time at which the `eventGenerator` should start generating events
     * @param every frequency with which the events are to be published
     * @return a handle to cancel the event generation through `eventGenerator`
     */
    //fun publish(eventGenerator: => Option[Event], startTime: TMTTime, every: FiniteDuration): Cancellable

    /**
     * Publish [[csw.params.events.Event]] from an `eventGenerator` function, which will be executed at `every` frequency And execute `onError` callback
     * for events whose publishing failed.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately.
     * In all other cases of exception, the stream receives a [[csw.event.api.exceptions.PublishFailure]] exception
     * which wraps the underlying exception and also provides the handle to the event which was failed to be published.
     * The provided callback is executed on the failed element and the generator resumes to publish remaining elements.
     *
     * @note Callbacks like `eventGenerator` and `onError` are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * Also note that any exception thrown from `onError` callback is expected to be handled by component developers.
     * @param eventGenerator a function which can generate an event to be published at `every` frequency
     * @param every frequency with which the events are to be published
     * @param onError a callback to execute for each event for which publishing failed
     * @return a handle to cancel the event generation through `eventGenerator`
     */
    //fun publish(eventGenerator: => Option[Event], every: FiniteDuration, onError: PublishFailure => Unit): Cancellable

    /**
     * Publish [[csw.params.events.Event]] from an `eventGenerator` function, which will be started at the specified `startTime`
     * and will be executed at `every` frequency.
     * Also, execute `onError` callback for events whose publishing failed.
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately.
     * In all other cases of exception, the stream receives a [[csw.event.api.exceptions.PublishFailure]] exception
     * which wraps the underlying exception and also provides the handle to the event which was failed to be published.
     * The provided callback is executed on the failed element and the generator resumes to publish remaining elements.
     *
     * @note Callbacks like `eventGenerator` and `onError` are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * Also note that any exception thrown from `onError` callback is expected to be handled by component developers.
     * @param eventGenerator a function which can generate an event to be published at `every` frequency
     * @param startTime the time at which the `eventGenerator` should start generating events
     * @param every frequency with which the events are to be published
     * @param onError a callback to execute for each event for which publishing failed
     * @return a handle to cancel the event generation through `eventGenerator`
     */
    /*
    fun publish(eventGenerator: => Option[Event],
    startTime: TMTTime,
    every: FiniteDuration,
    onError: PublishFailure => Unit
    ): Cancellable
*/
    /**
     * Publish [[csw.params.events.Event]] from an asynchronous `eventGenerator` function, which will be executed at `every` frequency.
     * This API is useful if your eventGenerator function generates an event asynchronously and returns a Future[Event].
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately.
     * In all other cases of exception, the stream receives a [[csw.event.api.exceptions.PublishFailure]] exception
     * which wraps the underlying exception and also provides the handle to the event which was failed to be published.
     *
     * @note Callbacks like `eventGenerator` are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * @param eventGenerator a function which can generate a Future of event to be published at `every` frequency
     * @param every frequency with which the events are to be published
     * @return a handle to cancel the event generation through `eventGenerator`
     */
    //def publishAsync(eventGenerator: => Future[Option[Event]], every: FiniteDuration): Cancellable

    /**
     * Publish [[csw.params.events.Event]] from an asynchronous `eventGenerator` function, which will be started at the specified `startTime`
     * and will be executed at `every` frequency.
     * This API is useful if your eventGenerator function generates an event asynchronously and returns a Future[Event].
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately.
     * In all other cases of exception, the stream receives a [[csw.event.api.exceptions.PublishFailure]] exception
     * which wraps the underlying exception and also provides the handle to the event which was failed to be published.
     *
     * @note Callbacks like `eventGenerator` are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * @param eventGenerator a function which can generate a Future of event to be published at `every` frequency
     * @param startTime the time at which the `eventGenerator` should start generating events
     * @param every frequency with which the events are to be published
     * @return a handle to cancel the event generation through `eventGenerator`
     */
    //def publishAsync(eventGenerator: => Future[Option[Event]], startTime: TMTTime, every: FiniteDuration): Cancellable

    /**
     * Publish [[csw.params.events.Event]] from an asynchronous `eventGenerator` function, which will be executed at `every` frequency
     * And execute `onError` callback for events whose publishing failed.
     * This API is useful if your eventGenerator function generates an event asynchronously and returns a Future[Event].
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately.
     * In all other cases of exception, the stream receives a [[csw.event.api.exceptions.PublishFailure]] exception
     * which wraps the underlying exception and also provides the handle to the event which was failed to be published.
     * The provided callback is executed on the failed element and the generator resumes to publish remaining elements.
     *
     * @note Callbacks like `eventGenerator` and `onError` are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * Also note that any exception thrown from `onError` callback is expected to be handled by component developers.
     * @param eventGenerator a function which can generate a Future of event to be published at `every` frequency
     * @param every frequency with which the events are to be published
     * @param onError a callback to execute for each event for which publishing failed
     * @return a handle to cancel the event generation through `eventGenerator`
     */
    //def publishAsync(eventGenerator: => Future[Option[Event]], every: FiniteDuration, onError: PublishFailure => Unit): Cancellable

    /**
     * Publish [[csw.params.events.Event]] from an asynchronous `eventGenerator` function, which will be started at the specified `startTime`
     * and will be executed at `every` frequency.
     * Also, execute `onError` callback for events whose publishing failed.
     * This API is useful if your eventGenerator function generates an event asynchronously and returns a Future[Event].
     *
     * At the time of invocation, in case the underlying server is not available, [[csw.event.api.exceptions.EventServerNotAvailable]] exception is thrown
     * and the stream is stopped after logging appropriately.
     * In all other cases of exception, the stream receives a [[csw.event.api.exceptions.PublishFailure]] exception
     * which wraps the underlying exception and also provides the handle to the event which was failed to be published.
     * The provided callback is executed on the failed element and the generator resumes to publish remaining elements.
     *
     * @note Callbacks like `eventGenerator` and `onError` are not thread-safe on the JVM. If you are doing side effects/mutations inside the callback, you should ensure that it is done in a thread-safe way inside an actor.
     * Also note that any exception thrown from `onError` callback is expected to be handled by component developers.
     * @param eventGenerator a function which can generate a Future of event to be published at `every` frequency
     * @param startTime the time at which the `eventGenerator` should start generating events
     * @param every frequency with which the events are to be published
     * @param onError a callback to execute for each event for which publishing failed
     * @return a handle to cancel the event generation through `eventGenerator`
     */
    /*
    def publishAsync(
            eventGenerator: => Future[Option[Event]],
    startTime: TMTTime,
    every: FiniteDuration,
    onError: PublishFailure => Unit
    ): Cancellable
*/
    /**
     * Shuts down the connection for this publisher. Using any api of publisher after shutdown should give exceptions.
     * This method should be called while the component is shutdown gracefully.
     *
     * Any exception that occurs will cause the future to complete with a Failure.
     *
     * @return a future which completes when the underlying connection is shut down
     */
    fun shutdown(): Unit // Future[Done]
}