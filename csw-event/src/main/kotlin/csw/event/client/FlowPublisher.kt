package csw.csw.event.client

import csw.params.core.models.Prefix
import csw.params.events.Event
import csw.params.events.SystemEvent
import csw.params.keys.ByteKey
import csw.params.keys.IntegerKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

class FlowPublisher {

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun broadcast(scope: CoroutineScope) {
        scope.launch {
            while(true) {
                delay(1000.milliseconds)
                val ev = testEvent()
                log("Emitting $ev")
                _eventFlow.emit(ev)
            }
        }
    }

}

private var zeroTime = System.currentTimeMillis()

fun log(message: Any?) =
    println("${System.currentTimeMillis() - zeroTime} " +
            "[${Thread.currentThread().name}] $message")


fun testEvent(): Event {
    val k1 = IntegerKey("encoder")
    val k2 = IntegerKey("windspeed")

    val i1 = k1.set(22)
    val i2 = k2.set(44)

    val sc1 = SystemEvent(Prefix("ESW.test"), "status").add(i1, i2)
    return sc1
}

fun main(): Unit = runBlocking {
    val fp = FlowPublisher()
    fp.broadcast(this)
    delay(250.milliseconds)
    fp.eventFlow.collect {
        log("A collecting event $it")
    }
}