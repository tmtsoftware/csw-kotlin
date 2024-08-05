package csw.params.core.models

import kotlinx.serialization.Serializable

data class ProperMotion(val pmx: Double, val pmy: Double) {
    override fun toString(): String = "$pmx/$pmy"

    companion object {
        val DEFAULT_PROPERMOTION = ProperMotion(0.0, 0.0)
    }
}

@Serializable
@JvmInline
value class PMValue(val uaspyr: Long): Comparable<PMValue> {

    //operators
    operator fun plus(a2: PMValue) = PMValue(uaspyr + a2.uaspyr)

    operator fun minus(a2: PMValue) = PMValue(uaspyr - a2.uaspyr)

    operator fun times(a2: Double) = PMValue((uaspyr * a2).toLong())

    operator fun times(a2: Int) = PMValue(uaspyr * a2)

    operator fun div(a2: Double) = PMValue((uaspyr / a2).toLong())

    operator fun div(a2: Int) = PMValue(uaspyr / a2)

    operator fun unaryPlus() = this

    operator fun unaryMinus() = PMValue(-uaspyr)

    override fun compareTo(other: PMValue): Int = uaspyr.compareTo(other.uaspyr)

}