package org.firstinspires.ftc.teamcode.control

import org.firstinspires.ftc.teamcode.math.enforceNotNaNOrInfinite
import kotlin.math.sign

class FFController(val coefficients: FFCoefficients) {
    constructor(kv: Double, ka: Double, ks: Double): this(FFCoefficients(kv, ka, ks))

    fun calculate(velocity: Double, acceleration: Double): Double {
        velocity.enforceNotNaNOrInfinite("velocity")
        acceleration.enforceNotNaNOrInfinite("acceleration")

        return coefficients.kv * velocity + coefficients.ka * acceleration + coefficients.ks * sign(velocity)
    }

    fun calculate(velocity: Double): Double {
        return calculate(velocity, 0.0)
    }
}