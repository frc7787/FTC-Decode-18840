package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.math.isReal
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Constrained(
    initial: Double,
    private val min: Double,
    private val max: Double,
    private val violationPolicy: ViolationPolicy = ViolationPolicy.SILENT
): ReadWriteProperty<Any, Double> {
    var value = initial

    init {
        require(!initial.isNaN()) {
            "Constrained value cannot be NaN"
        }
        require(!min.isNaN()) {
            "Constrained value minimum cannot be NaN"
        }
        require(!max.isNaN()) {
            "Constrained value maximum cannot be NaN"
        }
        require(min < max) {
            "Constrained value minimum must be less than maximum"
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): Double {
        return value
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Double) {
        require(value.isReal()) {
            "Constrained value cannot be NaN or Infinite. Got: $value"
        }
        if (value !in min..max) {
            this.value = when (violationPolicy) {
                ViolationPolicy.SILENT -> this.value
                ViolationPolicy.COERCE -> value.coerceIn(min, max)
                ViolationPolicy.ERROR  -> throw ConstraintViolationException(value, min, max)
            }
        }
    }

    enum class ViolationPolicy {
        SILENT,
        ERROR,
        COERCE
    }
}

class ConstraintViolationException(value: Double, min: Double, max: Double): Exception(
    "Expected value to be within the range $min to $max (inclusive). Got: $value"
)

class NotNaN(initial: Double): ReadWriteProperty<Any, Double> {
    var value = initial

    init {
        if (initial.isNaN()) {
            throw IllegalArgumentException("Attempted to set the value of NotNaN to NaN.")
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): Double {
        return value
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Double) {
        if (value.isNaN()) {
            throw IllegalArgumentException("Attempted to set the value of NotNan to NaN.")
        }
        this.value = value
    }
}
