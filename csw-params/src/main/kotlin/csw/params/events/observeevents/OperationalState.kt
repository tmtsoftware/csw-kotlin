package csw.params.events.observeevents

import csw.params.keys.Choices
import kotlinx.serialization.Serializable

/**
 * Enumeration indicating if the detector system is available and operational.
 *  READY, BUSY, ERROR.
 *  READY indicates system can execute exposures.
 *  BUSY indicates system is BUSY most likely acquiring data.
 *  ERROR indicates the detector system is in an error state.
 *  This could  happen as a result of a command or a spontaneous failure.
 *  Corrective  action is required.
 */
@Serializable
enum class OperationalState {
    READY,
    ERROR,
    BUSY;

    companion object {
        fun toChoices(): Choices = Choices(OperationalState.entries.map { it.name }.toSet())
    }
}

/*
object JOperationalState {
    val READY: OperationalState = OperationalState.READY
    val ERROR: OperationalState = OperationalState.ERROR
    val BUSY: OperationalState  = OperationalState.BUSY
}
*/

