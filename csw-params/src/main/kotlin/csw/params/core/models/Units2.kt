package csw.params.core.models

/*1
Clone this repository at &lt;script src=&quot;https://gist.github.com/willhbr/424f33324588107ee59d4b1ae929843d.js&quot;&gt;&lt;/script&gt;

Type-safe unit conversion with composite units
Units.kt

 */
/**
 * Based on Ben Trengrove's example: https://gist.github.com/bentrengrove/9759a3fbb564d62e1e63f417c58a3895
 */

import java.util.concurrent.TimeUnit

abstract class Unit(val suffix: String, val ratio: Double) {
    internal fun convertToBaseUnit(amount: Double) = amount * ratio
    internal fun convertFromBaseUnit(amount: Double) = amount / ratio

    override fun toString() = suffix
}

/**
 * For units after division, A/B
 */
class QuotientUnit<A: Unit, B: Unit>(a: A, b: B):
    Unit("$a/$b", a.ratio / b.ratio)

/**
 * Creates a QuotientUnit using the division operator, eg Mile / Hour
 */
operator fun <A: Unit, B: Unit> A.div(other: B) = QuotientUnit(this, other)

/**
 * For units after multiplication
 */
class ProductUnit<A: Unit, B: Unit>(a: A, b: B):
    Unit("$a.$b", a.ratio * b.ratio)

/**
 * Create a ProductUnit using multiplication operator, eg Metre * Metre for area
 */
operator fun <A: Unit, B: Unit> A.times(other: B) = ProductUnit(this, other)

/**
 * A quantity with a unit type
 */
class Quantity<T: Unit>(val amount: Double, val unit: T): Comparable<Quantity<T>> {
    /**
     * Convert this type into another compatible type.
     * Type must share parent
     * (eg Mile into Kilometer, because they both are made from Distance)
     */
    infix fun <A: T> into(unit: A): Quantity<A> {
        val baseUnit = this.unit.convertToBaseUnit(amount)
        return Quantity(unit.convertFromBaseUnit(baseUnit), unit)
    }

    /**
     * Add another compatible quantity to this one
     */
    operator fun plus(quantity: Quantity<T>): Quantity<T> {
        val converted = quantity.into(unit).amount
        return Quantity(this.amount + converted, this.unit)
    }

    /**
     * Subtract a compatible quantity from this one
     */
    operator fun minus(quantity: Quantity<T>): Quantity<T> {
        val converted = quantity.into(unit).amount
        return Quantity(this.amount - converted, this.unit)
    }

    /**
     * Divide this by another compatible quantity, gives back a ratio of the sizes
     */
    operator fun div(other: Quantity<T>) = unit.convertToBaseUnit(amount) / other.unit.convertToBaseUnit(other.amount)

    /**
     * Divide this by an incompatible quantity, creating a Quantity<QuotientUnit<T, R>>
     */
    operator fun <R: Unit> div(quantity: Quantity<R>) = Quantity(amount / quantity.amount, unit / quantity.unit)

    /**
     * Multiply this by a quantity to get a product quantity, Quantity<ProductQuantity<T, R>>
     */
    operator fun <R: Unit> times(quantity: Quantity<R>) = Quantity(amount * quantity.amount, unit * quantity.unit)

    /**
     * Multiply this by a scalar value, used for things like "double this distance",
     * "1.5 times the speed", etc
     */
    operator fun times(other: Number) = Quantity(amount * other.toDouble(), unit)

    /**
     * Divide this by a scalar, used for things like "halve the speed"
     */
    operator fun div(other: Number) = Quantity(amount / other.toDouble(), unit)

    /**
     * Compare this value with another quantity - which must have the same type
     * Units are converted before comparison
     */
    override fun compareTo(other: Quantity<T>): Int {
        return this.into(other.unit).amount.compareTo(other.amount)
    }

    /**
     * Check if quantities are equal, not sure if there's a nicer way of checking this
     * partly due to type erasure
     */
    override fun equals(other: Any?): Boolean {
        return this === other || (other is Quantity<*> &&
                other.unit.javaClass == unit.javaClass &&
                unit.convertToBaseUnit(amount) == other.unit.convertToBaseUnit(other.amount))
    }

    override fun hashCode(): Int {
        return unit.convertFromBaseUnit(amount).hashCode()
    }

    override fun toString(): String {
        return "$amount ${unit.suffix}"
    }
}

/**
 * Helpers for converting numbers into quantities
 */
infix fun <T: Unit> Number.into(unit: T) = Quantity(this.toDouble(), unit)
operator fun <T: Unit> Number.times(unit: T) = this into unit
operator fun <T: Unit> T.times(value: Number) = value into this
operator fun <T: Unit> T.invoke(value: Number) = value into this
/**
 * Inverse of Quantity.times(value: Number)
 * Haven't implemented division due to no reciprocal unit
 */
operator fun <T: Unit> Number.times(quantity: Quantity<T>) = quantity * this

open class Distance(suffix: String, ratio: Double): Unit(suffix, ratio) {
    companion object {
        val Mile = Distance("mi", 1.60934 * 1000.0)
        val Kilometer = Distance("km", 1000.0)
        val Meter = Distance("m", 1.0)
        val Centimeter = Distance("cm", 0.1)
        val Millimeter = Distance("mm", 0.01)
    }
}

open class Time(suffix: String, ratio: Double): Unit(suffix, ratio) {
    companion object {
        val Hour = Time("h", TimeUnit.HOURS.toMillis(1L).toDouble())
        val Minute = Time("m", TimeUnit.MINUTES.toMillis(1L).toDouble())
        val Second = Time("s", TimeUnit.SECONDS.toMillis(1L).toDouble())
        val Millisecond = Time("ms", 1.0)
    }
}

open class Mass(suffix: String, ratio: Double): Unit(suffix, ratio) {
    companion object {
        val Gram = Mass("g", 1.0)
        val Kilogram = Mass("kg", 1000.0)
        val Tonne = Mass("t", 1000 * 1000.0)
    }
}
