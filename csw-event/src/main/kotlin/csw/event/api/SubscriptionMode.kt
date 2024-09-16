package csw.event.api

/**
 * The subscription mode is used to control the rate of events received by the subscriber.
 *
 * Terminology:
 *
 * `Tick:` Refers to the exact clock tick
 * `Interval:` Refers to the duration between two ticks
 *
 * Parameter mode can have two values -
 *
 * - [[SubscriptionModes.RateAdapterMode]]
 * - [[SubscriptionModes.RateLimiterMode]]
 *
 * These two modes can be accessed  in Java as:
 *
 * - [[SubscriptionModes.jRateAdapterMode]]
 * - [[SubscriptionModes.jRateLimiterMode]]
 *
 * {{{
 * ---------------------------------------------------------------------------------------------------------------------
 *                    |                 Rate Adapter                  |                 Rate Limiter
 * ---------------------------------------------------------------------------------------------------------------------
 *                    |                                               |
 * 1. Description     | Ensures that the subscriber will receive an   | Ensures that events will be delivered as soon
 *                    | event exactly at each tick.                   | as they are published along with the guarantee
 *                    |                                               | that no more than one event is delivered within
 *                    |                                               | a given interval.
 *                    |                                               |
 * 2. Fast Publisher  | The subscriber receives the most recently     | The subscriber receives the event as soon as it
 *                    | published event at each tick.                 | is published during the interval.
 *                    |                                               |
 *                    | Some events will be dropped because of the    | Some events will be dropped because of the
 *                    | guarantee that exactly one event is received  | guarantee that at most one event is received
 *                    | at each tick.                                 | within an interval.
 *                    |                                               |
 * 3. Slow Publisher  | The subscriber will get the last published    | The subscriber will not receive events at some
 *                    | event on each interval.                       | intervals.
 *                    |                                               |
 *                    | The subscriber will receive duplicate events  | The subscriber will never receive duplicate
 *                    | in case no element is published during that   | events.
 *                    | interval.                                     |
 *                    |                                               |
 * 4. Use cases       | The synchronized event stream received on     | When there is no strict requirement of
 *                    | subscription can be used as clock ticks by a  | synchronization of received events with the
 *                    | component to perform other arbitrary tasks.   | clock tick and minimum latency is required.
 *                    |                                               |
 * 5. Trade offs      | Latency of events received in this mode will  | The events received will not be in
 *                    | be more than that in                          | synchronization with the subscription frequency.
 *                    | [[SubscriptionModes.RateLimiterMode]].        |
 *                    |                                               | Some intervals might not receive an event.
 *                    |                                               |
 *                    |                                               | Duration observed between events received might
 *                    |                                               | not match with the specified subscription rate.
 *                    |                                               |
 * }}}
 */
enum class SubscriptionMode {
    /**
    * List of supported Subscription Modes needed while subscribing to set of [[csw.params.events.EventKey]].
    * Refer to documentation for [[csw.event.api.scaladsl.SubscriptionMode]] for more details.
    */

    /**
     * Subscription mode to adjust the rate in which subscribed events arrive.
     * See [[csw.event.api.scaladsl.SubscriptionMode]] for more details.
     */
    RateAdapterMode,

    /**
     * Subscription mode to limit the rate in which subscribed events arrive.
     * See [[csw.event.api.scaladsl.SubscriptionMode]] for more details.
     */
    RateLimiterMode

    /**
     * Subscription mode for Java to adjust the rate in which subscribed events arrive.
     * See [[csw.event.api.scaladsl.SubscriptionMode]] for more details.
     */
    //jRateAdapterMode: SubscriptionMode = RateAdapterMode

    /**
     * Subscription mode for Java to limit the rate in which subscribed events arrive.
     * See [[csw.event.api.scaladsl.SubscriptionMode]] for more details.
     */
    //val jRateLimiterMode: SubscriptionMode = RateLimiterMode
}
