package org.firstinspires.ftc.teamcode.control

import kotlin.jvm.Throws
import kotlin.math.sign

class Feedforward(val coefficients: Coefficients) {
    constructor(kv: Double, ka: Double, ks: Double): this(Coefficients(kv, ka, ks))
    constructor(kv: Int, ka: Int, ks: Int): this(Coefficients(kv, ka, ks))

    var maxOutput: Double = 1.0
        @Throws(IllegalArgumentException::class)
        set(new) {
            if (new.isNaN()) throw IllegalArgumentException("Max Output Cannot Be NaN")
            field = new
        }

    var minOutput: Double = -1.0
        @Throws(IllegalArgumentException::class)
        set(new) {
            if (new.isNaN()) throw IllegalArgumentException("Max Output Cannot Be NaN")
            field = new
        }

    fun calculate(currentVelocity: Double, targetVelocity: Double, targetAcceleration: Double): Double {
        return (coefficients.kv * targetVelocity + coefficients.ka * targetAcceleration + coefficients.ks * sign(currentVelocity))
            .coerceIn(minOutput..maxOutput)
    }

    fun calculate(currentVelocity: Double, targetVelocity: Double) = calculate(currentVelocity, targetVelocity, 0.0)

    data class Coefficients(val kv: Double, val ka: Double, val ks: Double) {
        constructor(kv: Int, ka: Int, ks: Int): this(kv.toDouble(), ka.toDouble(), ks.toDouble())
    }
}