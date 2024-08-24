package csw.csw.location.api.models

enum class NetworkType(val envKey: String) {
    Outside("AAS_INTERFACE_NAME"),
    Inside("INTERFACE_NAME")
}

