package csw.location.api.models

/**
 * Represents a type of connection offered by the Component
 *
 * @param entryName A name of the connection type e.g. akka, http or tcp
 */
enum class ConnectionType(val cname: String) {

    /**
     * Represents a HTTP type of connection
     */
    HttpType("http"),

    /**
     * Represents a TCP type of connection
     */
    TcpType("tcp"),

    /**
     * Represents an Akka type of connection
     */
    AkkaType("akka");

    companion object {
        fun withName(name: String): ConnectionType {
            for (entry in entries) {
                if (entry.cname == name) return entry
            }
            throw IllegalArgumentException("No connection type with name: $name")
        }
    }
}
