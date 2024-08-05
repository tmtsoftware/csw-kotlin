package csw.params.core.models

import kotlinx.serialization.Serializable

@Serializable
data class ExposureNumber(val exposureNumber: Int, val subArray: Int? = null) {
    override fun toString(): String {
        val exNumStr = String.format("%04d", exposureNumber)
        if (subArray != null) {
            val subArrayStr = String.format("%02d", subArray)
            return "$exNumStr-$subArrayStr"
        } else
            return exNumStr
    }

    /** Returns the next exposure number */
    fun next(): ExposureNumber = ExposureNumber(exposureNumber + 1, subArray)

    /** Returns next subarray number */
    fun nextSubArray(): ExposureNumber =
        ExposureNumber(exposureNumber, if (subArray == null) 0 else subArray+1)

    companion object {
        private fun isNumeric(toCheck: String): Boolean = toCheck.all { char -> char.isDigit() }

        /**
         * A convenience to use when constructing ExposureId
         * @return the default ExposureNumber
         */
        fun default() = ExposureNumber(0, null)

        private fun parseToInt(exposureNo: String, allowedLength: Int): Int {
            require(exposureNo.isNotEmpty() && exposureNo.length == allowedLength) {
                "Invalid exposure number: $exposureNo. " +
                        "An ExposureNumber must be a 4 digit number and optional 2 digit subarray in format XXXX or XXXX-XX."
            }
            require(isNumeric(exposureNo)) { "A non-numeric exposure number: $exposureNo was provided."}
            return exposureNo.toInt()
        }

        // This provides apply constructor
        operator fun invoke(exposureNumber: String): ExposureNumber {
            val items = exposureNumber.split(Separator.Hyphen)
            require(items.size <= 2 && items.isNotEmpty()) { "An exposure number consists of up to 2 parts in format: XXXX or XXXX-XX." }

            val exposureArrayStr = items[0]

            return when (items.size) {
                2 -> {
                    val subArrayStr = items[1]
                    val x = parseToInt(exposureArrayStr, allowedLength = 4)
                    val y = parseToInt(subArrayStr, allowedLength = 2)
                    ExposureNumber(x, y)
                }
                1 -> {
                    ExposureNumber(parseToInt(exposureNumber, allowedLength = 4))
                }
                else -> throw IllegalArgumentException("There are zero ExposureNumber arguments somehow!")
            }
        }
    }
}
