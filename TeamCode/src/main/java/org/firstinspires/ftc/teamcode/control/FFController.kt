package org.firstinspires.ftc.teamcode.control

import org.firstinspires.ftc.teamcode.math.isReal
import kotlin.math.sign

class FFController(val coefficients: FFCoefficients) {
    constructor(kv: Double, ka: Double, ks: Double): this(FFCoefficients(kv, ka, ks))

    // Configuration

    var maxOutput: Double = 1.0
        @Throws(IllegalArgumentException::class)
        set(max) {
            require(max.isReal()) {
                "Expected real max output. Got: $max"
            }
            field = max
        }

    var minOutput: Double = -1.0
        @Throws(IllegalArgumentException::class)
        set(min) {
            require(min.isReal()) {
                "Expected real min output. Got: $min"
            }
            field = min
        }

    /**
     * Calculates the feedforward power for the input target velocity and acceleration.
     *
     * @throws IllegalArgumentException if either velocity or acceleration is one of: Nan, Infinite
     */
    @Throws(IllegalArgumentException::class)
    fun calculate(velocity: Double, acceleration: Double): Double {
        require(velocity.isReal()) {
            "Expected real velocity. Got: $velocity"
        }
        require(acceleration.isReal()) {
            "Expected real acceleration. Got: $acceleration"
        }

        val rawOutput = coefficients.kv * velocity + coefficients.ka * acceleration + coefficients.ks * sign(velocity)

        return rawOutput.coerceIn(minOutput, maxOutput)
    }

    /**
     * Calculates the feedforward power for the input target velocity, the acceleration is set to 0.0
     *
     * @throws IllegalArgumentException if velocity is one of: Nan, Infinite
     */
    @Throws(IllegalArgumentException::class)
    fun calculate(velocity: Double): Double {
        return calculate(velocity, 0.0)
    }
}