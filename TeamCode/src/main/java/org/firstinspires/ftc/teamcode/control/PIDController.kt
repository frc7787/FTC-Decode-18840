package org.firstinspires.ftc.teamcode.control

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.math.enforceNotNaNOrInfinite
import kotlin.jvm.Throws
import kotlin.math.abs

class PIDController(val coefficients: PIDCoefficients) {
    constructor(kp: Double, ki: Double, kd: Double): this(PIDCoefficients(kp, ki, kd))

    // ----------
    // State

    private val timer = ElapsedTime()

    private var isFirstIteration = false

    private var previousTime      = 0.0
    private var previousError     = 0.0
    private var previousReference = 0.0
    private var integralSum       = 0.0

    // ----------
    // Config

    var integralMax = 0.25
        @Throws(IllegalArgumentException::class)
        set(max) {
            max.enforceNotNaNOrInfinite("integral max")
            require(max > 0.0) {
                "Expected integral max > 0.0. Got: $max"
            }
            field = max
        }

    var integralMin = -0.25
        @Throws(IllegalArgumentException::class)
        set(min) {
            min.enforceNotNaNOrInfinite("integral min")
            require(min < 0.0) {
                "Expected integral min < 0.0. Got: $min"
            }
            field = min
        }

    var tolerance = 10.0
        set(tolerance) {
            tolerance.enforceNotNaNOrInfinite("tolerance")
            require(tolerance >= 0.0) {
                "Expected positive tolerance. Got: $tolerance"
            }
        }

    // ----------
    // Core

    /**
     * Calculates the output power based on the current state (state) and the desired state
     * (reference).
     *
     * @throws IllegalArgumentException if either state or reference are one of: Nan, Infinity
     */
    @Throws(IllegalArgumentException::class)
    fun calculate(state: Double, reference: Double): Double {
        state.enforceNotNaNOrInfinite("state")
        reference.enforceNotNaNOrInfinite("reference")

        if (isFirstIteration) {
            timer.reset()
            isFirstIteration = false
        }

        val currentTime = timer.milliseconds()
        val deltaTime = currentTime - previousTime

        var error = reference - state
        if (abs(error) < tolerance) error = 0.0

        val deltaError = error - previousError

        val proportional = coefficients.kp * error

        // Reset the integral sum if the reference changes (and we aren't already within tolerance)
        if (reference != previousReference && abs(error) < tolerance) {
            integralSum = 0.0
        }

        integralSum += error * deltaTime
        integralSum.coerceIn(integralMin, integralMax)

        val integral = integralSum * coefficients.ki

        val derivative = if (deltaTime == 0.0) 0.0 else (deltaError / deltaTime) * coefficients.kd

        previousTime      = currentTime
        previousError     = error
        previousReference = reference

        return proportional + integral + derivative
    }

    fun reset() {
        isFirstIteration = true
        previousError     = 0.0
        previousReference = 0.0
        previousTime      = 0.0
        integralSum       = 0.0
    }
}