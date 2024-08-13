package csw.location.api.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class ConnectionTypeTest: FunSpec( {

    // DEOPSCSW-14: Codec for data model
    test("ConnectionType should be any one of this types : 'http', 'tcp' and 'akka' | DEOPSCSW-14") {

        val expectedConnectionTypeValues = setOf("http", "tcp", "akka")

        val actualConnectionTypeValues: Set<String> =
            ConnectionType.values().map { it.cname }.toSet()

        actualConnectionTypeValues shouldContainExactlyInAnyOrder  expectedConnectionTypeValues
    }

}
)
