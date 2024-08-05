package csw.params.core.models

import java.util.UUID

/**
 * Implementation of unique id fulfilling TMT requirements (returned from a queue submit).
 *
 * @param id a string representation of unique id
 */
data class Id(val id: String) {

    companion object {
        /**
         * A helper method to create Id with random unique id generator
         *
         * @return an instance of Id
         */
        operator fun invoke(): Id = Id(UUID.randomUUID().toString())
    }
}
